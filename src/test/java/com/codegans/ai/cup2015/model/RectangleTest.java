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
    public void testHasCollision_InsidePoint() {
        Rectangle self = new Rectangle(new Point(0, 0), 10, 10, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 2, 2, 0);

        print(self, obstacle);

        Assert.assertTrue(self.hasCollision(obstacle));
    }

    @Test
    public void testHasCollision_OutsidePoint() {
        Rectangle self = new Rectangle(new Point(0, 0), 10, 10, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(20, 20), 2, 2, 0);

        print(self, obstacle);

        Assert.assertFalse(self.hasCollision(obstacle));
    }

    @Test
    public void testHasCollision_OrthoBlocks_NoCollision() {
        Rectangle self = new Rectangle(new Point(50, 50), 30, 45, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 100, 15, 0);

        print(self, obstacle);

        Assert.assertFalse(self.hasCollision(obstacle));
    }

    @Test
    public void testHasCollision_OrthoBlocks_Collision() {
        Rectangle self = new Rectangle(new Point(40, 25), 35, 45, -PI / 2);
        Rectangle obstacle = new Rectangle(new Point(0, 0), 100, 105, 0);

        print(self, obstacle);

        Assert.assertTrue(self.hasCollision(obstacle));
    }

    @Test
    public void testHasFrontalCollision() {
        Rectangle self = new Rectangle(new Point(1175.806 + 35, 7500.498 + 35), 210, 70, 2.072193556);
        Rectangle obstacle = new Rectangle(new Point(1005.764, 7606.7279), 210, 140, -2.63617017);

        print(self, obstacle);

        Assert.assertTrue(self.hasCollision(obstacle));
    }

    private static void print(Rectangle self, Rectangle other) {
        System.out.println(self);
        System.out.println(other);
    }
}
