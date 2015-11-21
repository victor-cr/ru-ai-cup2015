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
 * @since 19.11.2015 10:40
 */
public class SpeedDecision implements Decision {
    private static final int WINDOW = 6;
    private static final int SHORT_RUN = 20;
    private static final int TURN = 10;

    private final Logger log = LoggerFactory.getLogger();

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        return Collections.singleton(new SpeedAction(Priority.NORMAL, 1.0D));
    }
}
