package com.codegans.ai.cup2015.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 12:26
 */
public class LineTest {
    @Test
    public void testShiftOrthogonal() {
        Line initial = new Line(1, 0, 3, 4);

        Line expected = new Line(9, -4, 11, 0);
        Line actual = initial.shiftOrthogonal(new Point(11, 0));

        Assert.assertEquals(expected, actual);
    }
}
