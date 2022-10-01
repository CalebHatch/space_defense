package com.spacedefense.space_defense;

/**
 * Helps determine the speed and direction of desired objects
 */
public class SpeedsAngles {
    public double x; // Initialize x value
    public double y; // Initialize y value

    // Helps set velocity of desired objects
    public SpeedsAngles() {
        this.set(0, 0);
    }

    // Allows addition of values to determine velocity
    public void add(double ax, double ay) {
        this.x += ax; // Increment defined x value
        this.y += ay; // Increment defined y value
    }

    // Allows multiplication of values to determine velocity
    public void multiply(double m) {
        this.x *= m; // Multiply defined x value
        this.y *= m; // Multiply defined y value
    }

    // Setter method for placing objects at designated points
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter method for determining velocity
    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y); //change to squared later
    }

    // Setter method for assigning velocity to desired objects
    public void setVelocity(double L) {
        double Length = this.getLength();
        if (Length == 0) {
            this.set(L, 0);
        } else {
            this.multiply(1 / Length);
            this.multiply(L);
        }
    }

    // Setter method to assign angle (direction) to desired objects
    public void setAngle(double angleDegrees) {
        double L = this.getLength(); // Get length from velocity method
        double angleInRadians = Math.toRadians(angleDegrees); // Convert to radians

        this.x = L * Math.cos(angleInRadians);
        this.y = L * Math.sin(angleInRadians);
    }

}
