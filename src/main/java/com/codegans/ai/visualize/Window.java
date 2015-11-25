package com.codegans.ai.visualize;

import com.codegans.ai.cup2015.MathUtil;
import com.codegans.ai.cup2015.Navigator;
import com.codegans.ai.cup2015.StrategyDelegate;
import com.codegans.ai.cup2015.action.Action;
import com.codegans.ai.cup2015.action.MoveAction;
import com.codegans.ai.cup2015.model.Marker;
import com.codegans.ai.cup2015.model.Point;
import com.codegans.ai.cup2015.model.TileInfo;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import model.Bonus;
import model.Car;
import model.CarType;
import model.Direction;
import model.Game;
import model.Move;
import model.OilSlick;
import model.Player;
import model.Projectile;
import model.ProjectileType;
import model.TileType;
import model.Unit;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2015 17:02
 */
public class Window extends Application {
    private final double padding = 20;
    private final double tileSize = 70;
    private final int size = 10;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Group field = new Group();

        World world = setupWorld();

        Game game = setupGame(world);
        TileType[][] tilesXY = world.getTilesXY();
        Collection<Direction> order = Arrays.asList(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT);

        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                Collection<Direction> directions = MathUtil.fromTileType(tilesXY[i][j]);

                Rectangle rectangle = new Rectangle(game.getTrackTileSize(), game.getTrackTileSize(), Color.web("white"));

                double x = padding + i * game.getTrackTileSize();
                double y = padding + j * game.getTrackTileSize();

                rectangle.setX(x);
                rectangle.setY(y);
                rectangle.setStrokeType(StrokeType.INSIDE);
                rectangle.setStroke(Color.web("black", 0.16));
                rectangle.setStrokeWidth(1);

                if (directions == null || directions.isEmpty()) {
                    rectangle.setFill(Color.web("green"));

                    field.getChildren().add(rectangle);
                } else {
                    field.getChildren().add(rectangle);
                    order.stream()
                            .filter(e -> !directions.contains(e))
                            .forEach(e -> {
                                int dx = MathUtil.dx(e);
                                int dy = MathUtil.dy(e);
                                int ix = (dx + 1) / 2;
                                int iy = (dy + 1) / 2;

                                Line wall = new Line(x + ix * tileSize, y + iy * tileSize, x + ix * tileSize + abs(dy) * tileSize, y + iy * tileSize + abs(dx) * tileSize);

                                wall.setStroke(Color.web("black"));
                                wall.setStrokeWidth(4);

                                field.getChildren().add(wall);
                            });
                }
            }
        }

        Group markers = new Group();

        field.setOnMouseClicked(event -> {
            markers.getChildren().clear();

            StrategyDelegate strategy = new StrategyDelegate();
            Navigator navigator = Navigator.getInstance(game, world);

            double mouseX = event.getSceneX() - padding;
            double mouseY = event.getSceneY() - padding;

            int x = (int) (mouseX / tileSize);
            int y = (int) (mouseY / tileSize);

            for (TileInfo info : navigator.getTileOptions(x, y)) {
                info.walls.forEach(e -> {
                    Line line = new Line(e.left.x + padding, e.left.y + padding, e.right.x + padding, e.right.y + padding);

                    line.setStroke(Color.web("green"));
                    line.setStrokeWidth(1.0D);

                    markers.getChildren().add(line);
                });

                if (event.getButton() == MouseButton.PRIMARY) {
                    Collection<Marker> path = navigator.getPath(setupUnit(mouseX, mouseY, 5, 0), info.index, size);
                    int j = 0;

                    for (Marker e : path) {
                        Circle circle = new Circle(padding + e.left.x, padding + e.left.y, 5, Color.web("red"));
                        Line marker = new Line(padding + e.left.x, padding + e.left.y, padding + e.right.x, padding + e.right.y);

                        marker.setStroke(Color.web("red", 1.0D - (j++ + 3.0D) / (path.size() + 3.0D)));
                        marker.setStrokeWidth(4);

                        markers.getChildren().addAll(marker, circle);
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    com.codegans.ai.cup2015.model.Rectangle r = new com.codegans.ai.cup2015.model.Rectangle(new Point(mouseX, mouseY), 100, 200, new Random().nextDouble() * 2 * PI - PI);

                    List<Point> selfXY = Arrays.asList(new Point(1286.059, 7616.658), new Point(1101.907, 7515.722), new Point(1135.553, 7454.338), new Point(1319.705, 7555.274)).stream()
                            .map(e -> new Point(e.x / 10, e.y / 10)).collect(Collectors.toList());
                    List<Point> obstacleXY = Arrays.asList(new Point(893.678, 7664.707), new Point(995.355, 7480.964), new Point(1117.850, 7548.748), new Point(1016.173, 7732.492)).stream()
                            .map(e -> new Point(e.x / 10, e.y / 10)).collect(Collectors.toList());

                    List<PathElement> selfPath = selfXY.stream().map(e -> new LineTo(e.x + padding, e.y + padding)).collect(Collectors.toList());
                    List<PathElement> obstaclePath = obstacleXY.stream().map(e -> new LineTo(e.x + padding, e.y + padding)).collect(Collectors.toList());

                    selfPath.add(new LineTo(selfXY.get(0).x + padding, selfXY.get(0).y + padding));
                    selfPath.add(0, new MoveTo(selfXY.get(selfXY.size() - 1).x + padding, selfXY.get(selfXY.size() - 1).y + padding));

                    obstaclePath.add(new LineTo(obstacleXY.get(0).x + padding, obstacleXY.get(0).y + padding));
                    obstaclePath.add(0, new MoveTo(obstacleXY.get(obstacleXY.size() - 1).x + padding, obstacleXY.get(obstacleXY.size() - 1).y + padding));

                    Path self = new Path(selfPath);
                    Path obstacle = new Path(obstaclePath);

                    self.setFill(Color.web("red", 0.5));
                    obstacle.setFill(Color.web("brown", 0.5));

                    Path rectangle = new Path(
                            new MoveTo(r.getTopLeft().x + padding, r.getTopLeft().y + padding),
                            new LineTo(r.getTopLeft().x + (r.getTopRight().x - r.getTopLeft().x) / 2 + (r.center.x - r.getTopLeft().x) / 10 + padding, r.getTopLeft().y + (r.getTopRight().y - r.getTopLeft().y) / 2 + (r.center.y - r.getTopLeft().y) / 10 + padding),
                            new LineTo(r.getTopRight().x + padding, r.getTopRight().y + padding),
                            new LineTo(r.getBottomRight().x + padding, r.getBottomRight().y + padding),
                            new LineTo(r.getBottomLeft().x + padding, r.getBottomLeft().y + padding),
                            new LineTo(r.getTopLeft().x + padding, r.getTopLeft().y + padding)
                    );

                    rectangle.setFill(Color.web("blue"));

                    System.out.println("===================== " + r.angle);

                    markers.getChildren().addAll(rectangle, obstacle, self);
                } else if (event.getButton() == MouseButton.MIDDLE) {
                    Move move = new Move();

                    Collection<Action<?>> actions = strategy.debugActions(setupCar(mouseX, mouseY, 1, 1, info.index), world, game, move);

                    MoveAction action = (MoveAction) actions.stream().filter(e -> e.getClass() == MoveAction.class).findFirst().get();

                    Circle circle = new Circle(padding + action.getPoint().x, padding + action.getPoint().y, 5, Color.web("red"));

                    markers.getChildren().addAll(circle);
                }
            }
        });

        root.getChildren().add(field);
        root.getChildren().add(markers);

        primaryStage.setScene(new Scene(
                root,
                padding * 2 + game.getTrackTileSize() * world.getWidth(),
                padding * 2 + game.getTrackTileSize() * world.getHeight(),
                Color.BLACK
        ));
        primaryStage.show();
    }

    private World setupWorld() {
        int[][] waypoints = new int[][]{{8, 9}, {1, 9}, {1, 0}, {1, 11}, {11, 11}, {11, 0}, {5, 0}, {5, 4}, {7, 4}, {7, 2}, {9, 2}, {9, 9}};

        TileType[][] field = {{TileType.LEFT_TOP_CORNER, TileType.VERTICAL, TileType.RIGHT_HEADED_T, TileType.RIGHT_HEADED_T, TileType.RIGHT_HEADED_T, TileType.RIGHT_HEADED_T, TileType.RIGHT_HEADED_T, TileType.LEFT_BOTTOM_CORNER, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.BOTTOM_HEADED_T, TileType.CROSSROADS, TileType.CROSSROADS, TileType.CROSSROADS, TileType.CROSSROADS, TileType.CROSSROADS, TileType.VERTICAL, TileType.RIGHT_HEADED_T, TileType.VERTICAL, TileType.LEFT_BOTTOM_CORNER}, {TileType.RIGHT_TOP_CORNER, TileType.VERTICAL, TileType.LEFT_HEADED_T, TileType.LEFT_HEADED_T, TileType.LEFT_HEADED_T, TileType.LEFT_HEADED_T, TileType.LEFT_HEADED_T, TileType.RIGHT_BOTTOM_CORNER, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.LEFT_TOP_CORNER, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.LEFT_BOTTOM_CORNER, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.LEFT_TOP_CORNER, TileType.VERTICAL, TileType.RIGHT_BOTTOM_CORNER, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.RIGHT_TOP_CORNER, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.RIGHT_BOTTOM_CORNER, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.HORIZONTAL, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.HORIZONTAL}, {TileType.RIGHT_TOP_CORNER, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.VERTICAL, TileType.RIGHT_BOTTOM_CORNER}};

        return new World(0, 0, 0, field.length, field[0].length,
                new Player[0], new Car[0], new Projectile[0], new Bonus[0], new OilSlick[0],
                "test", field, waypoints, null);
    }

    private Game setupGame(World world) {
        return new Game(0, 0, world.getWidth(), world.getHeight(), tileSize, tileSize / 12,
                0, 0, 0, 0, new int[0], 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private Unit setupUnit(double x, double y, double speedX, double speedY) {
        return new Projectile(0, 0, x, y, speedX, speedY, 0, 0, 0, 0, 0, ProjectileType.TIRE);
    }

    private Car setupCar(double x, double y, double speedX, double speedY, int index) {
        return new Car(0, 0, x, y, speedX, speedY, 0, 0, tileSize / 5, tileSize / 4, 0, 0, true,
                CarType.BUGGY, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, index, 0, 0, false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
