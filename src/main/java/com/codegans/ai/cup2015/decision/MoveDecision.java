package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
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
 * @since 19.11.2015 10:40
 */
public class MoveDecision implements Decision {
    private static final int WINDOW = 2;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        Collection<Marker> path = navigator.getPath(self, self.getNextWaypointIndex(), WINDOW);

        Point target = path.stream().map(e -> e.left.shiftTo(e.right, 0)).reduce(new Point(self.getX(), self.getY()), (a, b) -> a.shiftTo(b, 0));

        return Collections.singleton(new MoveAction(Priority.NONE, self, target.x, target.y));
    }
}
