package com.codegans.ai.cup2015.model;

import com.codegans.ai.cup2015.MathUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 24/11/2015 22:40
 */
public class MathUtilTest {
    @Test
    public void testOrientedArea_Right() {
        double s = MathUtil.orientedArea(new Point(0, 0), new Point(0, 1), new Point(1, 1));

        Assert.assertEquals(1.0D, StrictMath.signum(s), 0.0D);
    }

    @Test
    public void testOrientedArea_Left() {
        double s = MathUtil.orientedArea(new Point(0, 1), new Point(0, 0), new Point(1, 1));

        Assert.assertEquals(-1.0D, StrictMath.signum(s), 0.0D);
    }

    @Test
    public void testOrientedArea_Same() {
        double s = MathUtil.orientedArea(new Point(0, 1), new Point(0, 0), new Point(0, 0.5));

        Assert.assertEquals(0.0D, StrictMath.signum(s), 0.0D);
    }
}
