package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.config.domain.DatabaseConfig;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MySQL {

    private final GetClan plugin;
    private final FileManager fileManager;

    @Getter
    private HikariDataSource hikariDataSource;

    private final Queue<QueuedQuery> queuedQueries = new ArrayDeque<>();

    public MySQL(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        connect(plugin);
    }

    private void connect(JavaPlugin plugin) {
        DatabaseConfig databaseConfig = fileManager.getDatabaseConfig();
        HikariConfig config = new HikariConfig();

        if (databaseConfig.getDatabaseType() == DatabaseType.MYSQL) {
            config.setJdbcUrl("jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase());
            config.setUsername(databaseConfig.getUsername());
            config.setPassword(databaseConfig.getPassword());
        } else {
            config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/database.db");
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.hikariDataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        if (this.hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    public boolean isConnected() {
        return (hikariDataSource != null && !hikariDataSource.isClosed());
    }

    public void executeQueued() {
        final int BATCH_LIMIT = 1000;
        int countQuery = 0;
        int nullSize = 0;
        List<QueuedQuery> batchList = new ArrayList<>();

        List<QueuedQuery> allQueries = new ArrayList<>(queuedQueries);
        queuedQueries.clear();

        try (Connection conn = this.hikariDataSource.getConnection()) {
            conn.setAutoCommit(false);

            for (QueuedQuery queuedQuery : allQueries) {
                if (queuedQuery == null) {
                    nullSize++;
                    continue;
                }

                batchList.add(queuedQuery);

                if (batchList.size() == BATCH_LIMIT) {
                    try {
                        executeBatch(conn, batchList);
                        countQuery += batchList.size();
                        batchList.clear();
                    } catch (SQLException e) {
                        conn.rollback();
                        MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error executing batch: " + e.getMessage());
                        return;
                    }
                }
            }

            if (!batchList.isEmpty()) {
                try {
                    executeBatch(conn, batchList);
                    countQuery += batchList.size();
                } catch (SQLException e) {
                    conn.rollback();
                    MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error executing remaining batch: " + e.getMessage());
                    return;
                }
            }

            conn.commit();
            MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully executed " + countQuery + " queries.");
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error obtaining connection or handling transaction: " + e.getMessage());
        }
        MessageUtil.logMessage(ConsoleColor.RED, "[getClan] Null size: " + nullSize);
    }

    private void executeBatch(Connection conn, List<QueuedQuery> batchList) throws SQLException {
        if (batchList.isEmpty()) {
            return;
        }

        try {
            for (QueuedQuery queuedQuery : batchList) {
                try (PreparedStatement statement = conn.prepareStatement(queuedQuery.getSql())) {
                    List<Object> parameters = queuedQuery.getParameters();

                    for (int i = 0; i < parameters.size(); i++) {
                        Object param = parameters.get(i);
                        statement.setObject(i + 1, param);
                    }

                    statement.addBatch();
                    statement.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing batch: " + e.getMessage(), e);
        }
    }

    public void addQueue(QueuedQuery queuedQuery) {
        if (queuedQuery == null) {
            MessageUtil.logMessage(ConsoleColor.RED, "HHHHHHHHERE!");
            return;
        }
        queuedQueries.add(queuedQuery);
    }

}
