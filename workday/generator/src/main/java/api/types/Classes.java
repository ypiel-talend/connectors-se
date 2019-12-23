package api.types;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Classes {
    private final Map<String, GeneralType> classes = new TreeMap<>();

    public Classes() {
        this.add(ClassType.builder().name("integer").javaName("Integer").initialValue("0").build());
        this.add(ClassType.builder().name("integer::int32").initialValue("0").javaName("Integer").build());
        this.add(ClassType.builder().name("integer::int64").initialValue("0L").javaName("Long").build());
        this.add(ClassType.builder().name("string").javaName("String").build());
        this.add(ClassType.builder().name("string::date").javaName("String").build());
        this.add(ClassType.builder().name("string::date-time").javaName("String").build());
        this.add(ClassType.builder().name("string::url").javaName("String").build());
        this.add(ClassType.builder().name("string::uri").javaName("String").build());
        this.add(ClassType.builder().name("string::string").javaName("String").build());
        this.add(ClassType.builder().name("string::Workday ID (WID)").javaName("String").build());
        this.add(ClassType.builder().name("array").javaName("List").packageName("java.util").subtypenumbers(1).build());
        this.add(ClassType.builder().name("boolean").initialValue("Boolean.TRUE").javaName("Boolean").build());
        this.add(ClassType.builder().name("number").initialValue("0.0").javaName("Float").build());
        this.add(ClassType.builder().name("number::int32").initialValue("0").javaName("Integer").build());
        this.add(ClassType.builder().name("number::double").initialValue("0.0D").javaName("Double").build());
    }

    public final void add(GeneralType cl) {
        if (cl == null || cl.getName() == null) {
            return;
        }
        this.classes.put(cl.getName(), cl);
    }

    public GeneralType get(String name) {
        return this.classes.get(name);
    }

    public Stream<GeneralType> stream() {
        return this.classes.values().stream();
    }

}
