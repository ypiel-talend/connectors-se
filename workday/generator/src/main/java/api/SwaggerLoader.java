package api;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

public class SwaggerLoader {

    /** all availables swaggers */
    private final Swagger swagger;

    /**
     * Constructor
     * @param swaggersPath : path for swaggers.
     */
    public SwaggerLoader(String swaggersPath) {
        this.swagger = this.init(swaggersPath);
    }

    private Swagger init(String swaggersPath) {
        try {
            SwaggerParser parser = new SwaggerParser();

            File swFile = new File(swaggersPath);
            final byte[] bytes = Files.readAllBytes(swFile.toPath());
            return parser.parse(new String(bytes));
        }
        catch (IOException e) {
            throw new UncheckedIOException("Unable to load workday swaggers files", e);
        }
    }

    public Map<String, List<Parameter>> extractServicesParams() {
        this.swagger.getPath("").getGet();
        return this.swagger.getPaths().entrySet().stream()
                .filter(e -> e.getValue().getGet() != null)
                .collect(Collectors.toMap(e -> this.swagger.getBasePath() + e.getKey(), e -> e.getValue().getGet().getParameters()));
    }

    public Model findDefinition(String name) {
        return this.swagger.getDefinitions().get(name);
    }

    public Swagger getSwagger() {
        return swagger;
    }
}
