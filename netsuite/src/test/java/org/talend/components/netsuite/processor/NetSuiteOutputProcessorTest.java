package org.talend.components.netsuite.processor;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.netsuite.dataset.NetsuiteOutputDataSet;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.output.Processor;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class NetsuiteOutputProcessorTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("org.talend.components.netsuite");

    @Test
    @Ignore("You need to complete this test")
    public void map() throws IOException {

        // Processor configuration
        // Setup your component configuration for the test here
        final NetsuiteOutputDataSet configuration = new NetsuiteOutputDataSet()
        /*
         * .setRole()
         * .setAccount()
         * .setBatchSize()
         * .setAction()
         * .setEmail()
         * .setEndpoint()
         * .setRecordType()
         * .setEnableCustomization()
         * .setApplicationId()
         * .setPassword()
         */;

        // We create the component processor instance using the configuration filled above
        final Processor processor = COMPONENT_FACTORY.createProcessor(NetsuiteOutputProcessor.class, configuration);

        // The join input factory construct inputs test data for every input branch you have defined for this component
        // Make sure to fil in some test data for the branches you want to test
        // You can also remove the branches that you don't need from the factory below
        final JoinInputFactory joinInputFactory = new JoinInputFactory().withInput("__default__",
                asList(/* TODO - list of your input data for this branch. Instances of JsonObject.class */));

        // Run the flow and get the outputs
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);

        // TODO - Test Asserts
        assertEquals(2, outputs.size()); // test of the output branches count of the component

        // Here you have all your processor output branches
        // You can fill in the expected data for every branch to test them
        final List<JsonObject> value_REJECT = outputs.get(JsonObject.class, "REJECT");
        assertEquals(asList(/* TODO - give a list of your expected values here. Instances of JsonObject.class */), value_REJECT);

        final List<JsonObject> value___default__ = outputs.get(JsonObject.class, "__default__");
        assertEquals(asList(/* TODO - give a list of your expected values here. Instances of JsonObject.class */),
                value___default__);

    }

}