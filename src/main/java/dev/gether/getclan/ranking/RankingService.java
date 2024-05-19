package dev.gether.getclan.ranking;

import java.util.*;

public class RankingService {
    private List<PlayerStat> ranking = new LinkedList<>();
    public void add(UUID uuid, String name, int value) {
        remove(uuid);
        ranking.add(new PlayerStat(uuid, name, value));
        Collections.sort(ranking);
        Collections.reverse(ranking);
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
    public int size() {
        return ranking.size();
    }
}
