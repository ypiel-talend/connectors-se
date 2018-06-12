package org.talend.components.processing.normalize;

import static java.util.function.Function.identity;
import static org.talend.sdk.component.api.component.Icon.IconType.NORMALIZE;

import java.io.Serializable;
import java.util.function.Function;

import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;

// todo: revise this logic, this would unlikely match the expected one
@Version
@Processor(name = "Normalize")
@Icon(NORMALIZE)
@Documentation("This component normalizes a column.")
public class Normalize implements Serializable {

    private final NormalizeConfiguration configuration;

    private final JsonBuilderFactory factory;

    private final JsonProvider provider;

    private transient JsonPointer pointer;

    private transient Function<String, String> normalizers;

    public Normalize(@Option("configuration") final NormalizeConfiguration configuration, final JsonBuilderFactory factory,
            final JsonProvider provider) {
        this.configuration = configuration;
        this.factory = factory;
        this.provider = provider;
    }

    @ElementListener
    public JsonObject normalize(final JsonObject element) {
        ensureInit();

        try {
            final JsonValue jsonValue = pointer.getValue(element);
            if (JsonValue.NULL.equals(jsonValue)) {
                return element;
            }
            return pointer.replace(element, doNormalize(jsonValue));
        } catch (final JsonException je) {
            return element; // no value to normalize
        }
    }

    private void ensureInit() {
        if (pointer == null) {
            pointer = provider.createPointer(configuration.getColumnToNormalize());

            normalizers = identity();
            if (configuration.isDiscardTrailingEmptyStr()) {
                normalizers = normalizers.andThen(src -> src.replaceAll("\\s+$", ""));
            }
            if (configuration.isTrim()) {
                normalizers = normalizers.andThen(String::trim);
            }
        }
    }

    private JsonValue doNormalize(final JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
        case STRING:
            return provider.createValue(normalizers.apply(JsonString.class.cast(jsonValue).getString()));
        default:
            return jsonValue;
        }
    }
}
