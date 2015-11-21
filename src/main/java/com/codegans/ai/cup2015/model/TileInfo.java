package com.codegans.ai.cup2015.model;

import model.Direction;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 20:09
 */
public class TileInfo {
    public final int index;
    public final int x;
    public final int y;
    public final Direction in;
    public final Direction out;

    public TileInfo(int index, int x, int y, Direction in, Direction out) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.in = in;
        this.out = out;
    }
}
