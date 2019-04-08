package org.talend.components.mongodb.source;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.mongodb.utils.MongoDBTestExtension;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.input.Mapper;

@WithComponents("org.talend.components.mongodb")
@ExtendWith(MongoDBTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDBInputMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("org.talend.components.mongodb");

    @Test
    @Ignore("You need to complete this test")
    public void produce() throws IOException {

        // Source configuration
        // Setup your component configuration for the test here
        final MongoDBInputMapperConfiguration configuration = new MongoDBInputMapperConfiguration()
        /* .setDataset() */;

        // We create the component mapper instance using the configuration filled above
        final Mapper mapper = COMPONENT_FACTORY.createMapper(MongoDBInputMapper.class, configuration);

        // Collect the source as a list
        assertEquals(asList(/* TODO - give the expected data */), COMPONENT_FACTORY.collectAsList(Record.class, mapper));
    }

}