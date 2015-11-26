package com.codegans.ai.cup2015.navigator;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import model.Direction;
import model.TileType;
import model.World;

import java.util.Arrays;
import java.util.Set;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 25.11.2015 16:44
 */
public class PathBuilder {
    private final Logger log = LoggerFactory.getLogger();
    private final TileType[][] field;
    private final int[][] waypoints;
    private final int[][][][] steps;
    private final Direction startingDirection;

    public PathBuilder(World world) {
        this.field = world.getTilesXY();
        this.waypoints = world.getWaypoints();
        this.steps = new int[waypoints.length][world.getWidth()][world.getHeight()][Direction.values().length];
        this.startingDirection = world.getStartingDirection();

        for (int[][][] i : steps) {
            for (int[][] j : i) {
                for (int[] k : j) {
                    Arrays.fill(k, Integer.MAX_VALUE);
                }
            }
        }
    }

    public void build() {
        Direction direction = startingDirection;

        for (int i = 0; i < waypoints.length; i++) {
            int level = (i + 1) % waypoints.length;

            int startX = waypoints[i][0];
            int startY = waypoints[i][1];
            int endX = waypoints[level][0];
            int endY = waypoints[level][1];

            Arrays.fill(steps[level][endX][endY], 0);

            traverse(endX, endY, level, 0, null);
        }
    }

    private void traverse(int x, int y, int level, int score, Direction in) {
        Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

        if (directions != null && !directions.isEmpty()) {
            for (Direction out : directions) {
                int i = out.ordinal();
                int dx = x + MathUtil.dx(out);
                int dy = y + MathUtil.dy(out);
                int ds = score + rate(in, out);

                if (steps[level][dx][dy][i] > ds) {
                    traverse(dx, dy, endX, endY, level, ds, out);
                }
            }
        }
    }

    private static int rate(Direction in, Direction out) {
        if (in == out || in == null || out == null) {
            return 1;
        } else if (in == MathUtil.opposite(out)) {
            return 10;
        } else {
            return 2;
        }
    }
}
