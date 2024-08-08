# MuleSoft to Azure API Management Migration

## Overview
This application migrates MuleSoft API Management to Azure API Management in three phases:
1. Discovery: Finds the main RAML file in the MuleSoft repository.
2. Parsing: Parses the RAML file into a common DSL format.
3. Migration: Migrates the DSL content to Azure API Management.

## Setup
1. Configure the application by updating `src/main/resources/application.yml` with your GitHub and Azure details.
2. Build and run the application using Maven:
   ```sh
   mvn spring-boot:run
