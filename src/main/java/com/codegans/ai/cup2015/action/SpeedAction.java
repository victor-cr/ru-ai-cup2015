package com.codegans.ai.cup2015.action;

import model.Move;

import java.util.Formatter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:32
 */
public class SpeedAction extends BaseAction<SpeedAction> {
    private final double acceleration;

    public SpeedAction(int score, double acceleration) {
        super(score);

        this.acceleration = acceleration;
    }

    @Override
    public void apply(Move move) {
        move.setEnginePower(acceleration);
    }

    @Override
    public String toString() {
        return super.toString() + new Formatter().format("(%.3f)", acceleration);
    }
}
