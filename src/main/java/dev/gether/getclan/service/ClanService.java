package dev.gether.getclan.service;

import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.QueuedQuery;
import dev.gether.getclan.utils.ConsoleColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class ClanService extends BaseService {

    public ClanService(MySQL sql, QueueService queueService)
    {
        super(sql, queueService);
    }

    public void createClan(Clan clans, Player player) {
        String sql = "INSERT INTO " + tableClans + " (tag, owner_uuid) VALUES (?, ?)";
        List<Object> parameters = Arrays.asList(clans.getTag(), player.getUniqueId().toString());
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
                String ownerUuid = resultSet.getString("owner_uuid");
                String deputyUuid = resultSet.getString("deputy_uuid");

                UUID ownerUUID = UUID.fromString(ownerUuid);
                UUID deputyUUID = deputyUuid != null ? UUID.fromString(deputyUuid) : null;

                plugin.getClansManager().getClansData().put(tag.toUpperCase(), new Clan(tag, ownerUUID, deputyUUID));
                countClan++;
            }
        } catch (SQLException sQLException) {
            plugin.getLogger().severe(sQLException.getMessage());
        }
        plugin.getLogger().info(ConsoleColor.GREEN + "Pomyslnie zaladowano "+countClan+" klanow");
    }

    public void updateClan(Clan clan) {
        String sql = "UPDATE " + tableClans + " SET owner_uuid = ? , deputy_uuid = ? WHERE tag = ?";
        List<Object> parameters = Arrays.asList(clan.getOwnerUUID().toString(), clan.getDeputyOwnerUUID().toString(), clan.getTag());
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }

    public void createAlliance(String clanTagFirst, String clanTagSecond) {
        String sql = "INSERT INTO " + tableAlliance + " (clan_tag1, clan_tag2) VALUES (?, ?)";
        List<Object> parameters = Arrays.asList(clanTagFirst, clanTagSecond);
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }

    public void deleteAlliance(String tag) {
        String sql = "DELETE FROM " + tableAlliance + " WHERE clan_tag1 = ? OR clan_tag2 = ?";
        List<Object> parameters = Arrays.asList(tag, tag);
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }

    public void loadAlliances() {
        int allianceCount = 0;
        plugin.getLogger().info(ConsoleColor.YELLOW+"Wczytywanie sojuszy...");
        ClanManager clansManager = plugin.getClansManager();
        String str = "SELECT * FROM " + this.tableAlliance;
        try {
            ResultSet resultSet = getResult(str);
            while (resultSet.next()) {
                String clanTag1 = resultSet.getString("clan_tag1").toUpperCase();
                String clanTag2 = resultSet.getString("clan_tag2").toUpperCase();
                Clan clan1 = clansManager.getClansData().get(clanTag1);
                clan1.addAlliance(clanTag2);
                Clan clan2 = clansManager.getClansData().get(clanTag2);
                clan2.addAlliance(clanTag1);
                allianceCount++;
            }
        } catch (SQLException sQLException) {
            this.plugin.getLogger().severe(sQLException.getMessage());
        }
        this.plugin.getLogger().info(ConsoleColor.GREEN+"Pomyslnie zaladowano " + allianceCount + " sojuszy");
    }


}
