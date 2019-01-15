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
package org.talend.components.jdbc.output.platforms;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Delegate;
import org.talend.sdk.component.api.record.Schema;

import java.io.Serializable;

@Data
@Builder
class Column implements Serializable {

    @Delegate
    private final Schema.Entry entry;

    private final boolean primaryKey;

    private final Integer size;

}
