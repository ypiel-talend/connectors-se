package api.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import api.code.Block;
import api.code.Content;

public class ClassType extends GeneralType {

    private final int subtypenumbers;

    private final List<FieldType> fields;

    private final boolean paginable;

    private final boolean inner;

    private final String  doc;
    
    private final String  initialValue;

    protected ClassType(Builder<?> builder) {
        super(builder);
        this.subtypenumbers = builder.subtypenumbers;
        this.fields = builder.fields;
        this.paginable = builder.paginable;
        this.doc = builder.doc;
        this.inner = builder.inner;
        this.initialValue = builder.initialValue;
    }

    @Override
    public String getJavaName(List<FieldType> sub) {
        if (this.subtypenumbers == 0) {
            return super.getJavaName(sub);
        }
        StringBuilder jname = new StringBuilder(super.getJavaName(sub));
        jname.append("<");
        String generics = sub.stream()
                .map((FieldType f) -> f.getType().getJavaName(f.getSubTypes()))
                .collect(Collectors.joining(", "));
        jname.append(generics);
        jname.append(">");


        return jname.toString();
    }
    
    @Override
    public String initialValue() {
        return this.initialValue;
    }

    /**
     * All of
     * @return
     */
    @Override
    public ClassType mix(String newName, GeneralType other) {
        if (!(other instanceof ClassType)) {
            return this;
        }
        ClassType o = (ClassType) other;
        final ClassType newType = ClassType.builder()
                .name(newName)
                .subtypenumbers(this.subtypenumbers + o.subtypenumbers)
                .addFields(this.fields)
                .addFields(o.fields)
                .build();

        return newType;
    }
    
    public String start(boolean isStatic) {
        String s = this.getJavaName(Collections.emptyList());
        StringBuilder start = new StringBuilder("public ");
        if (isStatic) {
            start.append("static ");
        }
        return start.append("class ")
                .append(s)
                .append(" implements Serializable ")
                .toString();
    }
    
    public Content getCodeMethods() {
        return new Block();
    }

    public Stream<FieldType> fields() {
        return this.fields.stream();
    }

    public boolean isPaginable() {
        return paginable;
    }

    public boolean isInner() {
        return inner;
    }

    public String getDoc() {
        return doc;
    }

    public abstract static class Builder<T extends Builder<T>> extends GeneralType.Builder<T> {

        private int subtypenumbers = 0;

        private List<FieldType> fields = new ArrayList<>();

        private boolean paginable = false;

        private boolean inner = true;

        private String doc = null;
        
        private String  initialValue = "";

        public T subtypenumbers(int sub) {
            this.subtypenumbers = sub;
            return this.self();
        }

        public T addField(FieldType field) {
            if (field.getName().charAt(0) >= 'A'
                    && field.getName().charAt(0) <= 'Z') {
                System.out.println("ici");
            }
            this.fields.add(field);
            return this.self();
        }

        public T addFields(Iterable<FieldType> newFields) {
            newFields.forEach(this.fields::add);
            return this.self();
        }

        public T addFields(Stream<FieldType> newFields) {
            newFields.forEach(this.fields::add);
            return this.self();
        }

        public T  paginable(boolean paginable) {
            this.paginable = paginable;
            return this.self();
        }

        public T  doc(String docInit) {
            this.doc = docInit;
            return this.self();
        }

        public T  inner(boolean innerInit) {
            this.inner = innerInit;
            return this.self();
        }
        
        public T  initialValue(String  initialValueInit) {
            this.initialValue = initialValueInit;
            return this.self();
        }

        @Override
        public ClassType build() {
            return new ClassType(this);
        }
    }

    public static class ClassTypeBuilder extends Builder<ClassTypeBuilder> {
        @Override
        public ClassTypeBuilder self() {
            return this;
        }
    }

    public static ClassTypeBuilder builder() {
        return new ClassTypeBuilder();
    }

}
