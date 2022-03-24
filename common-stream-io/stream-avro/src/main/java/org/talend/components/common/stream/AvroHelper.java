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
package org.talend.components.common.stream;

import java.util.List;
import org.apache.avro.Schema;
import static java.util.stream.Collectors.toList;
import static org.talend.components.common.stream.input.avro.Constants.AVRO_LOGICAL_TYPE;

public class AvroHelper {

    private AvroHelper() {
    }

    public static org.apache.avro.Schema getUnionSchema(org.apache.avro.Schema inputSchema) {
        org.apache.avro.Schema elementType;
        if (inputSchema.getType() == org.apache.avro.Schema.Type.UNION) {
            List<Schema> extractedSchemas = inputSchema
                    .getTypes()
                    .stream()
                    .filter(schema -> schema.getType() != org.apache.avro.Schema.Type.NULL)
                    .collect(toList());
            // should have only one schema element with nullable (UNION)
            elementType = extractedSchemas.get(0);
        } else {
            elementType = inputSchema;
        }
        return elementType;
    }

    public static org.apache.avro.Schema.Type getFieldType(org.apache.avro.Schema.Field field) {
        return getUnionSchema(field.schema()).getType();
    }

    public static String getLogicalType(Schema.Field field) {
        return getUnionSchema(field.schema()).getProp(AVRO_LOGICAL_TYPE);
    }
}
