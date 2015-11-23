package com.codegans.ai.cup2015.model;

import java.util.Formatter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 20:12
 */
public final class Marker extends Line {
    public final int x;
    public final int y;

    public Marker(int x, int y, double leftX, double leftY, double rightX, double rightY) {
        super(new Point(leftX, leftY), new Point(rightX, rightY));
        this.x = x;
        this.y = y;
    }

    public double pointX() {
        return pointX(0);
    }

    public double pointY() {
        return pointY(0);
    }

    @Override
    public String toString() {
        return new Formatter().format("(%.3f;%.3f)", pointX(), pointY()).toString();
    }
}
