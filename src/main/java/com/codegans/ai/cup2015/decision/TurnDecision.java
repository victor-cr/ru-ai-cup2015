package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.SpeedAction;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Collection;
import java.util.Collections;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 19:46
 */
public class TurnDecision implements Decision {
    private static final int THRESHOLD = 2;
    private static final double OPTIMAL_SPEED = 12.6D;
    private static final double MINIMUM_ACCELERATION = -0.25D;

    private final Logger log = LoggerFactory.getLogger();

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        TileInfo tile = navigator.getNextTurnTile(self);

        if (tile.index > THRESHOLD) {
            return Collections.emptySet();
        }

        double delta = game.getCarEnginePowerChangePerTick();
        double power = self.getEnginePower();
        double speed = StrictMath.hypot(self.getSpeedX(), self.getSpeedY());

        if (speed < OPTIMAL_SPEED) {
            power += delta;
            log.printf("Accelerating: %.3f (+%f)%n", power, delta);
        } else if (speed > OPTIMAL_SPEED && power > MINIMUM_ACCELERATION) {
            power -= delta;
            log.printf("Decelerating: %.3f (-%f)%n", power, delta);
        }

        return Collections.singleton(new SpeedAction(Priority.HIGH, power));
    }
}
