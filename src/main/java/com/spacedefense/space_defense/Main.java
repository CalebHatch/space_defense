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

    /**
     * Drives the program. Will try to launch the program, and will throw an error if unsuccessful.
     */
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

    /**
     * The bulk of the program. A window is created, sprites are loaded, and the way in which those sprites interact
     * are all defined. Calls upon Hitboxes, SpeedsAngles, and Sprites classes.
     * @param mainPane is the main window in which the game will run inside.
     */
    public void start(@NotNull Stage mainPane) {
        // Creates the window for the game to run in
        BorderPane main = new BorderPane();
        Scene mainLevel = new Scene(main);
        mainPane.setScene(mainLevel);

        Canvas backdrop = new Canvas(800, 600); // Defines resolution of the window
        GraphicsContext game = backdrop.getGraphicsContext2D();
        main.setCenter(backdrop); // Creates backdrop for all assets to be put on

        // Loads the sprite for the background
        Sprites background = new Sprites(); // load new sprite
        background.point.set(400, 300); // places sprite in window
        background.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\space.png");

        // Loads the sprite for the player's ship
        Sprites ship = new Sprites();
        ship.point.set(400, 550);
        ship.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\ship.png");

        int droneAmount = 10; // Defines the amount of drones that the player will be facing
        ArrayList<Sprites> drones = new ArrayList<>(); // An ArrayList of drones to streamline interaction

        Sprites drone = null;
        for (int i = 0; i < droneAmount; i++) { // Creates a new drone sprite each time up to the defined drone amount
            drone = new Sprites();
            drone.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\drone.png");

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

        // If a drone hits the player's ship, the player loses 50 points
        if (ship.overlap(drone)) {
            playerScore -= 50; // Subtract from player score
        }

        // An array list for the projectiles tha the player's ship fires
        ArrayList<Sprites> projectiles = new ArrayList<>();

        // Starts the game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            public void handle(long t) {
                background.render(game); // Renders background sprite
                ship.render(game); // Renders player ship sprite

                // Each time a projectile is generated, the sprite for it will be rendered
                for (Sprites projectile : projectiles)
                    projectile.render(game);

                // Each time a drone is generated, the sprite for it will be rendered
                for (Sprites drone : drones)
                    drone.render(game);

                // 60 times per second (matching default framerate), the drone condition will be updated
                for (Sprites drone : drones)
                    drone.update(1 / 60.0);

                // Defines user controls
                if (pressedKeys.contains("A")) // Use A to rotate to the left
                    ship.rotation -= 2; // - is left
                if (pressedKeys.contains("D")) // Use D to rotate to the right
                    ship.rotation += 2; // + is right

                // Allows the player to press the space key to fire a projectile
                if (pressedOnce.contains("SPACE")) { // The space key can not be held down like the other keys can
                    // Once the space key is pressed, the sprite for the projectile will be accessed
                    Sprites projectile = new Sprites();
                    projectile.setImage("C:\\Users\\Caleb\\Documents\\Fall 2022\\space_defense\\src\\images\\projectile.png");

                    projectile.point.set(ship.point.x, ship.point.y); // Set projectile initial point at ship's point

                    projectile.velocity.setVelocity(300); // Defines how fast the projectile will go
                    projectile.velocity.setAngle(ship.rotation); // Projectile will face same direction as the ship
                    projectile.rotation = ship.rotation;
                    projectiles.add(projectile); // Adds projectile to the ArrayList

                    pressedOnce.clear(); // Clears input from the ArrayList once the actions above are performed
                }

                // Loop to handle the deletion of projectiles after 8 seconds. Memory leak will occur otherwise
                // However big the ArrayList of projectiles is, the loop will occur
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