package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.NitroAction;
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
 * @since 24.11.2015 2:03
 */
public class NitroDecision implements Decision {
    private static final int THRESHOLD = 9;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        if (self.getNitroChargeCount() != 0 && self.getRemainingNitroCooldownTicks() == 0 && self.getRemainingNitroTicks() == 0) {

            TileInfo tile = navigator.getNextTurnTile(self);

            if (tile.index > THRESHOLD) {
                return Collections.singleton(new NitroAction(Priority.NORMAL));
            }
        }

        return Collections.emptySet();
    }
}
