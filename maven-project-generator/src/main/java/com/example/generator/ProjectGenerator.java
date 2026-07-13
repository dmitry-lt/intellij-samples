package com.example.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProjectGenerator {

    private final GeneratorConfig config;

    public ProjectGenerator(GeneratorConfig config) {
        this.config = config;
    }

    public void generate() throws IOException {
        Path root = config.outputDir;
        Files.createDirectories(root);

        List<String> modules = new ArrayList<>();
        modules.add("core");
        for (int i = 1; i <= config.n; i++) {
            modules.add("service-" + i);
        }

        writeParentPom(root, modules);
        generateCoreModule(root);
        for (int i = 1; i <= config.n; i++) {
            generateServiceModule(root, i);
        }

        System.out.println("Generated project at: " + root.toAbsolutePath());
        System.out.println("  modules : 1 core + " + config.n + " services");
        System.out.println("  classes : " + config.m + " per module");
        System.out.println("  methods : " + config.k + " per class");
    }

    // -------------------------------------------------------------------------
    // Parent POM
    // -------------------------------------------------------------------------

    private void writeParentPom(Path root, List<String> modules) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n");
        sb.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        sb.append("    <modelVersion>4.0.0</modelVersion>\n\n");
        sb.append("    <groupId>").append(config.groupId).append("</groupId>\n");
        sb.append("    <artifactId>").append(config.artifactId).append("</artifactId>\n");
        sb.append("    <version>").append(config.version).append("</version>\n");
        sb.append("    <packaging>pom</packaging>\n\n");
        sb.append("    <properties>\n");
        sb.append("        <maven.compiler.source>11</maven.compiler.source>\n");
        sb.append("        <maven.compiler.target>11</maven.compiler.target>\n");
        sb.append("        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n");
        sb.append("    </properties>\n\n");
        sb.append("    <modules>\n");
        for (String module : modules) {
            sb.append("        <module>").append(module).append("</module>\n");
        }
        sb.append("    </modules>\n");
        sb.append("</project>\n");

        Files.writeString(root.resolve("pom.xml"), sb.toString());
    }

    // -------------------------------------------------------------------------
    // Core module
    // -------------------------------------------------------------------------

    private void generateCoreModule(Path root) throws IOException {
        Path moduleDir = root.resolve("core");
        String packageName = config.groupId + ".core";
        Path srcDir = moduleDir.resolve("src/main/java/" + packageName.replace('.', '/'));
        Files.createDirectories(srcDir);

        writeModulePom(moduleDir, "core", false);

        for (int i = 1; i <= config.m; i++) {
            writeCoreClass(srcDir, packageName, i);
        }
    }

    // -------------------------------------------------------------------------
    // Service modules
    // -------------------------------------------------------------------------

    private void generateServiceModule(Path root, int serviceIndex) throws IOException {
        String moduleName = "service-" + serviceIndex;
        Path moduleDir = root.resolve(moduleName);
        String packageName = config.groupId + ".service" + serviceIndex;
        Path srcDir = moduleDir.resolve("src/main/java/" + packageName.replace('.', '/'));
        Files.createDirectories(srcDir);

        writeModulePom(moduleDir, moduleName, true);

        String corePackage = config.groupId + ".core";
        for (int i = 1; i <= config.m; i++) {
            writeServiceClass(srcDir, packageName, serviceIndex, i, corePackage);
        }
    }

    // -------------------------------------------------------------------------
    // Module POM
    // -------------------------------------------------------------------------

    private void writeModulePom(Path moduleDir, String artifactId, boolean dependsOnCore) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n");
        sb.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        sb.append("    <modelVersion>4.0.0</modelVersion>\n\n");
        sb.append("    <parent>\n");
        sb.append("        <groupId>").append(config.groupId).append("</groupId>\n");
        sb.append("        <artifactId>").append(config.artifactId).append("</artifactId>\n");
        sb.append("        <version>").append(config.version).append("</version>\n");
        sb.append("    </parent>\n\n");
        sb.append("    <artifactId>").append(artifactId).append("</artifactId>\n");

        if (dependsOnCore) {
            sb.append("\n    <dependencies>\n");
            sb.append("        <dependency>\n");
            sb.append("            <groupId>").append(config.groupId).append("</groupId>\n");
            sb.append("            <artifactId>core</artifactId>\n");
            sb.append("            <version>").append(config.version).append("</version>\n");
            sb.append("        </dependency>\n");
            sb.append("    </dependencies>\n");
        }

        sb.append("</project>\n");

        Files.writeString(moduleDir.resolve("pom.xml"), sb.toString());
    }

    // -------------------------------------------------------------------------
    // Java classes
    // -------------------------------------------------------------------------

    private void writeCoreClass(Path srcDir, String packageName, int classIndex) throws IOException {
        String className = "CoreClass" + classIndex;
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("public class ").append(className).append(" {\n\n");

        for (int j = 1; j <= config.k; j++) {
            sb.append("    public String method").append(j).append("() {\n");
            sb.append("        return \"").append(className).append(".method").append(j).append("\";\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        Files.writeString(srcDir.resolve(className + ".java"), sb.toString());
    }

    private void writeServiceClass(Path srcDir, String packageName, int serviceIndex,
                                   int classIndex, String corePackage) throws IOException {
        String className = "Service" + serviceIndex + "Class" + classIndex;
        String coreClassName = "CoreClass" + classIndex;
        String fieldName = "core" + classIndex;

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import ").append(corePackage).append(".").append(coreClassName).append(";\n\n");
        sb.append("public class ").append(className).append(" {\n\n");
        sb.append("    private final ").append(coreClassName).append(" ").append(fieldName)
          .append(" = new ").append(coreClassName).append("();\n\n");

        for (int j = 1; j <= config.k; j++) {
            sb.append("    public String method").append(j).append("() {\n");
            sb.append("        return ").append(fieldName).append(".method").append(j).append("();\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        Files.writeString(srcDir.resolve(className + ".java"), sb.toString());
    }
}
