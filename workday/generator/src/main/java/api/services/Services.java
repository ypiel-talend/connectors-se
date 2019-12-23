package api.services;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Services {

    private final Map<String, Service> services = new TreeMap<>();

    private final String version;

    public Services(String version) {
        this.version = version;
    }

    public void add(Service s) {
        if (s != null && s.getName() != null) {
            this.services.put(s.getName(), s);
        }
    }

    public Stream<Service> get() {
        return this.services.values().stream();
    }

    public String getVersion() {
        return version;
    }
    
    public boolean isEmpty() {
        return this.services.isEmpty();
    }
}
