package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Column;
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
    private final Logger log = LoggerFactory.getLogger();
    private final World world;
    private final Game game;
    private final Navigator navigator;
    private final double radius;

    public CollisionDetector(World world, Game game, Navigator navigator) {
        this.world = world;
        this.game = game;
        this.navigator = navigator;
        this.radius = game.getTrackTileMargin() + 1;
    }

    public boolean hasCollision(Car car) {
        Rectangle zone = new Rectangle(car, radius);

        return getNeighbourCars(car, zone).anyMatch(e -> true) || getNeighbourWalls(car, zone).anyMatch(e -> true);
    }

    public boolean hasFrontalCollision(Car car) {
        Rectangle zone = new Rectangle(car).addHeight(radius).topHalf();

        boolean cars = getNeighbourCars(car, zone).anyMatch(e -> true);
        boolean walls = getNeighbourWalls(car, zone).anyMatch(e -> true);
        boolean columns = getNeighbourColumns(car, zone).anyMatch(e -> true);

        if (cars) {
            log.printf("Car collision: %s%n", getNeighbourCars(car));
        }

        if (walls) {
            log.printf("Wall collision: %s%n", getNeighbourWalls(car));
        }

        if (columns) {
            log.printf("Column collision: %s%n", getNeighbourColumns(car));
        }

        return cars || walls || columns;
    }

    public boolean hasBackwardCollision(Car car) {
        Rectangle zone = new Rectangle(car).addHeight(radius).lowHalf();

        boolean cars = getNeighbourCars(car, zone).anyMatch(e -> true);
        boolean walls = getNeighbourWalls(car, zone).anyMatch(e -> true);
        boolean columns = getNeighbourColumns(car, zone).anyMatch(e -> true);

        if (cars) {
            log.printf("Car collision: %s%n", getNeighbourCars(car));
        }

        if (walls) {
            log.printf("Wall collision: %s%n", getNeighbourWalls(car));
        }

        if (columns) {
            log.printf("Column collision: %s%n", getNeighbourColumns(car));
        }

        return cars || walls || columns;
    }

    public Collection<Car> getNeighbourCars(Car car) {
        return getNeighbourCars(car, new Rectangle(car, radius)).collect(Collectors.toList());
    }

    public Collection<Line> getNeighbourWalls(Car car) {
        return getNeighbourWalls(car, new Rectangle(car, radius)).collect(Collectors.toList());
    }

    public Collection<Column> getNeighbourColumns(Car car) {
        return getNeighbourColumns(car, new Rectangle(car, radius)).collect(Collectors.toList());
    }

    private Stream<Car> getNeighbourCars(Car car, Rectangle zone) {
        return Arrays.stream(world.getCars())
                .filter(e -> e.getId() != car.getId())
                .filter(e -> zone.hasCollision(new Rectangle(e)));
    }

    private Stream<Line> getNeighbourWalls(Car car, Rectangle zone) {
        return navigator.getCurrentTile(car).walls.stream().filter(zone::hasCollision);
    }

    private Stream<Column> getNeighbourColumns(Car car, Rectangle zone) {
        return navigator.getCurrentTile(car).getColumns().stream().filter(e -> zone.getLines().stream().anyMatch(e::crosses));
    }
}
