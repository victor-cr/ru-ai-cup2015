package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.BrakeAction;
import com.codegans.ai.cup2015.model.Rectangle;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Collection;
import java.util.Collections;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 9:14
 */
public class TurnBrakeDecision implements Decision {
    private static final double THRESHOLD = PI / 2;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        double speed = hypot(self.getSpeedX(), self.getSpeedY()) * 40;
        double angularSpeed = abs(self.getAngularSpeed()) * 100;
        double threshold = game.getTrackTileSize();

        if (speed > threshold && self.getEnginePower() > 0.0D && angularSpeed > 2.0D) {
            return Collections.singleton(new BrakeAction(Priority.NONE));
        }

        boolean collisionWithColumn = navigator.getCollisionDetector().getNeighbourColumns(self, new Rectangle(self).addHeight(self.getHeight()).topHalf())
                .filter(e -> abs(self.getAngleTo(e.center.x, e.center.y)) <= THRESHOLD).anyMatch(e -> true);

        if (collisionWithColumn && speed * 2 > threshold) {
            return Collections.singleton(new BrakeAction(Priority.NORMAL));
        }

        return Collections.emptySet();
    }
}
