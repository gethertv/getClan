package dev.gether.getclan.ranking;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RankingService {

    private final Map<UUID, PlayerStat> playerStatsMap = new HashMap<>();
    @Getter
    private final List<PlayerStat> ranking = new ArrayList<>();
    private final JavaPlugin plugin;
    @Getter
    private final Object rankingLock = new Object();

    public RankingService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void add(UUID uuid, String name, int value) {
        synchronized (rankingLock) {
            PlayerStat playerStat = playerStatsMap.get(uuid);
            if (playerStat != null) {
                playerStat.setValue(value);
            } else {
                playerStat = new PlayerStat(uuid, name, value);
                playerStatsMap.put(uuid, playerStat);
                ranking.add(playerStat);
            }
        }
    }

    public void remove(UUID uuid) {
        ranking.removeIf(playerStat -> playerStat.getUuid().equals(uuid));
    }
    public Optional<PlayerStat> getByIndex(int index) {
        if(index < 0)
            return Optional.empty();

        return Optional.ofNullable(ranking.get(index));
    }

    public int getIndexByUUID(UUID uuid) {
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getUuid().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }
    public int size() {
        return ranking.size();
    }
}
