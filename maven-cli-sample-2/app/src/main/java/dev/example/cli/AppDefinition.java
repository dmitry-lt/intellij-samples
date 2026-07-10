package dev.example.cli;

import dev.example.cli.annotation.CliApp;

/** The annotation processor generates {@code AppDefinitionMeta} from this marker class. */
@CliApp(description = "Greets people by name.")
final class AppDefinition {
    private AppDefinition() {
    }
}
