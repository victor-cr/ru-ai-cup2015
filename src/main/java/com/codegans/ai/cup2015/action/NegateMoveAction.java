package com.codegans.ai.cup2015.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:32
 */
public class NegateMoveAction extends BaseAction<NegateMoveAction> {
    public NegateMoveAction(int score) {
        super(score);
    }

    @Override
    public void apply(Move move) {
        move.setWheelTurn(-move.getWheelTurn());
    }
}
