package com.example.generator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point for the Maven project generator.
 *
 * Usage:
 *   java -jar maven-project-generator.jar [options]
 *
 * Options:
 *   --services <int>   Number of service modules (default: 10)
 *   --classes <int>    Classes per module (default: 10)
 *   --methods <int>    Methods per class (default: 10)
 *   --output <path>    Output directory (default: ./generated-project)
 *   --groupId <str>    Generated project groupId (default: com.example.generated)
 *   --artifactId <str> Generated project artifactId (default: generated-project)
 *   --version <str>    Generated project version (default: 1.0-SNAPSHOT)
 */
public class Main {

    public static void main(String[] args) throws IOException {
        int serviceCount = 10;
        int classesPerModule = 10;
        int methodsPerClass = 10;
        String output = "./generated-project";
        String groupId = "com.example.generated";
        String artifactId = "generated-project";
        String version = "1.0-SNAPSHOT";

        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "--services":   serviceCount    = Integer.parseInt(args[++i]); break;
                case "--classes":    classesPerModule = Integer.parseInt(args[++i]); break;
                case "--methods":    methodsPerClass  = Integer.parseInt(args[++i]); break;
                case "--output":     output           = args[++i]; break;
                case "--groupId":    groupId          = args[++i]; break;
                case "--artifactId": artifactId       = args[++i]; break;
                case "--version":    version          = args[++i]; break;
                default:
                    System.err.println("Unknown option: " + args[i]);
                    printUsage();
                    System.exit(1);
            }
        }

        Path outputPath = Paths.get(output);
        GeneratorConfig config = new GeneratorConfig(
                serviceCount, classesPerModule, methodsPerClass,
                outputPath, groupId, artifactId, version);
        new ProjectGenerator(config).generate();
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar maven-project-generator.jar [--services 10] [--classes 10] [--methods 10]");
        System.err.println("       [--output ./generated-project] [--groupId com.example.generated]");
        System.err.println("       [--artifactId generated-project] [--version 1.0-SNAPSHOT]");
    }
}
