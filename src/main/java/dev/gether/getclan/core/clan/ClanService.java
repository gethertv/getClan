package dev.gether.getclan.core.clan;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.upgrade.LevelData;
import dev.gether.getclan.core.upgrade.UpgradeType;
import dev.gether.getclan.database.DatabaseType;
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
    private final FileManager fileManager;
    private final GetClan plugin;

    public ClanService(MySQL mySQL, FileManager fileManager, GetClan plugin) {
        this.mySQL = mySQL;
        this.fileManager = fileManager;
        this.plugin = plugin;
        createTable();
    }

    @Override
    public void createTable() {

        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id " + (this.fileManager.getDatabaseConfig().getDatabaseType() == DatabaseType.SQLITE
                ? "INTEGER PRIMARY KEY AUTOINCREMENT"
                : "INT(10) AUTO_INCREMENT PRIMARY KEY") + ","
                + "tag VARCHAR(10),"
                + "uuid VARCHAR(100),"
                + "owner_uuid VARCHAR(100),"
                + "deputy_uuid VARCHAR(100),"
                + "upgrade_members INTEGER(11) DEFAULT 0,"
                + "members_deposit INTEGER(11) DEFAULT 0,"
                + "upgrade_drop_boost INTEGER(11) DEFAULT 0,"
                + "drop_boost_deposit INTEGER(11) DEFAULT 0,"
                + "upgrade_points_boost INTEGER(11) DEFAULT 0,"
                + "points_boost_deposit INTEGER(11) DEFAULT 0,"
                + "pvpEnable " + (this.fileManager.getDatabaseConfig().getDatabaseType() == DatabaseType.SQLITE
                ? "INTEGER"
                : "BOOLEAN")
                + ")";

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
                int upgradeMembers = resultSet.getInt("upgrade_members");
                int membersDeposit = resultSet.getInt("members_deposit");
                int upgradeDropBoost = resultSet.getInt("upgrade_drop_boost");
                int dropBoostDeposit = resultSet.getInt("drop_boost_deposit");
                int upgradePointsBoost = resultSet.getInt("upgrade_points_boost");
                int pointsBoostDeposit = resultSet.getInt("points_boost_deposit");

                UUID ownerUUID = UUID.fromString(ownerUuid);
                UUID clanUUID = UUID.fromString(clanUuid);
                UUID deputyUUID = null;
                if (deputyUuid != null && !deputyUuid.isEmpty()) {
                    deputyUUID = UUID.fromString(deputyUuid);
                }

                Map<UpgradeType, LevelData> upgrades = new HashMap<>();
                upgrades.put(UpgradeType.MEMBERS, new LevelData(upgradeMembers, membersDeposit));
                upgrades.put(UpgradeType.DROP_BOOST, new LevelData(upgradeDropBoost, dropBoostDeposit));
                upgrades.put(UpgradeType.POINTS_BOOST, new LevelData(upgradePointsBoost, pointsBoostDeposit));

                Clan clan = new Clan(tag, clanUUID, ownerUUID, deputyUUID, pvpEnable, fileManager.getUpgradesConfig(), upgrades);
                plugin.getClanManager().updateItem(clan);
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
        LevelData memberLevelData = clan.getUpgrades().get(UpgradeType.MEMBERS);
        LevelData dropBoostLevelData = clan.getUpgrades().get(UpgradeType.DROP_BOOST);
        LevelData pointsBoostLevelData = clan.getUpgrades().get(UpgradeType.POINTS_BOOST);
        String sql = "UPDATE " + table + " SET " +
                "owner_uuid = ?, " +
                "deputy_uuid = ?, " +
                "pvpEnable = ?, " +
                "upgrade_members = ?, " +
                "members_deposit = ?, " +
                "upgrade_drop_boost = ?, " +
                "drop_boost_deposit = ?, " +
                "upgrade_points_boost = ?, " +
                "points_boost_deposit = ? " +
                "WHERE tag = ?";
        List<Object> parameters = Arrays.asList(
                (clan.getOwnerUUID() != null ? clan.getOwnerUUID().toString() : ""),
                (clan.getDeputyOwnerUUID() != null ? clan.getDeputyOwnerUUID().toString() : ""),
                clan.isPvpEnable(),
                memberLevelData.getLevel(),
                memberLevelData.getDepositAmount(),
                dropBoostLevelData.getLevel(),
                dropBoostLevelData.getDepositAmount(),
                pointsBoostLevelData.getLevel(),
                pointsBoostLevelData.getDepositAmount(),
                clan.getTag()
        );
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }


    @Override
    public String getTable() {
        return this.table;
    }
}
