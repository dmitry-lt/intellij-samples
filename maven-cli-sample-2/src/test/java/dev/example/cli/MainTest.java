package dev.example.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void greetsName() {
        var result = invoke("Alice");

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals("Hello, Alice!" + System.lineSeparator(), result.stdout()),
                () -> assertEquals("", result.stderr()));
    }

    @Test
    void printsHelpWithoutError() {
        var result = invoke("--help");

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertTrue(result.stdout().contains("Usage:")),
                () -> assertEquals("", result.stderr()));
    }

    @Test
    void printsVersion() {
        var result = invoke("--version");

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertTrue(result.stdout().contains(Main.APP_NAME)),
                () -> assertEquals("", result.stderr()));
    }

    @Test
    void greetsWorldWhenNoArgs() {
        var result = invoke();

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals("Hello, World!" + System.lineSeparator(), result.stdout()),
                () -> assertEquals("", result.stderr()));
    }

    @Test
    void failsOnUnknownOption() {
        var result = invoke("--unknown");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.stderr().contains("Unknown option")));
    }

    @Test
    void failsOnTooManyArgs() {
        var result = invoke("Alice", "Bob");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.stderr().contains("Too many")));
    }

    private static Result invoke(String... args) {
        var outBytes = new ByteArrayOutputStream();
        var errBytes = new ByteArrayOutputStream();
        int exitCode;
        try (var out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
             var err = new PrintStream(errBytes, true, StandardCharsets.UTF_8)) {
            exitCode = Main.run(args, out, err);
        }
        return new Result(
                exitCode,
                outBytes.toString(StandardCharsets.UTF_8),
                errBytes.toString(StandardCharsets.UTF_8));
    }

    private record Result(int exitCode, String stdout, String stderr) {
    }
}
