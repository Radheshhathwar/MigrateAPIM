package com.migrate.apim.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "azure")
@Data
public class AzureConfig {
    private String subscriptionId;
    private String resourceGroup;
    private String apimServiceName;
}