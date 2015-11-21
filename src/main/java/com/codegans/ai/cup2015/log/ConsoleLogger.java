package com.codegans.ai.cup2015.log;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.TileType;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class ConsoleLogger implements Logger {
    @Override
    public void print(Object message) {
        System.out.print(message);
    }

    @Override
    public void printf(String pattern, Object... params) {
        System.out.printf(pattern, params);
    }

    @Override
    public void action(Action<?> action) {
        printf("Perform action: %s%n", action);
    }

    @Override
    public void waypoint(Car car) {
        TileInfo info = Navigator.getInstance(null, null).getCurrentTile(car);
        double speed = StrictMath.hypot(car.getSpeedX(), car.getSpeedY());

        printf("Car with %.1f%% at: [%d,%d] (%5.3f;%5.3f). Speed: %.3f/%.3f. Engine: %.3f. Waypoint #%d: (%d;%d)%n", car.getDurability() * 100, info.x, info.y, car.getX(), car.getY(), speed, car.getAngularSpeed(), car.getEnginePower(), car.getNextWaypointIndex(), car.getNextWaypointX(), car.getNextWaypointY());
    }

    @Override
    public void turn(World world) {
        print('\n');
        printf("Turn #%d%n", world.getTick());
    }

    @Override
    public void layout(World world) {
        TileType[][] field = world.getTilesXY();

        for (TileType[] row : field) {
            for (TileType tile : row) {
                print(tile.ordinal());
            }

            print('\n');
        }
    }
}
