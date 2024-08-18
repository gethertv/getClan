package dev.gether.getclan.core.user;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.database.DatabaseType;
import dev.gether.getclan.database.GetTable;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.database.QueuedQuery;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class UserService implements GetTable {


    private final String table = "get_clan_users";
    private final MySQL mySQL;

    private final FileManager fileManager;

    public UserService(MySQL mySQL, FileManager fileManager) {
        this.mySQL = mySQL;
        this.fileManager = fileManager;
        createTable();
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id " + (this.fileManager.getDatabaseConfig().getDatabaseType() == DatabaseType.SQLITE
                ? "INTEGER PRIMARY KEY AUTOINCREMENT"
                : "INT(10) AUTO_INCREMENT PRIMARY KEY") + ","
                + "uuid VARCHAR(100),"
                + "username VARCHAR(100),"
                + "kills INT(11) DEFAULT 0,"
                + "deaths INT(11) DEFAULT 0,"
                + "points INT(11) DEFAULT 0,"
                + "clan_tag VARCHAR(100) NULL)";

        try(Statement stmt = mySQL.getHikariDataSource().getConnection().createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Cannot create the table "+table+ ". Error "+e.getMessage());
        }

    }

    public void createUser(Player player, int points) {
        String sql = "INSERT INTO " + table + " (uuid, username, points) VALUES (?, ?, ?)";
        List<Object> parameters = Arrays.asList(player.getUniqueId().toString(), player.getName(), points);
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }

    public void updateUser(User user) {
        if(user==null) {
            MessageUtil.logMessage(ConsoleColor.RED, "Something wrong! User is null "+user.getUuid());
            return;
        }

        String sql = "UPDATE "+ table +" SET kills = ? , deaths = ? , points = ? , clan_tag = ? WHERE uuid = ?";
        List<Object> parameters = Arrays.asList(user.getKills(), user.getDeath(), user.getPoints(), user.getTag(), user.getUuid().toString());
        mySQL.addQueue(new QueuedQuery(sql, parameters));
    }

    public Set<User> loadUsers() {
        // USER -> TAG_NAME
        Set<User> users = new HashSet<>();

        int countUser = 0;
        MessageUtil.logMessage(ConsoleColor.YELLOW, "Loading users...");
        String sql = "SELECT * FROM " + table;
        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String username = resultSet.getString("username");
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");
                int points = resultSet.getInt("points");
                String tag = resultSet.getString("clan_tag");
                users.add(new User(uuid, username, kills, deaths, points, tag));
                countUser++;
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, e.getMessage());
        }
        MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully loaded " + countUser + " users");
        return users;
    }


    @Override
    public String getTable() {
        return this.table;
    }
}
