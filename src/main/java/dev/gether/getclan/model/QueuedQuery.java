package dev.gether.getclan.model;

import java.util.List;

public class QueuedQuery {
    private final String sql;
    private final List<Object> parameters;

    public QueuedQuery(String sql, List<Object> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}