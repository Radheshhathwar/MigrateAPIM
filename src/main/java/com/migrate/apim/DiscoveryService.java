package com.migrate.apim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    public Path discoverMainRamlFile(Path repoPath) throws IOException {
        List<Path> ramlFiles = Files.walk(repoPath)
                .filter(path -> path.toString().endsWith(".raml"))
                .toList();

        // Patterns to identify the main RAML file
        List<String> mainFilePatterns = List.of("main", "api", "procurement", "supplierdata");

        Path mainRamlFile = ramlFiles.stream()
                .filter(path -> {
                    String filename = path.getFileName().toString().toLowerCase();
                    return mainFilePatterns.stream().anyMatch(filename::contains);
                })
                .findFirst()
                .orElseThrow(() -> new IOException("Main RAML file not found"));

        // Log the identified file
        logger.info("Main RAML file identified: {}", mainRamlFile);

        return mainRamlFile;
    }
}
