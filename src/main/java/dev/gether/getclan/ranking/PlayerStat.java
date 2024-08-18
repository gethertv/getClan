package dev.gether.getclan.ranking;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerStat implements Comparable<PlayerStat>{
    private UUID uuid;
    private String name;
    private int value;

    public PlayerStat(UUID uuid, String name, int value) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(PlayerStat other) {
        int compare = Integer.compare(this.value, other.value);
        if (compare != 0) return compare;
        return this.uuid.compareTo(other.uuid);
    }


}
