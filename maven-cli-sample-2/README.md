# Maven CLI Sample 2

A multi-module Maven CLI project demonstrating **protobuf source generation** and **annotation processing**.

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

## Other commands

### Run tests

```bash
./mvnw test
```

### Clean

```bash
./mvnw clean
```
