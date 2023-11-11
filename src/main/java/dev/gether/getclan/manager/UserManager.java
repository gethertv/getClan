package dev.gether.getclan.manager;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.model.User;
import dev.gether.getclan.service.UserService;
import dev.gether.getclan.utils.ColorFixer;
import dev.gether.getclan.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.OptionalInt;
import java.util.UUID;

public class UserManager {

    private Config config;
    private LangMessage lang;
    private HashMap<UUID, User> userData = new HashMap<>();
    private UserService userService;
    private final GetClan plugin;

    public UserManager(GetClan plugin, UserService userService, LangMessage lang)
    {
        this.plugin = plugin;
        this.userService = userService;
        this.config = plugin.getConfigPlugin();
        this.lang = lang;

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


    public void infoPlayer(Player player, User user) {
        // get player object
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getUuid());

        OptionalInt clanRankIndexByTag = plugin.getTopRankScheduler().getUserRankByName(offlinePlayer.getName());
        int index = 9999;
        if(clanRankIndexByTag.isPresent())
            index = clanRankIndexByTag.getAsInt()+1;

        String clan = (user.getClan() == null) ? config.noneTag : ColorFixer.addColors(config.formatTag.replace("{tag}", user.getClan().getTag()));

        String infoMessage = MessageUtil.joinListToString(lang.langInfoUser);
        infoMessage = infoMessage.replace("{player}", offlinePlayer.getName())
                .replace("{kills}", String.valueOf(user.getKills()))
                .replace("{deaths}", String.valueOf(user.getDeath()))
                .replace("{points}", String.valueOf(user.getPoints()))
                .replace("{tag}", clan)
                .replace("{rank}", String.valueOf(index));


        MessageUtil.sendMessage(player, infoMessage);
    }
}
