package com.codegans.ai.cup2015.intrinsic;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.action.SpeedAction;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static java.lang.StrictMath.abs;

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
        TileInfo nextTurn = navigator.getNextTurnTile(self);

        if (accept(current, nextTurn)) {
            TileInfo turnTile = navigator.getTileOptions(nextTurn.x, nextTurn.y).stream()
                    .filter(e -> e.in == nextTurn.in && e.out == nextTurn.out)
                    .findFirst().get();

            TileInfo tile = navigator.getNextTile(turnTile.x, turnTile.y, turnTile.index);

            if (accept(nextTurn, tile)) {
                Marker marker = nextTurn.markers.stream().skip(nextTurn.markers.size() - 1).findAny().get();

                Point target = marker.left.shiftTo(marker.right, 0);

                return Arrays.asList(new SpeedAction(Priority.HIGH, 1.0D), new MoveAction(Priority.HIGH, self, target.x, target.y));
            }
        }

        return Collections.emptySet();
    }

    private static boolean accept(TileInfo current, TileInfo next) {
        return abs(current.x - next.x) + abs(current.y - next.y) == 1 && current.in == MathUtil.opposite(next.out);
    }
}
