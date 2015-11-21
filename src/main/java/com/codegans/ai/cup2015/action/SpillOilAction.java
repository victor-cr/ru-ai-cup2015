package com.codegans.ai.cup2015.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public class SpillOilAction extends BaseAction<SpillOilAction> {
    public SpillOilAction(int score) {
        super(score);
    }

    @Override
    public void apply(Move move) {
        move.setSpillOil(true);
    }
}
