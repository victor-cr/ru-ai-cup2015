package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
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
    private static final int WINDOW = 3;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
//        Collection<Direction> path = navigator.getPath(self, WINDOW);

        int wpX = self.getNextWaypointX();
        int wpY = self.getNextWaypointY();
        double tileSize = game.getTrackTileSize();
        double tileMargin = game.getTrackTileMargin();

        double x = (wpX + 0.5D) * tileSize;
        double y = (wpY + 0.5D) * tileSize;

        double cornerTileOffset = 0.25D * tileSize;

        switch (world.getTilesXY()[wpX][wpY]) {
            case LEFT_TOP_CORNER:
                x += cornerTileOffset;
                y += cornerTileOffset;
                break;
            case RIGHT_TOP_CORNER:
                x -= cornerTileOffset;
                y += cornerTileOffset;
                break;
            case LEFT_BOTTOM_CORNER:
                x += cornerTileOffset;
                y -= cornerTileOffset;
                break;
            case RIGHT_BOTTOM_CORNER:
                x -= cornerTileOffset;
                y -= cornerTileOffset;
                break;
            default:
        }

        double angle = self.getAngleTo(x, y);

//        if ((x * x + y * y) * abs(angle) > 2.5D * 2.5D * PI) {
//            return Collections.singleton(new BrakeAction(12));
//        }

        return Collections.singleton(new MoveAction(12, self, x, y));
    }
}
