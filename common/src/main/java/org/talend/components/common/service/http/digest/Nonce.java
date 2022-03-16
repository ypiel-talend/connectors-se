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
package org.talend.components.common.service.http.digest;

import java.security.SecureRandom;

import lombok.Getter;

public class Nonce {

    private final SecureRandom rnd = new SecureRandom();

    private String lastNonce;

    private long nounceCount;

    @Getter
    private String cnonce;

    public String renew(final String nonce) {
        if (nonce.equals(this.lastNonce)) {
            nounceCount++;
        } else {
            nounceCount = 1;
            cnonce = null;
            lastNonce = nonce;
        }

        final String nc = String.format("%08x", nounceCount);

        if (cnonce == null) {
            cnonce = createCnonce();
        }
        return nc;
    }

    /**
     * Creates a random cnonce value based on the current time.
     *
     * @return The cnonce value as String.
     */
    private String createCnonce() {
        return String.format("%016x", rnd.nextLong());
    }

}
