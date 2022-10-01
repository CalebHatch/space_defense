package com.spacedefense.space_defense;

import org.jetbrains.annotations.NotNull;

public class Hitboxes {

    double x;
    double y;

    double width;
    double height;

    // Helps define attributes of the hitbox
    public Hitboxes() {
        this.setSize(0, 0); // Size of hitbox
        this.setPoint(0, 0); // Where hitbox is
    }

    // Setter method for size of sprite hitbox
    public void setSize(double w, double h) { // Can pass width and height of hitbox
        this.width = w;
        this.height = h;
    }

    // Setter method for point in the game where hitbox is placed
    public void setPoint(double x, double y) { // Can pass x and y value for placement
        this.x = x;
        this.y = y;
    }

    // Determines if two sprites overlap
    public boolean overlap(@NotNull Hitboxes other) {
        boolean noOverlap =
                this.x + this.width < other.x || other.x + other.width < this.x || this.y + this.height < other.y ||
                        other.y + other.height < this.y; // If x and y values aren't equal in any way, there is overlap

        return !noOverlap; // There isn't no overlap (only way I could get it to return correctly)
    }
}
