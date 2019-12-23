package api.code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Block implements Content {
    
    private final int sizeIndent;
    
    private final List<Content> sub = new ArrayList<>();

    public Block() {
        this(0);
    }

    public Block(int sizeIndent) {
        super();
        this.sizeIndent = sizeIndent;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        sub.forEach((Content ct) -> ct.print(out, this.sizeIndent + indent));
    }
    
    public Block add(Content ct) {
        this.sub.add(ct);
        return this;
    }
    
    public Block add(String lineContent) {
        return this.add(new Line(lineContent));
    }
    
    public Block addLines(Block bl) {
        this.sub.addAll(bl.sub);
        return this;
    }
    
    public Block newLine() {
        return this.add(Content.separator);
    }

    public Block child(Consumer<Block> newBlock) {
        Block child = new Block(sizeIndent + 1);
        newBlock.accept(child);
        this.add(child);
        return this;
    }

}
