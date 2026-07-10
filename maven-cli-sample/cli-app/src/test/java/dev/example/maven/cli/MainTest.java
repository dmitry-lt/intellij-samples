package dev.example.maven.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void greetsANameUsingTheProcessorGeneratedClass() {
        Invocation invocation = invoke("Ada");

        assertAll(
                () -> assertEquals(0, invocation.exitCode()),
                () -> assertEquals("Hello, Ada!" + System.lineSeparator(), invocation.stdout()),
                () -> assertEquals("", invocation.stderr()));
    }

    @Test
    void printsGeneratedBuildMetadata() {
        Invocation invocation = invoke("--version");

        assertAll(
                () -> assertEquals(0, invocation.exitCode()),
                () -> assertTrue(invocation.stdout().contains("maven-cli-sample 1.0.0-SNAPSHOT")),
                () -> assertTrue(invocation.stdout().contains("Java 21")),
                () -> assertEquals("", invocation.stderr()));
    }

    @Test
    void rejectsMissingArgumentsWithAUsageError() {
        Invocation invocation = invoke();

        assertAll(
                () -> assertEquals(2, invocation.exitCode()),
                () -> assertEquals("", invocation.stdout()),
                () -> assertTrue(invocation.stderr().contains("Expected exactly one name.")),
                () -> assertTrue(invocation.stderr().contains("Usage: maven-cli-sample")));
    }

    @Test
    void printsHelpWithoutAnError() {
        Invocation invocation = invoke("--help");

        assertAll(
                () -> assertEquals(0, invocation.exitCode()),
                () -> assertTrue(invocation.stdout().contains("Usage: maven-cli-sample")),
                () -> assertEquals("", invocation.stderr()));
    }

    private static Invocation invoke(String... arguments) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        int exitCode;
        try (PrintStream out = new PrintStream(stdout, true, StandardCharsets.UTF_8);
                PrintStream err = new PrintStream(stderr, true, StandardCharsets.UTF_8)) {
            exitCode = Main.run(arguments, out, err);
        }
        return new Invocation(
                exitCode,
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8));
    }

    private record Invocation(int exitCode, String stdout, String stderr) {
    }
}
