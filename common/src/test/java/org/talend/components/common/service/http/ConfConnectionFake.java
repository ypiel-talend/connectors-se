package org.talend.components.common.service.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.sdk.component.api.service.http.Configurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfConnectionFake implements Configurer.Connection {

    private final String method;

    private final String url;

    private final String payload;

    private final Map<String, List<String>> headers = new HashMap<>();

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public byte[] getPayload() {
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Configurer.Connection withHeader(String name, String value) {
        this.headers.computeIfAbsent(name, (String key) -> new ArrayList<>()).add(value);
        return this;
    }

    @Override
    public Configurer.Connection withReadTimeout(int timeout) {
        return this;
    }

    @Override
    public Configurer.Connection withConnectionTimeout(int timeout) {
        return this;
    }

    @Override
    public Configurer.Connection withoutFollowRedirects() {
        return this;
    }
}
