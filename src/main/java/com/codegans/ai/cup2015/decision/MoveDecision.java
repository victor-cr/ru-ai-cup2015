package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.model.Marker;
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

        double x = path.stream().mapToDouble(Marker::pointX).average().orElse(0);
        double y = path.stream().mapToDouble(Marker::pointY).average().orElse(0);

        return Collections.singleton(new MoveAction(Priority.NORMAL, self, x, y));
    }
}
