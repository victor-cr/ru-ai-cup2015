package com.codegans.ai.cup2015.model;

import model.Direction;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 25/11/2015 06:46
 */
public class Wall extends Line {
    public final Direction direction;

    public Wall(Direction direction, double leftX, double leftY, double rightX, double rightY) {
        super(leftX, leftY, rightX, rightY);

        this.direction = direction;
    }

    @Override
    public String toString() {
        return direction + ":" + super.toString();
    }
}
