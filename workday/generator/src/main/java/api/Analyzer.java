package api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import api.services.Service;
import api.services.Services;
import api.types.ClassType;
import api.types.Classes;
import api.types.EnumType;
import api.types.FieldType;
import api.types.GeneralType;
import api.types.ServiceClassType;
import api.types.TUple;
import io.swagger.models.ArrayModel;
import io.swagger.models.ComposedModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.RefParameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

public class Analyzer {

    private final Swagger sw;

    private final Classes classes = new Classes();

    private final Services services;

    private final String fileName;

    private final String description;
    
    private final String basePath;

    public Analyzer(Swagger sw, String fileNameInit) {
        this.sw = sw;
        this.services = new Services(sw.getInfo().getVersion());
        this.fileName = fileNameInit;
        this.description = sw.getInfo().getTitle();
        this.basePath = sw.getBasePath();
        this.init();
    }

    public Classes getClasses() {
        return classes;
    }

    public Services getServices() {
        return services;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    private void init() {
        if (this.sw.getDefinitions() != null) {
            this.sw.getDefinitions().entrySet()
                    .stream()
                    .map((Map.Entry<String, Model> e) -> this.convert(e.getKey(), e.getValue()))
                    .filter(Objects::nonNull)
                    .forEach(this.classes::add);
        }

        this.sw.getPaths().entrySet()
                .stream()
                .filter(Objects::nonNull)
                .map(e -> TUple.of(e.getKey(), e.getValue()))
                .map(e -> e.secondMap(Path::getGet))
                .filter(e -> e.getV() != null)
                .map(this::convertForOperation)
                .forEach(this.classes::add);
    }

    private ClassType convertForOperation(TUple<String, Operation> op) {

        final boolean isPagineable = op.getV().getParameters().stream().map(Parameter::getName).anyMatch("offset"::equals);

        final ServiceClassType cl = ServiceClassType.builderService()
                .name(op.getU())
                .paginable(isPagineable)
                .addFields(op.getV().getParameters().stream().map(this::convert))
                .basePath(this.basePath)
                .build();

        services.add(new Service(op.getU(),
                op.getV().getDescription(),
                cl,
                isPagineable));
        return cl;
    }

    private GeneralType find(String name) {
        GeneralType cl = this.classes.get(name);
        if (cl == null) {
            final Model model = sw.getDefinitions().get(name);
            if (model == null) {
                throw new RuntimeException("unknown model '" + name + "'");
            }
            cl = this.convert(name, model);
            if (cl == null) {
                throw new RuntimeException("unknown class '" + name + "'");
            }
        }
        return cl;
    }

    private GeneralType convert(String name, Model model) {
        ClassType.Builder<?> cl = ClassType.builder().name(name);
        this.complete(cl, model.getProperties());

        if (model instanceof ComposedModel) {
            ComposedModel c = (ComposedModel) model;
            return c.getAllOf().stream().map((Model m) -> this.convert(name, m))
                    .reduce(cl.build(), (GeneralType c1, GeneralType c2) -> c1.mix(cl.getName(), c2));
        }
        if (model instanceof RefModel) {
            RefModel ref = (RefModel) model;
            String refName = decodeRef(ref.getSimpleRef());
            return this.find(refName);
        }
        if (model instanceof ArrayModel) {
            ArrayModel c = (ArrayModel) model;
            System.out.println("Array model (name:" + name + ")" + model.getReference() + " : " + c.getItems().getName());
            final Property items = c.getItems();
            if (items.getName() == null && (items instanceof ObjectProperty)) {
                ObjectProperty ob = (ObjectProperty) items;

                ClassType.Builder<?> genericClassBuilder =  ClassType.builder().name(name + "Generic");
                this.complete(genericClassBuilder, ob.getProperties());
                ClassType genericClass = genericClassBuilder.build();
                this.classes.add(genericClass);

                FieldType generic = FieldType.builder()
                        .name("generic")
                        .type(genericClass)
                        .build();

                FieldType f = FieldType.builder()
                        .name("array")
                        .type(classes.get("array"))
                        .addSubType(generic)
                        .build();
                cl.addField(f);
            }
        }


        return cl.build();
    }

    private String decodeRef(String ref) {
        if (ref == null) {
            return "";
        }
        if (ref.contains("%")) {
            try {
                return URLDecoder.decode(ref, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return ref;
            }
        }
        return ref;
    }

    private void complete(ClassType.Builder<?> cl, Map<String, Property> properties) {
        if (properties != null && !properties.isEmpty()) {
            properties.entrySet().stream()
                    .map(this::convert)
                    .forEach(cl::addField);
        }
    }


    private FieldType convert(Map.Entry<String, Property> p) {
        return this.convert(p.getKey(), p.getValue());
    }

    private FieldType convert(String name, Property value) {
        String typeName = getTypeName(value);

        if (value instanceof ObjectProperty) {
            typeName = name + "Object";
            if (this.classes.get(typeName) == null) {
                ObjectProperty o = (ObjectProperty) value;

                ClassType.Builder<?> cl = ClassType.builder().name(typeName);
                this.complete(cl, o.getProperties());
                this.classes.add(cl.build());
            }
        }
        final GeneralType fieldClass = this.find(typeName);

        return FieldType.builder()
                .name(name)
                .type(fieldClass)
                .doc(value.getDescription())
                .build();
    }

    private FieldType convert(Parameter p) {
        FieldType.Builder<?> builder = FieldType.builder()
                .name(p.getName())
                .paramIn(p.getIn())
                .doc(p.getDescription());
        if (p instanceof SerializableParameter) {
            SerializableParameter s = (SerializableParameter) p;
            final String typeName = getTypeName(s);
            final List<String> anEnum = s.getEnum();
            if (anEnum != null && !anEnum.isEmpty()) {
                final EnumType es = EnumType.builder()
                        .name(typeName)
                        .addAll(anEnum)
                        .build();
                this.classes.add(es);
                return builder.type(es)
                        .build();
            }
            if ("array".equals(s.getType())) {
                FieldType f = convert("", s.getItems());
                return builder.type(classes.get("array"))
                        .addSubType(f)
                        .build();
            }

            final GeneralType generalType = this.find(typeName);
            return builder.type(generalType).build();
        }
        if (p instanceof RefParameter) {
            RefParameter r = (RefParameter) p;
            final GeneralType generalType = this.find(r.getSimpleRef());
            return builder.type(generalType).build();
        }
        throw new RuntimeException("unknown type for param '" + p.getName() + "'");
    }

    private String getTypeName(SerializableParameter p) {
        if (p.getFormat() != null) {
            return p.getType() + "::" + p.getFormat();
        } else if (p.getEnum() != null && !(p.getEnum().isEmpty()) && p.getName() != null) {
            return p.getIn() + Character.toUpperCase(p.getName().charAt(0)) + p.getName().substring(1);
        }
        return p.getType();
    }

    private String getTypeName(Property p) {
        if (p instanceof RefProperty) {
            RefProperty r = (RefProperty) p;
            return r.getSimpleRef();
        }
        if (p.getFormat() != null) {
            return p.getType() + "::" + p.getFormat();
        }
        return p.getType();
    }
}
