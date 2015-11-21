package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.TileInfo;
import model.Car;
import model.Direction;
import model.Game;
import model.TileType;
import model.Unit;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:14
 */
public class Navigator {
    private static final Navigator INSTANCE = new Navigator();
    private static final Logger LOG = LoggerFactory.getLogger();

    private volatile Tile startTile;
    private volatile int width;
    private volatile int height;
    private volatile double tileSize;
    private volatile double tileMargin;
    private volatile boolean completed = false;

    public static Navigator getInstance(Game game, World world) {
        if (!INSTANCE.completed) {
            INSTANCE.width = world.getWidth();
            INSTANCE.height = world.getHeight();
            INSTANCE.tileSize = game.getTrackTileSize();
            INSTANCE.tileMargin = game.getTrackTileMargin();

            INSTANCE.fetch(world);
        }

        return INSTANCE;
    }

    public int positionX(Unit unit) {
        return (int) (unit.getX() / tileSize);
    }

    public int positionY(Unit unit) {
        return (int) (unit.getY() / tileSize);
    }

    public TileInfo getCurrentTile(Car car) {
        Tile current = findTile(car.getX(), car.getY(), car.getNextWaypointIndex());

        return new TileInfo(0, current.x, current.y, current.in, current.out);
    }

    public TileInfo getNextTurnTile(Car car) {
        Tile current = findTile(car.getX(), car.getY(), car.getNextWaypointIndex());
        int i = 0;

        do {
            i++;
            current = current.next;
        } while (current.prev.out == current.out);

        return new TileInfo(i, current.x, current.y, current.in, current.out);
    }

    public Collection<Marker> getPath(Unit unit, int nextWaypointIndex, int size) {
        double x = unit.getX();
        double y = unit.getY();

        Tile current = findTile(x, y, nextWaypointIndex);

        if (current != null) {
            int parts = 10;
            Collection<Marker> path = new ArrayList<>(size * parts);

            for (int i = 0; i < size; i++) {
                if (current.in == DirectionUtil.opposite(current.out)) {
                    path.addAll(linear(current.out, current.x, current.y, parts));
                } else {
                    path.addAll(circular(current.in, current.out, current.x, current.y, parts));
                }

                current = current.next;
            }

            for (Iterator<Marker> i = path.iterator(); i.hasNext(); ) {
                Marker e = i.next();

                if ((e.rightX - e.leftX) * (y - e.leftY) - (e.rightY - e.leftY) * (x - e.leftX) > 0) {
                    i.remove();
                } else {
                    break;
                }
            }

            return path;
        }

        return Collections.emptySet();
    }

    private Tile findTile(double x, double y, int nextWaypointIndex) {
        int startX = (int) (x / tileSize);
        int startY = (int) (y / tileSize);

        return findTile(startX, startY, nextWaypointIndex);
    }

    private Tile findTile(int x, int y, int nextWaypointIndex) {
        Tile current = startTile;

        while (current != startTile.prev && (current.x != x || current.y != y || current.waypointIndex != nextWaypointIndex)) {
            current = current.next;
        }

        if (current.x == x && current.y == y && current.waypointIndex == nextWaypointIndex) {
            return current;
        }

        throw new IllegalStateException("Cannot find a tile: (" + x + ";" + y + ";#" + nextWaypointIndex + ")");
    }

    private synchronized void fetch(World world) {
        if (completed) {
            return;
        }

        LOG.layout(world);

        TileType[][] field = world.getTilesXY();
        int[][] waypoints = world.getWaypoints();
        List<Tile> route = new LinkedList<>();

        int i = 0;
        int startX = waypoints[0][0];
        int startY = waypoints[0][1];
        boolean incomplete = false;

        for (int j = 0; j <= waypoints.length; j++) {
            int[] waypoint = waypoints[j % waypoints.length];

            int finishX = waypoint[0];
            int finishY = waypoint[1];
            Queue<Point> queue = new LinkedList<>();
            Optional<Direction>[][] progress = createDirectionArray(width, height);

            LOG.printf("(%d;%d)->(%d;%d)%n", startX, startY, finishX, finishY);

            progress[startX][startY] = Optional.empty();
            queue.offer(new Point(startX, startY));

            while (!queue.isEmpty()) {
                Point point = queue.poll();

                if (finishX == point.x && finishY == point.y) {
                    break;
                }

                Set<Direction> directions = DirectionUtil.fromTileType(field[point.x][point.y]);

                if (directions != null) {
                    for (Direction direction : directions) {
                        int x = point.x + DirectionUtil.dx(direction);
                        int y = point.y + DirectionUtil.dy(direction);

                        if (progress[x][y] == null) {
                            progress[x][y] = Optional.of(direction);
                            queue.offer(new Point(x, y));
                        }
                    }
                } else {
                    incomplete = true;
                }
            }

            int x = startX = finishX;
            int y = startY = finishY;
            int k = 1;

            for (Optional<Direction> direction = progress[x][y]; direction != null && direction.isPresent(); direction = progress[x][y]) {
                route.add(i, new Tile(x, y, (j + k) % waypoints.length));

                x -= DirectionUtil.dx(direction.get());
                y -= DirectionUtil.dy(direction.get());
                k = 0;
            }

            LOG.printf("Sub-path: %s%n", route.subList(i, route.size()));

            i = route.size();
        }

        String message = Arrays.asList(waypoints).stream()
                .map(e -> "(" + e[0] + ";" + e[1] + ")")
                .collect(Collectors.joining());

        LOG.printf("%s%n", message);

        Tile prev = route.get(route.size() - 1);

        LOG.printf("Raw path: %s%n", route);

        for (Tile tile : route) {
            prev.out = DirectionUtil.shift(tile.x - prev.x, tile.y - prev.y);
            tile.in = DirectionUtil.opposite(prev.out);
            tile.prev = prev;
            prev = prev.next = tile;
        }

        startTile = prev;

        if (!incomplete) {
            completed = true;
        }

        LOG.printf("Calculated path: %s%n", route);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Direction>[][] createDirectionArray(int width, int height) {
        return new Optional[width][height];
    }

    private Collection<Marker> linear(Direction out, int x, int y, int markers) {
        List<Marker> markerList = new ArrayList<>(markers + 2);
        int dx = DirectionUtil.dx(out);
        int dy = DirectionUtil.dy(out);

//        LOG.printf("%n (%d;%d;%s)%n", x, y, out);

        double aX = tileMargin;
        double bX = tileSize - tileMargin;
        double delta = tileSize / (markers + 1);
        double baseX = (x + x + 1 - dy - dx) * tileSize / 2;
        double baseY = (y + y + 1 + dx - dy) * tileSize / 2;

        for (int i = 0; i <= markers; i++) {
            double abY = delta * i;

            Marker marker = rotate(baseX, baseY, aX, abY, bX, abY, dy, dx);

            markerList.add(marker);

//            LOG.printf("#%d: (%.3f;%.3f)->(%.3f;%.3f)%n", i, mark.leftX, mark.leftY, mark.rightX, mark.rightY);
        }

        markerList.add(rotate(baseX, baseY, aX, tileSize, bX, tileSize, dy, dx));

        return markerList;
    }

    private Collection<Marker> circular(Direction in, Direction out, int x, int y, int markers) {
        List<Marker> markerList = new ArrayList<>(markers + 2);

        int dx = -DirectionUtil.dx(out);
        int dy = -DirectionUtil.dy(out);

//        LOG.printf("%n (%d;%d;%s)%n", x, y, out);

        double inner = tileMargin;
        double outer = tileSize - tileMargin;
        double delta = PI / 2 / (markers + 1);
        double baseX = (x + x + 1 - dy - dx) * tileSize / 2;
        double baseY = (y + y + 1 + dx - dy) * tileSize / 2;

//        LOG.printf("%n (%d;%d;%s->%s)%n", x, y, in, out);

        for (int i = 0; i <= markers; i++) {
            double cos = cos(delta * i);
            double sin = sin(delta * i);

            double aX = outer * cos;
            double aY = outer * sin;
            double bX = inner * cos;
            double bY = inner * sin;

            Marker marker = rotate(baseX, baseY, aX, aY, bX, bY, dy, dx);

            markerList.add(marker);

//            LOG.printf("#%d: (%.3f;%.3f)->(%.3f;%.3f)%n", i, mark.leftX, mark.leftY, mark.rightX, mark.rightY);
        }

        markerList.add(rotate(baseX, baseY, 0, outer, 0, inner, dy, dx));

        if (DirectionUtil.relative(in, out) == Direction.RIGHT) {
            Collections.reverse(markerList);
        }

        return markerList;
    }

    private static Marker rotate(double baseX, double baseY, double aX, double aY, double bX, double bY, int cos, int sin) {
        return new Marker(
                baseX + aX * cos + aY * sin,
                baseY - aX * sin + aY * cos,
                baseX + bX * cos + bY * sin,
                baseY - bX * sin + bY * cos
        );
    }

    private static final class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Tile {
        private final int x;
        private final int y;
        private final int waypointIndex;
        private Tile prev;
        private Tile next;
        private Direction in;
        private Direction out;

        public Tile(int x, int y, int waypointIndex) {
            this.x = x;
            this.y = y;
            this.waypointIndex = waypointIndex;
        }

        @Override
        public String toString() {
            return "(" + x + ";" + y + ";#" + waypointIndex + ";" + in + "->" + out + ")";
        }
    }

}
