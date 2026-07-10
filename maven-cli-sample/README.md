# Java Maven CLI Sample

A deliberately small but representative Java 21 command-line project. It demonstrates a Maven reactor, an executable JAR, Maven Wrapper launchers, ordinary source code, unit tests, integration tests, build-time source generation, and a real annotation processor.

## Build and run

Requirements: a JDK 21 or newer. A global Maven installation is not required; the wrapper downloads Maven 3.9.16 on first use.

```bash
./mvnw clean verify
java -jar cli-app/target/cli-app-1.0.0-SNAPSHOT.jar Ada
```

Expected output:

```text
Hello, Ada!
```

On Windows, use `mvnw.cmd clean verify`.

Other CLI commands:

```bash
java -jar cli-app/target/cli-app-1.0.0-SNAPSHOT.jar --help
java -jar cli-app/target/cli-app-1.0.0-SNAPSHOT.jar --version
```

## Modules

```text
greeting-annotations/  Source-retention @GenerateGreeting annotation
greeting-processor/    javax.annotation.processing implementation and service registration
cli-app/                CLI sources, generated code template, unit tests, and integration tests
```

The application depends on the annotation API and processor only at compile time, so the packaged CLI has no third-party runtime dependencies.

## What happens during the Maven lifecycle

1. `generate-sources`: `maven-resources-plugin` filters `src/main/codegen/.../BuildInfo.java` into `target/generated-sources/build-info`. `build-helper-maven-plugin` adds that directory as a Java source root.
2. `compile`: `maven-compiler-plugin` loads `greeting-processor` from its explicit annotation-processor path. `@GenerateGreeting` on `GreetingDefinition` produces `GreetingDefinitionGreeting.java` under `target/generated-sources/annotations`.
3. `test`: Surefire runs `*Test` unit tests from `src/test/java`.
4. `package`: `maven-jar-plugin` writes an executable JAR with `dev.example.maven.cli.Main` as its entry point.
5. `integration-test` and `verify`: Failsafe runs `*IT` tests from `src/it/java`. `CliIT` starts the packaged JAR in a separate JVM and verifies its exit code and output.

The generated files are intentionally kept under `target/`; they should not be committed.

## Useful focused commands

```bash
# Unit tests and compilation only
./mvnw test

# Full reactor, including the packaged-JAR integration test
./mvnw verify

# Inspect both forms of generated source after compilation
find cli-app/target/generated-sources -type f -name '*.java' -print
```

## Layout

```text
.
├── .mvn/wrapper/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── greeting-annotations/
├── greeting-processor/
└── cli-app/
    └── src/
        ├── main/java/       Handwritten application sources
        ├── main/codegen/    Filtered Java source template
        ├── test/java/       Surefire unit tests
        └── it/java/         Failsafe integration tests
```
