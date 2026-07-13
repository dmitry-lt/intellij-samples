package com.example.generator;

import java.nio.file.Path;

public class GeneratorConfig {
    public final int serviceCount;
    public final int classesPerModule;
    public final int methodsPerClass;
    public final Path outputDir;
    public final String groupId;
    public final String artifactId;
    public final String version;

    public GeneratorConfig(int serviceCount, int classesPerModule, int methodsPerClass,
                           Path outputDir, String groupId, String artifactId, String version) {
        this.serviceCount = serviceCount;
        this.classesPerModule = classesPerModule;
        this.methodsPerClass = methodsPerClass;
        this.outputDir = outputDir;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
}
