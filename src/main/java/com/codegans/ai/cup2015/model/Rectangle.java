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

    public Rectangle addWidth(double dWidth) {
        return new Rectangle(center, width + dWidth, height, angle, cos, sin);
    }

    public Rectangle addHeight(double dWidth) {
        return new Rectangle(center, width, height + dWidth, angle, cos, sin);
    }

    public boolean isInner(Point point) {
        return within(getPoints(), point);
    }

    public boolean hasCollision(Rectangle rectangle) {
        Collection<Point> thisPoints = getPoints();
        Collection<Point> thatPoints = rectangle.getPoints();

        return thisPoints.stream().anyMatch(e -> within(thatPoints, e))
                || thatPoints.stream().anyMatch(e -> within(thisPoints, e));
    }

    public boolean hasCollision(Line line) {
        Collection<Point> points = getPoints();

        long count = points.stream()
                .mapToDouble(e -> MathUtil.orientedArea(line, e))
                .filter(e -> e < 0)
                .count();


        return count != 0 && count != points.size();
    }

    public boolean isOuter(Point point) {
        return !isInner(point);
    }

    public Collection<Line> getLines() {
        Point topLeft = getTopLeft();
        Point topRight = getTopRight();
        Point bottomRight = getBottomRight();
        Point bottomLeft = getBottomLeft();

        return Arrays.asList(
                new Line(topLeft, topRight),
                new Line(topRight, bottomRight),
                new Line(bottomRight, bottomLeft),
                new Line(bottomLeft, topLeft)
        );
    }

    private static boolean within(Collection<Point> area, Point point) {
        Point first = null;
        Point prev = null;

        for (Point current : area) {
            if (first == null) {
                first = current;
            } else {
                double orientedArea = MathUtil.orientedArea(prev, current, point);

                if (orientedArea < 0) {
                    return false;
                }
            }

            prev = current;
        }

        return first != null && prev != null && MathUtil.orientedArea(prev, first, point) >= 0;
    }

    @Override
    public String toString() {
        return getPoints().toString();
    }
}
