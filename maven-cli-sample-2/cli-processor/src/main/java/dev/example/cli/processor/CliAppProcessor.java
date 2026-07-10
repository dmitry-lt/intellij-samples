package dev.example.cli.processor;

import dev.example.cli.annotation.CliApp;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
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

@SupportedAnnotationTypes("dev.example.cli.annotation.CliApp")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public final class CliAppProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(CliApp.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR, "@CliApp can only be applied to classes", element);
                continue;
            }
            TypeElement type = (TypeElement) element;
            PackageElement pkg = processingEnv.getElementUtils().getPackageOf(type);
            String packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();
            String simpleName = type.getSimpleName() + "Meta";
            String qualifiedName = packageName.isEmpty() ? simpleName : packageName + "." + simpleName;

            CliApp annotation = type.getAnnotation(CliApp.class);
            writeMetaClass(type, packageName, simpleName, qualifiedName, annotation.description());
        }
        return true;
    }

    private void writeMetaClass(
            TypeElement source,
            String packageName,
            String simpleName,
            String qualifiedName,
            String description) {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedName, source);
            try (Writer w = file.openWriter()) {
                if (!packageName.isEmpty()) {
                    w.write("package " + packageName + ";\n\n");
                }
                w.write("import javax.annotation.processing.Generated;\n\n");
                w.write("@Generated(\"" + CliAppProcessor.class.getName() + "\")\n");
                w.write("public final class " + simpleName + " {\n");
                w.write("    public static final String DESCRIPTION = " + literal(description) + ";\n\n");
                w.write("    private " + simpleName + "() {}\n");
                w.write("}\n");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Could not generate " + qualifiedName + ": " + e.getMessage(),
                    source);
        }
    }

    private static String literal(String value) {
        StringBuilder sb = new StringBuilder().append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.append('"').toString();
    }
}
