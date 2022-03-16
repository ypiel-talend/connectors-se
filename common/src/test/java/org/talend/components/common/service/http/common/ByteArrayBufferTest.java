package org.talend.components.common.service.http.common;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteArrayBufferTest {

    @Test
    void append() {
        final ByteArrayBuffer buffer = new ByteArrayBuffer(10);
        Assertions.assertEquals(0, buffer.length());
        Assertions.assertEquals(10, buffer.buffer().length);

        buffer.append("Hello".getBytes(StandardCharsets.UTF_8), 0, 5);

        Assertions.assertArrayEquals("Hello\0\0\0\0\0".getBytes(StandardCharsets.UTF_8), buffer.buffer());
        buffer.append("World".getBytes(StandardCharsets.UTF_8), 0, 5);
        Assertions.assertArrayEquals("HelloWorld".getBytes(StandardCharsets.UTF_8), buffer.buffer());

        buffer.append(65);

        Assertions.assertEquals('A', buffer.buffer()[10]);
    }
}