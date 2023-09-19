package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.ColorFixer;
import dev.gether.getclan.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public class PlayerDeathListener implements Listener {

    private final GetClan plugin;

    private Config config;
    public PlayerDeathListener(GetClan plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
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

        int newPointDeath = calculateEloRating(userDeath.getPoints(), userKiller.getPoints(), 0);
        int newPointKiller = calculateEloRating(userKiller.getPoints(), userDeath.getPoints(), 1);


        int deathPointTake = userDeath.getPoints()-newPointDeath;
        int killerPointAdd = newPointKiller-userKiller.getPoints();

        userDeath.increaseDeath();
        userKiller.increaseKill();
        if(newPointDeath>=0)
        {
            userKiller.addPoint(killerPointAdd);
            userDeath.takePoint(deathPointTake);
        }
        event.deathMessage(Component.text(
                ColorFixer.addColors(
                        config.langBroadcastDeathInfo
                                .replace("{victim}", player.getName())
                                .replace("{killer}", killer.getName())
                                .replace("{victim-points}", String.valueOf(deathPointTake))
                                .replace("{killer-points}", String.valueOf(killerPointAdd))
                )
        ));
    }

    public int calculateEloRating(int oldRating, int opponentRating, double score) {
        int K = 30;
        double expectedScore = calculateExpectedScore(oldRating, opponentRating);
        return oldRating + (int) (K * (score - expectedScore));
    }

    private double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }

}
