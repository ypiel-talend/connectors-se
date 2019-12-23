package api.types;

import java.util.ArrayList;
import java.util.List;

public class EnumType extends GeneralType {

    private final List<String> names;

    protected EnumType(Builder<?> builder) {
        super(builder);
        this.names = builder.names;
    }

    public List<String> getNames() {
        return names;
    }
    
    @Override
    public String initialValue() {
        String value = "";
        if (!this.getNames().isEmpty()) {
            value = this.getName() + "." + this.getNames().get(0);
        }
        return value;
    }

    public abstract static class Builder<T extends Builder<T>> extends GeneralType.Builder<T> {

        private final List<String> names = new ArrayList<>();

        public T add(String name) {
            this.names.add(this.filterName(name));
            return this.self();
        }

        public T addAll(Iterable<String> namesInit) {
            namesInit.forEach(name -> this.add(name));
            return this.self();
        }

        private String filterName(String n) {
            StringBuilder builder = new StringBuilder(n);
            for (int pos = builder.length() - 1; pos >= 0; pos--) {
                if (!isOKinName(n.charAt(pos))) {
                    builder.deleteCharAt(pos);
                }
            }
            return builder.toString();
        }

        private boolean isOKinName(char n) {
            return (n >= 'a' && n <= 'z')
                    || (n >= 'A' && n <= 'Z')
                    || (n >= '0' && n <= '9')
                    || n == '_';
        }

        public EnumType build() {
            return new EnumType(this);
        }
    }

    public static class EnumTypeBuilder extends Builder<EnumTypeBuilder> {

        @Override
        public EnumTypeBuilder self() {
            return this;
        }
    }

    public static EnumTypeBuilder builder() {
        return new EnumTypeBuilder();
    }

}
