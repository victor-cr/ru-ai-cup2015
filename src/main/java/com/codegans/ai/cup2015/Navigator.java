package com.codegans.ai.cup2015;

import com.codegans.ai.cup2015.log.Logger;
import com.codegans.ai.cup2015.log.LoggerFactory;
import model.Direction;
import model.Game;
import model.TileType;
import model.Unit;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.hypot;

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

    public Collection<Direction> getPath(int startX, int startY, int nextWaypointIndex, int size) {
        LOG.printf("Get %d tile(s) path from (%d;%d;#%d)%n", size, startX, startY, nextWaypointIndex);

        Collection<Direction> path = new ArrayList<>(size);
        Tile current = startTile;

        while (current != startTile.prev && (current.x != startX || current.y != startY || current.waypointIndex != nextWaypointIndex)) {
            current = current.next;
        }

        if (current.x == startX && current.y == startY && current.waypointIndex == nextWaypointIndex) {
            path.add(current.in);

            for (int i = 0; current != null && i < size; i++) {
                path.add(current.out);
                current = current.next;
            }
        }

        return path;
    }

    public Collection<Direction> getPath(Unit unit, int nextWaypointIndex, int size) {
        int startX = (int) (unit.getX() / tileSize);
        int startY = (int) (unit.getY() / tileSize);

        return getPath(startX, startY, nextWaypointIndex, size);
    }

    public int positionX(Unit unit) {
        return (int) (unit.getX() / tileSize);
    }

    public int positionY(Unit unit) {
        return (int) (unit.getY() / tileSize);
    }

    public Collection<Mark> getPathNew(Unit unit, int nextWaypointIndex, int size) {
        int startX = (int) (unit.getX() / tileSize);
        int startY = (int) (unit.getY() / tileSize);

        double speed = hypot(unit.getSpeedX(), unit.getSpeedY());

        Collection<Mark> path = new ArrayList<>(size);
        Tile current = startTile;

        while (current != startTile.prev && (current.x != startX || current.y != startY || current.waypointIndex != nextWaypointIndex)) {
            current = current.next;
        }

        if (current.x == startX && current.y == startY && current.waypointIndex == nextWaypointIndex) {
            if (current.in == DirectionUtil.opposite(current.out)) {
                return linear(current.out, current.x, current.y, 10);
            }

            int x = current.x;
            int y = current.y;

            return Arrays.asList(current.in, current.out).stream()
                    .map(e -> {
                        switch (e) {
                            case DOWN:
                                return new Mark(x * tileSize + tileMargin, (y + 1) * tileSize, (x + 1) * tileSize - tileMargin, (y + 1) * tileSize);
                            case LEFT:
                                return new Mark(x * tileSize, y * tileSize + tileMargin, x * tileSize, (y + 1) * tileSize - tileMargin);
                            case UP:
                                return new Mark(x * tileSize + tileMargin, y * tileSize, (x + 1) * tileSize - tileMargin, y * tileSize);
                            case RIGHT:
                                return new Mark((x + 1) * tileSize, y * tileSize + tileMargin, (x + 1) * tileSize, (y + 1) * tileSize - tileMargin);
                            default:
                                return null;
                        }
                    }).collect(Collectors.toList());
        }

        return path;
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

//        route.add(new Tile(startX, startY, 1));

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

//    private static Mark mark(Direction in, Direction out, int x, int y, int position) {
//        switch (e) {
//            case DOWN:
//                return new Mark(x * tileSize + tileMargin, (y + 1) * tileSize, (x + 1) * tileSize - tileMargin, (y + 1) * tileSize);
//            case LEFT:
//                return new Mark(x * tileSize, y * tileSize + tileMargin, x * tileSize, (y + 1) * tileSize - tileMargin);
//            case UP:
//                return new Mark(x * tileSize + tileMargin, y * tileSize, (x + 1) * tileSize - tileMargin, y * tileSize);
//            case RIGHT:
//                return new Mark((x + 1) * tileSize, y * tileSize + tileMargin, (x + 1) * tileSize, (y + 1) * tileSize - tileMargin);
//            default:
//                return null;
//        }
//    }

    private Collection<Mark> linear(Direction direction, int x, int y, int markers) {
        List<Mark> markList = new ArrayList<>(markers + 2);
        int dx = DirectionUtil.dx(direction); // -1: Left, 1: Right
        int dy = DirectionUtil.dy(direction); // -1: Up, 1: Down

        double norm = dx == -1 || dy == -1 ? tileSize : 0;
        double delta = tileSize / (markers + 1);

        LOG.printf("%n (%d;%d;%s)%n", x, y, direction);

        double baseX = x * tileSize + norm;
        double baseY = y * tileSize + norm;
        double deltaX = dx * delta;
        double deltaY = dy * delta;

        for (int i = 0; i <= markers; i++) {
            double aX = baseX + dy * tileMargin + deltaX * i;
            double aY = baseY + dx * tileMargin + deltaY * i;
            double bX = baseX + dy * (tileSize - tileMargin) + deltaX * i;
            double bY = baseY + dx * (tileSize - tileMargin) + deltaY * i;

            markList.add(new Mark(aX, aY, bX, bY));
            LOG.printf("#%d: (%.3f;%.3f)->(%.3f;%.3f)%n", i, aX, aY, bX, bY);
        }

        return markList;
    }

    private Collection<Mark> circular(Direction in, Direction out, int x, int y, int markers) {
        List<Mark> markList = new ArrayList<>(markers + 2);

        int inDx = DirectionUtil.dx(in); // -1: Left, 1: Right
        int inDy = DirectionUtil.dy(in); // -1: Up, 1: Down
        int outDx = DirectionUtil.dx(out); // -1: Left, 1: Right
        int outDy = DirectionUtil.dy(out); // -1: Up, 1: Down

        double inNorm = inDx == -1 || inDy == -1 ? tileSize : 0;
        double outNorm = inDx == -1 || inDy == -1 ? tileSize : 0;
        double angle = PI / 2 / (markers + 1);

        LOG.printf("%n (%d;%d;%s)%n", x, y, direction);

        double baseX = x * tileSize + inNorm;
        double baseY = y * tileSize + inNorm;

        for (int i = 0; i <= markers; i++) {
            double aX = baseX + dy  + deltaX * i;
            double aY = baseY + dx  + deltaY * i;
            double bX = baseX + dy * tileSize + deltaX * i;
            double bY = baseY + dx * tileSize + deltaY * i;

            markList.add(new Mark(aX, aY, bX, bY));
            LOG.printf("#%d: (%.3f;%.3f)->(%.3f;%.3f)%n", i, aX, aY, bX, bY);
        }

        return markList;
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

    public static final class Path {
        private final double padding;
        private final Collection<Mark> marks = new ArrayList<>();

        public Path(double padding) {
            this.padding = padding;
        }

        public Collection<Mark> getMarks() {
            return Collections.unmodifiableCollection(marks);
        }
    }

    public static final class Mark {
        public final double leftX;
        public final double leftY;
        public final double rightX;
        public final double rightY;

        private Mark(double leftX, double leftY, double rightX, double rightY) {
            this.leftX = leftX;
            this.leftY = leftY;
            this.rightX = rightX;
            this.rightY = rightY;
        }
    }
}
