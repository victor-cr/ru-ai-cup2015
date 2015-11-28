package com.codegans.ai.cup2015.model;

import com.codegans.ai.cup2015.MathUtil;

import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:24
 */
public class Line {
    public final Point left;
    public final Point right;
//    public final double angle;

    public Line(double leftX, double leftY, double rightX, double rightY) {
        this(new Point(leftX, leftY), new Point(rightX, rightY));
    }

    public Line(Point left, Point right) {
        this.left = left;
        this.right = right;
//        this.angle = atan2(left.x - right.x, left.y - right.y);
    }

    public double length() {
        return StrictMath.hypot(left.x - right.x, left.y - right.y);
    }

    public Point center() {
        return point(0);
    }

    public Point point(double gravity) {
        return left.shiftTo(right, gravity);
    }

    public Line shiftOrthogonal(Point point) {
        double angle = atan2(left.y - right.y, left.x - right.x);

        double cos = cos(angle);
        double sin = sin(angle);

        Point base = center();

        Point target = MathUtil.rotate(point.minus(base), cos, -sin);

        Point delta = MathUtil.rotate(new Point(0, target.y), cos, sin);

        return new Line(left.plus(delta), right.plus(delta));
    }

    public Line withLeft(Point left) {
        return new Line(left, right);
    }

    public Line withRight(Point right) {
        return new Line(left, right);
    }

    public Line moveTo(Point center) {
        Point delta = center.minus(center());

        return new Line(left.plus(delta), right.plus(delta));
    }

    public double pointX(double proportion) {
        if (proportion > 1.0D) {
            proportion = 1.0D;
        } else if (proportion < -1.0D) {
            proportion = -1.0D;
        }
        return left.x + (right.x - left.x) * (0.5D + proportion / 2);
    }

    public double pointY(double proportion) {
        if (proportion > 1.0D) {
            proportion = 1.0D;
        } else if (proportion < -1.0D) {
            proportion = -1.0D;
        }

        return left.y + (right.y - left.y) * (0.5D + proportion / 2);
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Line && left.equals(((Line) obj).left) && right.equals(((Line) obj).right);
    }

    @Override
    public String toString() {
        return String.format("[%s->%s]", left, right);
    }
}
