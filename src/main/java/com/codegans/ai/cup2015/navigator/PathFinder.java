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

            traverse(steps[level], endX, endY, score);

            score = steps[level][startX][startY];

            if (score == Integer.MAX_VALUE) {
                throw new IllegalStateException("Unreachable destination: #" + level + "(" + startX + ";" + startY + ")");
            }
        }
    }

    public Collection<Tile> find(int x, int y, int level, Evaluator evaluator) {
        int[][] layout = steps[level];
        Collection<Tile> path = new ArrayList<>();

        int score = layout[x][y];

        for (int i = score; i != 0; i++) {
            for (Direction direction : Direction.values()) {
                int dx = MathUtil.dx(direction);
                int dy = MathUtil.dy(direction);

                int value = layout[x + dx][y + dy];

                if (i == value) {
                    path.add(new Tile(level, x + dx, y + dy));
                    break;
                }
            }
        }

        return path;
    }

    private void traverse(int[][] layer, int x, int y, int score) {
        if (layer[x][y] < score) {
            return;
        }

        layer[x][y] = score;

        Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

        if (directions != null && !directions.isEmpty()) {
            for (Direction in : directions) {
                int dx = MathUtil.dx(in);
                int dy = MathUtil.dy(in);

                traverse(layer, x + dx, y + dy, score + 1);
            }
        }
    }
}
