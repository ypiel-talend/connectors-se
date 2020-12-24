package org.talend.components.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LazyTest {

    @Test
    void synchronizedLazy() {
        final AtomicInteger ai = new AtomicInteger(0);

        final Supplier<Integer> lazy = Lazy.synchronizedLazy(() -> ai.incrementAndGet());
        Assertions.assertEquals(1, lazy.get());
        Assertions.assertEquals(1, lazy.get());
    }

    @Test
    void lazy() {
        final AtomicInteger ai = new AtomicInteger(0);

        final Supplier<Integer> lazy = Lazy.lazy(() -> ai.incrementAndGet());
        Assertions.assertEquals(1, lazy.get());
        Assertions.assertEquals(1, lazy.get());
    }
}