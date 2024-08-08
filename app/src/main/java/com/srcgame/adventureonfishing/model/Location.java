package com.srcgame.adventureonfishing.model;

public class Location {
    private String name;
    private int imageResId;
    private double waterHeight;
    private double playerMargin;

    public Location(String name, int imageResId, double waterHeight, double playerMargin) {
        this.name = name;
        this.imageResId = imageResId;
        this.waterHeight = waterHeight;
        this.playerMargin = playerMargin;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getWaterHeight() {
        return waterHeight;
    }

    public double getPlayerMargin() {
        return playerMargin;
    }
}