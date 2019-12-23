package api.services;

import java.util.Collections;

import api.types.Name;
import api.types.ServiceClassType;

public class Service {
    private final String name;

    private final String doc;

    private final ServiceClassType typeService;

    private final boolean paginable;

    public Service(String name, String doc, ServiceClassType typeService, boolean paginable) {
        this.name = new Name(name).getValue();
        this.doc = doc;
        this.typeService = typeService;
        this.paginable = paginable;
    }

    public String getName() {
        return name;
    }
    
    public String getJavaNameType() {
        return typeService.getJavaName(Collections.emptyList());
    }

    public boolean isPaginable() {
        return paginable;
    }

    public String getDoc() {
        return doc;
    }

    
    public ServiceClassType getTypeService() {
        return typeService;
    }
}
