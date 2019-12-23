package api.types;


import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class ClassTypeTest {

    @Test
    public void testBuilder() {
        final ClassType hello = ClassType.builder().name("hello").build();
        Assertions.assertNotNull(hello);
        Assertions.assertEquals(hello.getJavaName(Collections.emptyList()), "Hello");

        final EnumType myEnum = EnumType.builder().name("hello.world").build();
        Assertions.assertEquals(myEnum.getJavaName(Collections.emptyList()), "HelloWorld");

    }



}