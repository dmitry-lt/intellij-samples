package dev.example.maven.cli;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CliIT {
    @Test
    void packagedJarRunsInASeparateJvm() throws IOException, InterruptedException {
        Path jar = Path.of(System.getProperty("app.jar"));
        assertTrue(Files.isRegularFile(jar), () -> "Packaged JAR is missing: " + jar);

        Path java = Path.of(
                System.getProperty("java.home"),
                "bin",
                System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java");

        Process process = new ProcessBuilder(java.toString(), "-jar", jar.toString(), "Maven")
                .redirectErrorStream(true)
                .start();

        assertTrue(process.waitFor(10, SECONDS), "CLI process did not finish within ten seconds");
        String output = new String(process.getInputStream().readAllBytes(), UTF_8);

        assertEquals(0, process.exitValue());
        assertEquals("Hello, Maven!" + System.lineSeparator(), output);
    }
}
