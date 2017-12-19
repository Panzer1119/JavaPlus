package de.codemakers.util;

import java.util.function.Supplier;

/**
 * Returner
 *
 * @author Paul Hagedorn
 */
public class Returner<T> {

    private T value;

    public Returner(T value) {
        this.value = value;
    }

    public final T getValue() {
        return value;
    }

    public final Returner<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public final T or(T other) {
        return (value != null) ? value : other;
    }

    public final T or(Supplier<T> supplier) {
        return (value != null || supplier == null) ? value : supplier.get();
    }

    public static final Returner<Integer> of(Integer value) {
        return new Returner<>(value);
    }

    public static final Returner<Float> of(Float value) {
        return new Returner<>(value);
    }

    public static final Returner<Boolean> of(Boolean value) {
        return new Returner<>(value);
    }

    public static final Returner<Object> of(Object value) {
        return new Returner<>(value);
    }

    public static final Returner<String> of(String value) {
        return new Returner<>(value);
    }

    public static final Returner<Short> of(Short value) {
        return new Returner<>(value);
    }

    public static final Returner<Long> of(Long value) {
        return new Returner<>(value);
    }

    public static final Returner<Character> of(Character value) {
        return new Returner<>(value);
    }

    public static final Returner<Byte> of(Byte value) {
        return new Returner<>(value);
    }

    public static final Returner<Double> of(Double value) {
        return new Returner<>(value);
    }

}
