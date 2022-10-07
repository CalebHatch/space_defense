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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main extends Application {

    int playerScore; // Initializer for player's score
    int scoreIncrement = 50; // Player's score goes up in increments of 50

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
     *
     * @param mainPane is the main window in which the game will run inside.
     */
    public void start(@NotNull Stage mainPane) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("Score.txt"); // Creates writer object to write to score file

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
        background.setImage("\\space_defense\\src\\images\\space.png");

        // Loads the sprite for the player's ship
        Sprites ship = new Sprites();
        ship.point.set(400, 550);
        ship.setImage("\\space_defense\\src\\images\\ship.png");

        int droneAmount = 10; // Defines the amount of drones that the player will be facing
        ArrayList<Sprites> drones = new ArrayList<>(); // An ArrayList of drones to streamline interaction

        Sprites drone = null;
        for (int i = 0; i < droneAmount; i++) { // Creates a new drone sprite each time up to the defined drone amount
            drone = new Sprites();
            drone.setImage("\\space_defense\\src\\images\\drone.png");

            // Sets random point in top portion of screen that the drone will spawn
            double x = 500 * Math.random();
            double y = 400 * Math.random();
            drone.point.set(x, y); // Sets drone position

            drone.velocity.setVelocity(15); // Sets speed of drone
            drone.velocity.setAngle(90); // Sets direction of drone (drone moves down towards player's ship)

            drones.add(drone); // Add this drone and its properties to the ArrayList of drones
        }

        // ArrayList of keys that the user has pressed
        ArrayList<String> pressedKeys = new ArrayList<>();
        // ArrayList of keys that the user can only press once before needing to release
        ArrayList<String> pressedOnce = new ArrayList<>();

        // Handles event in which the player presses a key down
        mainLevel.setOnKeyPressed(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString(); // Gets name of the key
                    if (!pressedKeys.contains(keyName) || (!pressedOnce.contains(keyName))) {
                        // Adds key to pressedKeys ArrayList
                        pressedKeys.add(keyName); // The key can be held down for multiple inputs
                        pressedOnce.add(keyName); // The key can not be held down for multiple inputs
                    }
                }
        );

        // Handles event in which key is released
        mainLevel.setOnKeyReleased(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString(); // Gets key name
                    pressedKeys.remove(keyName); // Remove that key from the pressedKeys ArrayList
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
                    projectile.setImage("\\space_defense\\src\\images\\projectile.png");

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
                    Sprites projectile = projectiles.get(i); // Get the projectile from ArrayList
                    projectile.update(1 / 60.0); // Updates projectile status 60 times per second

                    if (projectile.travelTime > 8) // If projectile has existed for longer than 8 seconds
                        projectiles.remove(projectile); // Delete the projectile entirely to prevent lag
                }

                // Loop to handle the behavior of the drone once it has been shot by the player
                for (int projectileNumber = 0; projectileNumber < projectiles.size(); projectileNumber++) {
                    Sprites projectile = projectiles.get(projectileNumber); // Gets projectile

                    // Nested for loop to get the drone that was shot
                    for (int droneNumber = 0; droneNumber < drones.size(); droneNumber++) {
                        Sprites drone = drones.get(droneNumber); // Gets target drone
                        if (projectile.overlap(drone)) { // If the hitboxes of the projectile and drone collide
                            drones.remove(droneNumber); // Remove the drone from the game
                            projectiles.remove(projectileNumber); // Remove the projectile as well
                            playerScore += 50; // Give the player 50 points
                        }
                    }
                }

                // Text for the game
                game.setFont(new Font("Impact", 48));
                game.setLineWidth(4);
                game.setFill(Color.GRAY);
                game.setStroke(Color.LIGHTGRAY);

                // Score is displayed on top right of screen
                String scoreText = "Score: " + playerScore;
                game.fillText(scoreText, 270, 80);
                game.strokeText(scoreText, 270, 80);

                // Sets win condition
                if (playerScore == droneAmount * scoreIncrement) { // If the player has defeated every drone
                    game.setFont(new Font("Impact", 48));
                    game.setLineWidth(4);
                    game.setFill(Color.DARKRED);
                    game.setStroke(Color.BLACK);

                    // Display victory message in center of screen
                    String winnerText = "You Win!";
                    game.fillText(winnerText, 300, 300);
                    game.strokeText(winnerText, 300, 300);

                    // Writes final score to the Score.txt file
                    writer.println("You won! Your score was: " + playerScore);
                    writer.close();
                }
            }
        };

        mainPane.setTitle("Space Defense!"); // Window name
        gameLoop.start(); // Start the game
        mainPane.show(); // Show the window and its contents to the user
    }
}