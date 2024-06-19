package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    private final GetClan plugin;
    private final FileManager fileManager;
    private final ClanManager clanManager;

    public AsyncPlayerChatListener(GetClan plugin, FileManager fileManager, ClanManager clanManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSendMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if (!user.hasClan())
            return;

        Clan clan = clanManager.getClan(user.getTag());
        if (message.startsWith("!!")) {
            event.setCancelled(true);
            message = message.substring(2);
            if (message.length() == 0)
                return;

            clan.broadcast(fileManager.getConfig().getFormatAllianceMessage()
                    .replace("{tag}", clan.getTag())
                    .replace("{message}", message)
                    .replace("{player}", player.getName())
            );
            return;
        }
        if (message.startsWith("!")) {
            event.setCancelled(true);
            message = message.substring(1);
            if (message.length() == 0)
                return;

            clan.broadcast(fileManager.getConfig().getFormatClanMessage()
                    .replace("{tag}", clan.getTag())
                    .replace("{message}", message)
                    .replace("{player}", player.getName())
            );
            return;
        }

    }

}
