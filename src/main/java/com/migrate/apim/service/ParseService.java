package com.migrate.apim.service;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ParseService {

    private static final Logger logger = LoggerFactory.getLogger(ParseService.class);

    public String parse(Path ramlFilePath) throws IOException {
        Api ramlApi = parseRamlFile(ramlFilePath);
        return convertRamlToOpenApi(ramlApi);
    }

    public Api parseRamlFile(Path ramlFilePath) throws IOException {
        // Read the RAML file content
        String ramlContent = new String(Files.readAllBytes(ramlFilePath));

        // Log the RAML content for debugging
        logger.debug("RAML Content: {}", ramlContent);

        try {
            // Parse the RAML content
            RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlContent, ramlFilePath.toString());

            if (ramlModelResult.hasErrors()) {
                for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                    logger.error("RAML Validation Error: {}", validationResult.getMessage());
                }
                throw new IllegalStateException("Failed to parse RAML file due to validation errors: " + ramlFilePath);
            }

            Api ramlApi = ramlModelResult.getApiV10();

            // Check if parsing was successful
            if (ramlApi == null) {
                throw new IllegalStateException("Failed to parse RAML file: " + ramlFilePath);
            }

            return ramlApi;
        } catch (Exception e) {
            logger.error("Error parsing RAML file: {}", ramlFilePath, e);
            throw new IOException("Error parsing RAML file: " + ramlFilePath, e);
        }
    }

    private String convertRamlToOpenApi(Api ramlApi) {
        StringBuilder openApi = new StringBuilder();
        openApi.append("{\n");
        openApi.append("  \"openapi\": \"3.0.0\",\n");
        openApi.append("  \"info\": {\n");
        openApi.append("    \"title\": \"").append(ramlApi.title()).append("\",\n");
        openApi.append("    \"version\": \"").append(ramlApi.version()).append("\"\n");
        openApi.append("  },\n");
        openApi.append("  \"servers\": [\n");
        openApi.append("    {\n");
        openApi.append("      \"url\": \"").append(ramlApi.baseUri() != null ? ramlApi.baseUri().value() : "").append("\"\n");
        openApi.append("    }\n");
        openApi.append("  ],\n");
        openApi.append("  \"paths\": {\n");

        List<Resource> resources = ramlApi.resources();
        for (Resource resource : resources) {
            openApi.append("    \"").append(resource.resourcePath()).append("\": {\n");
            List<Method> methods = resource.methods();
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                openApi.append("      \"").append(method.method()).append("\": {\n");
                openApi.append("        \"description\": \"").append(method.description() != null ? method.description().value() : "").append("\",\n");
                openApi.append("        \"responses\": {\n");
                List<Response> responses = method.responses();
                for (int j = 0; j < responses.size(); j++) {
                    Response response = responses.get(j);
                    openApi.append("          \"").append(response.code().value()).append("\": {\n");
                    String description = response.description() != null && response.description().value() != null
                            ? response.description().value()
                            : "No description provided";
                    openApi.append("            \"description\": \"").append(description).append("\"\n");
                    openApi.append("          }");
                    if (j < responses.size() - 1) {
                        openApi.append(",");
                    }
                    openApi.append("\n");
                }
                openApi.append("        }\n");
                openApi.append("      }");
                if (i < methods.size() - 1) {
                    openApi.append(",");
                }
                openApi.append("\n");
            }
            openApi.append("    },");
            openApi.append("\n");
        }

        // Remove the trailing comma and newline from the last resource if there are any paths
        if (!resources.isEmpty()) {
            openApi.setLength(openApi.length() - 2); // removes the comma and newline
            openApi.append("\n");
        }

        openApi.append("  }\n");
        openApi.append("}\n");

        return openApi.toString();
    }
}
