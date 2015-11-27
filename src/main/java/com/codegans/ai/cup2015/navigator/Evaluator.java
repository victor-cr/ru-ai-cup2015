package com.codegans.ai.cup2015.navigator;

import model.Direction;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 26/11/2015 19:32
 */
public interface Evaluator {
    int apply(int x, int y, Direction in, Direction out);
}
