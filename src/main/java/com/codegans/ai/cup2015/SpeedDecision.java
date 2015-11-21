package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.SpeedAction;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
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
 * @since 19.11.2015 10:40
 */
public class SpeedDecision implements Decision {
    private static final int LONG_RUN = 6;
    private static final int SHORT_RUN = 3;
    private static final int TURN = 1;

    private final Logger log = LoggerFactory.getLogger();

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        Collection<Direction> path = navigator.getPath(self, self.getNextWaypointIndex(), LONG_RUN);

        log.printf("Path: %s%n", path);

        int i = 0;
        Direction first = null;

        for (Direction direction : path) {
            if (first == null) {
                first = direction;
            } else if (first != direction) {
                break;
            }
            i++;
        }

        if (i <= TURN) {
            return Collections.singleton(new SpeedAction(12, 0.3D));
        }

        if (i <= SHORT_RUN) {
            return Collections.singleton(new SpeedAction(12, 0.7D));
        }

        return Collections.singleton(new SpeedAction(12, 1.0D));
    }
}
