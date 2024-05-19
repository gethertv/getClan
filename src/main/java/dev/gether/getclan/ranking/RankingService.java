package dev.gether.getclan.ranking;

import dev.gether.getclan.model.PlayerStat;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class RankingService {
    private ArrayList<PlayerStat> ranking = new ArrayList<>();
    public void add(UUID uuid, String name, double value) {
        remove(uuid);
        ranking.add(new PlayerStat(uuid, name, value));
    }

    public void remove(UUID uuid) {
        ranking.removeIf(playerStat -> playerStat.getUuid().equals(uuid));
    }
    public Optional<PlayerStat> getByIndex(int index) {
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
}
