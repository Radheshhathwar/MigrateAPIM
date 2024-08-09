package com.migrate.apim.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "github")
@Data
public class GithubConfig {
    private String repoUrl;
    private String username;
    private String token;

}