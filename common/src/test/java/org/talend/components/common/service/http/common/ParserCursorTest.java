package org.talend.components.common.service.http.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParserCursorTest {

    @Test
    void parseTo() {
        final String ch = "hi there";
        final ParserCursor cursor = new ParserCursor(0, 10);
        final String result = cursor.parseTo((Character x) -> x == ' ', ch::charAt);
        Assertions.assertEquals("hi", result);
    }

    @Test
    void updatePos() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> new ParserCursor(-5, 10));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> new ParserCursor(10, 5));


        final ParserCursor cursor = new ParserCursor(5, 10);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> cursor.updatePos(3));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> cursor.updatePos(11));
        cursor.updatePos(7);
        Assertions.assertEquals(7,  cursor.getPos());
        Assertions.assertFalse(cursor.atEnd());

        cursor.updatePos(10);
        Assertions.assertEquals(10,  cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());
    }


    @Test
    void testToString() {
        final ParserCursor cursor = new ParserCursor(5, 10);
        cursor.increment();
        Assertions.assertEquals("[5>6>10]", cursor.toString());
    }
}