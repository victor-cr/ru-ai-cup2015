package com.codegans.ai.cup2015.intrinsic;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.Priority;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.decision.Decision;
import com.codegans.ai.cup2015.model.Line;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
import com.codegans.ai.cup2015.model.Rectangle;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Bonus;
import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;
import static java.lang.StrictMath.signum;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.11.2015 11:05
 */
public class BonusHuntingDecision implements Decision {
    private static final double OVERLAP = 2;
    private static final double THRESHOLD = PI / 20;

    @Override
    public Collection<Action<?>> decide(Car self, World world, Game game, Move move, Navigator navigator) {
        TileInfo nextTurn = navigator.getNextTurnTile(self);
        Collection<Marker> markers = navigator.getPath(self, nextTurn.index);

        Marker last = markers.stream().skip(markers.size() - 1).findAny().get();

        Line first = last.shiftOrthogonal(new Point(self.getX(), self.getY()));

        Point firstCenter = first.center();
        Point lastCenter = last.center();

        Point center = lastCenter.shiftTo(firstCenter, 0);

        Rectangle area = new Rectangle(center, last.length(), new Line(firstCenter, lastCenter).length(), 0);

        // TODO: compare by requirements
        Optional<Bonus> target = Arrays.stream(world.getBonuses())
                .filter(e -> abs(self.getAngleTo(e)) < THRESHOLD)
                .filter(e -> area.belongs(new Point(e.getX(), e.getY())))
                .sorted((a, b) -> Double.compare(self.getDistanceTo(a), self.getDistanceTo(b)))
                .findFirst();

        if (target.isPresent()) {
            Bonus bonus = target.get();

            Point bonusPoint = new Point(bonus);
            double bonusSize = min(bonus.getHeight(), bonus.getWidth());
            double carSize = self.getWidth();

            Line bonusLine = last.shiftOrthogonal(bonusPoint);

            Point lineCenter = bonusLine.center();

            double shift = new Line(bonusPoint, lineCenter).length() - bonusSize / 2 - carSize / 2 + OVERLAP;

            if (Double.compare(shift, 0) <= 0) {
                return Collections.singleton(new MoveAction(Priority.NORMAL, self, lineCenter.x, lineCenter.y));
            }

            double gravity = signum(MathUtil.orientedArea(firstCenter, lastCenter, bonusPoint)) * 2 * shift / bonusLine.length();

            Point targetPoint = bonusLine.point(gravity);

            return Collections.singleton(new MoveAction(Priority.NORMAL, self, targetPoint.x, targetPoint.y));
        }

        return Collections.emptySet();
    }
}
