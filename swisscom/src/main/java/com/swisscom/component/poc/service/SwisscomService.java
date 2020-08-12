/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package com.swisscom.component.poc.service;

import com.swisscom.component.poc.config.Connection;
import com.swisscom.component.poc.config.Dataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SwisscomService {

    public final static String HEALTHCHECK = "HEALTHCHECK";

    @HealthCheck(HEALTHCHECK)
    public HealthCheckStatus healthCheck(@Option final Connection connection) {
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Health check ok");
    }

    @DiscoverSchema("discover")
    public Schema discover(@Option("dataSet") final Dataset dataSet) {
        Record record = null;
        return record.getSchema();
    }

}
