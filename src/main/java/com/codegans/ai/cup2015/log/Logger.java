package com.codegans.ai.cup2015.log;

import com.codegans.ai.cup2015.action.Action;
import model.Car;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 11:46
 */
public interface Logger {
    void print(Object message);

    void printf(String pattern, Object... params);

    void action(Action<?> action);

    void waypoint(Car car);

    void turn(World world);

    void layout(World world);
}
