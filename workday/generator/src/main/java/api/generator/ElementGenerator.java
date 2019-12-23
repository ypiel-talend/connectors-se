package api.generator;

import java.io.PrintWriter;

public interface ElementGenerator<T> {

    void generate(T element, PrintWriter out);
}
