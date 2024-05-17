package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;

import java.sql.SQLException;
import java.sql.Statement;

public class AllianceService implements GetTable {


    private final String table = "get_clans";
    private final HikariDataSource hikariDataSource;

    public AllianceService(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }



    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "clan_tag1 VARCHAR(20),"
                + "clan_tag2 VARCHAR(20))";

        try(Statement stmt = this.hikariDataSource.getConnection().createStatement()) {
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
