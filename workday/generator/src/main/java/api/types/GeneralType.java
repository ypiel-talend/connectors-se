package api.types;

import java.util.List;

public class GeneralType {

    private final String name;

    private final String packageName; // empty == default.

    private final String javaName;

    protected GeneralType(GeneralType.Builder<?> builder) {
        this.name = builder.name;
        this.javaName = builder.javaName;
        this.packageName = builder.packageName;
    }

    public String getName() {
        return name;
    }

    public String getJavaName(List<FieldType> sub) {
        return javaName;
    }

    public String getPackageName() {
        return packageName;
    }
    
    public String initialValue() {
        return "";
    }

    public GeneralType mix(String newName, GeneralType other) {
        if (other instanceof ClassType) {
           return other.mix(newName, this);
        }
        return this;
    }

    public abstract static class Builder<T extends Builder<T>> {
        public abstract T self();

        private String name = null;

        private String packageName = ""; // empty == default.

        private String javaName = null;

        public T name(String name) {
            this.name = name;
            if (this.javaName == null) {
                this.javaName(name);
            }
            return this.self();
        }

        public String getName() {
            return name;
        }

        public T packageName(String name) {
            this.packageName = name;
            return this.self();
        }

        public T javaName(String name) {
            this.javaName = new Name(name).getValue();
            return this.self();
        }

        public GeneralType build() {
            return new GeneralType(this);
        }
    }

    private static class GeneralTypeBuilder extends Builder<GeneralTypeBuilder> {

        @Override
        public GeneralTypeBuilder self() {
            return this;
        }
    }

    public static Builder<?> builder() {
        return new GeneralTypeBuilder();
    }

}
