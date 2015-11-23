package com.codegans;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.decision.MoveDecision;
import com.codegans.ai.cup2015.decision.SpeedDecision;
import com.codegans.ai.cup2015.decision.TurnDecision;
import com.codegans.ai.cup2015.decision.UnstuckDecision;
import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 23.11.2015 21:29
 */
public class StrategyDelegate {
    private final Logger log = LoggerFactory.getLogger();
    private final Collection<Decision> decisions = Arrays.asList(
            new MoveDecision(),
            new SpeedDecision(),
            new TurnDecision(),
            new UnstuckDecision()
    );

    public Collection<Action<?>> debugActions(Car self, World world, Game game, Move move) {
        Navigator navigator = Navigator.getInstance(game, world);

        log.turn(world);
        log.car(self, navigator);

        return decisions.stream()
                .map(e -> e.decide(self, world, game, move, navigator))
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toMap(Action::getClass, e -> e, (l, r) -> l, HashMap::new))
                .values().stream()
                .peek(log::action)
                .collect(Collectors.toList());
    }

    public void move(Car self, World world, Game game, Move move) {
        if (self.isFinishedTrack()) {
            log.stop(self);
            return;
        }

        debugActions(self, world, game, move).forEach(e -> e.apply(move));
    }
}
