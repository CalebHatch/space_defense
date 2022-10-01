package com.spacedefense.space_defense;

import org.jetbrains.annotations.NotNull;

public class Hitboxes {

    double x;
    double y;

    double width;
    double height;

    public void setSize(double w, double h) {
        this.width = w;
        this.height = h;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Hitboxes() {
        this.setSize(0, 0);
        this.setPosition(0, 0);
    }

    public boolean overlap(@NotNull Hitboxes other) {
        boolean noOverlap =
                this.x + this.width < other.x || other.x + other.width < this.x || this.y + this.height < other.y ||
                        other.y + other.height < this.y;

        return !noOverlap;
    }
}
