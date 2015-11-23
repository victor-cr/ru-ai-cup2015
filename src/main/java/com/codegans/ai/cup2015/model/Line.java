package com.codegans.ai.cup2015.model;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:24
 */
public class Line {
    public final Point left;
    public final Point right;

    public Line(double leftX, double leftY, double rightX, double rightY) {
        this.left = new Point(leftX, leftY);
        this.right = new Point(rightX, rightY);
    }

    public Line(Point left, Point right) {
        this.left = left;
        this.right = right;
    }

    public Line withLeft(Point left) {
        return new Line(left, right);
    }

    public Line withRight(Point right) {
        return new Line(left, right);
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
    public String toString() {
        return String.format("[%s->%s]", left, right);
    }
}
