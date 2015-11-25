package com.codegans.ai.cup2015.model;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 25/11/2015 06:49
 */
public class Column {
    public final double radius;
    public final Point center;

    public Column(double x, double y, double radius) {
        this.center = new Point(x, y);
        this.radius = radius;
    }

    public boolean crosses(Line line) {
        double x01 = line.left.x - center.x;
        double y01 = line.left.y - center.y;
        double x02 = line.right.x - center.x;
        double y02 = line.right.y - center.y;

        double dx = x02 - x01;
        double dy = y02 - y01;

        double a = dx * dx + dy * dy;
        double b = 2.0D * (x01 * dx + y01 * dy);
        double c = x01 * x01 + y01 * y01 - radius * radius;

        if (-b < 0) {
            return (c < 0);
        }

        if (-b < (2.0D * a)) {
            return (4.0f * a * c - b * b < 0);
        }

        return (a + b + c < 0);
    }
}
