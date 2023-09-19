package dev.gether.getclan.utils;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.Clan;
import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

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
        sender.sendMessage(ColorFixer.addColors(String.join("\n", message)));
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

    public static void sendMessageAlliance(Clan clan, String message) {
        ClanManager clansManager = GetClan.getInstance().getClansManager();
        sendMessage(clan, message);
        for (String allianceTag : clan.getAlliances()) {
            Clan alliaceClan = clansManager.getClansData().get(allianceTag.toUpperCase());
            sendMessage(alliaceClan, message);
        }
    }
}
