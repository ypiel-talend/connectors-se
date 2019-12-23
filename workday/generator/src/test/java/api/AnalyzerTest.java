package api;

import api.generator.FileGenerator;
import api.services.Services;
import api.types.Classes;
import api.types.GeneralType;
import io.swagger.models.Swagger;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnalyzerTest {

    @Test
    public void testAn() throws IOException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("swaggers/human-resource-management-swagger.json");

        final SwaggerLoader loader = new SwaggerLoader(resource.getPath());
        final Swagger swagger = loader.getSwagger();

        Analyzer analyzer = new Analyzer(swagger, "human-resource-management-swagger");
        Classes cl = analyzer.getClasses();

        Assertions.assertNotNull(cl);
        final GeneralType transactionStatus = cl.get("transactionStatus_ccabdb57917b100015f1c48ca6900026");
        Assertions.assertNotNull(transactionStatus);

        final GeneralType timeOffRequest = cl.get("/timeOffRequest");
        Assertions.assertNotNull(timeOffRequest);



        final URL urlRepo = Thread.currentThread().getContextClassLoader().getResource("./generate");
        File f = new File(urlRepo.getPath(), "HumanResource.java");
        File pfile = new File(urlRepo.getPath(), "Messages.properties");
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        FileGenerator gen = new FileGenerator(cl, analyzer.getServices(), "HumanResource");
        gen.generate("comment", f, pfile);

    }

    @Test
    public void testConv() throws IOException {
        generate("conversation-services-swagger.json", "ConversationService");
        generate("customer-accounts-swagger.json", "CustomerAccount");



    }


    private void generate(String fileName, String className) throws IOException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("swaggers/" + fileName);

        final SwaggerLoader loader = new SwaggerLoader(resource.getPath());
        final Swagger swagger = loader.getSwagger();

        Analyzer analyzer = new Analyzer(swagger, fileName);
        Classes cl = analyzer.getClasses();
        Services services = analyzer.getServices();

        Assertions.assertNotNull(cl);



        final URL urlRepo = Thread.currentThread().getContextClassLoader().getResource("./generate");
        File f = new File(urlRepo.getPath(), className + ".java");

        File pfile = new File(urlRepo.getPath(), "Messages.properties");
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        FileGenerator gen = new FileGenerator(cl, services, className);
        gen.generate("comment", f, pfile);
    }
}