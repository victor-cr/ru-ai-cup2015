package com.codegans.ai.cup2015.log;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import model.Car;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class NullLogger implements Logger {
    @Override
    public void print(Object message) {
    }

    @Override
    public void printf(String pattern, Object... params) {
    }

    @Override
    public void action(Action<?> action) {
    }

    @Override
    public void car(Car car, Navigator navigator) {
    }

    @Override
    public void turn(World world) {
    }

    @Override
    public void layout(World world) {
    }

    @Override
    public void stop(Car car) {
    }
}
