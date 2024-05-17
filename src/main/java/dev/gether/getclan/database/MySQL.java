package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.config.domain.DatabaseConfig;
import dev.gether.getclan.model.QueuedQuery;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class MySQL {

    private final GetClan plugin;
    private FileManager fileManager;

    private HikariDataSource hikariDataSource;

    private Queue<QueuedQuery> queuedQueries = new PriorityQueue<>();

    public MySQL(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    private void connect() {
        DatabaseConfig databaseConfig = fileManager.getDatabaseConfig();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase());
        config.setUsername(databaseConfig.getUsername());
        config.setPassword(databaseConfig.getPassword());
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
        try (Connection conn = this.hikariDataSource.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction block
            try (Statement stmt = conn.createStatement()) {
                int countQuery = 0;
                while (!queuedQueries.isEmpty()) {
                    QueuedQuery queuedQuery = queuedQueries.poll();
                    try (PreparedStatement statement = conn.prepareStatement(queuedQuery.getSql())) {
                        List<Object> parameters = queuedQuery.getParameters();
                        for (int i = 0; i < parameters.size(); i++) {
                            statement.setObject(i + 1, parameters.get(i));
                        }
                        statement.addBatch();  // add to batch
                        countQuery++;
                    } catch (SQLException e) {
                        conn.rollback();
                        MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error executing query: " + e.getMessage());
                        return;
                    }
                }
                stmt.executeBatch();
                conn.commit();
                MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully executed " + countQuery + " queries.");
            } catch (SQLException e) {
                conn.rollback();
                MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error during batch execution or commit: " + e.getMessage());
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[MySQL] - Error obtaining connection or handling transaction: " + e.getMessage());
        }
    }

}
