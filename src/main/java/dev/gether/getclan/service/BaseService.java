package dev.gether.getclans.service;

import dev.gether.getclans.GetClans;
import dev.gether.getclans.config.Config;
import dev.gether.getclans.database.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseService {
    protected final GetClans plugin = GetClans.getInstance();
    protected Config config;
    protected MySQL sql;
    protected QueueService queueService;
    protected String tableUsers;
    protected String tableClans;

    public BaseService(MySQL sql, QueueService queueService) {
        this.config = plugin.getConfigPlugin();
        this.sql = sql;
        this.queueService = queueService;
        this.tableUsers = sql.getTableUsers();
        this.tableClans = sql.getTableClans();
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