package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

/*
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Usa un cookie per il token CSRF
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/auth/**").permitAll() // Consenti l'accesso pubblico a queste rotte
                                .anyRequest().authenticated() // Richiede autenticazione per tutte le altre rotte
                )
                .formLogin(withDefaults()); // Permetti il login pubblico

        return http.build();
    }
}
*/


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilita completamente la protezione CSRF usando la nuova API
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/auth/**").permitAll() // Consenti l'accesso pubblico a queste rotte
                                .anyRequest().authenticated() // Richiede autenticazione per tutte le altre rotte
                )
                .formLogin(withDefaults()); // Permetti il login pubblico

        return http.build();
    }
}
