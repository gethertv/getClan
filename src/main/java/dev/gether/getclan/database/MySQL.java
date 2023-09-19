package dev.gether.getclans.database;

import dev.gether.getclans.GetClans;
import dev.gether.getclans.config.MySqlConfig;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

public class MySQL {

    private final GetClans plugin;
    private MySqlConfig mySqlConfig;

    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean ssl;
    private Connection connection;

    private String tableUsers = "get_clan_users";
    private String tableClans = "get_clans";

    public MySQL(GetClans plugin) {
        this.plugin = plugin;
        loadConfig();
        setupMysql();
        openConnection();
        createTable();
    }

    private void loadConfig() {
        mySqlConfig = ConfigManager.create(MySqlConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(plugin.getDataFolder(), "database.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private void setupMysql() {
        this.host = mySqlConfig.host;
        this.username = mySqlConfig.username;
        this.password = mySqlConfig.password;
        this.database = mySqlConfig.database;
        this.port = mySqlConfig.port;
        this.ssl = mySqlConfig.ssl;
    }

    private void openConnection() {
        try {
            long startTime = System.currentTimeMillis();
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("useSSL", String.valueOf(ssl));
            properties.setProperty("requireSSL", String.valueOf(ssl));
            properties.setProperty("verifyServerCertificate", "false");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, properties);

            long endTime = System.currentTimeMillis();
            plugin.getLogger().log(Level.INFO, "Połączono z bazą danych w " + (endTime - startTime) + "ms");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie można połączyć się z bazą danych!", e);
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private void createTable() {
        try {
            String createClansTable = "CREATE TABLE IF NOT EXISTS " + tableClans + " ("
                    + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                    + "tag VARCHAR(100),"
                    + "owner_name VARCHAR(60))";

            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + tableUsers + " ("
                    + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                    + "uuid VARCHAR(100),"
                    + "username VARCHAR(100),"
                    + "kills INT(11) DEFAULT 0,"
                    + "deaths INT(11) DEFAULT 0,"
                    + "points INT(11) DEFAULT 0,"
                    + "clan_tag VARCHAR(100))";

            Statement stmt = connection.createStatement();
            stmt.execute(createClansTable);
            stmt.execute(createUsersTable);
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie można stworzyć tabeli!", e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().log(Level.INFO, "Zamknięto połączenie z bazą danych.");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Nie można zamknąć połączenia z bazą danych!", e);
            }
        }
    }

    public boolean isConnected() {
        try {
            return (connection != null && !connection.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Błąd podczas sprawdzania połączenia z bazą danych!", e);
        }
        return connection;
    }


    public String getTableClans() {
        return tableClans;
    }

    public String getTableUsers() {
        return tableUsers;
    }
}
