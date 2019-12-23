package api.types;

public class ModuleType {

    private final String className;

    private final String description;


    public ModuleType(String className, String description) {
        this.className = className;
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }
}
