package dev.gether.getclan.placeholder;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.PlayerStat;
import dev.gether.getclan.model.RankType;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.ColorFixer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


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
        if (identifier.startsWith("top")) {
            String[] args = identifier.split("_");
            if (args.length >= 4 && isNumber(args[2])) {
                int top = Integer.parseInt(args[2]);
                if (identifier.startsWith("top_kill")) {
                    return handleTopType(RankType.KILLS, identifier, top);
                }
                if (identifier.startsWith("top_death")) {
                    return handleTopType(RankType.DEATHS, identifier, top);
                }
                if (identifier.startsWith("top_points")) {
                    return handleTopType(RankType.USER_POINTS, identifier, top);
                }
                if (identifier.startsWith("top_clan")) {
                    return handleTopType(RankType.CLAN_POINTS, identifier, top);
                }
            }

        }
        if (identifier.startsWith("user")) {
            User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
            if (user == null) {
                return "";
            }
            switch (identifier.toLowerCase()) {
                case "user_has_clan":
                    return user.hasClan() ? ColorFixer.addColors(config.hasClan) : ColorFixer.addColors(config.hasNotClan);
                case "user_format_points":
                    return ColorFixer.addColors(
                            config.formatUserPoints.replace("{points}", String.valueOf(user.getPoints()))
                    );
                case "user_points":
                    return String.valueOf(user.getPoints());
                case "user_kills":
                    return String.valueOf(user.getKills());
                case "user_death":
                    return String.valueOf(user.getDeath());
            }
            return null;
        }
        if (identifier.startsWith("clan")) {
            User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
            if (user == null) {
                return "";
            }
            switch (identifier.toLowerCase()) {
                case "clan_format_points":
                    String averagePoints = plugin.getClansManager().getAveragePoint(player);
                    return ColorFixer.addColors(
                            config.formatClanPoints.replace("{points}", averagePoints)
                    );
                case "clan_format_tag":
                    if (user.getClan() == null) return config.noneTag;
                    return ColorFixer.addColors(
                            config.formatTag.replace("{tag}", user.getClan().getTag())
                    );
                case "clan_points":
                    if (user.getClan() == null) return "";
                    return plugin.getClansManager().getAveragePoint(player);
                case "clan_tag":
                    if (user.getClan() == null) return "";
                    return user.getClan().getTag();
                case "clan_members_size":
                    if (user.getClan() == null) return "0";
                    return String.valueOf(user.getClan().getMembers().size());
                case "clan_members_online":
                    if (user.getClan() == null) return "0";
                    return String.valueOf(plugin.getClansManager().countOnlineMember(user.getClan()));
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

    private boolean isNumber(String arg) {
        try {
            int a = Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException ignore) {}

        return false;
    }

    private String handleTopType(RankType rankType, String identifier, int top) {
        Optional<PlayerStat> rank = plugin.getTopRankScheduler().getRank(rankType, top);
        if(rank.isEmpty())
            return "";

        PlayerStat playerStat = rank.get();
        if (identifier.endsWith("_value")) {
            return String.valueOf(playerStat.getInt());
        }
        if (identifier.endsWith("_name")) {
            return String.valueOf(playerStat.getName());
        }
        return "";
    }

}
