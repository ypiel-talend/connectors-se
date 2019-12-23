package api.generator;

import java.io.PrintWriter;

import api.types.FieldType;

public class FieldGenerator implements ElementGenerator<FieldType> {



    @Override
    public void generate(FieldType element, PrintWriter out) {
        if (this.ignore(element)) {
            return;
        }
        out.println("");
        out.println("        @Option");
        if (element.getDoc() != null && !element.getDoc().isEmpty()) {
            out.println("        @Documentation(\"" + GeneratorHelper.normalizeDoc(element.getDoc()) + "\")");
        }
        else {
            out.println("        @Documentation(\"unkonwn doc in workday\")");
        }
        out.print("        private " + element.buildType());
        out.print(" ");
        out.print(element.getName());
        
        String val = element.getType().initialValue();
        if (val != null && !val.isEmpty()) {
            out.print(" = ");
            out.print(val);
        }
        
        out.println(';');
    }

    private boolean ignore(FieldType element) {
        String name = element.getName();
        return "limit".equals(name) || "offset".equals(name) || "view".equals(name);
    }



}
