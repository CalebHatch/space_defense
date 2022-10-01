package com.spacedefense.space_defense;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

public class Sprites {
    public SpeedsAngles point;
    public SpeedsAngles velocity;
    public Hitboxes borders;
    public Image image;

    public double rotation;
    public double travelTime;

    public Sprites() {
        this.point = new SpeedsAngles();
        this.velocity = new SpeedsAngles();
        this.borders = new Hitboxes();
        this.rotation = 0;

        travelTime = 0; // How long an object has existed
    }

    // Setter method to add an image for sprites
    public void setImage(String imageFileName) {
        this.image = new Image(imageFileName); // Define image file name
        this.borders.setSize(this.image.getWidth(), this.image.getHeight()); // Determine image height and width
    }

    // Method to update condition of desired objects
    public void update(double gameTime) {
        this.travelTime += gameTime;
        this.point.add(this.velocity.x * gameTime, this.velocity.y * gameTime);
    }

    public void render(@NotNull GraphicsContext game) {
        game.save();

        game.translate(this.point.x, this.point.y);
        game.rotate(this.rotation);
        game.translate(-this.image.getWidth() / 2, -this.image.getHeight() / 2);
        game.drawImage(this.image, 0, 0);

        game.restore();
    }

    public Hitboxes getBorders() {
        this.borders.setPosition(this.point.x, this.point.y);
        return this.borders;
    }


    public boolean overlap(@NotNull Sprites other) {
        return this.getBorders().overlap(other.getBorders());
    }

}
