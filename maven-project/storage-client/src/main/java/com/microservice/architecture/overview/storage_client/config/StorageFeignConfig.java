package com.microservice.architecture.overview.storage_client.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Slf4j
@Configuration
public class StorageFeignConfig {

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(
            OAuth2AuthorizedClientService authorizedClientService) {

        return requestTemplate -> {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientService.loadAuthorizedClient(
                            "storage-client",
                            authentication.getName()
                    );

            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            log.info("DEMO ONLY - JWT sent to storage-service: {}", accessToken);

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + accessToken
            );
        };
    }

}
