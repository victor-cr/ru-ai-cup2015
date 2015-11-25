package com.codegans.ai.cup2015.model;

import model.Direction;

import java.util.Arrays;
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
    public final double marginSize;
    public final double tileSize;
    public final Direction in;
    public final Direction out;
    public final Collection<Line> walls;
    public final Collection<Marker> markers;

    public TileInfo(int index, int x, int y, double marginSize, double tileSize, Direction in, Direction out, Collection<Line> walls, Collection<Marker> markers) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.marginSize = marginSize;
        this.tileSize = tileSize;
        this.in = in;
        this.out = out;
        this.walls = walls;
        this.markers = markers;
    }

    public Collection<Column> getColumns() {
        return Arrays.asList(
                new Column(x * tileSize, y * tileSize, marginSize),
                new Column((x + 1) * tileSize, y * tileSize, marginSize),
                new Column(x * tileSize, (y + 1) * tileSize, marginSize),
                new Column((x + 1) * tileSize, (y + 1) * tileSize, marginSize)
        );
    }
}
