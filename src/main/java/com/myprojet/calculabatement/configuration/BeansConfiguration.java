package com.myprojet.calculabatement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeansConfiguration {
    @Autowired
    private CustomProperties customProperties;

    @Bean
    public WebClient myWebClientForApiInsee(WebClient.Builder webClientBuilder) {
        String baseApiUrl = customProperties.getApiInseeBdmUrl();
        return webClientBuilder
                .clone()
                .baseUrl(baseApiUrl)
                .defaultHeader("Authorization", "Bearer a72981eb-1d7a-3d7e-9e21-ef554dbf6f35")
                .build();
    }

}
