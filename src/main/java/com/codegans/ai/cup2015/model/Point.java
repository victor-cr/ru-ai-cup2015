package com.codegans.ai.cup2015.model;

import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:18
 */
public final class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point withX(double x) {
        return new Point(x, y);
    }

    public Point withY(double y) {
        return new Point(x, y);
    }

    public Point addX(double dx) {
        return new Point(x + dx, y);
    }

    public Point addY(double dy) {
        return new Point(x, y + dy);
    }

    public Point add(Point base) {
        return new Point(x + base.x, y + base.y);
    }

    public Point shiftTo(Point other, double gravity) {
        double val = (1 + max(min(gravity, 1.0D), -1.0D)) / 2;

        return addX((other.x - x) * val).addY((other.y - y) * val);
    }

    @Override
    public String toString() {
        return String.format("(%.3f;%.3f)", x, y);
    }
}
