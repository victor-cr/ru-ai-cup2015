package com.codegans.ai.cup2015.decision;

import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.action.Action;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:40
 */
public interface Decision {
    Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator);
}
