package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.BrakeAction;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Collection;
import java.util.Collections;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 9:14
 */
public class TurnBrakeDecision implements Decision {
    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        double speed = hypot(self.getSpeedX(), self.getSpeedY()) * 40;
        double angularSpeed = abs(self.getAngularSpeed()) * 100;
        double threshold = game.getTrackTileSize();

        if (speed > threshold && self.getEnginePower() > 0.0D && angularSpeed > 2.0D) {
            return Collections.singleton(new BrakeAction(Priority.NONE));
        }

        return Collections.emptySet();
    }
}
