package org.talend.components.mongodb.output;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MongoDocumentWrapperTest {

    @Test
    public void testPutValues() {
        MongoDocumentWrapper wrapper = new MongoDocumentWrapper();

        Document doc = Document.parse("{\"a\":1, \"b\":{\"c\":\"ValueC\", \"d\":{\"e\":\"ValueE\"}}}");

        wrapper.put(null, "a", 1);
        wrapper.put(new OutputMapping("c", "b", false), "c", "ValueC");
        wrapper.put(new OutputMapping("e", "b.d", false), "e", "ValueE");

        assertEquals(doc, wrapper.getObject(), "MongoDocumentWrapper returns wrong document");
    }

    @Test
    public void testPutKeyValues() {
        MongoDocumentWrapper wrapper = new MongoDocumentWrapper();

        Document doc = Document.parse("{\"a\":1, \"b.c\":\"ValueC\", \"b.d.e\":\"ValueE\"}");

        wrapper.putkeyNode(null, "a", 1);
        wrapper.putkeyNode(new OutputMapping("c", "b", false), "c", "ValueC");
        wrapper.putkeyNode(new OutputMapping("e", "b.d", false), "e", "ValueE");

        assertEquals(doc, wrapper.getObject(), "MongoDocumentWrapper returns wrong key values document");
    }

}
