package dev.gether.getclan.core.user;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
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
        int index = plugin.getRankingManager().findTopPlayerByName(user);

        String clanTag = user.hasClan() ? fileManager.getConfig().getFormatTag().replace("{tag}", user.getTag()) : fileManager.getConfig().getNoneTag();

        String infoMessage = fileManager.getLangConfig().getMessage("info-user");
        infoMessage = infoMessage.replace("{player}", user.getName())
                .replace("{kills}", String.valueOf(user.getKills()))
                .replace("{deaths}", String.valueOf(user.getDeath()))
                .replace("{points}", String.valueOf(user.getPoints()))
                .replace("{tag}", ColorFixer.addColors(clanTag))
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

    public void loadUsers() {
        Set<User> users = userService.loadUsers();
        users.forEach(user -> {
            if(user.hasClan()) {
                Clan clan = plugin.getClanManager().getClan(user.getTag());
                if(!clan.isOwner(user.getUuid()))
                    clan.addMember(user.getUuid());
            }
            userData.put(user.getUuid(), user);
        });
    }

    public void update(User user) {
        userService.updateUser(user);
    }
}
