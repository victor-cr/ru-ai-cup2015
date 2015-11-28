package com.codegans.ai.cup2015.model;

import model.Unit;

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

    public Point(Unit unit) {
        this.x = unit.getX();
        this.y = unit.getY();
    }

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

    public Point plusX(double dx) {
        return new Point(x + dx, y);
    }

    public Point plusY(double dy) {
        return new Point(x, y + dy);
    }

    public Point minusX(double dx) {
        return new Point(x - dx, y);
    }

    public Point minusY(double dy) {
        return new Point(x, y - dy);
    }

    public Point plus(Point base) {
        return new Point(x + base.x, y + base.y);
    }

    public Point minus(Point base) {
        return new Point(x - base.x, y - base.y);
    }

    public Point shiftTo(Point other, double gravity) {
        double val = (1 + max(min(gravity, 1.0D), -1.0D)) / 2;

        return plusX((other.x - x) * val).plusY((other.y - y) * val);
    }

    @Override
    public int hashCode() {
        return (int) (x * y);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Point && x - ((Point) obj).x < 0.001D && y - ((Point) obj).y < 0.001D;
    }

    @Override
    public String toString() {
        return String.format("(%.3f;%.3f)", x, y);
    }
}
