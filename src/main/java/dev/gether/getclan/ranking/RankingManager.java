package dev.gether.getclan.ranking;

import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.user.User;

import java.util.*;

public class RankingManager {

    private HashMap<RankType, RankingService> ranking = new HashMap<>();
    private final ClanManager clanManager;

    public RankingManager(ClanManager clanManager) {
        this.clanManager = clanManager;

        for (RankType rankType : RankType.values()) {
            ranking.put(rankType, new RankingService());
        }
    }

    public void updateUser(User user) {
        update(RankType.KILLS, user.getUuid(), user.getName(), user.getKills());
        update(RankType.DEATHS, user.getUuid(), user.getName(), user.getDeath());
        update(RankType.USER_POINTS, user.getUuid(), user.getName(), user.getPoints());

        if(!user.hasClan())
            return;

        Clan clan = clanManager.getClan(user.getTag());
        addClan(clan);
    }

    public Optional<PlayerStat> findTopPlayerByIndex(RankType rankType, int index) {
        RankingService rankingService = ranking.get(rankType);
        if(rankingService.size() <= index)
            return Optional.empty();

        return rankingService.getByIndex(index);
    }
    private void update(RankType rankType, UUID uuid, String name, int value) {
        RankingService rankingService = ranking.get(rankType);
        rankingService.add(uuid, name, value);
    }

    public void updateAll(Collection<User> users) {
        new ArrayList<>(users).forEach(this::updateUser);
    }

    public int findTopPlayerByName(User user) {
        RankingService rankingService = ranking.get(RankType.USER_POINTS);
        return rankingService.getIndexByUUID(user.getUuid());
    }

    public int findTopClan(Clan clan) {
        RankingService rankingService = ranking.get(RankType.CLAN_POINTS);
        return rankingService.getIndexByUUID(clan.getUuid());
    }

    public void removeClan(Clan clan) {
        RankingService rankingService = ranking.get(RankType.CLAN_POINTS);
        rankingService.remove(clan.getUuid());
    }

    public void addClan(Clan clan) {
        String averagePoint = clanManager.getAveragePoint(clan);

        update(RankType.CLAN_POINTS, clan.getUuid(), clan.getTag(), Integer.parseInt(averagePoint));
    }
}
