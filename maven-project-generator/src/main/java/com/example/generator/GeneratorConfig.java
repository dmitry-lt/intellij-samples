package com.example.generator;

import java.nio.file.Path;

public class GeneratorConfig {
    public final int n;           // number of service modules
    public final int m;           // classes per module
    public final int k;           // methods per class
    public final Path outputDir;
    public final String groupId;
    public final String artifactId;
    public final String version;

    public GeneratorConfig(int n, int m, int k, Path outputDir, String groupId, String artifactId, String version) {
        this.n = n;
        this.m = m;
        this.k = k;
        this.outputDir = outputDir;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
}
