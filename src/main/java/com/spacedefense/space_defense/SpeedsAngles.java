package com.spacedefense.space_defense;

public class SpeedsAngles {
    public double x;
    public double y;

    public SpeedsAngles() {
        this.set(0, 0);
    }

    public void add(double ax, double ay) {
        this.x += ax;
        this.y += ay;
    }

    public void multiply(double m) {
        this.x *= m;
        this.y *= m;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y); //change to squared later
    }

    public void setLength(double L) {
        double currentLength = this.getLength();
        // if current length is 0, current angle is undefined
        // assume current angle is 0
        if (currentLength == 0) {
            this.set(L, 0);
        } else {
            // scale vector to length 1
            this.multiply(1 / currentLength);
            //scale vector to length L
            this.multiply(L);
        }
    }

    public void setAngle(double angleDegrees) {
        double L = this.getLength();
        double angleRadians = Math.toRadians(angleDegrees);

        this.x = L * Math.cos(angleRadians);
        this.y = L * Math.sin(angleRadians);
    }

}
