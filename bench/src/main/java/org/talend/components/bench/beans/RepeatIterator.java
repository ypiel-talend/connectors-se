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
package org.talend.components.bench.beans;

import java.util.Iterator;

public class RepeatIterator implements Iterator<Object> {

    private final long size;

    private long current = 0;

    private final Object object;

    public RepeatIterator(long size, Object object) {
        this.size = size;
        this.object = object;
    }

    @Override
    public boolean hasNext() {
        return this.current < this.size;
    }

    @Override
    public Object next() {
        if (!this.hasNext()) {
            return null;
        }
        this.current++;
        return this.object;
    }
}
