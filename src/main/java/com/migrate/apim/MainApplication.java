package com.migrate.apim;

import org.raml.v2.api.model.v10.api.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private ParseService parseService;

    @Autowired
    private MigrationService migrationService;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            String localRepoPath = "/home/radhesh/mulesoft-test-repo/supplier-data-radar-api";
            Path repoPath = Paths.get(localRepoPath); // Convert the string path to a Path object

            // Discover the main RAML file
            Path filepath = discoveryService.discoverMainRamlFile(repoPath);
            logger.info("Discovered RAML file: {}", filepath);

            // Parse the RAML file
            String openApiContent = parseService.parse(filepath);
            logger.info("Parsed RAML file content successfully.");

            // Migrate to Azure
            migrationService.writeOpenApiToFileAndPushToGit(openApiContent);
            logger.info("Migration to Azure API Management completed successfully.");

        } catch (Exception e) {
            logger.error("An error occurred during the migration process", e);
        }
    }
}