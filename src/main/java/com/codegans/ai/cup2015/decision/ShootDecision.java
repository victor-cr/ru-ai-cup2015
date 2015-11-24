package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.ShootAction;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 24.11.2015 2:03
 */
public class ShootDecision implements Decision {
    private static final double BASE_ANGLE = StrictMath.PI / 12;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        if (self.getProjectileCount() != 0 && self.getRemainingProjectileCooldownTicks() == 0) {
            double maxDistance = self.getHeight() * 4;

            boolean sureShoot = Arrays.stream(world.getCars())
                    .filter(Car::isFinishedTrack)
                    .filter(Car::isTeammate)
                    .filter(e -> self.getDistanceTo(e) <= maxDistance)
                    .filter(e -> self.getDistanceTo(e) >= self.getHeight())
                    .anyMatch(e -> BASE_ANGLE * self.getHeight() / self.getDistanceTo(e) >= self.getAngleTo(e));

            if (sureShoot) {
                return Collections.singleton(new ShootAction(Priority.NORMAL));
            }
        }

        return Collections.emptySet();
    }
}
