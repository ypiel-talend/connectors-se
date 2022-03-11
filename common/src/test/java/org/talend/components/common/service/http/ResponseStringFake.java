package org.talend.components.common.service.http;

import java.util.List;
import java.util.Map;

import org.talend.sdk.component.api.service.http.Response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseStringFake implements Response<String> {

    private final int status;

    private final Map<String, List<String>> headers;

    private final String body;

    @Override
    public int status() {
        return this.status;
    }

    @Override
    public Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public <E> E error(Class<E> type) {
        return null;
    }
}
