package dev.gether.getclan.manager;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.service.UserService;
import dev.gether.getclan.model.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private Config config;
    private HashMap<UUID, User> userData = new HashMap<>();
    private UserService userService;
    public UserManager(GetClan plugin, UserService userService)
    {
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

    public void resetUser(User user) {
        user.setPoints(config.defaultPoints);
        user.resetKill();
        user.resetDeath();
    }
    public void resetPoints(User user) {
        user.setPoints(config.defaultPoints);
    }

    public void resetKill(User user) {
        user.resetKill();
    }

    public void resetDeath(User user) {
        user.resetDeath();
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }


}
