package com.example.meetupserver.service;

import com.example.meetupserver.config.jwt.AuthTokenFilter;
import com.example.meetupserver.config.jwt.JwtUtils;
import com.example.meetupserver.model.User;
import com.example.meetupserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public User getUserById(long id){
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void saveOrUpdate(User user){
        userRepository.save(user);
    }

    public void deleteUserById(long id){
        userRepository.deleteById(id);
    }



    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(login)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s is not found", login)));
        return UserDetailsImpl.build(user);
    }


    public User getUserFromJWT(HttpServletRequest request) throws UsernameNotFoundException{
        String jwt = AuthTokenFilter.parseJwt(request);
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User is not found")));
        return user;

    }

}
