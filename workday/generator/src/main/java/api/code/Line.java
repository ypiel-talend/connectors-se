package api.code;

import java.io.PrintWriter;

public class Line implements Content {

    private final String content;

    public Line(String line) {
        super();
        this.content = line;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        Content.printIndent(out, indent);
        out.println(content);
    }
    
    public static LineBuilder builder() {
        return new LineBuilder();
    }
  
    public static class LineBuilder {
        private final StringBuilder builder = new StringBuilder();
        
        public LineBuilder add(String s) {
            this.builder.append(s);
            return this;
        }
        
        public LineBuilder quoted(String s) {
            this.builder.append('"').append(s).append('"');
            return this;
        }
        
        public Line build() {
            return new Line(this.builder.toString());
        }
        
    }
}
