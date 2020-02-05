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
package org.talend.components.workday.datastore;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Data;

@Data
public class Token {

    @JsonbProperty("access_token")
    private String accessToken;

    @JsonbProperty("token_type")
    private String tokenType;

    @JsonbProperty("expires_in")
    private String expiresIn;

    private Instant expireDate;

    public String getAuthorizationHeaderValue() {
        return tokenType + ' ' + accessToken;
    }
}
