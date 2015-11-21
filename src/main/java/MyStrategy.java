import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.decision.MoveDecision;
import com.codegans.ai.cup2015.decision.SpeedDecision;
import com.codegans.ai.cup2015.decision.TurnDecision;
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

public final class MyStrategy implements Strategy {
    private final Logger log = LoggerFactory.getLogger();
    private final Collection<Decision> decisions = Arrays.asList(
            new MoveDecision(),
            new SpeedDecision(),
            new TurnDecision()
    );

    @Override
    public void move(Car self, World world, Game game, Move move) {
        if (self.isFinishedTrack()) {
            log.print("Race is over\n");
            return;
        }

        Navigator navigator = Navigator.getInstance(game, world);

        log.turn(world);
        log.waypoint(self);

        decisions.stream()
                .map(e -> e.decide(self, world, game, move, navigator))
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toMap(Action::getClass, e -> e, (l, r) -> l, HashMap::new))
                .values().stream()
                .peek(log::action)
                .forEach(e -> e.apply(move));
    }
}
