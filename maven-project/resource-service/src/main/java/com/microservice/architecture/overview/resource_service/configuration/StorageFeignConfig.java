package com.microservice.architecture.overview.resource_service.configuration;


import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Slf4j
@Configuration
public class StorageFeignConfig {

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(
            OAuth2AuthorizedClientManager authorizedClientManager) {

        return requestTemplate -> {

            OAuth2AuthorizeRequest authorizeRequest =
                    OAuth2AuthorizeRequest
                            .withClientRegistrationId("storage-service")
                            .principal("resource-service")
                            .build();

            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientManager.authorize(authorizeRequest);

            String accessToken =
                    authorizedClient.getAccessToken().getTokenValue();

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + accessToken
            );
        };
    }

}
