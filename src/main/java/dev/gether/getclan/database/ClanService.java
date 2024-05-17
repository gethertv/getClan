package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClanService implements GetTable {

    private final String table = "get_clans";
    private final HikariDataSource hikariDataSource;

    public ClanService(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void createTable() {

        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                + "tag VARCHAR(10),"
                + "owner_uuid VARCHAR(100),"
                + "deputy_uuid VARCHAR(100),"
                + "pvpEnable BOOLEAN)";

        try(Statement stmt = this.hikariDataSource.getConnection().createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "Cannot create the table "+table+ ". Error "+e.getMessage());
        }

    }

    // INGORE IT
    @Deprecated
    private void checkColumn() {
        final String column = "pvpEnable";
        try (Connection conn = hikariDataSource.getConnection();
             Statement statement = conn.createStatement()) {

            ResultSet resultSet = conn.getMetaData().getTables(null, null, table, null);
            if (resultSet.next()) {
                // check column exists (pvpEnable)
                ResultSet columnResultSet = conn.getMetaData().getColumns(null, null, table, column);
                if (!columnResultSet.next()) {
                    // if column not exists so add it
                    String addColumnQuery = "ALTER TABLE " + table + " ADD COLUMN " + column + " BOOLEAN";
                    statement.executeUpdate(addColumnQuery);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTable() {
        return this.table;
    }
}
