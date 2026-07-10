package dev.example.maven.cli;

import dev.example.maven.cli.generated.BuildInfo;
import java.io.PrintStream;
import java.util.Objects;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        Objects.requireNonNull(args, "args");
        Objects.requireNonNull(out, "out");
        Objects.requireNonNull(err, "err");

        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printUsage(out);
            return 0;
        }
        if (args.length == 1 && ("--version".equals(args[0]) || "-V".equals(args[0]))) {
            out.printf("%s %s (Java %d)%n", BuildInfo.NAME, BuildInfo.VERSION, BuildInfo.JAVA_RELEASE);
            return 0;
        }
        if (args.length != 1) {
            err.println("Expected exactly one name.");
            printUsage(err);
            return 2;
        }
        if (args[0].startsWith("-")) {
            err.println("Unknown option: " + args[0]);
            printUsage(err);
            return 2;
        }

        String name = args[0].strip();
        if (name.isEmpty()) {
            err.println("Name must not be blank.");
            printUsage(err);
            return 2;
        }

        out.println(GreetingDefinitionGreeting.message(name));
        return 0;
    }

    private static void printUsage(PrintStream stream) {
        stream.printf("Usage: %s [--help | --version | <name>]%n", BuildInfo.NAME);
        stream.println("Greets the supplied name.");
    }
}
