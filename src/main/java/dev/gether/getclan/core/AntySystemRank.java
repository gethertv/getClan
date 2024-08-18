package dev.gether.getclan.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AntySystemRank {
    private final String killerIp;
    private final Map<String, Long> cooldowns;

    public AntySystemRank(String killerIp) {
        this.killerIp = killerIp;
        this.cooldowns = new HashMap<>();
    }

    public boolean isPlayerKillable(String victimIp, Player killer) {
        if (victimIp.equals(killerIp) && killer.hasPermission("getclan.abuse.bypass")) {
            return true;
        }
        Long cooldownEnd = cooldowns.get(victimIp);
        long currentTime = System.currentTimeMillis();
        boolean killable = cooldownEnd == null || currentTime > cooldownEnd;

        return killable;
    }

    public void addCooldown(String victimIp, int cooldownTime) {
        long newCooldownEnd = System.currentTimeMillis() + (cooldownTime * 1000L);
        cooldowns.put(victimIp, newCooldownEnd);
    }

    public long getRemainingCooldown(String victimIp) {
        Long cooldownEnd = cooldowns.get(victimIp);
        if (cooldownEnd == null) {
            return 0;
        }
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }
}