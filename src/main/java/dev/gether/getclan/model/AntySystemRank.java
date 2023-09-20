package dev.gether.getclan.model;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AntySystemRank {
    private String ipKiller;

    // key (ip) value (last_death)
    private HashMap<String, Long> cooldown = new HashMap<>();

    public AntySystemRank(String ipKiller, String ipDeath, int timeSec) {
        this.ipKiller = ipKiller;
        this.cooldown.put(ipDeath, System.currentTimeMillis()+(timeSec*1000L));
    }

    public boolean isPlayerKillable(String ipDeath)
    {
        Long lastDeath = cooldown.get(ipDeath);
        if(lastDeath==null)
            return true;

        return lastDeath<=System.currentTimeMillis();
    }

    public void addCooldown(String ipDeath, int timeSec)
    {
        this.cooldown.put(ipDeath, System.currentTimeMillis()+(timeSec*1000L));
    }

    public long getRemainingCooldown(String playerIp) {
        long currentTime = System.currentTimeMillis();
        Long endTime = cooldown.get(playerIp);

        long remainingTime = endTime - currentTime;

        return Math.max(remainingTime, 0);
    }
}
