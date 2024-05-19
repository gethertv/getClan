package dev.gether.getclan.core.clan;

import dev.gether.getclan.database.GetTable;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.database.QueuedQuery;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class ClanService implements GetTable {

    private final String table = "get_clans";
    private final MySQL mySQL;

    public ClanService(MySQL mySQL) {
        this.mySQL = mySQL;
        createTable();
    }

    @Override
    public void createTable() {

        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                + "tag VARCHAR(10),"
                + "uuid VARCHAR(100),"
                + "owner_uuid VARCHAR(100),"
                + "deputy_uuid VARCHAR(100),"
                + "pvpEnable BOOLEAN)";

        try (Statement stmt = mySQL.getHikariDataSource().getConnection().createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Cannot create the table " + table + ". Error " + e.getMessage());
        }

    }

    public void createClan(Clan clan, Player player) {
        String sql = "INSERT INTO " + table + " (tag, uuid, owner_uuid) VALUES (?, ?, ?)";
        List<Object> parameters = Arrays.asList(clan.getTag(), clan.getUuid().toString(), player.getUniqueId().toString());
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }

    public void deleteClan(String tag) {
        String sql = "DELETE FROM " + table + " WHERE tag = ?";
        List<Object> parameters = Arrays.asList(tag);
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }


    public Set<Clan> loadClans() {
        int countClan = 0;
        Set<Clan> clans = new HashSet<>();
        MessageUtil.logMessage(ConsoleColor.YELLOW, "Loading clans...");
        String sql = "SELECT * FROM " + table;
        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                String clanUuid = resultSet.getString("uuid");
                String ownerUuid = resultSet.getString("owner_uuid");
                String deputyUuid = resultSet.getString("deputy_uuid");
                boolean pvpEnable = resultSet.getBoolean("pvpEnable");

                UUID ownerUUID = UUID.fromString(ownerUuid);
                UUID clanUUID = UUID.fromString(clanUuid);
                UUID deputyUUID = null;
                if (deputyUuid != null && !deputyUuid.isEmpty()) {
                    deputyUUID = UUID.fromString(deputyUuid);
                }

                Clan clan = new Clan(tag, clanUUID, ownerUUID, deputyUUID, pvpEnable);
                clans.add(clan);
                countClan++;
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Error loading clans: " + e.getMessage());
        }
        MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully loaded " + countClan + " clans");
        return clans;
    }

    public void updateClan(Clan clan) {
        String sql = "UPDATE " + table + " SET owner_uuid = ? , deputy_uuid = ? , pvpEnable = ? WHERE tag = ?";
        List<Object> parameters = Arrays.asList(
                (clan.getOwnerUUID() != null ? clan.getOwnerUUID().toString() : ""),
                (clan.getDeputyOwnerUUID() != null ? clan.getDeputyOwnerUUID().toString() : ""),
                clan.isPvpEnable(),
                clan.getTag()
        );
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }


    @Override
    public String getTable() {
        return this.table;
    }
}
