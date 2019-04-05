package org.talend.components.mongodb.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
public class MongoDBTestExtension implements ExtensionContext.Store.CloseableResource, BeforeAllCallback, ParameterResolver {

    private TestContext testContext = new TestContext();

    private static final GenericContainer MONGO_CONTAINER = new GenericContainer(
            new ImageFromDockerfile().withFileFromClasspath("Dockerfile", "docker/Dockerfile")
                    .withFileFromPath("docker-entrypoint.sh", Paths.get("src/test/resources/docker/docker-entrypoint.sh"))
                    .withFileFromPath("server.pem", Paths.get("src/test/resources/docker/server.pem"))
                    .withFileFromPath("ca.pem", Paths.get("src/test/resources/docker/ca.pem")))
                            .withEnv("MONGO_INITDB_ROOT_USERNAME", MongoDBTestConstants.USERNAME)
                            .withEnv("MONGO_INITDB_ROOT_PASSWORD", MongoDBTestConstants.PASSWORD)
                            .withCommand("--sslMode requireSSL --sslPEMKeyFile /mongocerts/server.pem")
                            .withExposedPorts(MongoDBTestConstants.PORT)
                            .waitingFor(Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(30)));

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        log.info("extension before all start");
        if (!started) {
            started = true;
            MONGO_CONTAINER.start();
            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("any unique name", this);
        }

        String dockerHostAddress = MONGO_CONTAINER.getContainerIpAddress();
        Integer port = MONGO_CONTAINER.getMappedPort(MongoDBTestConstants.PORT);

        MongoDBDatastore dataStore = new MongoDBDatastore();

        dataStore.setServer(dockerHostAddress);
        dataStore.setPort(port);
        dataStore.setUseSSL(true);
        dataStore.setAuthentication(true);
        dataStore.setUsername(MongoDBTestConstants.USERNAME);
        dataStore.setPassword(MongoDBTestConstants.PASSWORD);
        dataStore.setDatabase(MongoDBTestConstants.DATABASE_NAME);
        dataStore.setSetAuthenticationDatabase(true);
        dataStore.setAuthenticationDatabase(MongoDBTestConstants.AUTH_DATABASE);
        dataStore.setAuthenticationMechanism(MongoDBDatastore.AuthenticationMechanism.NEGOTIATE_MEC);

        testContext.dataStore = dataStore;

        log.info("docker machine: " + dockerHostAddress + ":" + port);
    }

    @Override
    public void close() throws Throwable {
        log.info("extension close call");
        MONGO_CONTAINER.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return TestContext.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return testContext;
    }

    @Data
    public static class TestContext {

        private MongoDBDatastore dataStore;

    }
}
