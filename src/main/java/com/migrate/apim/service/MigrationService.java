package com.migrate.apim.service;

import com.migrate.apim.configuration.ApimConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MigrationService {

    @Autowired
    private ApimConfig apimConfig;

    public void writeOpenApiToFileAndPushToGit(String openApiContent, String projectName) {
        // Define paths for git repo
        Path gitRepoDirectory = Paths.get(apimConfig.getRepoPath(), projectName);
        Path gitRepoFilePath = gitRepoDirectory.resolve(apimConfig.getFileName());

        try {
            // Ensure the git repo directory exists
            Files.createDirectories(gitRepoDirectory);

            // Write OpenAPI content to file
            Files.write(gitRepoFilePath, openApiContent.getBytes());
            System.out.println("OpenAPI content written to git repo file: " + gitRepoFilePath);

            // Open Git repository
            try (Git git = Git.open(new File(apimConfig.getRepoPath()))) {
                // Add, commit, and push changes
                git.add().addFilepattern(projectName + "/" + apimConfig.getFileName()).call();
                git.commit().setMessage("Add/Update OpenAPI spec for " + projectName).call();
                git.push()
                        .setRemote(apimConfig.getRemoteUrl())
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(apimConfig.getUsername(), apimConfig.getPassword()))
                        .call();

                System.out.println("Changes pushed to remote repository for project: " + projectName);
            }

        } catch (IOException | GitAPIException e) {
            System.err.println("Error during file write or Git operations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}