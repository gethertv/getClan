package dev.gether.getclan.manager;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.service.UserService;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class UserManager {

    private HashMap<UUID, User> userData = new HashMap<>();
    private UserService userService;
    private final GetClan plugin;
    private final FileManager fileManager;

    public UserManager(UserService userService, GetClan plugin, FileManager fileManager) {
        this.userService = userService;
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public void loadUser(Player player) {
        User user = userData.get(player.getUniqueId());
        if (user == null) {
            user = new User(player, fileManager.getConfig().getDefaultPoints());
            // add user to system of ranking
            plugin.getTopRankScheduler().addUser(user);
            userData.put(player.getUniqueId(), user);
            userService.createUser(player);
        }
    }

    public void resetUser(User user) {
        user.setPoints(fileManager.getConfig().getDefaultPoints());
        user.resetKill();
        user.resetDeath();
    }


    public void infoPlayer(Player player, User user) {
        // get player object
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getUuid());
        OptionalInt clanRankIndexByTag = plugin.getTopRankScheduler().getUserRankByName(offlinePlayer.getName());
        int index = 9999;
        if (clanRankIndexByTag.isPresent())
            index = clanRankIndexByTag.getAsInt() + 1;

        String clan = (user.getClan() == null) ? fileManager.getConfig().getNoneTag() : fileManager.getConfig().getFormatTag().replace("{tag}", user.getClan().getTag());

        String infoMessage = fileManager.getLangConfig().getMessage("info-user");
        infoMessage = infoMessage.replace("{player}", offlinePlayer.getName())
                .replace("{kills}", String.valueOf(user.getKills()))
                .replace("{deaths}", String.valueOf(user.getDeath()))
                .replace("{points}", String.valueOf(user.getPoints()))
                .replace("{tag}", ColorFixer.addColors(clan))
                .replace("{rank}", String.valueOf(index));


        MessageUtil.sendMessage(player, infoMessage);
    }

    public Optional<User> findUserByUUID(UUID uuid) {
        return Optional.ofNullable(userData.get(uuid));
    }

    public Optional<User> findUserByPlayer(Player player) {
        return findUserByUUID(player.getUniqueId());
    }


    public void resetPoints(User user) {
        user.setPoints(fileManager.getConfig().getDefaultPoints());
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
