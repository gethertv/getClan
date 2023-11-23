package dev.gether.getclan.scheduler;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.PlayerStat;
import dev.gether.getclan.model.RankType;
import dev.gether.getclan.model.User;
import dev.rollczi.litecommands.argument.option.Opt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TopRankScheduler extends BukkitRunnable {

    private UserManager userManager;
    private ClanManager clanManager;
    private Comparator<PlayerStat> comparator = (a, b) -> Integer.compare(b.getInt(), a.getInt());

    private HashMap<RankType, PriorityQueue<PlayerStat>> rankData = new HashMap<>();
    public TopRankScheduler(UserManager userManager, ClanManager clanManager)
    {
        this.userManager = userManager;
        this.clanManager = clanManager;
    }
    @Override
    public void run() {
        // initialize sort ranking - rank data
        prepareQueue();

        // iplements user and clan
        implementsUser();
        implementsClan();
    }

    private void implementsClan() {
        Queue<Clan> clanQueue = new LinkedList<>(clanManager.getClansData().values());
        int size = clanQueue.size();
        for (int i = 0; i < size; i++) {
            Clan clan = clanQueue.poll();
            addClan(clan);
        }
    }

    private void prepareQueue() {
        rankData.put(RankType.KILLS, new PriorityQueue<>(comparator));
        rankData.put(RankType.DEATHS, new PriorityQueue<>(comparator));
        rankData.put(RankType.USER_POINTS, new PriorityQueue<>(comparator));
        rankData.put(RankType.CLAN_POINTS, new PriorityQueue<>(comparator));
    }
    private void implementsUser() {
        Queue<User> userQueue = new LinkedList<>(userManager.getUserData().values());
        int queueSize = userQueue.size(); // Zapamiętaj początkowy rozmiar kolejki
        for (int i = 0; i < queueSize; i++) { // Popraw warunek pętli
            User user = userQueue.poll();
            addUser(user);
        }
    }

    public void addUser(User user) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getUuid());
        int death = user.getDeath();
        int kills = user.getKills();
        int points = user.getPoints();

        String name = offlinePlayer.getName();

        addStats(name, kills, death, points);
    }

    public void addClan(Clan clan) {
        // check size of members fulfill threshold to counting a ranking
        if(!clanManager.doesClanFulfillThreshold(clan)) {
            return;
        }
        String tag = clan.getTag();
        String averagePoint = clanManager.getAveragePoint(clan);

        PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.CLAN_POINTS);
        playerStats.add(new PlayerStat(tag, Integer.parseInt(averagePoint)));
    }

    public void removeClan(Clan clan) {
        PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.CLAN_POINTS);
        PriorityQueue<PlayerStat> newQueue = new PriorityQueue<>(playerStats.comparator());
        for (PlayerStat stat : playerStats) {
            if (!stat.getName().equals(clan.getTag())) {
                newQueue.add(stat);
            }
        }
        rankData.put(RankType.CLAN_POINTS, newQueue);
    }


    private void addStats(String name, int kills, int death, int points) {
        // kills
        {
            PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.KILLS);
            playerStats.add(new PlayerStat(name, kills));
        }
        // deaths
        {
            PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.DEATHS);
            playerStats.add(new PlayerStat(name, death));
        }
        // user points
        {
            PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.USER_POINTS);
            playerStats.add(new PlayerStat(name, points));
        }
    }

    public OptionalInt getClanRankIndexByTag(String tag) {
        PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.CLAN_POINTS);
        PriorityQueue<PlayerStat> tempQueue = new PriorityQueue<>(playerStats);
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

    public OptionalInt getUserRankByName(String username) {
        PriorityQueue<PlayerStat> playerStats = rankData.get(RankType.USER_POINTS);
        PriorityQueue<PlayerStat> tempQueue = new PriorityQueue<>(playerStats);
        PlayerStat stat = null;
        int index = 0;
        while ((stat = tempQueue.poll()) != null) {
            if(stat.getName()==null) {
                continue;
            }
            if (stat.getName().equals(username)) {
                return OptionalInt.of(index);
            }
            index++;
        }
        return OptionalInt.of(index);
    }


    public Optional<PlayerStat> getRank(RankType rankType, int top) {
        PriorityQueue<PlayerStat> tempQueue = new PriorityQueue<>(rankData.get(rankType));
        PlayerStat playerStat = null;
        for (int i = 0; i < top; i++) {
            playerStat = tempQueue.poll();
        }
        return Optional.ofNullable(playerStat);
    }
}
