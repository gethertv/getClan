package dev.gether.getclan.service;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.database.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseService {
    protected final GetClan plugin = GetClan.getInstance();
    protected MySQL sql;
    protected QueueService queueService;
    protected String tableUsers;
    protected String tableClans;
    protected String tableAlliance;

    public BaseService(MySQL sql, QueueService queueService) {
        this.sql = sql;
        this.queueService = queueService;
        this.tableUsers = sql.getTableUsers();
        this.tableClans = sql.getTableClans();
        this.tableAlliance = sql.getTableAlliance();
    }
    public ResultSet getResult(String paramString) throws SQLException {
        Connection connection = getConnection();
        if (connection != null) {
            PreparedStatement statement = connection.prepareStatement(paramString);
            return statement.executeQuery();
        }
        return null;
    }
    private Connection getConnection() {
        return sql.getConnection();
    }

}