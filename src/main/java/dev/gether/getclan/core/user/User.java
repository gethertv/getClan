package dev.gether.getclan.core.user;

import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String name;
    private int kills;
    private int death;
    private int points;
    private String tag;
    private boolean update = false;
    public User(UUID uuid, String name, int kills, int death, int points, String tag) {
        this.kills = kills;
        this.death = death;
        this.points = points;
        this.uuid = uuid;
        this.name = name;
        this.tag = tag;
    }

    public User(Player player, int points) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.kills = 0;
        this.death = 0;
        this.points = points;
    }

    public void increaseDeath() {
        this.death++;
        this.update = true;
    }

    public void increaseKill() {
        this.kills++;
        this.update = true;
    }

    public void takePoint(int points) {
        this.points -= points;
        this.update = true;
    }

    public void addPoint(int points) {
        this.points += points;
        this.update = true;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.update = true;
    }

    public void resetKill() {
        kills = 0;
        this.update = true;
    }

    public void resetDeath() {
        death = 0;
        this.update = true;
    }

    public void setPoints(int points) {
        this.points = points;
        this.update = true;
    }

    public String getTag() {
        return tag;
    }

    public boolean hasClan() {
        return tag != null;
    }


    public boolean isUpdate() {
        return update;
    }

    public int getKills() {
        return kills;
    }

    public int getDeath() {
        return death;
    }

    public int getPoints() {
        return points;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setUpdate(boolean status) {
        this.update = status;
    }
}
