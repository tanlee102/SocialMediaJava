package com.example.CloudAPI.Users.service;

import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserDetailsServiceImplement(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userService.findByCustomUserDetail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

}
