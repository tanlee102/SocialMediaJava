package com.example.CloudAPI.Users.controller;

import com.example.CloudAPI.Users.model.AuthenticationResponse;
import com.example.CloudAPI.Users.model.RequestUser;
import com.example.CloudAPI.Users.model.User;
import com.example.CloudAPI.Users.service.AuthenticationService;
import com.example.CloudAPI.Users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${application.secret.api_key}")
    private String API_KEY;

    @PostMapping("/third")
    public ResponseEntity<AuthenticationResponse> thirdLogin(
            @RequestBody RequestUser request,
            @RequestHeader("API-KEY") String apiKey
    ) {
        if(apiKey.equals(API_KEY)){
            Optional<User> optionalUser = userService.findByEmail(request.getEmail());
            if(optionalUser.isPresent()){
                User userDetails = optionalUser.get();
                return ResponseEntity.ok(authenticationService.generateDirectToken(userDetails));
            }else{
                return ResponseEntity.ok(authenticationService.register(request));
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RequestUser request,
            @RequestHeader("API-KEY") String apiKey
    ){
        if(apiKey.equals(API_KEY)){
            if(userService.isEmailExists(request.getEmail())){
                return ResponseEntity.ok(authenticationService.authenticate(request));
            }else{
                return ResponseEntity.ok(authenticationService.register(request));
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody RequestUser request,
            @RequestHeader("API-KEY") String apiKey
    ) {
        if(apiKey.equals(API_KEY)){
            return ResponseEntity.ok(authenticationService.authenticate(request));
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestBody RequestUser request,
                                  @RequestHeader("API-KEY") String apiKey) {
        if(apiKey.equals(API_KEY)) {
            if(userService.changePassword(passwordEncoder.encode(request.getPassword()), request.getEmail())){
                return ResponseEntity.ok(authenticationService.authenticate(request));
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/email/change")
    public ResponseEntity<AuthenticationResponse> changeEmail(@RequestBody RequestUser requestUser, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Optional<User> user = userService.getUserById(userId);
        User myUser = user.get();
        if(passwordEncoder.matches(requestUser.getPassword(), myUser.getPassword())){
            myUser.setEmail(requestUser.getEmail());
            myUser.setVerified(false);
            userService.saveUser(myUser);
            return ResponseEntity.ok(authenticationService.generateDirectToken(myUser));
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/password/edit")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestBody RequestUser requestUser, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Optional<User> user = userService.getUserById(userId);
        User myUser = user.get();
        if(passwordEncoder.matches(requestUser.getPassword(), myUser.getPassword())){
            myUser.setPassword(passwordEncoder.encode(requestUser.getNewPassword()));
            userService.saveUser(myUser);
            return ResponseEntity.ok(authenticationService.generateDirectToken(myUser));
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/email/verify")
    public ResponseEntity<AuthenticationResponse> verifyEmail(
            @RequestBody RequestUser request,
            @RequestHeader("API-KEY") String apiKey
    ){
        if(apiKey.equals(API_KEY)){
            Optional<User> user = userService.findByEmail(request.getEmail());
            if(user.isPresent()){
                User userDetails = user.get();
                userDetails.setVerified(true);
                userService.saveUser(userDetails);
                return ResponseEntity.ok(authenticationService.generateDirectToken(userDetails));
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

}