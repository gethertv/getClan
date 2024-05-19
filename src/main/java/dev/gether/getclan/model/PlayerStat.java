package dev.gether.getclan.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerStat implements Comparable<PlayerStat>{
    private UUID uuid;
    private String name;
    private double value;

    public PlayerStat(UUID uuid, String name, double value) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(PlayerStat other) {
        int compare = Double.compare(this.value, other.value);
        if(compare != 0) return compare;
        return this.uuid.compareTo(other.uuid);
    }


}
