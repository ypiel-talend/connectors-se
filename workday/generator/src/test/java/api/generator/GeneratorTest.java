package api.generator;



import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class GeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        final URL input = Thread.currentThread().getContextClassLoader().getResource("./swaggers/" );
        final URL output = Thread.currentThread().getContextClassLoader().getResource("./all/" );

        File inputRep = new File(input.getPath());
        File outputRep = new File(output.getPath());
        Generator gen = new Generator(inputRep, outputRep);
        gen.generate();
    }
}