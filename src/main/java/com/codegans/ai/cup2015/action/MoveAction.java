package com.codegans.ai.cup2015.action;

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
    private final Unit unit;
    private final double x;
    private final double y;

    public MoveAction(int score, Unit unit, double x, double y) {
        super(score);

        this.unit = unit;
        this.x = x;
        this.y = y;
    }

    @Override
    public void apply(Move move) {
        double angle = unit.getAngleTo(x, y);

        move.setWheelTurn(angle * 32.0D / PI);
    }

    @Override
    public String toString() {
        return super.toString() + new Formatter().format("->(%5.3f;%5.3f)", x, y);
    }
}
