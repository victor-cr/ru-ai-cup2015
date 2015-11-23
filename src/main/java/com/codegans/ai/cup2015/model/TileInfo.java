package com.codegans.ai.cup2015.model;

import model.Direction;

import java.util.Collection;

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
    public final Collection<Line> walls;

    public TileInfo(int index, int x, int y, Direction in, Direction out, Collection<Line> walls) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.in = in;
        this.out = out;
        this.walls = walls;
    }
}
