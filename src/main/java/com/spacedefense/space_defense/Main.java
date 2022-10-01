package com.spacedefense.space_defense;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Main extends Application {

    int playerScore;
    int scoreIncrement = 50;

    public static void main(String[] args) {
        {
            try {
                launch(args);
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    public void start(@NotNull Stage mainPane) {
        BorderPane main = new BorderPane();
        Scene mainLevel = new Scene(main);
        mainPane.setScene(mainLevel);

        Canvas backdrop = new Canvas(800, 600);
        GraphicsContext game = backdrop.getGraphicsContext2D();
        main.setCenter(backdrop);

        Sprites background = new Sprites();
        background.point.set(400, 300);
        background.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\space.png");

        Sprites ship = new Sprites();
        ship.point.set(400, 550);
        ship.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\ship.png");

        int droneAmount = 10;
        ArrayList<Sprites> drones = new ArrayList<>();

        for (int i = 0; i < droneAmount; i++) {
            Sprites drone = new Sprites();
            drone.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\drone.png");

            if (ship.overlap(drone)) {
                playerScore -= 50;
            }

            double x = 500 * Math.random();
            double y = 400 * Math.random();
            drone.point.set(x, y);

            drone.velocity.setVelocity(15);
            drone.velocity.setAngle(90);

            drones.add(drone);
        }

        ArrayList<String> pressedKeys = new ArrayList<>();
        ArrayList<String> pressedOnce = new ArrayList<>();

        mainLevel.setOnKeyPressed(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString();
                    if (!pressedKeys.contains(keyName) || (!pressedOnce.contains(keyName))) {
                        pressedKeys.add(keyName);
                        pressedOnce.add(keyName);
                    }
                }
        );

        mainLevel.setOnKeyReleased(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString();
                    pressedKeys.remove(keyName);
                }
        );

        ArrayList<Sprites> projectiles = new ArrayList<>();

        AnimationTimer gameLoop = new AnimationTimer() {
            public void handle(long t) {
                background.render(game);
                ship.render(game);

                for (Sprites projectile : projectiles)
                    projectile.render(game);

                for (Sprites drone : drones)
                    drone.render(game);

                for (Sprites drone : drones)
                    drone.update(1 / 60.0);

                if (pressedKeys.contains("A"))
                    ship.rotation -= 2;
                if (pressedKeys.contains("D"))
                    ship.rotation += 2;

                if (pressedOnce.contains("SPACE")) {
                    Sprites projectile = new Sprites();
                    projectile.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\projectile.png");

                    projectile.point.set(ship.point.x, ship.point.y);

                    projectile.velocity.setVelocity(300);
                    projectile.velocity.setAngle(ship.rotation);

                    projectile.rotation = ship.rotation;
                    projectiles.add(projectile);

                    pressedOnce.clear();
                }

                for (int i = 0; i < projectiles.size(); i++) {
                    Sprites projectile = projectiles.get(i);
                    projectile.update(1 / 60.0);

                    if (projectile.travelTime > 8)
                        projectiles.remove(projectile);
                }

                for (int projectileNumber = 0; projectileNumber < projectiles.size(); projectileNumber++) {
                    Sprites projectile = projectiles.get(projectileNumber);

                    for (int droneNumber = 0; droneNumber < drones.size(); droneNumber++) {
                        Sprites drone = drones.get(droneNumber);
                        if (projectile.overlap(drone)) {
                            drones.remove(droneNumber);
                            projectiles.remove(projectileNumber);
                            playerScore += 50;
                        }
                    }
                }

                game.setFont(new Font("Impact", 48));
                game.setLineWidth(4);
                game.setFill(Color.GRAY);
                game.setStroke(Color.LIGHTGRAY);

                String scoreText = "Score: " + playerScore;
                game.fillText(scoreText, 270, 80);
                game.strokeText(scoreText, 270, 80);

                if (playerScore == droneAmount * scoreIncrement) {
                    game.setFont(new Font("Impact", 48));
                    game.setLineWidth(4);
                    game.setFill(Color.DARKRED);
                    game.setStroke(Color.BLACK);

                    String winnerText = "You Win!";
                    game.fillText(winnerText, 300, 300);
                    game.strokeText(winnerText, 300, 300);
                }
            }
        };

        mainPane.setTitle("Space Defense!");
        gameLoop.start();
        mainPane.show();
    }
}