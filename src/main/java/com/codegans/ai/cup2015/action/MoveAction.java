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
    private final double angle;

    public MoveAction(int score, Unit unit, double x, double y) {
        super(score);

        this.angle = unit.getAngleTo(x, y) * 32 / PI;
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
