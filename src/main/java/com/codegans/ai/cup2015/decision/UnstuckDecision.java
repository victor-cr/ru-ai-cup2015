package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.CollisionDetector;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.NegateMoveAction;
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

import static java.lang.StrictMath.abs;
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
    private int requiredTicks = 0;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        double fallback = self.getHeight() / 2;

        CollisionDetector collisionDetector = navigator.getCollisionDetector();

        boolean collision = detectCollision(self, collisionDetector);

        if (collision && !resolving && !fixed) {
            log.printf("Frontal collision detected: %s%n", collisionDetector.getNeighbourWalls(self));
            resolving = true;
            requiredTicks = (int) (2 * abs(self.getWheelTurn()) / game.getCarWheelTurnChangePerTick());
            collision = false;
            x = self.getX();
            y = self.getY();
            resetTicks();
        }

        if (resolving && !fixed) {
            ticks++;
            double distance = hypot(self.getX() - x, self.getY() - y);

            if (ticks < requiredTicks) {
                log.printf("Wait for back drive wheel adjustment: %d idle ticks%n", ticks);

                return Arrays.asList(new SpeedAction(Priority.TOP, 0), new NegateMoveAction(Priority.NONE));
            } else if (!collision && distance < fallback) {
                log.printf("Drive back: distance is %.3f with %d idle ticks%n", distance, ticks);

                return Arrays.asList(new SpeedAction(Priority.TOP, -1.0D), new NegateMoveAction(Priority.NONE));
            } else {
                if (collision) {
                    log.printf("Backward collision detected: %s%n", collisionDetector.getNeighbourWalls(self));
                }

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

            log.printf("Wait for main route wheel adjustment: %d idle ticks%n", ticks);

            return Collections.singleton(new SpeedAction(Priority.TOP, 0));
        }

        return Collections.emptySet();
    }

    private void resetTicks() {
        ticks = 0;
    }

    private boolean detectCollision(Car self, CollisionDetector collisionDetector) {
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
                    && self.getEnginePower() * 10 < -9.0D
                    && collisionDetector.hasBackwardCollision(self);
        } else {
            return speed < 1.0D
                    && self.getEnginePower() * 10 > 2.0D
                    && collisionDetector.hasFrontalCollision(self);
        }
    }
}
