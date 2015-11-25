package com.codegans.ai.cup2015.log;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.TileType;
import model.World;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class ConsoleLogger implements Logger {
    private volatile PrintStream out = System.out;
    private final PrintStream nullOut = new PrintStream(NullOutputStream.INSTANCE);

    @Override
    public void print(Object message) {
        out.print(message);
    }

    @Override
    public void printf(String pattern, Object... params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Collection) {
                Collection<?> c = (Collection) params[i];

                if (!c.isEmpty()) {
                    c = c.stream()
                            .filter(e -> e instanceof Car)
                            .map(e -> (Car) e)
                            .map(e -> String.format("Car@(%.3f;%.3f)", e.getX(), e.getY()))
                            .collect(Collectors.toList());
                }

                if (!c.isEmpty()) {
                    params[i] = c;
                }
            }

            if (params[i] instanceof Car) {
                Car e = (Car) params[i];

                params[i] = String.format("Car@(%.3f;%.3f)", e.getX(), e.getY());
            }
        }

        out.printf(pattern, params);
    }

    @Override
    public void action(Action<?> action) {
        printf("Perform action: %s%n", action);
    }

    @Override
    public void car(Car car, Navigator navigator) {
        TileInfo info = navigator.getCurrentTile(car);
        double speed = StrictMath.hypot(car.getSpeedX(), car.getSpeedY());

        printf("Car with %.1f%% at: [%d,%d] (%.3f;%.3f) %.1fx%.1f. Angle: %.3f. Speed: %.3f/%.3f. Wheels: %.3f. Engine: %.3f. Waypoint #%d: (%d;%d)%n",
                car.getDurability() * 100, info.x, info.y, car.getX(), car.getY(), car.getWidth(), car.getHeight(), car.getAngle(), speed, car.getAngularSpeed(), car.getWheelTurn(), car.getEnginePower(), car.getNextWaypointIndex(), car.getNextWaypointX(), car.getNextWaypointY());
    }

    @Override
    public void others(World world) {
        printf("All cars: %s%n", Arrays.asList(world.getCars()));
    }

    @Override
    public void turn(World world) {
        print('\n');
        printf("Turn #%d%n", world.getTick());
    }

    @Override
    public void layout(World world) {
        int[][] waypoints = world.getWaypoints();
        TileType[][] field = world.getTilesXY();

        print(Arrays.stream(field)
                .map(e -> Arrays.stream(e).map(Enum::name).map(x -> "TileType." + x).collect(Collectors.joining(",", "{", "}")))
                .collect(Collectors.joining(",", "{", "}")));
        print('\n');
        print(Arrays.stream(waypoints)
                .map(e -> "{" + e[0] + "," + e[1] + "}")
                .collect(Collectors.joining(",", "{", "}")));
        print('\n');
    }

    @Override
    public void stop(Car car) {
        print("Race is over: " + car.getId() + "\n");
        out = nullOut;
    }

    private static class NullOutputStream extends OutputStream {
        private static final NullOutputStream INSTANCE = new NullOutputStream();

        private NullOutputStream() {
        }

        public void write(int b) throws IOException {
        }
    }
}
