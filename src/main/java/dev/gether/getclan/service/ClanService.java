package dev.gether.getclans.service;

import dev.gether.getclans.database.MySQL;
import dev.gether.getclans.model.Clan;
import dev.gether.getclans.model.QueuedQuery;
import dev.gether.getclans.utils.ConsoleColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class ClanService extends BaseService {

    public ClanService(MySQL sql, QueueService queueService)
    {
        super(sql, queueService);
    }

    public void createClan(Clan clans, Player player) {
        String sql = "INSERT INTO " + tableClans + " (tag, owner_name) VALUES (?, ?)";
        List<Object> parameters = Arrays.asList(clans.getTag(), player.getName());
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }

    public void deleteClan(String tag)
    {
        String sql = "DELETE FROM "+tableClans+" WHERE tag = ?";
        List<Object> parameters = Arrays.asList(tag);
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }
    public void loadClans()
    {
        int countClan = 0;
        plugin.getLogger().info(ConsoleColor.YELLOW+"Wczytywanie klanow...");

        String str = "SELECT * FROM "+tableClans;
        try {
            ResultSet resultSet = getResult(str);

            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                String owner_name = resultSet.getString("owner_name");
                plugin.getClansManager().getClansData().put(tag.toUpperCase(), new Clan(tag, owner_name));
                countClan++;
            }
        } catch (SQLException sQLException) {
            plugin.getLogger().severe(sQLException.getMessage());
        }
        plugin.getLogger().info(ConsoleColor.GREEN + "Pomyslnie zaladowano "+countClan+" klanow");
    }
}
