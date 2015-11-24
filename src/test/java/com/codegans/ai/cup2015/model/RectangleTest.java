package com.codegans.ai.cup2015.model;

import org.junit.Assert;
import org.junit.Test;

import static java.lang.StrictMath.PI;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 24.11.2015 7:07
 */
public class RectangleTest {
    @Test
    public void testHasCollision_Point() {
        Rectangle self = new Rectangle(new Point(0, 0), 10, 10, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 2, 2, 0);

        Assert.assertTrue(self.hasCollision(obstacle));
    }

    @Test
    public void testHasCollision_OrthoBlocks_NoCollision() {
        Rectangle self = new Rectangle(new Point(50, 50), 30, 45, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 100, 15, 0);

        Assert.assertFalse(self.hasCollision(obstacle));
    }

    @Test
    public void testHasCollision_OrthoBlocks_Collision() {
        Rectangle self = new Rectangle(new Point(40, 25), 35, 45, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 100, 15, 0);

        System.out.println("Self: " + self);
        System.out.println("Obstacle: " + obstacle);

        Assert.assertTrue(self.hasCollision(obstacle));
    }
}
