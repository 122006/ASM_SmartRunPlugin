package com.by122006.asm_smartrunpluginimp.line;

import android.util.Pair;

import java.util.Objects;

/**
 * Created by zwh on 2020/4/23.
 */

public class LinePair<F, S> {
    public final F first;
    public final S second;

    /**
     * Constructor for a Pair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public LinePair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    /**
     * Convenience method for creating an appropriately typed pair.
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <A, B> LinePair <A, B> create(A a, B b) {
        return new LinePair<A, B>(a, b);
    }
}
