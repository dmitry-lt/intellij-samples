package dev.example.cli;

import dev.example.cli.proto.GreetProtos.GreetRequest;
import java.io.PrintStream;
import java.util.Objects;
import java.util.jar.Manifest;
import java.net.URL;

public final class Main {

    static final String APP_NAME = "maven-cli-sample-2";

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
            out.printf("%s %s%n", APP_NAME, version());
            return 0;
        }

        if (args.length > 1) {
            err.println("Too many arguments.");
            printUsage(err);
            return 2;
        }

        if (args.length == 1 && args[0].startsWith("-")) {
            err.println("Unknown option: " + args[0]);
            printUsage(err);
            return 2;
        }

        String name = args.length == 0 ? "World" : args[0].strip();
        if (name.isEmpty()) {
            err.println("Name must not be blank.");
            printUsage(err);
            return 2;
        }

        GreetRequest request = GreetRequest.newBuilder().setName(name).build();
        out.printf("Hello, %s!%n", request.getName());
        return 0;
    }

    private static String version() {
        try {
            URL url = Main.class.getClassLoader()
                    .getResource("META-INF/MANIFEST.MF");
            if (url != null) {
                try (var stream = url.openStream()) {
                    Manifest manifest = new Manifest(stream);
                    String v = manifest.getMainAttributes().getValue("Implementation-Version");
                    if (v != null) return v;
                }
            }
        } catch (Exception ignored) {
        }
        return "dev";
    }

    private static void printUsage(PrintStream stream) {
        stream.printf("Usage: %s [--help | --version | [<name>]]%n", APP_NAME);
        stream.println("  " + AppDefinitionMeta.DESCRIPTION);
    }
}
