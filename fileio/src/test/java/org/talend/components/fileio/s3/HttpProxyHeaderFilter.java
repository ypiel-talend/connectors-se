package org.talend.components.fileio.s3;

import java.util.Locale;
import java.util.function.Predicate;

public class HttpProxyHeaderFilter implements Predicate<String> {

    @Override
    public boolean test(final String s) {
        final String header = s.toLowerCase(Locale.ROOT);
        return "authorization".equals(header) || "user-agent".equals(header) || "connection".equals(header)
                || "date".equals(header) || "range".equals(header);
    }
}
