package dev.example.cli.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as the CLI application entry point.
 * The annotation processor generates a companion {@code <TypeName>Meta} class
 * containing the metadata declared here.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CliApp {
    String description() default "";
}
