package api.types;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.SourceVersion;

public class FieldType {
    
    private final String original;

    private final String name;

    private final String doc;

    private final GeneralType type;
    
    private final ParamIn paramIn;

    private final List<FieldType> subTypes;
    
    protected FieldType(Builder<?> builder) {
        original = builder.name;
        this.name = this.normalize(builder.name);
        this.doc = builder.doc;
        this.type = builder.type;
        this.subTypes = builder.subTypes;
        this.paramIn = builder.paramIn;
    }

    public GeneralType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public String getOriginal() {
        return original;
    }

    public String getDoc() {
        return doc;
    }

    public List<FieldType> getSubTypes() {
        return subTypes;
    }
    
    
    public ParamIn getParamIn() {
        return paramIn;
    }

    public String buildType() {
        StringBuilder builder = new StringBuilder();
        String pack = this.type.getPackageName();
        if (pack != null && !pack.isEmpty()) {
            builder.append(pack).append(".");
        }
        builder.append(this.getType().getJavaName(this.getSubTypes()));
        return builder.toString();
    }

    private String normalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        StringBuilder builder = new StringBuilder(name);
        char firstLetter = builder.charAt(0);
        if (firstLetter >= 'A' && firstLetter <= 'Z') {
            builder.setCharAt(0, Character.toLowerCase(firstLetter));
        }
        for (int curs = builder.length() - 1; curs >= 0; curs--) {
            if (this.isForbidden(builder.charAt(curs))) {
                builder.deleteCharAt(curs);
                if (curs < builder.length()) {
                    char letter = builder.charAt(curs);
                    if (letter >= 'a' && letter <= 'z') {
                        builder.setCharAt(curs, Character.toUpperCase(letter));
                    }
                }
            }
        }

        while (SourceVersion.isKeyword(builder.toString())) {
            builder.append('0');
        }

        return builder.toString();
    }

    private boolean isForbidden(char c) {
        return c == '.'
                || c == '-'
                || c == ' ';
    }
    
    public enum ParamIn {
        Query, Path, None;
    }

    public abstract static class Builder<T extends Builder<T>> {
        public abstract T self();

        private String name = null;

        private String doc = null;

        private GeneralType type = null;
        
        private ParamIn paramIn = ParamIn.None;

        private final List<FieldType> subTypes = new ArrayList<>();

        public T name(String name) {
            if (name != null && name.startsWith("custom object")) {
                System.out.println(name);
            }
            this.name = name;
            if (name != null && name.length() > 0 && name.charAt(0) >= 'A' && name.charAt(0) <= 'Z') {
                this.name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
            return this.self();
        }

        public T doc(String doc) {
            this.doc = doc;
            return this.self();
        }
        
        public T paramIn(ParamIn paramIn) {
            this.paramIn = paramIn;
            return this.self();
        }
        
        public T paramIn(String paramIn) {
            if ("Query".equalsIgnoreCase(paramIn)) {
                this.paramIn = ParamIn.Query;
            }
            else if ("Path".equalsIgnoreCase(paramIn)) {
                this.paramIn = ParamIn.Path;
            }
            else {
                this.paramIn = ParamIn.None;
            }
            return this.self();
        }

        public T type(GeneralType type) {
            this.type = type;
            return this.self();
        }

        public T addSubType(FieldType ft) {
            this.subTypes.add(ft);
            return this.self();
        }



        public FieldType build() {
            return new FieldType(this);
        }
    }

    private static class FieldTypeBuilder extends Builder<FieldTypeBuilder> {
        @Override
        public FieldTypeBuilder self() {
            return this;
        }
    }

    public static Builder<?> builder() {
        return new FieldTypeBuilder();
    }
}
