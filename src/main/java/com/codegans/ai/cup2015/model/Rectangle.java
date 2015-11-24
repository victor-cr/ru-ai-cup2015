package com.codegans.ai.cup2015.model;

import com.codegans.ai.cup2015.MathUtil;
import model.RectangularUnit;

import java.util.Arrays;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 23.11.2015 15:55
 */
public class Rectangle {
    public final Point center;
    public final double width;
    public final double height;
    public final double angle;
    private final double cos;
    private final double sin;

    public Rectangle(RectangularUnit unit) {
        this(unit, 0);
    }

    public Rectangle(RectangularUnit unit, double radius) {
        this(new Point(unit.getX(), unit.getY()), unit.getWidth() + radius, unit.getHeight() + radius, unit.getAngle());
    }

    public Rectangle(Point center, double width, double height, double angle) {
        this(center, width, height, angle, StrictMath.cos(-angle), StrictMath.sin(-angle));
    }

    private Rectangle(Point center, double width, double height, double angle, double cos, double sin) {
        this.center = center;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.cos = cos;
        this.sin = sin;
    }

    public Rectangle topHalf() {
        return new Rectangle(MathUtil.rotate(new Point(height / 4, 0), cos, sin).add(center), width, height / 2, angle, cos, sin);
    }

    public Rectangle lowHalf() {
        return new Rectangle(MathUtil.rotate(new Point(-height / 4, 0), cos, sin).add(center), width, height / 2, angle, cos, sin);
    }

    public Point getTopLeft() {
        return MathUtil.rotate(new Point(height / 2, -width / 2), cos, sin).add(center);
    }

    public Point getTopRight() {
        return MathUtil.rotate(new Point(height / 2, width / 2), cos, sin).add(center);
    }

    public Point getBottomRight() {
        return MathUtil.rotate(new Point(-height / 2, width / 2), cos, sin).add(center);
    }

    public Point getBottomLeft() {
        return MathUtil.rotate(new Point(-height / 2, -width / 2), cos, sin).add(center);
    }

    public Collection<Point> getPoints() {
        return Arrays.asList(getTopLeft(), getTopRight(), getBottomRight(), getBottomLeft());
    }

    public boolean isInner(Point point) {
        return within(getPoints(), point);
    }

    public boolean hasCollision(Rectangle rectangle) {
        Collection<Point> thisPoints = getPoints();
        Collection<Point> thatPoints = rectangle.getPoints();

        return thatPoints.stream().anyMatch(e -> within(thisPoints, e)) || thisPoints.stream().anyMatch(e -> within(thatPoints, e));
    }

    public boolean hasCollision(Line line) {
        Collection<Point> points = getPoints();

        long count = points.stream()
                .mapToDouble(e -> MathUtil.orientedArea(line, e))
                .peek(System.out::print)
                .peek(e -> System.out.print(","))
                .filter(e -> e < 0)
                .count();

        System.out.println(line + ": " + count);

        return count != 0 && count != points.size();
    }

    public boolean isOuter(Point point) {
        return !isInner(point);
    }

    private static boolean within(Collection<Point> area, Point point) {
        Point first = null;
        Point prev = null;

        for (Point current : area) {
            if (first == null) {
                first = current;
            } else if (MathUtil.orientedArea(prev, current, point) < 0) {
                return false;
            }

            prev = current;
        }

        return first != null && prev != null && MathUtil.orientedArea(prev, first, point) >= 0;
    }
}
