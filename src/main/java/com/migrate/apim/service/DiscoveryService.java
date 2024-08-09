package com.migrate.apim.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    public Path discoverMainRamlFile(Path repoPath) throws IOException {
        List<Path> ramlFiles = Files.walk(repoPath)
                .filter(path -> path.toString().endsWith(".raml"))
                .filter(this::isInApiProjectDirectory)
                .filter(path -> {
                    String pathString = path.toString().toLowerCase();
                    return !pathString.contains("documentation") &&
                            !pathString.contains("examples") &&
                            !pathString.contains("exchange_modules") &&
                            !pathString.contains("libraries") &&
                            !pathString.contains("types");
                })
                .collect(Collectors.toList());

        logger.info("Found {} RAML files after filtering", ramlFiles.size());

        if (ramlFiles.isEmpty()) {
            throw new IOException("No suitable RAML files found in the repository");
        }

        // Try to find the main RAML file using the specific pattern
        Path mainRamlFile = findMainRamlFile(ramlFiles);

        if (mainRamlFile == null) {
            logger.warn("Could not identify a main RAML file. Using the first RAML file found.");
            mainRamlFile = ramlFiles.get(0);
        }

        logger.info("Main RAML file identified: {}", mainRamlFile);

        return mainRamlFile;
    }

    private boolean isInApiProjectDirectory(Path path) {
        return path.toString().matches(".*(/|\\\\)api(/|\\\\)[^/\\\\]+(/|\\\\)[^/\\\\]+\\.raml");
    }

    private Path findMainRamlFile(List<Path> ramlFiles) {
        // First, try to find a file that matches the pattern: projectName-api.raml
        for (Path path : ramlFiles) {
            String filename = path.getFileName().toString().toLowerCase();
            if (filename.endsWith("-api.raml")) {
                return path;
            }
        }

        // If not found, look for any file with 'api' in its name
        for (Path path : ramlFiles) {
            String filename = path.getFileName().toString().toLowerCase();
            if (filename.contains("api")) {
                return path;
            }
        }

        // If still not found, return the first file
        return ramlFiles.isEmpty() ? null : ramlFiles.get(0);
    }
}