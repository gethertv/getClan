package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.domain.Config;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AsyncPlayerChatListener implements Listener {

    private GetClan plugin;
    private Config config;

    public AsyncPlayerChatListener(GetClan plugin){
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSendMessage(AsyncChatEvent event)
    {
        Player player = event.getPlayer();
        Component messageComponent = event.message();
        if(!(messageComponent instanceof TextComponent))
            return;

        TextComponent textMessage = (TextComponent) messageComponent;
        String message = textMessage.content();

        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(!user.hasClan())
            return;

        Clan clan = user.getClan();
        if(message.startsWith("!!"))
        {
            event.setCancelled(true);
            message = message.substring(2);
            if(message.length()==0)
                return;

            MessageUtil.sendMessageAlliance(clan, config.formatAllianceMessage
                    .replace("{tag}", clan.getTag())
                    .replace("{message}", message)
                    .replace("{player}", player.getName())
            );
            return;
        }
        if(message.startsWith("!"))
        {
            event.setCancelled(true);
            message = message.substring(1);
            if(message.length()==0)
                return;

            MessageUtil.sendMessage(clan, config.formatClanMessage
                    .replace("{tag}", clan.getTag())
                    .replace("{message}", message)
                    .replace("{player}", player.getName())
            );
            return;
        }

    }

}
