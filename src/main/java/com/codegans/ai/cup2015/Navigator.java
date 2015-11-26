package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.TileInfo;
import com.codegans.ai.cup2015.model.Wall;
import model.Car;
import model.Direction;
import model.Game;
import model.TileType;
import model.Unit;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private static final int TILE_MARKER_SIZE = 10;

    private volatile CollisionDetector collisionDetector;
    private volatile Tile startTile;
    private volatile int width;
    private volatile int height;
    private volatile double tileSize;
    private volatile double tileMargin;
    private volatile boolean completed = false;

    private Navigator() {
    }

    public static Navigator getInstance(Game game, World world) {
        if (!INSTANCE.completed) {
            INSTANCE.width = world.getWidth();
            INSTANCE.height = world.getHeight();
            INSTANCE.tileSize = game.getTrackTileSize();
            INSTANCE.tileMargin = game.getTrackTileMargin();

            INSTANCE.fetch(world);
        }

        INSTANCE.collisionDetector = new CollisionDetector(world, game, INSTANCE);

        return INSTANCE;
    }

    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    public Collection<TileInfo> getTileOptions(int x, int y) {
        Tile current = startTile;
        Collection<TileInfo> tiles = new ArrayList<>();

        while (current != startTile.prev) {
            if (current.x == x && current.y == y) {
                tiles.add(current.create());
            }

            current = current.next;
        }

        return tiles;
    }

    public TileInfo getCurrentTile(Car car) {
        Tile current = findTile(car.getX(), car.getY(), car.getNextWaypointIndex());

        return current.create(0);
    }

    public TileInfo getNextTile(int x, int y, int nextWaypoint) {
        Tile next = findTile(x, y, nextWaypoint).next;

        return next.create();
    }

    public TileInfo getNextTurnTile(Car car) {
        Tile current = findTile(car.getX(), car.getY(), car.getNextWaypointIndex());
        int i = 0;

        do {
            i++;
            current = current.next;
        } while (current.prev.out == current.out);

        return current.create(i);
    }

    public Collection<Marker> getPath(Car car, int size) {
        return getPath(car, car.getNextWaypointIndex(), size);
    }

    public Collection<Marker> getPath(Unit unit, int nextWaypointIndex, int size) {
        double x = unit.getX();
        double y = unit.getY();

        Tile current = findTile(x, y, nextWaypointIndex);

        if (current != null) {
            Collection<Tile> path = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                if (current.tileMarkers.isEmpty()) {
                    if (current.in == MathUtil.opposite(current.out)) {
                        current.tileMarkers.addAll(linear(current.out, current.x, current.y));
                    } else {
                        current.tileMarkers.addAll(circular(current.in, current.out, current.x, current.y));
                    }
                }

                path.add(current);

                current = current.next;
            }

            Stream<Marker> head = path.stream()
                    .limit(1)
                    .flatMap(e -> e.tileMarkers.stream())
                    .filter(e -> MathUtil.relative(e, x, y) != Direction.LEFT);

            Stream<Marker> tail = path.stream()
                    .limit(size)
                    .skip(1)
                    .flatMap(e -> e.tileMarkers.stream());

            return Stream.concat(head, tail).collect(Collectors.toList());
        }

        return Collections.emptySet();
    }

    private Tile findTile(double x, double y, int nextWaypointIndex) {
        int startX = (int) (x / tileSize);
        int startY = (int) (y / tileSize);

        return findTile(startX, startY, nextWaypointIndex);
    }

    private Tile findTile(int x, int y, int nextWaypointIndex) {
        int radius = 0;

        do {
            Tile current = startTile;

            do {
                if (current.waypointIndex == nextWaypointIndex
                        && (current.x == x - radius && current.y == y
                        || current.x == x + radius && current.y == y
                        || current.x == x && current.y == y - radius
                        || current.x == x && current.y == y + radius)) {
                    return current;
                }

                current = current.next;
            } while (current != startTile);
        } while (radius++ < 4);

        throw new IllegalStateException("Cannot find a tile in (" + x + ";" + y + ";#" + nextWaypointIndex + ")");
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
        Step start = null;

        for (int j = 0; j <= waypoints.length; j++) {
            int[] waypoint = waypoints[j % waypoints.length];

            int finishX = waypoint[0];
            int finishY = waypoint[1];
            Queue<Step> queue = new LinkedList<>();
            Step[][] progress = new Step[width][height];

            LOG.printf("Calculate path: (%d;%d)->(%d;%d)%n", startX, startY, finishX, finishY);

            BitSet column = progress.get(startX);

            if (column == null) {
                progress.set(startX, column = new BitSet(height));
            }

            column.set(startY);
            queue.offer(new Step(start, startX, startY, direction, score));

            while (!queue.isEmpty()) {
                Step step = queue.poll();

                Set<Direction> directions = MathUtil.fromTileType(field[step.x][step.y]);

                if (directions != null) {
                    for (Direction direction : directions) {
                        int x = step.x + MathUtil.dx(direction);
                        int y = step.y + MathUtil.dy(direction);

                        if (progress[x][y] == null) {
                            progress[x][y] = Optional.of(direction);
                            queue.offer(new Step(step, x, y, direction, score));
                        }
                    }
                } else {
                    incomplete = true;
                }
            }

            int x = startX = finishX;
            int y = startY = finishY;
            int k = 1;

            priorityDirection = progress[x][y] != null ? progress[x][y].orElse(priorityDirection) : priorityDirection;

            for (Optional<Direction> direction = progress[x][y]; direction != null && direction.isPresent(); direction = progress[x][y]) {
                route.add(i, new Tile(x, y, (j + k) % waypoints.length, MathUtil.fromTileType(field[x][y])));

                x -= MathUtil.dx(direction.get());
                y -= MathUtil.dy(direction.get());
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
            prev.out = MathUtil.shift(tile.x - prev.x, tile.y - prev.y);
            tile.in = MathUtil.opposite(prev.out);
            tile.prev = prev;
            prev = prev.next = tile;
        }

        startTile = prev;

        if (!incomplete) {
            completed = true;

            LOG.printf("Calculated path: %s%n", route);
        }
    }

    @SuppressWarnings("unchecked")
    private static Optional<Direction>[][] createDirectionArray(int width, int height) {
        return new Optional[width][height];
    }

    private Collection<Marker> linear(Direction out, int x, int y) {
        int dx = MathUtil.dx(out);
        int dy = MathUtil.dy(out);

        double leftX = tileSize - tileMargin;
        double rightX = tileMargin;
        double delta = tileSize / (TILE_MARKER_SIZE + 1);
        double baseX = (x + x + 1 - dx - dy) * tileSize / 2;
        double baseY = (y + y + 1 - dy + dx) * tileSize / 2;

        return IntStream.range(0, TILE_MARKER_SIZE + 2)
                .mapToDouble(i -> delta * i)
                .mapToObj(i -> rotate(x, y, baseX, baseY, leftX, i, rightX, i, dy, dx))
                .collect(Collectors.toList());
    }

    private Collection<Marker> circular(Direction in, Direction out, int x, int y) {
        int dx = MathUtil.dx(out);
        int dy = MathUtil.dy(out);
        int reverse = MathUtil.relative(in, out) == Direction.RIGHT ? -1 : 1;

        double left = (1 + reverse) * tileSize / 2 - reverse * tileMargin;
        double right = (1 - reverse) * tileSize / 2 + reverse * tileMargin;
        double delta = PI / 2 / (TILE_MARKER_SIZE + 1) * reverse;
        double baseX = (x + x + 1 + dx - reverse * dy) * tileSize / 2;
        double baseY = (y + y + 1 + dy + reverse * dx) * tileSize / 2;

        return IntStream.range(0, TILE_MARKER_SIZE + 2)
                .mapToDouble(i -> delta * i)
                .mapToObj(i -> {
                    double cos = cos(i);
                    double sin = sin(i);

                    return rotate(x, y, baseX, baseY, left * cos, left * sin, right * cos, right * sin, -dx, dy);
                })
                .collect(Collectors.toList());
    }

    private static Marker rotate(int x, int y, double baseX, double baseY, double aX, double aY, double bX, double bY, int cos, int sin) {
        return new Marker(
                x, y, baseX + aX * cos + aY * sin,
                baseY - aX * sin + aY * cos,
                baseX + bX * cos + bY * sin,
                baseY - bX * sin + bY * cos
        );
    }

    private static final class Tile {
        private final int x;
        private final int y;
        private final int waypointIndex;
        private final Collection<Marker> tileMarkers = new ArrayList<>(TILE_MARKER_SIZE + 1);
        private final Collection<Wall> tileWalls;
        private Tile prev;
        private Tile next;
        private Direction in;
        private Direction out;

        public Tile(int x, int y, int waypointIndex, Collection<Direction> directions) {
            this.x = x;
            this.y = y;
            this.waypointIndex = waypointIndex;

            double halfSize = INSTANCE.tileSize / 2;
            double margin = INSTANCE.tileMargin;

            tileWalls = Arrays.stream(Direction.values()).filter(e -> !directions.contains(e)).map(e -> {
                int dx = MathUtil.dx(e);
                int dy = MathUtil.dy(e);

                return new Wall(e,
                        (x + x + 1 + dx + dy) * halfSize - dx * margin,
                        (y + y + 1 + dy + dx) * halfSize - dy * margin,
                        (x + x + 1 + dx - dy) * halfSize - dx * margin,
                        (y + y + 1 + dy - dx) * halfSize - dy * margin
                );
            }).collect(Collectors.toList());
        }

        public TileInfo create() {
            return create(waypointIndex);
        }

        public TileInfo create(int i) {
            return new TileInfo(i, x, y, INSTANCE.tileMargin, INSTANCE.tileSize, in, out, new ArrayList<>(tileWalls), new ArrayList<>(tileMarkers));
        }

        @Override
        public String toString() {
            return "(" + x + ";" + y + ";#" + waypointIndex + ";" + in + "->" + out + ")";
        }
    }

}
