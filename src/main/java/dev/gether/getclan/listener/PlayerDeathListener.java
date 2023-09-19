package dev.gether.getclans.listener;

import dev.gether.getclans.GetClans;
import dev.gether.getclans.config.Config;
import dev.gether.getclans.model.User;
import dev.gether.getclans.manager.UserManager;
import dev.gether.getclans.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public class PlayerDeathListener implements Listener {

    private final GetClans plugin;
    private Config config;
    public PlayerDeathListener(GetClans plugin)
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

        int diffDeathPoint = userDeath.getPoints()-newPointDeath;
        int diffKillerPoint = newPointKiller-userKiller.getPoints();

        userKiller.addKill(diffKillerPoint);
        userDeath.addDeath(diffDeathPoint);


        MessageUtil.broadcast(
                config.langBroadcastDeathInfo
                .replace("{death}", player.getName())
                .replace("{killer}", killer.getName())
                .replace("{d-point}", String.valueOf(diffDeathPoint))
                .replace("{k-point}", String.valueOf(diffKillerPoint))
        );
    }

    public int calculateEloRating(int oldRating, int opponentRating, double score) {
        int K = 50;
        double expectedScore = calculateExpectedScore(oldRating, opponentRating);
        return oldRating + (int) (K * (score - expectedScore));
    }

    private double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }
}
