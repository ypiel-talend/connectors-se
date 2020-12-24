package bench;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.talend.components.common.stream.input.json.JsonToRecord;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonToRecordBench {

    private JsonToRecord toRecord;

    private void start(final boolean forceDouble) {
        final RecordBuilderFactory recordBuilderFactory = new RecordBuilderFactoryImpl("test");
        this.toRecord = new JsonToRecord(recordBuilderFactory, forceDouble);
    }


    private JsonObject buildJsonWithoutLongArray() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (int s = 1; s <= 10; s++) {
            builder.add("string_" + s, "val_" + s);
        }

        for (int i = 0; i <= 10; i++) {
            builder.add("int_" + i, i * 10);
        }

        final JsonArrayBuilder stringArrayBuilder = Json.createArrayBuilder();
        IntStream.range(1, 11).forEach(i -> stringArrayBuilder.add("element_" + i));
        builder.add("a_string_array", stringArrayBuilder.build());

        final JsonObjectBuilder main = Json.createObjectBuilder();
        main.add("objA", builder.build());
        main.add("objB", builder.build());
        main.add("objC", builder.build());
        for (int i = 0; i <= 10; i++) {
            main.add("bool_" + i, i % 2 == 0);
        }

        final JsonArrayBuilder objArrayBuilder = Json.createArrayBuilder();
        IntStream.range(1, 5).forEach(i -> objArrayBuilder.add(builder.build()));
        main.add("a_obj_array", objArrayBuilder.build());

        return main.build();
    }

    @Test
    @Disabled
    void benchComplexJson2() {
        start(true);
        String file = "complex.json";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            String source = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            final JsonObject json = getJsonObject(source);
            _bench(json, 1, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    void benchComplexJson() {
        start(true);
        String file = "corona_countries.json";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            String source = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            final JsonObject json = getJsonObject(source);
            _bench(json, 10, 1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    void benchJsonMediumComplexity() {
        start(true);
        final JsonObject json = buildJsonWithoutLongArray();
        _bench(json, 10, 1000);
    }


    private void _bench(final JsonObject json, final int mx_j, final int mx_i) {
        long total = 0;
        for (int j = 1; j <= mx_j; j++) {
            long start = System.currentTimeMillis();
            for (int i = 1; i <= mx_i; i++) {
                final Record record = toRecord.toRecord(json);
            }
            long end = System.currentTimeMillis();
            final long delta = end - start;
            System.out.printf("%d\t : duration for %d json objects :\t%d\n", j, mx_i, delta);
            total += delta;
        }

        System.out.printf("Total : %d\n", total);
    }

    private JsonObject getJsonObject(String content) {
        try (JsonReader reader = Json.createReader(new StringReader(content))) {
            return reader.readObject();
        }
    }

}
