package com.example.CloudAPI;

import com.example.CloudAPI.Role.Role;
import com.example.CloudAPI.Role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RunConfig {

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository){
        return args -> {
//            Role role_user = new Role();
//            role_user.setName("USER");
//            roleRepository.save(role_user);
//
//            Role role_admin = new Role();
//            role_admin.setName("ADMIN");
//            roleRepository.save(role_admin);
//
//            Role role_supreme = new Role();
//            role_supreme.setName("SUPREME");
//            roleRepository.save(role_supreme);
        };
    }
}
