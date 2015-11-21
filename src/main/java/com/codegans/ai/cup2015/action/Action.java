package com.codegans.ai.cup2015.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:16
 */
public interface Action<A extends Action<A>> extends Comparable<A> {
    int score();

    void apply(Move move);
}
