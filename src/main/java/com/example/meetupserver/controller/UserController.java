package com.example.meetupserver.controller;

import com.example.meetupserver.dto.MessageResponse;
import com.example.meetupserver.dto.Password;
import com.example.meetupserver.dto.SignupRequest;
import com.example.meetupserver.dto.entityDTO.UserDTO;
import com.example.meetupserver.model.ERole;
import com.example.meetupserver.model.Role;
import com.example.meetupserver.model.User;
import com.example.meetupserver.repository.RoleRepository;
import com.example.meetupserver.repository.UserRepository;
import com.example.meetupserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")

@CrossOrigin
public class UserController {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public User getUser(@PathVariable long id){
        User user = userService.getUserById(id);
        return user;
    }


    @PostMapping("/user")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> addUser(@RequestBody @Valid SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is exist"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is exist"));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getName(),
                signupRequest.getSurname(),
                signupRequest.getPost(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> reqRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (reqRoles == null) {
            Role userRole = roleRepository
                    .findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error, Role EMPLOYEE is not found"));
            roles.add(userRole);
        } else {
            reqRoles.forEach(r -> {
                switch (r) {
                    case "chief":
                        Role adminRole = roleRepository
                                .findByName(ERole.ROLE_CHIEF)
                                .orElseThrow(() -> new RuntimeException("Error, Role CHIEF is not found"));
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleRepository
                                .findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error, Role EMPLOYEE is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User CREATED"));
    }

    @PutMapping("/user")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO){
        User user = userService.getUserById(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(user.getName());
        user.setSurname(user.getSurname());
        user.setPost(user.getPost());

        if (userDTO.getRoles().size() != 0) {
            user.setRoles(new HashSet<>());
            userDTO.getRoles().forEach(r -> {
                switch (r) {
                    case "chief":
                        Role chiefRole = roleRepository
                                .findByName(ERole.ROLE_CHIEF)
                                .orElseThrow(() -> new RuntimeException("Error, Role Chief is not found"));
                        user.getRoles().add(chiefRole);
                        break;
                    default:
                        Role employeeRole = roleRepository
                                .findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error, Role EMPLOYEE is not found"));
                        user.getRoles().add(employeeRole);
                        break;
                }
            });
        }
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User UPDATED"));
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> deleteUser(@PathVariable long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse("User DELETED"));
    }

    @PutMapping("/user/password/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody @Valid Password password){
        User user = userService.getUserById(id);
        user.setPassword(passwordEncoder.encode(password.getPassword()));
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User password CHANGED"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('CHIEF')")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> {user.setPassword(null);});
        return users;
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('CHIEF')")
    public User getYourUser(HttpServletRequest request){
        User user = userService.getUserFromJWT(request);
        user.setPassword(null);
        return user;
    }
}
