package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.model.Line;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
import model.Direction;
import model.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 11:38
 */
public class MathUtil {
    private static final List<Node> DIRECTIONS = new ArrayList<>();

    static {
        DIRECTIONS.add(new Node(0, 0, 1, Direction.DOWN));
        DIRECTIONS.add(new Node(1, -1, 0, Direction.LEFT));
        DIRECTIONS.add(new Node(2, 0, -1, Direction.UP));
        DIRECTIONS.add(new Node(3, 1, 0, Direction.RIGHT));
    }

    public static double orientedArea(Point first, Point second, Point third) {
        double x1 = first.x;
        double x2 = second.x;
        double x3 = third.x;
        double y1 = first.y;
        double y2 = second.y;
        double y3 = third.y;
//        #abc
//        #def
//        #ghi
//        #aei+bfg+cdh-ceg-bdi-afh
//        #ae+bg+dh-eg-bd-ah   #e(a-g)+b(g-d)+h(d-a) # e(a-g)+b(g-d)-h(a-g+g-d) # e(a-g)+b(g-d)-h(a-g)-h(g-d)
//return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
//        # (a-g)(e-h)+(g-d)(b-h)
//        # (x1-x3)(y2-y3)+(x3-x2)(y1-y3)

//        return (first.x - third.x) * (second.y - third.y) - (third.x - second.x) * (first.y - third.y);
        //
//        return (x1 - x3) * (y2 - y3) - (y1 - y3) * (x2 - x3);
        return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
//        return (first.x - third.x) * (second.y - third.y) - (third.x - second.x) * (first.y - third.y);
    }

    public static double orientedArea(Line line, Point point) {
        return orientedArea(line.left, line.right, point);
    }

    public static Point rotate(Point point, double cos, double sin) {
        return point.withX(0 + point.x * cos - point.y * sin).withY(0 + point.x * sin + point.y * cos);
    }

    public static Line rotate(Line line, double cos, double sin) {
        return line.withLeft(rotate(line.left, cos, sin)).withRight(rotate(line.right, cos, sin));
    }

    public static Direction relative(Marker marker, double x, double y) {
        double s = orientedArea(marker, new Point(x, y));

        if (s > 0) {
            return Direction.RIGHT;
        } else if (s < 0) {
            return Direction.LEFT;
        } else if (marker.left.x < x) {
            return Direction.DOWN;
        }

        return Direction.UP;
    }

    public static int dx(Direction direction) {
        return DIRECTIONS.stream()
                .filter(e -> e.direction == direction)
                .mapToInt(e -> e.dx)
                .findFirst().getAsInt();
    }

    public static int dy(Direction direction) {
        return DIRECTIONS.stream()
                .filter(e -> e.direction == direction)
                .mapToInt(e -> e.dy)
                .findFirst().getAsInt();
    }

    public static Direction shift(int dx, int dy) {
        return DIRECTIONS.stream()
                .filter(e -> e.dx == dx && e.dy == dy)
                .map(e -> e.direction)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid shift: (" + dx + ";" + dy + ")"));
    }

    public static Direction opposite(Direction direction) {
        return DIRECTIONS.stream()
                .filter(e -> e.direction == direction)
                .map(e -> DIRECTIONS.get((DIRECTIONS.size() + e.index + 2) % DIRECTIONS.size()))
                .map(e -> e.direction)
                .findFirst().get();
    }

    public static Direction relative(Direction in, Direction out) {
        int inIndex = DIRECTIONS.stream().filter(e -> e.direction == in).mapToInt(e -> e.index).findFirst().getAsInt();
        int outIndex = DIRECTIONS.stream().filter(e -> e.direction == out).mapToInt(e -> e.index).findFirst().getAsInt();

        int diff = inIndex - outIndex;

        return DIRECTIONS.get((DIRECTIONS.size() + diff) % DIRECTIONS.size()).direction;
    }

    public static Set<Direction> fromTileType(TileType type) {
        switch (type) {
            case EMPTY:
                return Collections.emptySet();
            case VERTICAL:
                return EnumSet.of(Direction.UP, Direction.DOWN);
            case HORIZONTAL:
                return EnumSet.of(Direction.LEFT, Direction.RIGHT);
            case LEFT_TOP_CORNER:
                return EnumSet.of(Direction.RIGHT, Direction.DOWN);
            case RIGHT_TOP_CORNER:
                return EnumSet.of(Direction.LEFT, Direction.DOWN);
            case LEFT_BOTTOM_CORNER:
                return EnumSet.of(Direction.RIGHT, Direction.UP);
            case RIGHT_BOTTOM_CORNER:
                return EnumSet.of(Direction.LEFT, Direction.UP);
            case LEFT_HEADED_T:
                return EnumSet.of(Direction.LEFT, Direction.DOWN, Direction.UP);
            case RIGHT_HEADED_T:
                return EnumSet.of(Direction.RIGHT, Direction.DOWN, Direction.UP);
            case TOP_HEADED_T:
                return EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP);
            case BOTTOM_HEADED_T:
                return EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.DOWN);
            case CROSSROADS:
                return EnumSet.allOf(Direction.class);
            case UNKNOWN:
            default:
                return null;
        }
    }

    private static final class Node {
        private final Direction direction;
        private final int dx;
        private final int dy;
        private final int index;

        public Node(int index, int dx, int dy, Direction direction) {
            this.direction = direction;
            this.dx = dx;
            this.dy = dy;
            this.index = index;
        }
    }
}
