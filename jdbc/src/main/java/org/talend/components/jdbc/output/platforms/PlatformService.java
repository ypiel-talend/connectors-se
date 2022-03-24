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
package org.talend.components.jdbc.output.platforms;

import org.talend.components.jdbc.configuration.JdbcConfiguration;
import org.talend.components.jdbc.datastore.JdbcConnection;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.configuration.Configuration;
import org.talend.sdk.component.api.service.configuration.LocalConfiguration;

import java.util.Locale;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static org.talend.components.jdbc.output.platforms.DerbyPlatform.DERBY;
import static org.talend.components.jdbc.output.platforms.MSSQLPlatform.MSSQL;
import static org.talend.components.jdbc.output.platforms.MSSQLPlatform.MSSQL_JTDS;
import static org.talend.components.jdbc.output.platforms.MariaDbPlatform.MARIADB;
import static org.talend.components.jdbc.output.platforms.MySQLPlatform.MYSQL;
import static org.talend.components.jdbc.output.platforms.OraclePlatform.ORACLE;
import static org.talend.components.jdbc.output.platforms.PostgreSQLPlatform.POSTGRESQL;
import static org.talend.components.jdbc.output.platforms.RedshiftPlatform.REDSHIFT;
import static org.talend.components.jdbc.output.platforms.SQLDWHPlatform.SQLDWH;
import static org.talend.components.jdbc.output.platforms.SnowflakePlatform.SNOWFLAKE;
import static org.talend.components.jdbc.output.platforms.DeltaLakePlatform.DELTALAKE;

@Service
public class PlatformService {

    @Configuration("jdbc")
    private Supplier<JdbcConfiguration> jdbcConfiguration;

    @Service
    private LocalConfiguration localConfiguration;

    @Service
    private I18nMessage i18n;

    public Supplier<JdbcConfiguration> getJdbcConfiguration() {
        return jdbcConfiguration;
    }

    public JdbcConfiguration.Driver getDriver(final JdbcConnection dataStore) {
        return getDriver(dataStore.getDbType(), dataStore.getHandler());
    }

    private JdbcConfiguration.Driver getDriver(final String dbType, final String handler) {
        JdbcConfiguration.Driver driver = jdbcConfiguration.get()
                .getDrivers()
                .stream()
                .filter(this::driverNotDisabled)
                .filter(d -> d.getId().toLowerCase(Locale.ROOT).equals(dbType.toLowerCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(i18n.errorDriverNotFound(dbType)));

        // We can get a configuration with a previous selected handler which is not related to selected driver
        // So we check handler only if it is defined in the found driver
        if (driver.getHandlers().contains(handler)) {
            driver = getDriver(handler, null);
        }

        return driver;
    }

    public boolean driverNotDisabled(JdbcConfiguration.Driver driver) {
        return !ofNullable(localConfiguration.get("jdbc.driver." + driver.getId().toLowerCase(Locale.ROOT) + ".skip"))
                .map(Boolean::valueOf)
                .orElse(false);
    }

    public Platform getPlatform(final JdbcConnection connection) {
        JdbcConfiguration.Driver driver = getDriver(connection);
        final String dbType = driver.getId();

        switch (dbType.toLowerCase(Locale.ROOT)) {
        case MYSQL:
            return new MySQLPlatform(i18n, driver);
        case MARIADB:
            return new MariaDbPlatform(i18n, driver);
        case POSTGRESQL:
            return new PostgreSQLPlatform(i18n, driver);
        case REDSHIFT:
            return new RedshiftPlatform(i18n, driver);
        case SNOWFLAKE:
            return new SnowflakePlatform(i18n, driver);
        case ORACLE:
            return new OraclePlatform(i18n, driver);
        case MSSQL:
        case MSSQL_JTDS:
            return new MSSQLPlatform(i18n, driver);
        case SQLDWH:
            return new SQLDWHPlatform(i18n, driver);
        case DELTALAKE:
            return new DeltaLakePlatform(i18n, driver);
        case DERBY:
        default:
            return new DerbyPlatform(i18n, driver);
        }
    }

}
