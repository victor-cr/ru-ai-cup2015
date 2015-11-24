package com.codegans.ai.cup2015.intrinsic;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 24/11/2015 20:23
 */
public class DiagonalMoveDecision implements Decision {
    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        TileInfo current = navigator.getCurrentTile(self);



        return null;
    }
}
