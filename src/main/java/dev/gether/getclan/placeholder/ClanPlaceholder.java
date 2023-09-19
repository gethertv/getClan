package dev.gether.getclan.placeholder;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.ColorFixer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class StatsPoints extends PlaceholderExpansion implements Relational {

    @Override
    public @NotNull String getIdentifier() {
        return "getclan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gethertv";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    private final GetClan plugin;

    public StatsPoints(GetClan plugin)
    {
        this.plugin = plugin;
        this.register();
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if(identifier.equalsIgnoreCase("upoints"))
        {
            User user = GetClan.getInstance().getUserManager().getUserData().get(player.getUniqueId());
            if(user==null)
                return "";

            return ""+user.getPoints();
        }
        if(identifier.equalsIgnoreCase("points"))
        {
            return getAveragePoint(player);
        }
        if(identifier.equalsIgnoreCase("tag"))
        {
            User user = GetClan.getInstance().getUserManager().getUserData().get(player.getUniqueId());
            if(user==null || user.getClan()==null)
                return "";

            return user.getClan().getTag();
        }


        return null;
    }


    public String getAveragePoint(Player player)
    {
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());
        if(user==null || user.hasClan())
            return "";

        List<UUID> members = user.getClan().getMembers();
        int sum = 0;
        int count = 0;

        for (UUID uuid : members) {
            User tempUser = userManager.getUserData().get(uuid);
            sum += tempUser.getPoints();
            count++;
        }
        double average = (double) sum / count;
        return String.valueOf((int) average);
    }

    @Override
    public String onPlaceholderRequest(Player first, Player second, String identifier) {
        if(first == null || second == null)
            return null;

        if(identifier.equalsIgnoreCase("rtag"))
        {
            User user = plugin.getUserManager().getUserData().get(first.getUniqueId());
            if(user==null || user.getClan()==null)
                return "";

            Clan clan = user.getClan();
            String tag = user.getClan().getTag();
            if(clan.isMember(second.getUniqueId()))
                return ColorFixer.addColors("&e"+tag);
            else
                return ColorFixer.addColors("&c"+tag);
        }
        return null;
    }
}
