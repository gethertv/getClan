package dev.gether.getclans.model;

import dev.gether.getclans.model.Clan;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private UUID uuid;
    private int kills;
    private int death;
    private int points;

    private Clan clan;

    public User(UUID uuid, int kills, int death, int points, Clan clan) {
        this.kills = kills;
        this.death = death;
        this.points = points;
        this.uuid = uuid;
        if(clan!=null) {
            this.clan = clan;
        }
    }
    public User(Player player, int points)
    {
        this.uuid = player.getUniqueId();
        this.kills = 0;
        this.death = 0;
        this.points = points;
    }

    public void addDeath(int points) {
        this.points-=points;
        this.death--;

    }
    public void addKill( int points) {
        this.points+=points;
        this.kills++;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
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


}
