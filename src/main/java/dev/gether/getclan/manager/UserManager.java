package dev.gether.getclans.manager;

import dev.gether.getclans.GetClans;
import dev.gether.getclans.config.Config;
import dev.gether.getclans.service.UserService;
import dev.gether.getclans.model.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private final GetClans plugin;
    private Config config;
    private HashMap<UUID, User> userData = new HashMap<>();

    private UserService userService;
    public UserManager(GetClans plugin, UserService userService)
    {
        this.plugin = plugin;
        this.userService = userService;
        this.config = plugin.getConfigPlugin();

    }

    public void loadUser(Player player)
    {
        User user = userData.get(player.getUniqueId());
        if(user==null)
        {
            user = new User(player, config.defaultPoints);
            userData.put(player.getUniqueId(), user);
            userService.createUser(player);
        }
    }

    public boolean resetUser(Player player) {
        User user = userData.get(player.getUniqueId());
        if(user==null)
            return false;

        user.setPoints(config.defaultPoints);
        return true;
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }
}
