package com.codegans.ai.cup2015.navigator;

import com.codegans.ai.cup2015.model.Tile;
import model.Direction;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 26/11/2015 19:32
 */
public interface Evaluator {
    int apply(Tile tile, Direction in, Direction out);
}
