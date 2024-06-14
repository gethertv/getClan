package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.config.domain.DatabaseConfig;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class MySQL {

    private final GetClan plugin;
    private FileManager fileManager;

    private HikariDataSource hikariDataSource;

    private Queue<QueuedQuery> queuedQueries = new ArrayDeque<>();

    public MySQL(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        connect(plugin);
    }

    private void connect(JavaPlugin plugin) {
        DatabaseConfig databaseConfig = fileManager.getDatabaseConfig();
        HikariConfig config = new HikariConfig();
        if(databaseConfig.getDatabaseType() == DatabaseType.MYSQL) {
            config.setJdbcUrl("jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase());
            config.setUsername(databaseConfig.getUsername());
            config.setPassword(databaseConfig.getPassword());
        } else {
            config.setJdbcUrl("jdbc:sqlite:"+plugin.getDataFolder() + "/database.db");
        }
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.hikariDataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        if(this.hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    public boolean isConnected() {
        return (hikariDataSource != null && !hikariDataSource.isClosed());
    }

    public void executeQueued() {
        final int BATCH_LIMIT = 1000;
        int countQuery = 0;

        try (Connection conn = this.hikariDataSource.getConnection()) {
            conn.setAutoCommit(false);

            while (!queuedQueries.isEmpty()) {
                int batchCount = 0;

                while (!queuedQueries.isEmpty() && batchCount < BATCH_LIMIT) {
                    QueuedQuery queuedQuery = queuedQueries.poll();

                    try (PreparedStatement statement = conn.prepareStatement(queuedQuery.getSql())) {
                        List<Object> parameters = queuedQuery.getParameters();

                        for (int i = 0; i < parameters.size(); i++) {
                            Object param = parameters.get(i);
                            statement.setObject(i + 1, param);
                        }

                        statement.addBatch();
                        batchCount++;
                        countQuery++;

                        statement.executeBatch();
                    } catch (SQLException e) {
                        conn.rollback();
                        MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error executing batch query: " + e.getMessage());
                        return;
                    }
                }
            }

            conn.commit();
            MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully executed " + countQuery + " queries.");
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error obtaining connection or handling transaction: " + e.getMessage());
        }
    }





    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public void addQueue(QueuedQuery queuedQuery) {
        queuedQueries.add(queuedQuery);
    }

}
