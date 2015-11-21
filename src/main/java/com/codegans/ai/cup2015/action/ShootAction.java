package com.codegans.ai.cup2015.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:23
 */
public class ShootAction extends BaseAction<ShootAction> {
    public ShootAction(int score) {
        super(score);
    }

    @Override
    public void apply(Move move) {
        move.setThrowProjectile(true);
    }
}
