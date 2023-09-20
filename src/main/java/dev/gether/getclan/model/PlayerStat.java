package dev.gether.getclan.model;

public class PlayerStat {
    private String name;
    private double value;

    public PlayerStat(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getDouble() {
        return value;
    }
    public int getInt() {
        return (int) value;
    }

}
