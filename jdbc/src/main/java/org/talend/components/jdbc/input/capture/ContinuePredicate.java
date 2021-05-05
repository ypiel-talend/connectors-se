/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.input.capture;

import java.sql.ResultSet;
import java.time.Clock;
import java.util.function.LongSupplier;


public class ContinuePredicate {

    private long counter = 0;

    private long startTime;

    private final LongSupplier timeGetter;

    public ContinuePredicate(LongSupplier timeGetter) {
        this.timeGetter = timeGetter;
        this.startTime = this.timeGetter.getAsLong();
    }

    public ContinuePredicate() {
        this(Clock.systemDefaultZone()::millis);
    }

    public void onNext(final boolean hasNext) {
        if (hasNext) {
            this.counter++;
        }
    }

    public ResultSet wrap(final ResultSet resultSet) {
        this.restart();
        return new ResultSetWrapper(resultSet, this::onNext);
    }

    private void restart() {
        this.counter = 0;
        this.startTime = this.timeGetter.getAsLong();
    }

    public boolean doContinue() {
        if (this.counter > 0) {
            return true;
        }
        // continue only if no result were found less than 2 seconds
        return this.timeGetter.getAsLong() > this.startTime + 2000L;
    }
}
