package com.example.CloudAPI.Users.service;

import com.example.CloudAPI.UserProfiles.UserProfile;
import com.example.CloudAPI.Users.model.AuthenticationResponse;
import com.example.CloudAPI.Role.Role;
import com.example.CloudAPI.Users.model.RequestUser;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Role.RoleRepository;
import com.example.CloudAPI.Users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RequestUser request) {
        User user = new User();

        if(userRepository.findByUsername(request.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("User with email " + request.getUsername() + " already exists");
        }

        Optional<Role> role = roleRepository.findByName(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role.get());
        user.setEmail(request.getEmail());
        user.setVerified(true);
        user.setUsername(request.getUsername());

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(RequestUser request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse generateDirectToken(User user){
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

}