package dev.gether.getclan.service;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.model.QueuedQuery;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.ConsoleColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueService {


    private final GetClan plugin;
    private Queue<QueuedQuery> queryQueue = new LinkedList<>();

    private MySQL mySQL;
    public QueueService(GetClan plugin, MySQL mySQL)
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

            int countQuery = 0;
            while (!queryQueue.isEmpty()) {
                QueuedQuery queuedQuery = queryQueue.poll();

                try (PreparedStatement statement = connection.prepareStatement(queuedQuery.getSql())) {
                    List<Object> parameters = queuedQuery.getParameters();

                    for (int i = 0; i < parameters.size(); i++) {
                        statement.setObject(i + 1, parameters.get(i));
                    }

                    statement.executeUpdate();
                    countQuery++;
                } catch (SQLException e) {
                    plugin.getLogger().severe("Błąd podczas przetwarzania zapytania: " + e.getMessage());
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
            plugin.getLogger().info(ConsoleColor.CYAN+"Zapis do bazy danych. Wykonane zapytania: "+countQuery);
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
