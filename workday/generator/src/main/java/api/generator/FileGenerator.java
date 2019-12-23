package api.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import api.services.Service;
import api.services.Services;
import api.types.ClassType;
import api.types.Classes;
import api.types.EnumType;
import api.types.FieldType;
import api.types.GeneralType;
import api.types.Property;

public class FileGenerator {

    private final Classes classes;

    private final Services services;

    /**  name of current generated class */
    private final String className;

    public FileGenerator(Classes classes, Services services, String className) {
        this.classes = classes;
        this.services = services;
        this.className = className;
    }

    public void generate(String doc,
                         File out,
                         File propsFile) throws IOException {

        FieldGenerator fg = new FieldGenerator();
        ClassGenerator cg = new ClassGenerator(fg);
        EnumGenerator  eg = new EnumGenerator();

        if (out.exists()) {
            out.delete();
        }
        out.createNewFile();

        long serialId = this.getSerialId();

        try (PrintWriter w = new PrintWriter(new FileOutputStream(out))) {

            StringWriter servicesOut = new StringWriter();
            PrintWriter servicesPrinter = new PrintWriter(servicesOut);
            
            StringWriter getQueryHelperContent = new StringWriter();
            PrintWriter queryHelperPrinter = new PrintWriter(getQueryHelperContent);
            
            List<String> layouts = new ArrayList<>();
            layouts.add("@GridLayout.Row(\"service\")");
            services.get()
                    .peek(s -> this.gridLayout(layouts, s))
                    .forEach( s -> this.serviceParamCode(servicesPrinter, propsFile, queryHelperPrinter, s));

            GeneratorHelper.fileHeader(w);
            w.println("@Data");

            String layoutsString = layouts.stream().collect(Collectors.joining(", ", "@GridLayout({", "})"));
            w.println(layoutsString);

            w.print("@Documentation(\"");
            w.print(GeneratorHelper.normalizeDoc(doc));
            w.println("\")");
            GeneratorHelper.startClass(w, "    ",  "public class " 
                    + this.className 
                    + " implements Serializable, QueryHelper.QueryHelperProvider", serialId);

            classes.stream().filter((GeneralType g) -> g.getName().startsWith("/") && g instanceof ClassType)
                .map(ClassType.class::cast)
                .forEach((ClassType clazz) -> cg.generate(clazz, w));
            
            w.println("");
            
            classes.stream().filter((GeneralType g) -> g instanceof EnumType)
                    .map(EnumType.class::cast)
                    .peek((EnumType newEnum) -> eg.generate(newEnum, w))
                    .forEach((EnumType newEnum) -> this.addEnumProperties(newEnum, propsFile));

            w.println("");
            serviceChoice(w, propsFile);
            w.println(servicesOut.toString());
            
            w.println("    @Override");
            w.println("    public QueryHelper getQueryHelper() {");
            w.println(getQueryHelperContent.toString());
            w.println("        return null;");
            w.println("    }");

            w.println("}");
        }
    }

    private void addEnumProperties(EnumType enumType, File propsFile) {
        final String parent = enumType.getName();

        enumType.getNames().stream().map(name -> new Property(parent + "." + name, "enum"))
                .forEach(p -> p.write(propsFile));
    }

    private void gridLayout(List<String> layouts, Service s) {
        long nbeAttr = s.getTypeService().acceptedField().count();
        if (nbeAttr > 0) {
            layouts.add("@GridLayout.Row(\"" + toInstanceName(s.getName()) + "Parameters\")");
        }
    }

    private void serviceChoice(PrintWriter w, File propsFile) {
        String serviceChoiceName = this.className + "ServiceChoice";
        w.println("    public enum " + serviceChoiceName + " {");
        String list = services.get().map(Service::getName).collect(Collectors.joining(", ", "        ", ";"));
        w.println(list);
        w.println("    }");
        w.println("");
        w.println("    @Option");
        w.println("    @Documentation(\"selected service\")");
        w.println("    private " + serviceChoiceName + " service = " +serviceChoiceName + "." + services.get().findFirst().get().getName() + ";");

        this.services.get()
                .map(s -> new Property(serviceChoiceName + "." + s.getName(), s.getName()))
                .forEach(p -> p.write(propsFile));
        new Property(this.className + ".service", "choice of service").write(propsFile);
    }

    private void serviceParamCode(PrintWriter w, 
            File propsFile, 
            PrintWriter qhProvider, 
            Service s) {
        String serviceChoiceName = this.className + "ServiceChoice";
        String clName = s.getJavaNameType();
        String instanceName = toInstanceName(s.getName() + "Parameters");

        w.println("");
        long nbeAttr = s.getTypeService().acceptedField().count();
        if (nbeAttr > 0) {
            w.println("    @Option");
            w.println("    @ActiveIf(target = \"service\", value = \"" + s.getName() +"\")");
            w.println("    @Documentation(\"parameters\")");
        }
        w.println("    private " + clName + " " + instanceName + " = new " + clName + "();");
        
        qhProvider.println("        if (this.service == " + serviceChoiceName + "." + clName + ") {");
        qhProvider.println("            return this." + instanceName + ";");
        qhProvider.println("        }");

        final Property p = new Property(this.className + "." + instanceName, s.getName()); // s.getDoc());
        p.write(propsFile);

    }

    private String toInstanceName(String name) {
        return FieldType.builder().name(name).build().getName();
    }

    private long getSerialId() {
        long res = 0L;
        final byte[] version = this.services.getVersion().getBytes(Charset.defaultCharset());

        for (int i = 0; i < version.length; i++) {
            byte r = version[i];
            if (r >= '0' && r <= '9') {
                res = res*10 + r - '0';
            }
        }
        return res;
    }

}
