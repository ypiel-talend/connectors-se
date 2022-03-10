package org.talend.components.common.service.http.common;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BasicHeaderElementTest {

    @Test
    void headerTest() {
        BasicNameValuePair p1 = new BasicNameValuePair("p1Name", "p1Value");
        BasicNameValuePair p2 = new BasicNameValuePair("p2Name", "p2Value");
        final BasicHeaderElement element = new BasicHeaderElement("thename",
                "thevalue",
                new BasicNameValuePair[] {p1, p2});
        final BasicHeaderElement clone = element.clone();
        Assertions.assertEquals(element, clone);
        final BasicHeaderElement element2 = new BasicHeaderElement("thename",
                "thevalue",
                new BasicNameValuePair[] {p1});
        Assertions.assertNotEquals(element2, element);

        Assertions.assertEquals(2, element.getParameterCount());
        Assertions.assertSame(p2, element.getParameterByName("p2Name"));

        final String param = element.toString();
        Assertions.assertEquals("thename=thevalue; p1Name=p1Value; p2Name=p2Value", param);
    }
}