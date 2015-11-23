package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Direction;
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
    private static final int THRESHOLD = 5;
    private final Logger log = LoggerFactory.getLogger();

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        TileInfo turnTile = navigator.getNextTurnTile(self);
        TileInfo currentTile = navigator.getCurrentTile(self);

        if (turnTile.index >= THRESHOLD) {
            return Collections.emptySet();
        }

        double calc;

        if (currentTile.in != MathUtil.opposite(currentTile.out)) {
            calc = -bestSide(currentTile.in, currentTile.out);
        } else {
            calc = bestSide(turnTile.in, turnTile.out);

            if (turnTile.index != 1) {
                calc *= -1;
            }
        }

        Collection<Marker> path = navigator.getPath(self, turnTile.index + 1);

        double coefficient = calc;

        Point target = path.stream()
                .filter(e -> turnTile.index == 1 || e.x != turnTile.x || e.y != turnTile.y)
                .map(e -> e.left.shiftTo(e.right, coefficient))
                .reduce(new Point(self.getX(), self.getY()), (a, b) -> a.shiftTo(b, 0));

        log.printf("Calculated next point: %s with %.3f%n", target, calc);

        return Collections.singleton(new MoveAction(Priority.HIGH, self, target.x, target.y));
    }

    private double bestSide(Direction in, Direction out) {
        return 0 - MathUtil.dx(MathUtil.relative(in, out)) / 2.0D;
    }
}
