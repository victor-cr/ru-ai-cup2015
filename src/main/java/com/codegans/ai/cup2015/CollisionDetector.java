package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.model.Line;
import com.codegans.ai.cup2015.model.Rectangle;
import model.Car;
import model.Game;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:05
 */
public class CollisionDetector {
    private final World world;
    private final Game game;
    private final Navigator navigator;

    public CollisionDetector(World world, Game game, Navigator navigator) {
        this.world = world;
        this.game = game;
        this.navigator = navigator;
    }

    public boolean hasCollision(Car car, double radius) {
        Rectangle zone = new Rectangle(car, radius);

        return getNeighbourCars(car, zone).anyMatch(e -> true) || getNeighbourWalls(car, zone).anyMatch(e -> true);
    }

    public boolean hasFrontalCollision(Car car, double radius) {
        Rectangle zone = new Rectangle(car, radius).topHalf();

        return getNeighbourCars(car, zone).anyMatch(e -> true) || getNeighbourWalls(car, zone).anyMatch(e -> true);
    }

    public boolean hasBackwardCollision(Car car, double radius) {
        Rectangle zone = new Rectangle(car, radius).lowHalf();

        return getNeighbourCars(car, zone).anyMatch(e -> true) || getNeighbourWalls(car, zone).anyMatch(e -> true);
    }

    public Collection<Car> getNeighbourCars(Car car, double radius) {
        return getNeighbourCars(car, new Rectangle(car, radius)).collect(Collectors.toList());
    }

    public Collection<Line> getNeighbourWalls(Car car, double radius) {
        return getNeighbourWalls(car, new Rectangle(car, radius)).collect(Collectors.toList());
    }

    private Stream<Car> getNeighbourCars(Car car, Rectangle zone) {
        return Arrays.stream(world.getCars()).filter(e -> e.getId() != car.getId()).filter(e -> new Rectangle(e).hasCollision(zone));
    }

    private Stream<Line> getNeighbourWalls(Car car, Rectangle zone) {
        return navigator.getCurrentTile(car).walls.stream().filter(zone::hasCollision);
    }
}
