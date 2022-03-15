package com.myprojet.calculabatement.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.myprojet.calculabatement")
@Getter
@Setter
@Slf4j
public class CustomProperties {
    private String apiInseeBdmUrl;
}
