package de.codemakers.util;

import java.util.function.Function;

/**
 * MultiFunction
 *
 * @author Paul Hagedorn
 */
public interface MultiFunction<T, R> extends Function<T, R> {

    R apply(T... t);

}
