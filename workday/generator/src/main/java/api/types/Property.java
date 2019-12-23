package api.types;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Property {

    private final String name;

    private final String value;

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Property convert(ClassType cl, FieldType f) {
        final String name = cl.getJavaName(Collections.emptyList()) + "." + f.getName();
        return new Property(name, f.getDoc());
    }

    public static Stream<Property> from(ClassType cl) {
        Property propClass = new Property(
                cl.getJavaName(Collections.emptyList()),
                cl.getDoc());
        return Stream.concat( Stream.of(propClass),
                cl.fields().map(f -> Property.convert(cl, f)) );
    }

    public static void write(File target, List<Property> props) {
        try {
            Files.write(target.toPath(),
                    props.stream().map(Property::toLine).collect(Collectors.toList()),
                    Charset.defaultCharset(),
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void write(File target) {
        try {
            Files.write(target.toPath(),
                    Collections.singleton(this.toLine()),
                    Charset.defaultCharset(),
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String toLine() {
        return name + "._displayName=" + toPropValue(value);
    }

    private String toPropValue(String brut) {
        if (brut != null) {
            int pos = brut.indexOf(System.lineSeparator());
            if (pos >= 0) {
                return brut.substring(0, pos - 1);
            }
        }
        return brut;
    }


}
