package com.codegans.ai.cup2015.navigator;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Tile;
import model.Direction;
import model.TileType;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private final int[][][] steps;
    private final Direction startingDirection;
    private final int levels;
    private final int width;
    private final int height;

    public PathFinder(World world) {
        field = world.getTilesXY();
        waypoints = world.getWaypoints();
        width = world.getWidth();
        height = world.getHeight();
        startingDirection = world.getStartingDirection();

        levels = waypoints.length;

        steps = new int[levels][width][height];

        for (int[][] i : steps) {
            for (int[] j : i) {
                Arrays.fill(j, Integer.MAX_VALUE);
            }
        }

        int score = 0;

        for (int i = 0; i < waypoints.length; i++) {
            int level = (i + 1) % waypoints.length;

            int startX = waypoints[i][0];
            int startY = waypoints[i][1];
            int endX = waypoints[level][0];
            int endY = waypoints[level][1];

            for (Direction in : Direction.values()) {
                traverse(steps[level], endX, endY, score, in, (a, b, c, d) -> 1);
            }

            score = steps[level][startX][startY];

            if (score == Integer.MAX_VALUE) {
                throw new IllegalStateException("Unreachable destination: #" + level + "(" + startX + ";" + startY + ")");
            }
        }
    }

    public Collection<Tile> find(int x, int y, int level, Evaluator evaluator) {
        int[][] layer = new int[width][height];
        Collection<Tile> path = new ArrayList<>();

        int targetX = waypoints[level][0];
        int targetY = waypoints[level][1];

        for (Direction in : Direction.values()) {
            traverse(layer, targetX, targetY, 0, in, (xx, yy, i, o) -> evaluator.apply(xx, yy, MathUtil.opposite(i), MathUtil.opposite(o)));
        }

        int score = layer[targetX][targetY];

        if (score == Integer.MAX_VALUE) {
            throw new IllegalStateException("Unreachable destination: #" + level + "(" + targetX + ";" + targetY + ") from (" + x + ";" + y + ")");
        }

        for (int xx = x, yy = y; xx != targetX && yy != targetY; ) {
            Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

            if (directions != null) {
                for (Direction direction : directions) {

                }
            }
        }

        return path;
    }

    private void traverse(int[][] layer, int x, int y, int score, Direction in, Evaluator evaluator) {
        if (layer[x][y] < score) {
            return;
        }

        layer[x][y] = score;

        Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

        if (directions != null && !directions.isEmpty()) {
            for (Direction out : directions) {
                int xx = x + MathUtil.dx(out);
                int yy = y + MathUtil.dy(out);

                int newScore = score + evaluator.apply(xx, yy, in, out);

                traverse(layer, xx, yy, newScore, out, evaluator);
            }
        }
    }
}
