# Maven CLI Sample 2

A multi-module Maven CLI project demonstrating **protobuf source generation**, **annotation processing**, and **build profiles**.

## Modules

| Module | Description |
|---|---|
| `cli-processor` | `@CliApp` annotation + compile-time annotation processor |
| `app` | CLI application — depends on generated protobuf and annotation-processor output |

## Running

```bash
./mvnw compile exec:java
```

Pass a name:

```bash
./mvnw compile exec:java -Dexec.args="Alice"
```

## Profiles

### `generate-build-info` (optional)

Adds an extra `generate-sources` step that filters `src/main/codegen/…/BuildInfo.java` with Maven
properties (`@app.name@`, `@project.version@`) and compiles the result into the JAR.

Run with the profile:

```bash
./mvnw clean compile exec:java -P generate-build-info
```

Run without the profile (default — `BuildInfo` class is not generated):

```bash
./mvnw compile exec:java
```

## Other commands

```bash
# Run tests
./mvnw test

# Full build
./mvnw verify
```
