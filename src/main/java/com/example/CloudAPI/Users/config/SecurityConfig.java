package com.example.CloudAPI.Users.config;

import com.example.CloudAPI.Users.filter.JwtAuthenticationFilter;
import com.example.CloudAPI.Users.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImplement userDetailsServiceImplement;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImplement userDetailsServiceImplement, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsServiceImplement = userDetailsServiceImplement;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private final List<UrlMethodPair> allowedUrls = Arrays.asList(

            new UrlMethodPair("/api/v1/auth/login/**", HttpMethod.POST),
            new UrlMethodPair("/api/v1/auth/register/**", HttpMethod.POST),
            new UrlMethodPair("/api/v1/auth/third/**", HttpMethod.POST),
            new UrlMethodPair("/api/v1/auth/password/change", HttpMethod.POST),
            new UrlMethodPair("/api/v1/auth/email/verify", HttpMethod.POST),

            new UrlMethodPair("/api/v1/posts/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/comments/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/media/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/users/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/likes/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/bookmarks/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/tags/**", HttpMethod.GET),
            new UrlMethodPair("/api/v1/notification/**", HttpMethod.GET),

            new UrlMethodPair("/home/**", HttpMethod.GET),
            new UrlMethodPair("/index", HttpMethod.GET),

            new UrlMethodPair("/api/conversations", HttpMethod.GET),
            new UrlMethodPair("/api/conversations/**", HttpMethod.GET),
            new UrlMethodPair("/api/conversation", HttpMethod.GET),
            new UrlMethodPair("/api/v1/messages/**", HttpMethod.GET)
    );

    private final List<UrlMethodPair> adminSupremeUrls = Arrays.asList(
            new UrlMethodPair("/api/v1/users/toggle-ban/**", HttpMethod.POST),
            new UrlMethodPair("/api/v1/users/role/user/**", HttpMethod.POST)
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    // Allow all requests to the allowedUrls without authentication
                    allowedUrls.forEach(pair -> req.requestMatchers(pair.getMethod(), pair.getUrl()).permitAll());

                    // Restrict access to adminSupremeUrls to users with either ADMIN or SUPREME authority
                    adminSupremeUrls.forEach(pair -> req.requestMatchers(pair.getMethod(), pair.getUrl())
                            .hasAnyAuthority("ADMIN", "SUPREME"));

                    req.requestMatchers("/ws/**").permitAll();
                    req.requestMatchers("/error/**").permitAll();
                    req.requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**", "/swagger-ui.html").permitAll();

                    // Any other request needs to be authenticated
                    req.anyRequest().authenticated();
                })
                .userDetailsService(userDetailsServiceImplement)
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
