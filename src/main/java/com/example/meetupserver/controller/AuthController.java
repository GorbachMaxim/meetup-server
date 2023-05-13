package com.example.meetupserver.controller;

import com.example.meetupserver.config.jwt.JwtUtils;
import com.example.meetupserver.dto.JwtResponse;
import com.example.meetupserver.dto.LoginRequest;
import com.example.meetupserver.dto.MessageResponse;
import com.example.meetupserver.dto.SignupRequest;
import com.example.meetupserver.model.ERole;
import com.example.meetupserver.model.Role;
import com.example.meetupserver.model.User;
import com.example.meetupserver.repository.RoleRepository;
import com.example.meetupserver.repository.UserRepository;
import com.example.meetupserver.service.UserDetailsImpl;
import com.example.meetupserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserDetailsServiceImpl userService;
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), 
						loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		User user = userService.getUserByUsername(loginRequest.getUsername());
		return ResponseEntity.ok(new JwtResponse(jwt,
				user));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequest signupRequest) {
		
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
		Set<Role> roles1 = new HashSet<>();
		
		if (reqRoles == null) {
			Role userRole = roleRepository
					.findByName(ERole.ROLE_EMPLOYEE)
					.orElseThrow(() -> new RuntimeException("Error, Role EMPLOYEE is not found"));
			roles1.add(userRole);
		} else {
			reqRoles.forEach(r -> {
				switch (r) {
				case "chief":
					Role adminRole = roleRepository
						.findByName(ERole.ROLE_CHIEF)
						.orElseThrow(() -> new RuntimeException("Error, Role CHIEF is not found"));
					roles1.add(adminRole);
					
					break;

				default:
					Role userRole = roleRepository
						.findByName(ERole.ROLE_EMPLOYEE)
						.orElseThrow(() -> new RuntimeException("Error, Role EMPLOYEE is not found"));
					roles1.add(userRole);
				}
			});
		}
		user.setRoles(roles1);
		userRepository.save(user);
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						signupRequest.getUsername(),
						signupRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		User user1 = userService.getUserByUsername(signupRequest.getUsername());
		return ResponseEntity.ok(new JwtResponse(jwt,
				user1));
	}
}
