package api.types;

import java.util.Optional;
import java.util.stream.Stream;

import api.code.Block;
import api.code.Content;
import api.code.Line;


public class ServiceClassType extends ClassType {

    private final String basePath;

    protected ServiceClassType(ServiceBuilder<?> builder) {
        super(builder);
        this.basePath = builder.basePath;
    }

    @Override
    public String start(boolean isStatic) {

        String startClass = super.start(isStatic);
        StringBuilder start = new StringBuilder(startClass);
        start.append(", QueryHelper ");
        return start.toString();
    }

    @Override
    public Content getCodeMethods() {

        Block blockMethods = new Block();
        if (this.isPaginable()) {
            // Paginable (default false)
            blockMethods
            .add("@Override")
            .add("public boolean isPaginable() { ")
            .add(new Block(1).add("return true;"))
            .add("}")
            .newLine();
        }

        // service To Call.
        blockMethods
        .add("@Override")
        .add("public String getServiceToCall() {")
        .child( (Block c) -> c.add("return " + this.getServiceToCallMethod() + ";") )
        .add("}")
        .newLine();

        // extract query map
        blockMethods
        .add("@Override")
        .add("public Map<String, Object> extractQueryParam() {")
        .child(this::extractQueryParamContent)
        .add("}");
        return blockMethods;
    }


    private void extractQueryParamContent(Block bloc) {
        bloc.add("final Map<String, Object> queryParam = new java.util.HashMap<>();");

        this.acceptedField()
        .filter((FieldType f) -> f.getParamIn() == FieldType.ParamIn.Query)
        .map(this::writeQueryField)
        .forEach(bloc::addLines);

        bloc.add("return queryParam;");
    }
    
    public Stream<FieldType> acceptedField() {
        return this.fields().filter(this::acceptField);
    }

    private Block writeQueryField(FieldType f) {

        // put field to map only if not null.
        Block code = new Block();
        code.add("if (this." + f.getName() + " != null) {");

        Line codePut = Line.builder()
                .add("queryParam.put(")
                .quoted(f.getName())
                .add(", this." + f.getName() + ");")
                .build();

        code.child((Block inner) -> inner.add(codePut));

        code.add("}");
        return code;
    }

    private boolean acceptField(FieldType fieldName) {
        return !("limit".equals(fieldName.getName()) 
                || "offset".equals(fieldName.getName()) 
                || "view".equals(fieldName.getName()));
    }

    private String getServiceToCallMethod() {

        String serviceToCall = '"' + this.basePath + this.getName() + '"';
        int start = serviceToCall.indexOf('{');
        while (start > 0) {
            int end = serviceToCall.indexOf('}', start);
            String paramName = serviceToCall.substring(start + 1, end);
            Optional<FieldType> fparam = this.fields()
                    .filter((FieldType f) -> paramName.equalsIgnoreCase(f.getOriginal()))
                    .findFirst();
            if (fparam.isPresent()) {

                serviceToCall = serviceToCall.substring(0, start)
                        + "\" + this." + fparam.get().getName() + " + \"" + serviceToCall.substring(end + 1);
            }
            else {
                serviceToCall = serviceToCall.substring(0, start)
                        + serviceToCall.substring(end + 1);
            }
            start = serviceToCall.indexOf('{');          
        }

        serviceToCall = serviceToCall.replace("//", "/");

        return serviceToCall;
    }


    public abstract static class ServiceBuilder<T extends ServiceBuilder<T>> extends ClassType.Builder<T> {

        private String basePath = "";

        public T basePath(String bp) {
            if (bp != null) {
                if (bp.startsWith("/")) {
                    this.basePath = bp.substring(1);
                }
                else {
                    this.basePath = bp;
                }
            }
            return this.self();
        }

        @Override
        public ServiceClassType build() {
            return new ServiceClassType(this);
        }
    }

    private static class ServiceClassTypeBuilder extends ServiceBuilder<ServiceClassTypeBuilder> {
        @Override
        public ServiceClassTypeBuilder self() {
            return this;
        }
    }

    public static ServiceBuilder<?> builderService() {
        return new ServiceClassTypeBuilder();
    }

}
