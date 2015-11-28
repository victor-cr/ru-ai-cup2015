package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.SpillOilAction;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 10:55
 */
public class SpillOilDecision implements Decision {
    private static final double BASE_ANGLE = PI / 12;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        TileInfo tile = navigator.getCurrentTile(self);

        if (self.getOilCanisterCount() != 0 && self.getRemainingOilCooldownTicks() == 0 && tile.in != MathUtil.opposite(tile.out)) {
            double maxDistance = self.getHeight() * 4;

            boolean sureShoot = Arrays.stream(world.getCars())
                    .filter(e -> !e.isFinishedTrack())
                    .filter(e -> !e.isTeammate())
                    .filter(e -> abs(PI - self.getAngleTo(e)) < BASE_ANGLE) // ??
                    .filter(e -> self.getDistanceTo(e) <= maxDistance)
                    .filter(e -> self.getDistanceTo(e) >= self.getHeight())
                    .anyMatch(e -> BASE_ANGLE * self.getHeight() / self.getDistanceTo(e) >= abs(PI - self.getAngleTo(e)));

            if (sureShoot) {
                return Collections.singleton(new SpillOilAction(Priority.NORMAL));
            }
        }

        return Collections.emptySet();
    }
}
