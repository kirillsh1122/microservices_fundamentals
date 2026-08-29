package com.microservice.architecture.overview.storage_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers(HttpMethod.POST, "/storages/**").hasAuthority("SCOPE_createStorageEntries")
                .requestMatchers(HttpMethod.DELETE, "/storages/**").hasAuthority("SCOPE_deleteStorageEntries")
                .requestMatchers(HttpMethod.GET, "/storages/**").hasAuthority("SCOPE_readStorageEntries")
                .anyRequest().authenticated()
        ).oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults())
        );

        return http.build();
    }
}
