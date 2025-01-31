package com.example.WanderHub.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Usa un cookie per il token CSRF
                .and()
                .authorizeRequests()
                .requestMatchers("/login", "/auth/**").permitAll() // Consenti l'accesso pubblico
                .anyRequest().authenticated() // Altre richieste necessitano di autenticazione
                .and()
                .formLogin()
                .permitAll();

        return http.build();
    }
}
