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
package org.talend.components.docdb.service;

import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.docdb.datastore.DocDBDataStore;
import org.talend.components.mongo.Address;
import org.talend.components.mongo.Auth;
import org.talend.components.mongo.ConnectionParameter;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.connection.CreateConnection;

import java.util.ArrayList;
import java.util.List;

@Version(1)
@Slf4j
@Service
public class DocDBConnectionService {

    private static final transient Logger LOG = LoggerFactory.getLogger(DocDBConnectionService.class);

    @CreateConnection
    public MongoClient createClient(@Option("configuration") DocDBDataStore dataStore) {
        MongoCredential credential = getMongoCredential(dataStore);
        try {
            switch (dataStore.getAddressType()) {
            case STANDALONE:
                ServerAddress address =
                        new ServerAddress(dataStore.getAddress().getHost(), dataStore.getAddress().getPort());
                if (credential != null) {
                    return new MongoClient(address, credential, getOptions(dataStore));
                } else {
                    return new MongoClient(address, getOptions(dataStore));
                }
            case REPLICA_SET:
                // TODO check if it's right, not miss parameter like "replicaSet=myRepl"?
                // https://docs.mongodb.com/manual/reference/connection-string/
                if (credential != null) {
                    return new MongoClient(getServerAddresses(dataStore.getReplicaSetAddress()), credential,
                            getOptions(dataStore));
                } else {
                    return new MongoClient(getServerAddresses(dataStore.getReplicaSetAddress()), getOptions(dataStore));
                }
            }
            return null;
        } catch (Exception e) {
            // TODO use i18n
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private MongoCredential getMongoCredential(DocDBDataStore dataStore) {
        Auth auth = dataStore.getAuth();
        if (!auth.isNeedAuth()) {
            return null;
        }

        String authDatabase = auth.isUseAuthDatabase() ? auth.getAuthDatabase() : dataStore.getDatabase();
        switch (auth.getAuthMech()) {
        case NEGOTIATE:
            return MongoCredential.createCredential(auth.getUsername(), authDatabase, auth.getPassword().toCharArray());
        /*
         * case PLAIN_SASL:
         * return MongoCredential.createPlainCredential(auth.getUsername(), "$external",
         * auth.getPassword().toCharArray());
         */
        case SCRAM_SHA_1_SASL:
            return MongoCredential
                    .createScramSha1Credential(auth.getUsername(), authDatabase, auth.getPassword().toCharArray());
        }

        return null;
    }

    // https://docs.mongodb.com/manual/reference/connection-string/#connection-string-options
    public MongoClientOptions getOptions(DocDBDataStore dataStore) {
        StringBuilder uri = new StringBuilder("mongodb://noexist:27017/");// a fake uri, only work for get the options
        // from key
        // value string
        boolean first = true;
        for (ConnectionParameter parameter : dataStore.getConnectionParameter()) {
            if (first) {
                uri.append('?');
                first = false;
            }
            uri.append(parameter.getKey()).append('=').append(parameter.getValue()).append('&');
        }
        uri.deleteCharAt(uri.length() - 1);
        MongoClientURI muri = new MongoClientURI(uri.toString());

        return muri.getOptions();
    }

    private List<ServerAddress> getServerAddresses(List<Address> addresses) {
        List<ServerAddress> result = new ArrayList<>();
        for (Address address : addresses) {
            result.add(new ServerAddress(address.getHost(), address.getPort()));
        }
        return result;
    }
}
