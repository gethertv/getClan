package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.model.AntySystemRank;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.ColorFixer;
import dev.gether.getclan.utils.MessageUtil;
import dev.gether.getclan.utils.SystemPoint;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;


public class PlayerDeathListener implements Listener {

    private final GetClan plugin;
    private LangMessage lang;
    private Config config;
    private HashMap<UUID, AntySystemRank> antySystem = new HashMap<>();
    public PlayerDeathListener(GetClan plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
        this.lang = plugin.lang;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if(killer == null)
            return;

        UserManager userManager = plugin.getUserManager();
        User userDeath = userManager.getUserData().get(player.getUniqueId());
        User userKiller = userManager.getUserData().get(killer.getUniqueId());
        if(userDeath == null || userKiller == null)
            return;

        userDeath.increaseDeath();
        userKiller.increaseKill();

        if (config.systemAntiabuse) {
            String playerIp = player.getAddress().getAddress().getHostAddress();
            AntySystemRank antySystemRank = antySystem.get(killer.getUniqueId());

            if (antySystemRank != null) {
                if (!antySystemRank.isPlayerKillable(playerIp)) {
                    int second = SystemPoint.roundUpToMinutes(antySystemRank.getRemainingCooldown(playerIp));
                    MessageUtil.sendMessage(killer, lang.cooldownKill.replace("{time}", String.valueOf(second)));
                    return;
                }
                antySystemRank.addCooldown(playerIp, config.cooldown);
            } else {
                antySystem.put(killer.getUniqueId(), new AntySystemRank(
                        killer.getAddress().getAddress().getHostAddress(),
                        playerIp,
                        config.cooldown
                ));
            }
        }

        int newPointDeath = SystemPoint.calculateEloRating(userDeath.getPoints(), userKiller.getPoints(), 0);
        int newPointKiller = SystemPoint.calculateEloRating(userKiller.getPoints(), userDeath.getPoints(), 1);


        int deathPointTake = userDeath.getPoints()-newPointDeath;
        int killerPointAdd = newPointKiller-userKiller.getPoints();


        if(newPointDeath>=0)
        {
            userKiller.addPoint(killerPointAdd);
            userDeath.takePoint(deathPointTake);
        }
        event.deathMessage(Component.text(
                ColorFixer.addColors(
                        lang.langBroadcastDeathInfo
                                .replace("{victim}", player.getName())
                                .replace("{killer}", killer.getName())
                                .replace("{victim-points}", String.valueOf(deathPointTake))
                                .replace("{killer-points}", String.valueOf(killerPointAdd))
                )
        ));
    }

}
