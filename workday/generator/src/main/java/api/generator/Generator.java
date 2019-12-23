package api.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import api.Analyzer;
import api.SwaggerLoader;
import api.services.Services;
import api.types.Classes;
import api.types.ModuleType;
import api.types.Property;
import api.types.ServiceClassType;
import io.swagger.models.Swagger;

public class Generator {

    private final File repInput;

    private final File repOutput;

    private final File propFile;

    public Generator(File repInput, File repOutput) {
        this.repInput = repInput;
        this.repOutput = repOutput;
        propFile = new File(repOutput, "Messages.properties");
        if (propFile.exists()) {
            propFile.delete();
        }
        try {
            propFile.createNewFile();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void generate() throws IOException {
        File moduleChoiceClass = new File(repOutput, "ModuleChoice.java");

        try (PrintWriter moduleWriter = new PrintWriter(new FileOutputStream(moduleChoiceClass))) {
            final List<ModuleType> modules = Files.walk(repInput.toPath(), 1)
                    .filter(p -> p.toFile().isFile() && p.toFile().getName().endsWith(".json"))
                    .map(this::extractAnalyser)
                    .filter(Objects::nonNull)
                    .peek(this::writeProperties)
                    .map(this::generateFile)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            header(moduleWriter, modules);
            addEnum(moduleWriter, modules);
            modules.forEach(m -> this.addField(moduleWriter, m, "ModuleChoice"));
            this.addSearchQueryHelperMethod(moduleWriter, modules);
            moduleWriter.println("}");
        }
    }


    private void header(PrintWriter w, List<ModuleType> module) {
        GeneratorHelper.fileHeader(w);
        w.println("");
        w.println("");

        w.println("@Data");
        Stream<String> modulesInstance = Stream.concat(Stream.of("module"),
                module.stream().map(ModuleType::getClassName).map(this::instanceName));
        final String layout = GeneratorHelper.buildLayout(modulesInstance);
        w.println(layout);
        w.println("    @Documentation(\"module\")");
        GeneratorHelper.startClass(w, "", "public class ModuleChoice implements Serializable, QueryHelper.QueryHelperProvider", 1);
    }
    
    private void addSearchQueryHelperMethod(PrintWriter w, List<ModuleType> modules) {
        w.println("    @Override");
        w.println("    public QueryHelper getQueryHelper() {");
        
        // condition for each module.
        modules.stream()
            .forEach((ModuleType module) -> this.conditionQueryHelper(w, module)); 
        
        w.println("        return null;");
        w.println("    }");
    }
    
    private void conditionQueryHelper(PrintWriter w, ModuleType module) {
        String instanceName = instanceName(module.getClassName());
        w.println("        if (this.module == Modules." + module.getClassName()+ " ) {");
        w.println("            if (this." + instanceName + " != null) {");
        w.println("                return this." + instanceName + ".getQueryHelper();");
        w.println("            }");
        w.println("        }");
    }

    private void addEnum(PrintWriter w, List<ModuleType> module) {
        final String enumDef = module.stream()
                .map(ModuleType::getClassName)
                .collect(Collectors.joining(", ", "public enum Modules { ", "}"));

        module.stream()
                .sorted( Comparator.comparing(ModuleType::getDescription))
                .map(m -> new Property("Modules." + m.getClassName(), m.getDescription()))
                .forEach(p -> p.write(propFile));

        w.print("    ");
        w.println(enumDef);

        w.println("");
        w.println("    @Option");
        w.println("    @Documentation(\"selected module\")");
        w.println("    private Modules module;");
        w.println("");

        Property prop = new Property("ModuleChoice.module", "module");
        prop.write(this.propFile);
    }


    private void addField(PrintWriter w, ModuleType module, String ownerClass) {
        String instanceName = instanceName(module.getClassName());
        w.println("");
        w.println("    @Option");
        w.println("    @ActiveIf(target = \"module\", value = \"" + module.getClassName() +"\")");
        w.println("    @Documentation(\"module " + module.getClassName() + "\")");
        w.println("    private " + module.getClassName() + "   " + instanceName + " = new " + module.getClassName() + "();");
        w.println("");

        Property prop = new Property(ownerClass+ "." + instanceName, module.getDescription());
        prop.write(this.propFile);
    }

    private String instanceName(String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    private Analyzer extractAnalyser(Path inputFile) {
        final SwaggerLoader loader = new SwaggerLoader(inputFile.toFile().getPath());
        final Swagger swagger = loader.getSwagger();
        if (swagger == null) {
            return null;
        }

        return new Analyzer(swagger, inputFile.toFile().getName());
    }

    private void writeProperties(Analyzer analyzer) {
        final List<Property> props = extractProperties(analyzer);
        Property.write(this.propFile, props);
    }

    private List<Property> extractProperties(Analyzer analyzer) {
        if (analyzer.getClasses() == null) {
            return Collections.emptyList();
        }
        return analyzer.getClasses().stream()
                .filter(ServiceClassType.class::isInstance)
                .map(ServiceClassType.class::cast)
                .flatMap(Property::from)
                .collect(Collectors.toList());
    }

    private ModuleType generateFile(Analyzer analyzer)  {
        String className = buildClassName(analyzer.getFileName());
        File f = new File(this.repOutput, className + ".java");
        if (f.exists()) {
            f.delete();
        }
        try {
            
            Classes cl = analyzer.getClasses();
            Services services = analyzer.getServices();
            
            if (!services.isEmpty()) {
                f.createNewFile();

                FileGenerator gen = new FileGenerator(cl, services, className);
                gen.generate(analyzer.getDescription(), f, this.propFile);
                return new ModuleType(className, analyzer.getDescription());
            }
            return null;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String buildClassName(String name) {
        StringBuilder builder =  new StringBuilder(name);
        GeneratorHelper.toMaj(builder, 0);

        final int posType = builder.lastIndexOf(".json");
        builder.delete(posType, posType + ".json".length());
        int pos = builder.length() - 1;
        while (pos >= 0) {
            if (builder.charAt(pos) == '-') {
                builder.delete(pos, pos + 1);
                if (pos < builder.length()) {
                    GeneratorHelper.toMaj(builder, pos);
                }
            }
            pos--;
        }
        return builder.toString();
    }


}
