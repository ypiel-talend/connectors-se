package org.talend.components.common.service.http.common;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteArrayBuilderTest {

    @Test
    void byteArrayTest() {
        ByteArrayBuilder array = new ByteArrayBuilder(20);
        array.charset(StandardCharsets.UTF_8);
        byte[] bytes = array.toByteArray();
        Assertions.assertEquals(0, bytes.length);

        array.append("Hello");
        Assertions.assertEquals("Hello", array.toString());
        array.reset();
        Assertions.assertEquals("", array.toString());
    }

}