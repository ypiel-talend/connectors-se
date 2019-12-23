package api.generator;

import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratorHelper {

    public static void fileHeader(PrintWriter w) {
        w.println("package org.talend.components.workday.dataset.service.input;");
        w.println("");
        w.println("");
        w.println("import java.io.Serializable;");
        w.println("import java.util.Map;");
        w.println("import lombok.Data;");
        w.println("import org.talend.sdk.component.api.configuration.Option;");
        w.println("import org.talend.sdk.component.api.configuration.condition.ActiveIf;");
        w.println("import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;");
        w.println("import org.talend.sdk.component.api.meta.Documentation;");
        w.println("import org.talend.components.workday.dataset.QueryHelper;");

        w.println("");
        w.println("");
    }

    public static void startClass(PrintWriter w,
                                  String indent,
                                  String classStart,
                                  long serialId) {
        w.print(indent);
        w.print(classStart);
        w.print(" {");
        w.println("");
        w.print(indent);
        w.print("    ");
        w.println(toSerialId(serialId));
        w.println("");
    }

    private static String toSerialId(long sid) {
        return "private static final long serialVersionUID = " + sid + "L;";
    }

    public static String buildLayout(Stream<String> fields) {
        return fields.map(fieldName -> "@GridLayout.Row(\"" + fieldName + "\")")
                .collect(Collectors.joining(", ", "@GridLayout({", " })"));
    }

    public static String normalizeDoc(String doc) {
        if (doc == null) {
            return null;
        }
        String ret = doc.replace("\r\n", "\\n");
        ret = ret.replace("\n", "\\n");
        ret = ret.replace('"', '\'');
        return ret;
    }

    public static StringBuilder toMaj(StringBuilder builder, int pos) {
        char c = builder.charAt(pos);
        if (c >= 'a' && c <= 'z') {
            builder.setCharAt(pos, Character.toUpperCase(c));
        }
        return builder;
    }

    public static StringBuilder toMin(StringBuilder builder, int pos) {
        char c = builder.charAt(pos);
        if (c >= 'A' && c <= 'Z') {
            builder.setCharAt(pos, Character.toLowerCase(c));
        }
        return builder;
    }
}
