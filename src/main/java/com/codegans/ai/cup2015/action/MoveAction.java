package com.codegans.ai.cup2015.action;

import com.codegans.ai.cup2015.model.Point;
import model.Move;
import model.Unit;

import java.util.Formatter;

import static java.lang.StrictMath.PI;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:32
 */
public class MoveAction extends BaseAction<MoveAction> {
    private final double angle;
    private final Point point;

    public MoveAction(int score, double angle) {
        super(score);

        this.angle = angle;
        this.point = null;
    }

    public MoveAction(int score, Unit unit, double x, double y) {
        super(score);

        this.angle = unit.getAngleTo(x, y) * 4 / PI;
        this.point = new Point(x, y);
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public void apply(Move move) {
        move.setWheelTurn(angle);
    }

    @Override
    public String toString() {
        return super.toString() + new Formatter().format("(%.5f)", angle);
    }
}
