import com.codegans.StrategyDelegate;
import model.Car;
import model.Game;
import model.Move;
import model.World;

public final class MyStrategy implements Strategy {
    private final StrategyDelegate delegate = new StrategyDelegate();

    @Override
    public void move(Car self, World world, Game game, Move move) {
        delegate.move(self, world, game, move);
    }
}
