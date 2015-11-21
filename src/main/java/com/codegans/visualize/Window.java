package com.codegans.visualize;

import com.codegans.ai.cup2015.Navigator;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import model.Bonus;
import model.Car;
import model.Game;
import model.OilSlick;
import model.Player;
import model.Projectile;
import model.TileType;
import model.Unit;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2015 17:02
 */
public class Window extends Application {
    private final double padding = 20;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Group field = new Group();

        World world = setupWorld();

        Game game = setupGame(world);

        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                Rectangle rectangle = new Rectangle(game.getTrackTileSize(), game.getTrackTileSize(), Color.web("white"));

                rectangle.setX(padding + i * game.getTrackTileSize());
                rectangle.setY(padding + j * game.getTrackTileSize());
                rectangle.setStrokeType(StrokeType.OUTSIDE);
                rectangle.setStroke(Color.web("black", 0.16));
                rectangle.setStrokeWidth(4);

                field.getChildren().add(rectangle);
            }
        }

        Group markers = new Group();

        field.setOnMouseClicked(event -> {
            markers.getChildren().clear();

            Navigator navigator = Navigator.getInstance(game, world);

            for (int i = 0; i < world.getWaypoints().length; i++) {
                navigator.getPathNew(setupUnit(event.getSceneX() - padding, event.getSceneY() - padding, 5, 0), i, 10).forEach(e -> {
                    Line marker = new Line(padding + e.leftX, padding + e.leftY, padding + e.rightX, padding + e.rightY);

                    marker.setStroke(Color.web("red", 0.5));
                    marker.setStrokeWidth(4);

                    markers.getChildren().add(marker);
                });
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
        int[][] waypoints = new int[][]{{1, 0}, {0, 1}, {2, 2}};
        TileType[][] field = new TileType[3][3];

        field[0][0] = TileType.LEFT_TOP_CORNER;
        field[0][1] = TileType.VERTICAL;
        field[0][2] = TileType.LEFT_BOTTOM_CORNER;

        field[1][0] = TileType.HORIZONTAL;
        field[1][1] = TileType.EMPTY;
        field[1][2] = TileType.HORIZONTAL;

        field[2][0] = TileType.RIGHT_TOP_CORNER;
        field[2][1] = TileType.VERTICAL;
        field[2][2] = TileType.RIGHT_BOTTOM_CORNER;

        return new World(0, 0, 0, field.length, field[0].length,
                new Player[0], new Car[0], new Projectile[0], new Bonus[0], new OilSlick[0],
                "test", field, waypoints, null);
    }

    private Game setupGame(World world) {
        return new Game(0, 0, world.getWidth(), world.getHeight(), 200, 20,
                0, 0, 0, 0, new int[0], 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private Unit setupUnit(double x, double y, double speedX, double speedY) {
        return new Projectile(0, 0, x, y, speedX, speedY, 0, 0, 0, 0, 0, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
