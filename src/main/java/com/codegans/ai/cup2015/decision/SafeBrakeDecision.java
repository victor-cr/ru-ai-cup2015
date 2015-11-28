package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.CollisionDetector;
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

import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 9:14
 */
public class SafeBrakeDecision implements Decision {
    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        double speed = hypot(self.getSpeedX(), self.getSpeedY()) * 25;
        double threshold = game.getTrackTileSize();

        if (speed > threshold && self.getEnginePower() > 0.0D) {
            Rectangle far = new Rectangle(self).addHeight(threshold).topHalf();
//            Rectangle near = new Rectangle(self).addHeight(self.getHeight()).topHalf();

            CollisionDetector collisionDetector = navigator.getCollisionDetector();

            boolean hasFarCollision = collisionDetector.getNeighbourColumns(self, far).anyMatch(e -> true)
                    || collisionDetector.getNeighbourWalls(self, far).anyMatch(e -> true);
//            boolean hasNearCollision = collisionDetector.getNeighbourColumns(self, near).anyMatch(e -> true)
//                    || collisionDetector.getNeighbourWalls(self, far).anyMatch(e -> true);

            if (hasFarCollision) {
                return Collections.singleton(new BrakeAction(Priority.NONE));
            }
        }

        return Collections.emptySet();
    }
}
