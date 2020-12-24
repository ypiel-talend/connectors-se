package org.talend.components.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Lazy {
    public static <T> Supplier<T> synchronizedLazy(final Supplier<T> decorated) {
        final AtomicReference<Optional<T>> value = new AtomicReference<>();

        return () -> {
            Optional<T> t = value.get();
            if (t == null || !t.isPresent()) {
                synchronized (decorated) {
                    t = value.get();
                    if (t == null || !t.isPresent()) {
                        t = Optional.ofNullable(decorated.get()); // optional to cache null case
                        value.set(t);
                    }
                }
            }
            return t.orElse(null);
        };
    }

    public static <T> Supplier<T> lazy(final Supplier<T> decorated) {
        final AtomicReference<Optional<T>> value = new AtomicReference<>();

        return () -> {
            Optional<T> t = value.get();
            if (t == null || !t.isPresent()) {
                t = Optional.ofNullable(decorated.get()); // optional to cache null case
                value.set(t);
            }
            return t.orElse(null);
        };
    }
}
