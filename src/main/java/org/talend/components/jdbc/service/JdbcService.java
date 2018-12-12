/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.jdbc.configuration.JdbcConfiguration;
import org.talend.components.jdbc.datastore.JdbcConnection;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.configuration.Configuration;
import org.talend.sdk.component.api.service.dependency.Resolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Slf4j
@Service
public class JdbcService {

    private static Pattern READ_ONLY_QUERY_PATTERN = Pattern.compile(
            "^SELECT\\s+((?!((\\bINTO\\b)|(\\bFOR\\s+UPDATE\\b)|(\\bLOCK\\s+IN\\s+SHARE\\s+MODE\\b))).)+$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    private Enumeration<Driver> initialRegisteredDrivers;

    private final Map<JdbcConfiguration.Driver, URLClassLoader> driversClassLoaders = new HashMap<>();

    @Service
    private Resolver resolver;

    @Service
    private I18nMessage i18n;

    @Configuration("jdbc")
    private Supplier<JdbcConfiguration> jdbcConfiguration;

    private Integer connectionValidationTimeout;

    @PostConstruct
    public void init() {
        initialRegisteredDrivers = DriverManager.getDrivers();
        connectionValidationTimeout = ofNullable(jdbcConfiguration.get().getConnection().getValidationTimeout()).orElse(5);
    }

    /**
     * At this point we will deregister all the drivers that may were registered by this service.
     */
    @PreDestroy
    public void clean() {
        driversClassLoaders.forEach((driver, classLoader) -> {
            while (initialRegisteredDrivers.hasMoreElements()) {
                if (!driver.getClassName().equals(initialRegisteredDrivers.nextElement().getClass().getName())) {
                    deregisterDriver(driver);
                }
            }
        });
        initialRegisteredDrivers = null;
    }

    private void deregisterDriver(final JdbcConfiguration.Driver driver) {
        final URLClassLoader loader = driversClassLoaders.get(driver);
        try {
            Driver d = (Driver) loader.loadClass(driver.getClassName()).newInstance();
            DriverManager.deregisterDriver(d);
        } catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException e) {
            log.error(i18n.errorDriverDeregister(driver.getClassName()), e);
        } finally {
            try {
                loader.close();
            } catch (IOException e) {
                log.warn(i18n.warnDriverClose(driver.getId()));
            }
        }
    }

    private URLClassLoader getDriverClassLoader(final JdbcConfiguration.Driver driver) {
        /*
         * remove invalid driver. when a driver property has changed
         */
        if (!driversClassLoaders.containsKey(driver)) {
            driversClassLoaders.entrySet().stream().filter(entry -> entry.getKey().getId().equals(driver.getId()))
                    .forEach(entry -> {
                        deregisterDriver(entry.getKey());
                        driversClassLoaders.remove(entry.getKey());
                    });
        }

        return driversClassLoaders.computeIfAbsent(driver, key -> {
            final Collection<File> driverFiles = resolver.resolveFromDescriptor(new ByteArrayInputStream(driver.getPaths()
                    .stream().filter(p -> p.getPath() != null && !p.getPath().isEmpty())
                    .map(JdbcConfiguration.Driver.Path::getPath).collect(joining("\n")).getBytes(StandardCharsets.UTF_8)));
            final String missingJars = driverFiles.stream().filter(f -> !f.exists()).map(File::getAbsolutePath)
                    .collect(joining("\n"));
            if (!missingJars.isEmpty()) {
                throw new IllegalStateException(i18n.errorDriverLoad(driver.getId(), missingJars));
            }
            final URL[] urls = driverFiles.stream().map(f -> {
                try {
                    return f.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new IllegalStateException(e);
                }
            }).toArray(URL[]::new);
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        });
    }

    /**
     * @return return false if the sql query is not a read only query, true otherwise
     */
    public boolean isNotReadOnlySQLQuery(final String query) {
        return query != null && !READ_ONLY_QUERY_PATTERN.matcher(query.trim()).matches();
    }

    public Connection connection(final JdbcConnection datastore, final boolean rewriteBatchedStatements) {
        if (datastore.getJdbcUrl() == null || datastore.getJdbcUrl().trim().isEmpty()) {
            throw new IllegalArgumentException(i18n.errorEmptyJdbcURL());
        }
        final JdbcConfiguration.Driver driver = getDriver(datastore);
        final URLClassLoader driverLoader = getDriverClassLoader(driver);
        try {
            final Driver driverInstance = (Driver) driverLoader.loadClass(driver.getClassName()).newInstance();
            final Properties info = new Properties() {

                {
                    setProperty("user", datastore.getUserId());
                    if (datastore.getPassword() != null && !datastore.getPassword().trim().isEmpty()) {
                        setProperty("password", datastore.getPassword());
                    }
                    if ("mysql".equalsIgnoreCase(datastore.getDbType())) {
                        setProperty("rewriteBatchedStatements", String.valueOf(rewriteBatchedStatements));
                    }
                }
            };
            return driverInstance.connect(datastore.getJdbcUrl().trim(), info);
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(i18n.errorCantLoadDriver(datastore.getDbType()), e);
        } catch (final SQLException e) {
            throw new IllegalStateException(i18n.errorSQL(e.getErrorCode(), e.getMessage()), e);
        }
    }

    public Connection connection(final JdbcConnection datastore) {
        return connection(datastore, false);
    }

    public boolean isConnectionValid(final Connection connection) throws SQLException {
        return connection.isValid(connectionValidationTimeout);
    }

    public void acceptsURL(final JdbcConnection connection) {
        final JdbcConfiguration.Driver driver = getDriver(connection);
        final URLClassLoader driverLoader = getDriverClassLoader(driver);
        try {
            final Driver driverInstance = (Driver) driverLoader.loadClass(driver.getClassName()).newInstance();

            if (!driverInstance.acceptsURL(connection.getJdbcUrl().trim())) {
                throw new IllegalStateException(i18n.errorUnsupportedSubProtocol());
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new IllegalStateException(i18n.errorCantLoadDriver(connection.getDbType()), e);
        } catch (SQLException e) {
            throw new IllegalStateException(i18n.errorSQL(e.getErrorCode(), e.getMessage()), e);
        }
    }

    private JdbcConfiguration.Driver getDriver(final JdbcConnection dataStore) {
        return jdbcConfiguration.get().getDrivers().stream()
                .filter(d -> d.getId()
                        .equals(ofNullable(dataStore.getHandler()).filter(h -> !h.isEmpty()).orElse(dataStore.getDbType())))
                .filter(d -> d.getHandlers() == null || d.getHandlers().isEmpty()).findFirst()
                .orElseThrow(() -> new IllegalStateException(i18n.errorDriverNotFound(dataStore.getDbType())));
    }
}
