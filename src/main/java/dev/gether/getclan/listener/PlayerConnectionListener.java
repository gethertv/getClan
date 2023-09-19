package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConnectionListener implements Listener {

    private final GetClan plugin;
    public PlayerConnectionListener(GetClan plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onJoinPlayer(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {
                plugin.getUserManager().loadUser(player);
            }
        }.runTaskAsynchronously(plugin);
    }
}
