package com.microservice.architecture.overview.storage_client.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.authorizeHttpRequests(
//                authorizeRequests -> authorizeRequests
//                        .anyRequest().permitAll()
//        ).build(); //.formLogin(Customizer.withDefaults())
//    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler)
            throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/storage-client", true)
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("STORAGE_CLIENT_SESSION")
                );

        return http.build();
    }

    @Bean
    public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository) {

        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(
                        clientRegistrationRepository
                );

        handler.setPostLogoutRedirectUri("{baseUrl}/storage-client");

        return handler;
    }

}
