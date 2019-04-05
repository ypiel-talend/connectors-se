// package org.talend.components.couchbase.testutils;
//
// import lombok.Data;
// import org.junit.jupiter.api.extension.*;
// import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.images.builder.ImageFromDockerfile;
//
// public class CouchbaseTestExtention implements ExtensionContext.Store.CloseableResource, BeforeAllCallback, ParameterResolver {
//
// private TestContext testContext = new TestContext();
//
// private static final GenericContainer COUCHBASE_CONTAINER = new GenericContainer<>("couchbase:6.0.1");
// private static boolean started = false;
//
// @Override
// public void beforeAll(ExtensionContext extensionContext) throws Exception {
//// log.info("extension before all start");
// if (!started) {
// started = true;
// COUCHBASE_CONTAINER.start();
// }
//
// String dockerHostAddress = COUCHBASE_CONTAINER.getContainerIpAddress();
// System.out.println("Container IP address: " + dockerHostAddress);
// }
//
// @Override
// public void close() throws Throwable {
// COUCHBASE_CONTAINER.close();
// }
//
// @Override
// public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
// ParameterResolutionException {
// return false;
// }
//
// @Override
// public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
// ParameterResolutionException {
// return null;
// }
//
// @Data
// public static class TestContext{
// private String bucket;
// private String password;
// private String bootstrapNodes;
// }
//
//// public static void main(String[] args) {
//// TestContext testContext = new TestContext();
//// testContext.setBucket("Student");
//// testContext.setPassword("123456");
//// testContext.setBootstrapNodes("127.0.0.1");
////
//// CouchbaseTestExtention couchbaseTestExtention = new CouchbaseTestExtention();
//// couchbaseTestExtention.beforeAll(testContext);
////
//// }
// }
