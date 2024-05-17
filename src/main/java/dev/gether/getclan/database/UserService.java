package dev.gether.getclan.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;

import java.sql.SQLException;
import java.sql.Statement;

public class UserService implements GetTable{


    private final String table = "get_clan_users";
    private final HikariDataSource hikariDataSource;

    public UserService(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }



    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                + "uuid VARCHAR(100),"
                + "username VARCHAR(100),"
                + "kills INT(11) DEFAULT 0,"
                + "deaths INT(11) DEFAULT 0,"
                + "points INT(11) DEFAULT 0,"
                + "clan_tag VARCHAR(100))";

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
