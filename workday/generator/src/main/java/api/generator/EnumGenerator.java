package api.generator;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.stream.Collectors;

import api.types.EnumType;

public class EnumGenerator implements ElementGenerator<EnumType> {

    @Override
    public void generate(EnumType e, PrintWriter out) {

        out.println("");

        out.print("    public enum ");
        out.print(e.getJavaName(Collections.emptyList()));

        final String elements = e.getNames()
                .stream()
                .collect(Collectors.joining(", ", " { ", " } "));

        out.println(elements);
        out.println("");

    }
}
