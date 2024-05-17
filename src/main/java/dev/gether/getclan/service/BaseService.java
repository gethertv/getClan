package dev.gether.getclan.service;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.database.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseService {
    protected final GetClan plugin = GetClan.getInstance();
    protected MySQL mysql;
    protected QueueService queueService;

    public BaseService(MySQL mySQL, QueueService queueService) {
        this.mysql = mySQL;
        this.queueService = queueService;
    }

}