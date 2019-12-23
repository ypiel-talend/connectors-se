package api.code;

import java.io.PrintWriter;

public interface Content {

    void print(PrintWriter out, int indent);
    
    static void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("    ");
        }
    }
    
    static Content separator =  (PrintWriter out, int indent) ->  out.println("");
        

}
