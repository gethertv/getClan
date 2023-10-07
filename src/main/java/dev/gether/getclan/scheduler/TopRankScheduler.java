package dev.gether.getclan.scheduler;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.PlayerStat;
import dev.gether.getclan.model.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.IntStream;

public class TopRankScheduler extends BukkitRunnable {

    private UserManager userManager;
    private ClanManager clanManager;

    private Comparator<PlayerStat> killComparator = (a, b) -> Integer.compare(b.getInt(), a.getInt());
    private Comparator<PlayerStat> deathComparator = (a, b) -> Integer.compare(b.getInt(), a.getInt());
    private Comparator<PlayerStat> pointsComparator = (a, b) -> Integer.compare(b.getInt(), a.getInt());
    private Comparator<PlayerStat> clanComparator = (a, b) -> Integer.compare(b.getInt(), a.getInt());

    private PriorityQueue<PlayerStat> killStatsQueue;
    private PriorityQueue<PlayerStat> deathStatsQueue;
    private PriorityQueue<PlayerStat> pointsStatsQueue;
    private PriorityQueue<PlayerStat> clanStatsQueue;
    public TopRankScheduler(UserManager userManager, ClanManager clanManager)
    {
        this.userManager = userManager;
        this.clanManager = clanManager;
    }
    @Override
    public void run() {

        prepareQueue();

        Collection<User> values = new ArrayList<>(userManager.getUserData().values());
        for (User user : values)
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getUuid());
            int death = user.getDeath();
            int kills = user.getKills();
            int points = user.getPoints();

            String name = offlinePlayer.getName();

            addStats(name, kills, death, points);
        }
        Collection<Clan> valuesClan = clanManager.getClansData().values();
        for(Clan clan : valuesClan)
        {
            addClanStats(clan.getTag(), clanManager.getAveragePoint(clan));
        }
    }

    private void addClanStats(String tag, String averagePoint) {
        clanStatsQueue.add(new PlayerStat(tag, Integer.parseInt(averagePoint)));
    }

    private void addStats(String name, int kills, int death, int points) {
        killStatsQueue.add(new PlayerStat(name, kills));
        deathStatsQueue.add(new PlayerStat(name, death));
        pointsStatsQueue.add(new PlayerStat(name, points));
    }

    private void prepareQueue() {

        if(killStatsQueue == null) {
            killStatsQueue = new PriorityQueue<>(killComparator);
        } else {
            killStatsQueue.clear();
        }
        if(deathStatsQueue == null) {
            deathStatsQueue = new PriorityQueue<>(deathComparator);
        } else {
            deathStatsQueue.clear();
        }
        if(pointsStatsQueue == null) {
            pointsStatsQueue = new PriorityQueue<>(pointsComparator);
        } else {
            pointsStatsQueue.clear();
        }
        if(clanStatsQueue == null) {
            clanStatsQueue = new PriorityQueue<>(clanComparator);
        } else {
            clanStatsQueue.clear();
        }
    }

    public PlayerStat getKillStatByIndex(int index) {
        return getStatByIndex(killStatsQueue, index);
    }

    public PlayerStat getDeathStatByIndex(int index) {
        return getStatByIndex(deathStatsQueue, index);
    }

    public PlayerStat getPointStatByIndex(int index) {
        return getStatByIndex(pointsStatsQueue, index);
    }
    public PlayerStat getClanStatByIndex(int index) {
        return getStatByIndex(clanStatsQueue, index);
    }

    private PlayerStat getStatByIndex(PriorityQueue<PlayerStat> queue, int index) {
        if (index >= 0 && index < queue.size()) {
            PriorityQueue<PlayerStat> tempQueue = new PriorityQueue<>(queue);
            PlayerStat stat = null;
            for (int i = 0; i <= index; i++) {
                stat = tempQueue.poll();
            }
            return stat;
        }
        return null;
    }
    public OptionalInt getClanRankIndexByTag(String tag) {
        PriorityQueue<PlayerStat> tempQueue = new PriorityQueue<>(clanStatsQueue);
        PlayerStat stat = null;
        int index = 0;
        while ((stat = tempQueue.poll()) != null) {
            if (stat.getName().equalsIgnoreCase(tag)) {
                return OptionalInt.of(index);
            }
            index++;
        }
        return OptionalInt.empty();
    }

}
