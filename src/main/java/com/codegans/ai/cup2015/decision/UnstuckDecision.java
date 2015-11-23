package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.action.SpeedAction;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 19:46
 */
public class UnstuckDecision implements Decision {
    private static final int PAUSE = 10;
    private final Logger log = LoggerFactory.getLogger();
    private boolean notYetStarted = true;
    private boolean resolving = false;
    private boolean fixed = false;
    private int ticks = 0;
    private double x = 0;
    private double y = 0;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        double fallback = self.getHeight() / 2;

        boolean collision = detectCollision(self, navigator);

        if (collision && !fixed) {
            resolving = !resolving;
            x = self.getX();
            y = self.getY();
            resetTicks();

            if (resolving) {
                log.printf("Frontal collision detected: %s%n", navigator.getCollisionDetector().getNeighbourWalls(self, 10));
            } else {
                log.printf("Backward collision detected: %s%n", navigator.getCollisionDetector().getNeighbourWalls(self, 10));
            }
        }

        if (resolving && !fixed) {
            ticks++;
            double distance = hypot(self.getX() - x, self.getY() - y);

            if (self.getWheelTurn() * 100 > 1.0D) {
                log.printf("Wait for wheel adjustment: %d idle ticks%n", ticks);

                return Arrays.asList(new SpeedAction(Priority.TOP, 0), new MoveAction(Priority.TOP, 0));
            } else if (distance < fallback) {
                log.printf("Drive back: distance is %.3f with %d idle ticks%n", distance, ticks);

                return Arrays.asList(new SpeedAction(Priority.TOP, -1.0D), new MoveAction(Priority.TOP, 0));
            } else {
                log.printf("Reactivation of the main routine after %d idle ticks%n", ticks);

                fixed = true;
                resolving = false;
                resetTicks();
            }
        }

        if (fixed) {
            ticks++;

            if (ticks > PAUSE) {
                fixed = false;
            }

            return Collections.singleton(new SpeedAction(Priority.TOP, 0));
        }

        return Collections.emptySet();
    }

    private void resetTicks() {
        ticks = 0;
    }

    private boolean detectCollision(Car self, Navigator navigator) {
        double speed = hypot(self.getSpeedX(), self.getSpeedY());

        if (notYetStarted) {
            if (speed < 1.0D) {
                return false;
            } else {
                notYetStarted = false;
            }
        }

        if (resolving) {
            return speed < 1.0D
                    && self.getEnginePower() * 10 < -5.0D
                    && navigator.getCollisionDetector().hasBackwardCollision(self, 10.0D);
        } else {
            return speed < 1.0D
                    && self.getEnginePower() * 10 > 2.0D
                    && navigator.getCollisionDetector().hasFrontalCollision(self, 10.0D);
        }
    }
}
