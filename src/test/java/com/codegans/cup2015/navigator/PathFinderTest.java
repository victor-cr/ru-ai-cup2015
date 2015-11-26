package com.codegans.cup2015.navigator;

import com.codegans.ai.cup2015.navigator.PathFinder;
import model.Bonus;
import model.Car;
import model.Direction;
import model.OilSlick;
import model.Player;
import model.Projectile;
import model.TileType;
import model.World;
import org.junit.Assert;
import org.junit.Test;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 26/11/2015 14:29
 */
public class PathFinderTest {
    @Test
    public void testBuild() {
        int[][] waypoints = new int[][]{{0, 0}, {1, 1}};
        TileType[][] tilesXY = new TileType[][]{
                {TileType.LEFT_TOP_CORNER, TileType.LEFT_BOTTOM_CORNER},
                {TileType.RIGHT_TOP_CORNER, TileType.RIGHT_BOTTOM_CORNER}
        };

        World world = new World(0, 0, 0, 2, 2, new Player[0], new Car[0], new Projectile[0], new Bonus[0], new OilSlick[0], "test", tilesXY, waypoints, Direction.DOWN);

        PathFinder builder = new PathFinder(world);

        int[][][][] expectedResult = new int[][][][]{
                {
                        {{Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 3}, {Integer.MAX_VALUE, 1, 1, Integer.MAX_VALUE}},
                        {{1, Integer.MAX_VALUE, Integer.MAX_VALUE, 1}, {0, 0, 0, 0}}
                },
                {
                        {{0, 0, 0, 0}, {1, Integer.MAX_VALUE, Integer.MAX_VALUE, 1}},
                        {{Integer.MAX_VALUE, 1, 1, Integer.MAX_VALUE}, {3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE}}
                }
        };

        int[][][][] actualResult = builder.build();

        Assert.assertArrayEquals(expectedResult, actualResult);
    }
}
