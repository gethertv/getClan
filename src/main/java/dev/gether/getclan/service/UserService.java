package dev.gether.getclan.service;

import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.QueuedQuery;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.ConsoleColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserService extends BaseService {

    public UserService(MySQL sql, QueueService queueService)
    {
        super(sql, queueService);
    }
    public void createUser(Player player) {
        String sql = "INSERT INTO " + tableUsers + " (uuid, username) VALUES (?, ?)";
        List<Object> parameters = Arrays.asList(player.getUniqueId().toString(), player.getName());
        queueService.addQueue(new QueuedQuery(sql, parameters));
    }

    public void updateUser(UUID uuid, User user) {
        if(user==null) return;

        String sql = "UPDATE "+tableUsers+" SET kills = ? , deaths = ? , points = ? , clan_tag = ? WHERE uuid = ?";

        String clan = (user.getClan()!=null ? user.getClan().getTag() : "");
        List<Object> parameters = Arrays.asList(user.getKills(), user.getDeath(), user.getPoints(), clan, uuid.toString());
        queueService.addQueue(new QueuedQuery(sql, parameters));

    }

    public void loadUsers() {
        int countUser = 0;
        plugin.getLogger().info(ConsoleColor.YELLOW+"Wczytywanie uzytkownikow...");
        String str = "SELECT * FROM " + tableUsers;
        try (ResultSet resultSet = getResult(str)) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");
                int points = resultSet.getInt("points");
                String tag = resultSet.getString("clan_tag");
                Clan clan = null;
                if (tag != null) {
                    Clan temp = plugin.getClansManager().getClan(tag.toUpperCase());
                    // check clan exists
                    if (temp != null) {
                        clan = temp;
                        if(!clan.isOwner(uuid))
                            clan.addMember(uuid);
                    }
                }
                plugin.getUserManager().getUserData().put(uuid, new User(uuid, kills, deaths, points, clan));
                countUser++;
            }
        } catch (SQLException sQLException) {
            plugin.getLogger().severe(sQLException.getMessage());
        }

        plugin.getLogger().info(ConsoleColor.GREEN+ "Pomyslnie zaladowano "+countUser+" uzytkownikow");
    }

}
