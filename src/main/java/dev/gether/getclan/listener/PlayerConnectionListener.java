package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(!user.hasClan())
            return;

        Clan clan = user.getClan();
        boolean owner = clan.isOwner(player.getUniqueId());
        if(owner)
            clan.resetInvite();

    }
}
