package com.migrate.apim.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "apim")
@Data
public class ApimConfig {
    private String fileName;
    private String repoPath;
    private String remoteUrl;
    private String username;
    private String password;

}