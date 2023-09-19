package dev.gether.getclans.utils;

import dev.gether.getclans.model.Clan;
import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MessageUtil {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ColorFixer.addColors(message));
    }

    public static void sendMessage(Clan clan, String message) {
        for(UUID uuid : clan.getMembers())
        {
            Player player = Bukkit.getPlayer(uuid);
            if(player==null)
                continue;

            player.sendMessage(ColorFixer.addColors(message));
        }
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(ColorFixer.addColors(message));
    }

    public static void sendMessage(CommandSender sender, List<String> message) {

    }
    public static void sendMessage(LiteSender sender, String message) {
        sender.sendMessage(ColorFixer.addColors(message));
    }
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ColorFixer.addColors(message));
    }

    public static String joinListToString(List<String> lists) {
        StringBuilder sb = new StringBuilder();

        for (String element : lists) {
            sb.append(element).append("\n");
        }

        return sb.toString();
    }
}
