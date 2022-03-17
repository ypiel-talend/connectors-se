/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
import org.talend.components.jdbc.datastore.JDBCDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.connection.CloseConnection;
import org.talend.sdk.component.api.service.connection.CloseConnectionObject;
import org.talend.sdk.component.api.service.connection.CreateConnection;
import org.talend.sdk.component.api.service.dependency.Resolver;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Slf4j
@Service
public class JDBCService implements Serializable {

    private static final long serialVersionUID = 1;

    // TODO get the classloader tool to use maven gav pathes to load the jdbc driver jars classes dynamiclly
    @Service
    private transient Resolver resolver;

    @Suggestions("GUESS_DRIVER_CLASS")
    public SuggestionValues loadRecordTypes(@Option final List<String> driverJars) {
        final List<SuggestionValues.Item> items = new ArrayList<>();

        // items.add(new SuggestionValues.Item("com.mysql.cj.jdbc.Driver", "com.mysql.cj.jdbc.Driver"));

        getDriverClasses(driverJars).stream().forEach(driverClass -> {
            items.add(new SuggestionValues.Item(driverClass, driverClass));
        });

        return new SuggestionValues(true, items);
    }

    private List<String> getDriverClasses(List<String> driverJars) {
        // TODO check it if right
        List<String> driverClasses = new ArrayList<>();

        try {
            List<URL> urls = new ArrayList<>();
            for (String maven_path : driverJars) {
                URL url = new URL(removeQuote(maven_path));
                urls.add(url);
            }

            // TODO before this, should register mvn protocol for : new URL("mvn:foo/bar");
            // tck should already support that and provide some way to do that
            // but if not, we can use tcompv0 way
            URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());

            for (URL jarUrl : urls) {
                try (JarInputStream jarInputStream = new JarInputStream(jarUrl.openStream())) {
                    JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
                    while (nextJarEntry != null) {
                        boolean isFile = !nextJarEntry.isDirectory();
                        if (isFile) {
                            String name = nextJarEntry.getName();
                            if (name != null && name.toLowerCase().endsWith(".class")) {
                                String className = changeFileNameToClassName(name);
                                try {
                                    Class clazz = classLoader.loadClass(className);
                                    if (Driver.class.isAssignableFrom(clazz)) {
                                        driverClasses.add(clazz.getName());
                                    }
                                } catch (Throwable th) {
                                    // ignore all the exceptions, especially the class not found exception when look up
                                    // a class
                                    // outside the jar
                                }
                            }
                        }

                        nextJarEntry = jarInputStream.getNextJarEntry();
                    }
                }
            }
        } catch (Exception ex) {
            // TODO process
        }

        if (driverClasses.isEmpty()) {
            // TODO process
        }

        return driverClasses;
    }

    private String changeFileNameToClassName(String name) {
        name = name.replace('/', '.');
        name = name.replace('\\', '.');
        name = name.substring(0, name.length() - 6);
        return name;
    }

    private String removeQuote(String content) {
        if (content.startsWith("\"") && content.endsWith("\"")) {
            return content.substring(1, content.length() - 1);
        }

        return content;
    }

    @HealthCheck("CheckConnection")
    public HealthCheckStatus validateBasicDataStore(@Option final JDBCDataStore datastore) {
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "success message, TODO, i18n");
    }

    @CreateConnection
    public Connection createConnection(@Option final JDBCDataStore datastore) {
        // TODO create jdbc connection
        return null;
    }

    @CloseConnection
    public CloseConnectionObject closeConnection() {
        // TODO create jdbc connection
        return new CloseConnectionObject() {

            public boolean close() {
                // TODO close connection here
                Optional.ofNullable(this.getConnection())
                        .map(Connection.class::cast)
                        .ifPresent(conn -> {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                // TODO
                            }
                        });
                return true;
            }

        };
    }

}
