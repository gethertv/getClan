package dev.gether.getclan.placeholder;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.ColorFixer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class ClanPlaceholder extends PlaceholderExpansion implements Relational {

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

    @Override
    public boolean persist() {
        return true;
    }

    private final GetClan plugin;
    private Config config;
    public ClanPlaceholder(GetClan plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
        this.register();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if (identifier.startsWith("user")) {
            if (identifier.equalsIgnoreCase("user_points")) {
                User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
                if (user == null) return "";

                return ColorFixer.addColors(
                        config.formatUserPoints.replace("{points}", String.valueOf(user.getPoints()))
                );
            }
            return null;
        }
        if (identifier.startsWith("clan")) {
            switch (identifier.toLowerCase()) {
                case "clan_points":
                    String averagePoints = plugin.getClansManager().getAveragePoint(player);
                    return ColorFixer.addColors(
                            config.formatClanPoints.replace("{points}", averagePoints)
                    );
                case "clan_tag":
                    User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
                    if (user == null || user.getClan() == null) return "";

                    return ColorFixer.addColors(
                            config.formatTag.replace("{tag}", user.getClan().getTag())
                    );
            }
            return null;
        }
        return null;
    }


    @Override
    public String onPlaceholderRequest(Player first, Player second, String identifier) {
        if (first == null || second == null) return null;

        if (identifier.equalsIgnoreCase("tag")) {
            User user1 = plugin.getUserManager().getUserData().get(first.getUniqueId());
            User user2 = plugin.getUserManager().getUserData().get(second.getUniqueId());

            if (user1 == null || user2 == null) return null;

            Clan clan1 = user2.getClan();
            if (clan1 == null) return "";

            String tag = clan1.getTag();

            if (clan1.isMember(first.getUniqueId())) {
                return ColorFixer.addColors(config.formatMember.replace("{tag}", String.valueOf(tag)));
            }

            Clan clan2 = user1.getClan();
            if (clan2 != null && clan1.isAlliance(clan2.getTag())) {
                return ColorFixer.addColors(config.formatAlliance.replace("{tag}", String.valueOf(tag)));
            }

            return ColorFixer.addColors(config.formatNormal.replace("{tag}", String.valueOf(tag)));
        }
        return null;
    }

}
