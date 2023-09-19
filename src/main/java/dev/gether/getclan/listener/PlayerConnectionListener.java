package dev.gether.getclans.listener;

import dev.gether.getclans.GetClans;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConnectionListener implements Listener {

    private final GetClans plugin;
    public PlayerConnectionListener(GetClans plugin)
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
