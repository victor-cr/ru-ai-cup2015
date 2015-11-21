package com.codegans.ai.cup2015.model;

import java.util.Formatter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 20:12
 */
public final class Marker {
    public final double leftX;
    public final double leftY;
    public final double rightX;
    public final double rightY;

    public Marker(double leftX, double leftY, double rightX, double rightY) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }

    public double pointX() {
        return leftX + (rightX - leftX) / 2;
    }

    public double pointY() {
        return leftY + (rightY - leftY) / 2;
    }

    @Override
    public String toString() {
        return new Formatter().format("(%.3f;%.3f)", pointX(), pointY()).toString();
    }
}
