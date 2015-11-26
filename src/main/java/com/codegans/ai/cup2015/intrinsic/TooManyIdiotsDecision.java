package com.codegans.ai.cup2015.intrinsic;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.SpeedAction;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Point;
import com.codegans.ai.cup2015.model.Rectangle;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 24/11/2015 20:29
 */
public class TooManyIdiotsDecision implements Decision {
    private static final int THRESHOLD = 40;
    private final Logger log = LoggerFactory.getLogger();
    private boolean initializing = true;
    private boolean happened = false;
    private int tick = 0;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        if (happened) {
            return Collections.emptySet();
        }

        if (initializing) {
            double speed = StrictMath.hypot(self.getSpeedX(), self.getSpeedY());

            if (speed > 1.0D) {
                log.print("Init\n");
                initializing = false;
            } else {
                return Collections.emptySet();
            }
        }

        Collection<Car> cars = cars(self, world, game).collect(Collectors.toList());

        log.printf("Sides #%d: %s%n", tick, cars.stream().map(e -> new Point(e.getX(), e.getY())).collect(Collectors.toList()));

        if (cars.size() >= 2 && tick == 0) {
            tick = world.getTick();
        } else if (cars.size() >= 2 && world.getTick() - tick > THRESHOLD) {
            log.printf("Let idiots pass: %s%n", cars.stream().map(e -> new Point(e.getX(), e.getY())).collect(Collectors.toList()));
            happened = true;
            return Collections.singleton(new SpeedAction(Priority.HIGH, 0.1D));
        } else if (cars.size() < 2) {
            tick = world.getTick();
        }

        return Collections.emptySet();
    }

    private Stream<Car> cars(Car self, World world, Game game) {
        return Arrays.stream(world.getCars())
                .filter(e -> !e.isTeammate())
                .filter(e -> new Rectangle(self, game.getTrackTileSize()).hasCollision(new Rectangle(e)));
    }
}
