package com.codegans.ai.cup2015.navigator;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import model.Car;
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
public class PathFinder {
    private static final int TURN0_COST = 1;
    private static final int TURN90_COST = 2;
    private static final int TURN180_COST = TURN90_COST * 5;

    private final Logger log = LoggerFactory.getLogger();
    private final TileType[][] field;
    private final int[][] waypoints;
    private final int[][][][] steps;
    private final Direction startingDirection;
    private final int levels;
    private final int width;
    private final int height;
    private final int directions;

    public PathFinder(World world) {
        field = world.getTilesXY();
        waypoints = world.getWaypoints();
        width = world.getWidth();
        height = world.getHeight();
        directions = Direction.values().length;
        startingDirection = world.getStartingDirection();

        levels = waypoints.length;

        steps = new int[levels][width][height][directions];

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

            Arrays.fill(steps[level][startX][startY], 0);

            for (Direction in : Direction.values()) {
                traverse(steps[level], startX, startY, 0, in);
            }
        }

        return steps;
    }

    public int nextPoint(Car car) {
        int targetIndex = car.getNextWaypointIndex();


    }

    private void traverse(int[][][] layer, int x, int y, int score, Direction out) {
        Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

        if (directions != null && !directions.isEmpty()) {
            for (Direction in : directions) {
                Direction localIn = MathUtil.opposite(in);

                int i = localIn.ordinal();
                int dx = x + MathUtil.dx(in);
                int dy = y + MathUtil.dy(in);
                int ds = score + rate(localIn, out);

                if (layer[dx][dy][i] > ds) {
                    layer[dx][dy][i] = ds;

                    traverse(layer, dx, dy, ds, localIn);
                }
            }
        }
    }

    private static int rate(Direction in, Direction out) {
        if (in == out
                ) {
            return TURN0_COST;
        } else if (in == MathUtil.opposite(out)) {
            return TURN180_COST;
        } else {
            return TURN90_COST;
        }
    }
}
