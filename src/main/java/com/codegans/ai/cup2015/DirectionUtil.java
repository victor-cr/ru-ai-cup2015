package com.codegans.ai.cup2015;

import model.Direction;
import model.TileType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 11:38
 */
public class DirectionUtil {
    public static int dx(Direction direction) {
        switch (direction) {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            default:
                return 0;
        }
    }

    public static int dy(Direction direction) {
        switch (direction) {
            case UP:
                return -1;
            case DOWN:
                return 1;
            default:
                return 0;
        }
    }

    public static Direction shift(int dx, int dy) {
        int o = dx * 10 + dy;

        switch (o) {
            case 10:
                return Direction.RIGHT;
            case -10:
                return Direction.LEFT;
            case 1:
                return Direction.DOWN;
            case -1:
                return Direction.UP;
            default:
                throw new IllegalArgumentException("Invalid shift: (" + dx + ";" + dy + ")");
        }
    }

    public static Direction opposite(Direction direction) {
        switch (direction) {
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            case UP:
                return Direction.DOWN;
            default:
                return null;
        }
    }

    public static Direction relative(Direction in, Direction out) {
        if (in == out) {
            return Direction.DOWN;
        }

        if (in == opposite(out)) {
            return Direction.UP;
        }

        switch (in) {
            case DOWN:
                return out == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
            case LEFT:
                return out == Direction.UP ? Direction.RIGHT : Direction.LEFT;
            case UP:
                return out == Direction.RIGHT ? Direction.RIGHT : Direction.LEFT;
            case RIGHT:
                return out == Direction.DOWN ? Direction.RIGHT : Direction.LEFT;
            default:
                return null;
        }
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
}
