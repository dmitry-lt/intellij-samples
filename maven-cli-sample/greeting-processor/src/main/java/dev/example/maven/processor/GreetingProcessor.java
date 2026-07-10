package dev.example.maven.processor;

import dev.example.maven.annotation.GenerateGreeting;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("dev.example.maven.annotation.GenerateGreeting")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public final class GreetingProcessor extends AbstractProcessor {
    private final Set<String> generatedTypes = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(GenerateGreeting.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@GenerateGreeting can only be applied to classes",
                        element);
                continue;
            }

            TypeElement sourceType = (TypeElement) element;
            PackageElement sourcePackage = processingEnv.getElementUtils().getPackageOf(sourceType);
            String packageName = sourcePackage.isUnnamed()
                    ? ""
                    : sourcePackage.getQualifiedName().toString();
            String generatedSimpleName = sourceType.getSimpleName() + "Greeting";
            String generatedQualifiedName = packageName.isEmpty()
                    ? generatedSimpleName
                    : packageName + "." + generatedSimpleName;

            if (!generatedTypes.add(generatedQualifiedName)) {
                continue;
            }

            GenerateGreeting annotation = sourceType.getAnnotation(GenerateGreeting.class);
            writeGreetingType(
                    sourceType,
                    packageName,
                    generatedSimpleName,
                    generatedQualifiedName,
                    annotation.prefix());
        }
        return true;
    }

    private void writeGreetingType(
            TypeElement sourceType,
            String packageName,
            String generatedSimpleName,
            String generatedQualifiedName,
            String prefix) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler()
                    .createSourceFile(generatedQualifiedName, sourceType);
            try (Writer writer = sourceFile.openWriter()) {
                if (!packageName.isEmpty()) {
                    writer.write("package " + packageName + ";\n\n");
                }
                writer.write("import javax.annotation.processing.Generated;\n\n");
                writer.write("@Generated(\"" + GreetingProcessor.class.getName() + "\")\n");
                writer.write("public final class " + generatedSimpleName + " {\n");
                writer.write("    private " + generatedSimpleName + "() {\n");
                writer.write("    }\n\n");
                writer.write("    public static String message(String name) {\n");
                writer.write("        String normalized = java.util.Objects.requireNonNull(name, \"name\").strip();\n");
                writer.write("        if (normalized.isEmpty()) {\n");
                writer.write("            throw new IllegalArgumentException(\"name must not be blank\");\n");
                writer.write("        }\n");
                writer.write("        return " + javaStringLiteral(prefix + ", ") + " + normalized + \"!\";\n");
                writer.write("    }\n");
                writer.write("}\n");
            }
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Could not generate " + generatedQualifiedName + ": " + exception.getMessage(),
                    sourceType);
        }
    }

    private static String javaStringLiteral(String value) {
        StringBuilder literal = new StringBuilder(value.length() + 2).append('"');
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\\' -> literal.append("\\\\");
                case '"' -> literal.append("\\\"");
                case '\n' -> literal.append("\\n");
                case '\r' -> literal.append("\\r");
                case '\t' -> literal.append("\\t");
                default -> {
                    if (Character.isISOControl(character)) {
                        literal.append(String.format("\\u%04x", (int) character));
                    } else {
                        literal.append(character);
                    }
                }
            }
        }
        return literal.append('"').toString();
    }
}
