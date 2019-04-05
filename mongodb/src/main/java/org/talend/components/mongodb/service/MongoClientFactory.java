package org.talend.components.mongodb.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.talend.components.mongodb.datastore.MongoDBDatastore;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MongoClientFactory {

    protected final MongoDBDatastore datastore;

    protected final MongoClientOptions clientOptions;

    public MongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
        this.datastore = datastore;
        this.clientOptions = clientOptions;
    }

    public static MongoClientFactory getInstance(MongoDBDatastore datastore, MongoClientOptions clientOptions,
            I18nMessage i18nMessage) {
        if (datastore.isAuthentication()) {
            switch (datastore.getAuthenticationMechanism()) {
            case NEGOTIATE_MEC:
                return new NegotiateAuthMongoClientBuilder(datastore, clientOptions);
            case PLAIN_MEC:
                return new PlainAuthMongoClientFactory(datastore, clientOptions);
            case SCRAMSHA1_MEC:
                return new ScramSha1AuthMongoClientBuilder(datastore, clientOptions);
            case KERBEROS_MEC:
                return new KerberosAuthMongoClientFactory(datastore, clientOptions);
            default:
                throw new IllegalArgumentException(
                        i18nMessage.authMechanismNotSupported(datastore.getAuthenticationMechanism().name()));
            }
        }
        return new DefaultMongoClientFactory(datastore, clientOptions);
    }

    protected abstract MongoClient createClient();

    protected List<ServerAddress> getServerAddresses() {
        List<ServerAddress> addressesList;
        if (datastore.isUseReplicaSetAddress()) {
            addressesList = datastore.getReplicaAddresses().stream().map(a -> new ServerAddress(a.getAddress(), a.getPort()))
                    .collect(Collectors.toList());
        } else {
            addressesList = Collections.singletonList(new ServerAddress(datastore.getServer(), datastore.getPort()));
        }
        return addressesList;
    }

    public static class DefaultMongoClientFactory extends MongoClientFactory {

        public DefaultMongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoClient createClient() {
            return new MongoClient(getServerAddresses(), clientOptions);
        }
    }

    public abstract static class AuthenticationMongoClientFactory extends MongoClientFactory {

        public AuthenticationMongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoClient createClient() {
            return new MongoClient(getServerAddresses(), getMongoCredentials(), clientOptions);
        }

        protected abstract MongoCredential getMongoCredentials();

    }

    public static class PlainAuthMongoClientFactory extends AuthenticationMongoClientFactory {

        public PlainAuthMongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoCredential getMongoCredentials() {
            return MongoCredential.createPlainCredential(datastore.getUsername(), datastore.getDatabase(),
                    datastore.getPassword().toCharArray());
        }
    }

    public static class KerberosAuthMongoClientFactory extends AuthenticationMongoClientFactory {

        public KerberosAuthMongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoCredential getMongoCredentials() {
            System.setProperty("java.security.krb5.realm", datastore.getKerberosCreds().getRealm());
            System.setProperty("java.security.krb5.kdc", datastore.getKerberosCreds().getKdcServer());
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
            return MongoCredential.createGSSAPICredential(datastore.getKerberosCreds().getUserPrincipal());
        }

    }

    protected abstract static class WithAuthDatabaseMongoClientFactory extends AuthenticationMongoClientFactory {

        public WithAuthDatabaseMongoClientFactory(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        protected String getAuthDatabase() {
            if (datastore.isSetAuthenticationDatabase()) {
                return datastore.getAuthenticationDatabase();
            }
            return datastore.getDatabase();
        }

    }

    public static class ScramSha1AuthMongoClientBuilder extends WithAuthDatabaseMongoClientFactory {

        public ScramSha1AuthMongoClientBuilder(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoCredential getMongoCredentials() {
            return MongoCredential.createScramSha1Credential(datastore.getUsername(), getAuthDatabase(),
                    datastore.getPassword().toCharArray());
        }

    }

    public static class NegotiateAuthMongoClientBuilder extends WithAuthDatabaseMongoClientFactory {

        public NegotiateAuthMongoClientBuilder(MongoDBDatastore datastore, MongoClientOptions clientOptions) {
            super(datastore, clientOptions);
        }

        @Override
        protected MongoCredential getMongoCredentials() {
            return MongoCredential.createCredential(datastore.getUsername(), getAuthDatabase(),
                    datastore.getPassword().toCharArray());
        }

    }

}
