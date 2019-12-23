package api.types;

import java.util.function.Function;

public class  TUple<U, V> {

    private final U u;

    private final V v;

    public TUple(U u, V v) {
        this.u = u;
        this.v = v;
    }

    public <U1> TUple<U1, V> firstMap(Function<U, U1> f) {
        if (u == null) {
            return new TUple<>(null, v);
        }
        return new TUple<>(f.apply(u), v);
    }

    public <V1> TUple<U, V1> secondMap(Function<V, V1> f) {
        if (v == null) {
            return new TUple<>(u, null);
        }
        return new TUple<>(u, f.apply(v));
    }

    public static <A, B> TUple<A, B> of(A a, B b) {
        return new TUple<>(a, b);
    }

    public U getU() {
        return u;
    }

    public V getV() {
        return v;
    }
}