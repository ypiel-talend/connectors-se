/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.talend.components.mongodb.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.talend.components.mongodb.datastore.*;
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

        MongoServerAddress serverAddress = new MongoServerAddress(dockerHostAddress, port);
        dataStore.setServerAddress(serverAddress);

        dataStore.setUseSSL(true);
        dataStore.setDatabase(MongoDBTestConstants.DATABASE_NAME);
        dataStore.setAuthentication(true);

        MongoAuthentication authentication = new MongoAuthentication();
        MongoUserPassConfiguration userPass = new MongoUserPassConfiguration();
        userPass.setUsername(MongoDBTestConstants.USERNAME);
        userPass.setPassword(MongoDBTestConstants.PASSWORD);
        authentication.setUserPassConfiguration(userPass);

        MongoAuthDatabaseConfiguration authDbConfig = new MongoAuthDatabaseConfiguration();
        authDbConfig.setSetAuthenticationDatabase(true);
        authDbConfig.setAuthenticationDatabase(MongoDBTestConstants.AUTH_DATABASE);
        authentication.setAuthDatabaseConfig(authDbConfig);
        authentication.setAuthenticationMechanism(MongoAuthentication.AuthenticationMechanism.NEGOTIATE_MEC);

        dataStore.setMongoAuthentication(authentication);
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
