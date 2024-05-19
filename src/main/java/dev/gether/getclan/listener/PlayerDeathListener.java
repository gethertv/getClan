package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.event.PointsChangeUserEvent;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.AntySystemRank;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.utils.SystemPoint;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;


public class PlayerDeathListener implements Listener {

    private final GetClan plugin;
    private final FileManager fileManager;
    private HashMap<UUID, AntySystemRank> antySystem = new HashMap<>();

    public PlayerDeathListener(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        UserManager userManager = plugin.getUserManager();
        User userDeath = userManager.getUserData().get(player.getUniqueId());

        if (userDeath == null)
            return;

        // increase death
        userDeath.increaseDeath();

        if (killer == null) {
            // message after the death
            if (!fileManager.getConfig().isDeathMessage())
                return;

            MessageUtil.broadcast(
                    fileManager.getLangConfig().getMessage("death-self-inflicted")
                            .replace("{victim}", player.getName())
            );
            return;
        }

        User userKiller = userManager.getUserData().get(killer.getUniqueId());
        if (userKiller == null)
            return;


        userKiller.increaseKill();

        if (fileManager.getConfig().isSystemAntiabuse()) {
            String playerIp = player.getAddress().getAddress().getHostAddress();
            AntySystemRank antySystemRank = antySystem.get(killer.getUniqueId());

            if (antySystemRank != null) {
                if (!antySystemRank.isPlayerKillable(playerIp)) {
                    int second = SystemPoint.roundUpToMinutes(antySystemRank.getRemainingCooldown(playerIp));
                    MessageUtil.sendMessage(killer, fileManager.getLangConfig().getMessage("cooldown-kill").replace("{time}", String.valueOf(second)));
                    return;
                }
                antySystemRank.addCooldown(playerIp, fileManager.getConfig().getCooldown());
            } else {
                antySystem.put(killer.getUniqueId(), new AntySystemRank(
                        killer.getAddress().getAddress().getHostAddress(),
                        playerIp,
                        fileManager.getConfig().getCooldown()
                ));
            }
        }

        int newPointDeath = SystemPoint.calculateEloRating(userDeath.getPoints(), userKiller.getPoints(), 0);
        int newPointKiller = SystemPoint.calculateEloRating(userKiller.getPoints(), userDeath.getPoints(), 1);

        int deathPointTake = userDeath.getPoints() - newPointDeath;
        int killerPointAdd = newPointKiller - userKiller.getPoints();

        PointsChangeUserEvent pointsChangeUserEvent = new PointsChangeUserEvent(killer, player, killerPointAdd, deathPointTake);
        Bukkit.getPluginManager().callEvent(pointsChangeUserEvent);
        if (pointsChangeUserEvent.isCancelled())
            return;

        if (newPointDeath >= 0) {
            userKiller.addPoint(pointsChangeUserEvent.getPointKiller());
            userDeath.takePoint(pointsChangeUserEvent.getPointVictim());
        }

        // message after the death
        if (!fileManager.getConfig().isDeathMessage())
            return;

        MessageUtil.broadcast(
                fileManager.getLangConfig().getMessage("death-info")
                                .replace("{victim}", player.getName())
                                .replace("{killer}", killer.getName())
                                .replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim()))
                                .replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller()))
        );
    }

}
