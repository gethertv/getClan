package dev.gether.getclans.service;

import dev.gether.getclans.GetClans;
import dev.gether.getclans.database.MySQL;
import dev.gether.getclans.model.QueuedQuery;
import dev.gether.getclans.model.User;
import dev.gether.getclans.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueService {


    private final GetClans plugin;
    private Queue<QueuedQuery> queryQueue = new LinkedList<>();

    private MySQL mySQL;
    public QueueService(GetClans plugin, MySQL mySQL)
    {
        this.plugin = plugin;
        this.mySQL = mySQL;
    }
    public void execute() {

        updateData();

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            while (!queryQueue.isEmpty()) {
                QueuedQuery queuedQuery = queryQueue.poll();

                try (PreparedStatement statement = connection.prepareStatement(queuedQuery.getSql())) {
                    List<Object> parameters = queuedQuery.getParameters();

                    for (int i = 0; i < parameters.size(); i++) {
                        statement.setObject(i + 1, parameters.get(i));
                    }

                    statement.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Błąd podczas przetwarzania zapytania: " + e.getMessage());
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
        } catch (SQLException e) {
            plugin.getLogger().severe("Błąd podczas nawiązywania połączenia lub transakcji: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    plugin.getLogger().severe("Błąd podczas cofania transakcji: " + ex.getMessage());
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Błąd podczas zamykania połączenia: " + e.getMessage());
                }
            }
        }
    }

    private void updateData() {
        UserManager userManager = plugin.getUserManager();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            User user = userManager.getUserData().get(player.getUniqueId());
            plugin.getUserService().updateUser(player.getUniqueId(), user);
        }
    }


    public void addQueue(QueuedQuery query)
    {
        this.queryQueue.add(query);
    }
    private Connection getConnection()
    {
        return mySQL.getConnection();
    }
}
