package api.generator;

import java.io.PrintWriter;

import api.code.Content;
import api.types.ClassType;
import api.types.FieldType;

public class ClassGenerator implements ElementGenerator<ClassType> {

    private final ElementGenerator<FieldType> fieldGenerator;

    public ClassGenerator(ElementGenerator<FieldType> fieldGenerator) {
        this.fieldGenerator = fieldGenerator;
    }

    @Override
    public void generate(ClassType cl, PrintWriter out) {

        out.println("");
        out.println("    @Data");
        if (cl.getDoc() != null && !cl.getDoc().isEmpty()) {
            out.println("    @Documentation(\"" + GeneratorHelper.normalizeDoc(cl.getDoc()) + "\")");
        }
        else {
            out.println("        @Documentation(\"unkonwn doc in workday\")");
        }
        final String grid = generateGridLayout(cl);
        out.println("    " + grid);
        GeneratorHelper.startClass(out, "   ", cl.start(true), 1L);

        cl.fields().forEach((FieldType f) -> this.fieldGenerator.generate(f, out) );
        
        Content codeMethods = cl.getCodeMethods();
        codeMethods.print(out, 2);

        out.println("    }");
    }

    private String generateGridLayout(ClassType cl) {
        return GeneratorHelper.buildLayout( cl.fields()
                .map(FieldType::getName)
                .filter(this::acceptField));
    }

    private boolean acceptField(String fieldName) {
        return !("limit".equals(fieldName) || "offset".equals(fieldName) || "view".equals(fieldName));
    }
}
