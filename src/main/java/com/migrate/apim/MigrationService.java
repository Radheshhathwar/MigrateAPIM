package com.migrate.apim;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MigrationService {

    public void writeOpenApiToFileAndPushToGit(String openApiContent) {
        // Define file and repository paths
        String fileName = "openapi.json";
        String repoPath = "/home/radhesh/Migrate/MuleSoftAPIM_AzureAPIM";
        Path filePath = Paths.get(repoPath, fileName);
        String remoteUrl = "https://IvolveAzureDemo@dev.azure.com/IvolveAzureDemo/Ivolve-Demo-Azure/_git/MuleSoftAPIM_AzureAPIM";
        String username = "";
        String password = "";  // Use PAT

        try {
            // Ensure the directory exists
            Files.createDirectories(filePath.getParent());

            // Write OpenAPI content to file
            Files.write(filePath, openApiContent.getBytes());
            System.out.println("OpenAPI content written to file: " + filePath);

            // Open Git repository
            try (Git git = Git.open(new File(repoPath))) {
                // Add, commit, and push changes
                git.add().addFilepattern(fileName).call();
                git.commit().setMessage("Add OpenAPI spec").call();
                git.push()
                        .setRemote(remoteUrl)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                        .call();

                System.out.println("Changes pushed to remote repository");
            }

        } catch (IOException | GitAPIException e) {
            System.err.println("Error during file write or Git operations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}