package dev.example.maven.cli;

import dev.example.maven.annotation.GenerateGreeting;

/** The annotation processor generates GreetingDefinitionGreeting from this marker class. */
@GenerateGreeting(prefix = "Hello")
final class GreetingDefinition {
    private GreetingDefinition() {
    }
}
