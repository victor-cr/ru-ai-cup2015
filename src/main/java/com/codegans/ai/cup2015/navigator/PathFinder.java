package com.codegans.ai.cup2015.navigator;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Tile;
import model.Direction;
import model.TileType;
import model.World;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

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
    }

    public Collection<Tile> find(int x, int y, int level, double angle, Evaluator evaluator) {
        int targetX = waypoints[level][0];
        int targetY = waypoints[level][1];
        Direction origin = rate(angle);
        Node[][] layer = new Node[width][height];

        layer[x][y] = new Node(null, 0);

        for (Direction in : Direction.values()) {
            int score = evaluator.apply(x, y, origin, in);

            traverse(layer, targetX, targetY, score, in, evaluator);
        }

        Node last = layer[targetX][targetY];

        if (last == null) {
            throw new IllegalStateException("Unreachable destination: #" + level + "(" + targetX + ";" + targetY + ") from (" + x + ";" + y + ")");
        }

        Deque<Tile> result = new LinkedList<>();

        while (targetX != x || targetY != y) {
            Node node = layer[targetX][targetY];

            result.addFirst(new Tile(level, targetX, targetY));

            Direction out = MathUtil.opposite(node.in);

            targetX += MathUtil.dx(out);
            targetY += MathUtil.dy(out);
        }

        return result;
    }

    private void traverse(Node[][] layer, int x, int y, int score, Direction in, Evaluator evaluator) {
        Set<Direction> directions = MathUtil.fromTileType(field[x][y]);

        if (directions != null && !directions.isEmpty()) {
            for (Direction out : directions) {
                int xx = x + MathUtil.dx(out);
                int yy = y + MathUtil.dy(out);

                int newScore = score + evaluator.apply(xx, yy, in, out);

                if (layer[xx][yy] == null || layer[x][y].score > newScore) {
                    layer[xx][yy] = new Node(in, score);

                    traverse(layer, xx, yy, newScore, out, evaluator);
                }
            }
        }
    }

    private static Direction rate(double angle) {
        double min = Double.MAX_VALUE;
        Direction result = null;

        for (Direction direction : Direction.values()) {
            int dx = (MathUtil.dx(direction) - 1) / 2;
            int dy = MathUtil.dy(direction);

            double deltaAngle = abs(angle - PI * dx + PI / 2 * dy);

            if (deltaAngle < min) {
                min = deltaAngle;
                result = direction;
            }
        }

        return result;
    }

    private static class Node {
        private final Direction in;
        private final int score;

        public Node(Direction in, int score) {
            this.in = in;
            this.score = score;
        }
    }
}
