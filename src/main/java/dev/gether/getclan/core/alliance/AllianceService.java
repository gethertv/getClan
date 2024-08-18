package dev.gether.getclan.core.alliance;

import dev.gether.getclan.database.GetTable;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.database.QueuedQuery;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllianceService implements GetTable {


    private final String table = "get_alliance";
    private final MySQL mySQL;

    public AllianceService(MySQL mySQL) {
        this.mySQL = mySQL;
        createTable();
    }

    public void createAlliance(String clanTagFirst, String clanTagSecond) {
        String sql = "INSERT INTO " + table + " (clan_tag1, clan_tag2) VALUES (?, ?)";
        List<Object> parameters = Arrays.asList(clanTagFirst, clanTagSecond);
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }

    public void deleteAlliance(String tag) {
        String sql = "DELETE FROM " + table + " WHERE clan_tag1 = ? OR clan_tag2 = ?";
        List<Object> parameters = Arrays.asList(tag, tag);
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }

    public Map<String, String> loadAlliances() {
        int allianceCount = 0;
        Map<String, String> alliances = new HashMap<>();

        MessageUtil.logMessage(ConsoleColor.GREEN, "Loading alliances...");
        String sql = "SELECT clan_tag1, clan_tag2 FROM " + table;
        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                String clanTag1 = resultSet.getString("clan_tag1");
                String clanTag2 = resultSet.getString("clan_tag2");

                alliances.put(clanTag1, clanTag2);
                allianceCount++;
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Error loading alliances: " + e.getMessage());
        }

        MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully loaded " + allianceCount + " alliances");
        return alliances;
    }


    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "clan_tag1 VARCHAR(20),"
                + "clan_tag2 VARCHAR(20))";

        try(Statement stmt = this.mySQL.getHikariDataSource().getConnection().createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Cannot create the table "+table+ ". Error "+e.getMessage());
        }

    }

    @Override
    public String getTable() {
        return this.table;
    }
}
