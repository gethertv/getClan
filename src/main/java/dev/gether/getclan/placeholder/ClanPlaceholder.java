package dev.gether.getclan.placeholder;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.ranking.PlayerStat;
import dev.gether.getclan.ranking.RankType;
import dev.gether.getconfig.utils.ColorFixer;
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
    private final FileManager fileManager;
    private final ClanManager clanManager;

    public ClanPlaceholder(GetClan plugin, FileManager fileManager, ClanManager clanManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
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
                    String message = user.hasClan() ? fileManager.getConfig().getHasClan() : fileManager.getConfig().getHasNotClan();
                    return ColorFixer.addColors(message);
                case "user_format_points":
                    return ColorFixer.addColors(
                            fileManager.getConfig().getFormatUserPoints().replace("{points}", String.valueOf(user.getPoints()))
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
            Clan clan = clanManager.getClan(user.getTag());
            switch (identifier.toLowerCase()) {
                case "clan_format_points":
                    String averagePoints = plugin.getClanManager().getAveragePoint(player);
                    return ColorFixer.addColors(fileManager.getConfig().getFormatClanPoints().replace("{points}", averagePoints));
                case "clan_format_tag":
                    if (!user.hasClan()) return fileManager.getConfig().getNoneTag();
                    return ColorFixer.addColors(fileManager.getConfig().getFormatTag().replace("{tag}", user.getTag()));
                case "clan_format_tag_upper":
                    if (!user.hasClan()) return fileManager.getConfig().getNoneTag();
                    return ColorFixer.addColors(fileManager.getConfig().getFormatTag().replace("{tag}", user.getTag().toUpperCase()));
                case "clan_points":
                    if (!user.hasClan()) return "";
                    return plugin.getClanManager().getAveragePoint(player);
                case "clan_tag":
                    if (!user.hasClan()) return "";
                    return user.getTag();
                case "clan_tag_upper":
                    if (!user.hasClan()) return "";
                    return user.getTag().toUpperCase();
                case "clan_members_size":
                    if (!user.hasClan()) return "0";

                    return String.valueOf(clan.getMembers().size());
                case "clan_members_online":
                    if (!user.hasClan()) return "0";

                    return String.valueOf(plugin.getClanManager().countOnlineMember(clan));
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

            Clan clan1 = clanManager.getClan(user2.getTag());
            if (clan1 == null) return "";

            String tag = clan1.getTag();

            if (clan1.isMember(first.getUniqueId())) {
                return ColorFixer.addColors(fileManager.getConfig().getFormatMember().replace("{tag}", String.valueOf(tag)));
            }

            Clan clan2 = clanManager.getClan(user1.getTag());
            if (clan2 != null && clan1.isAlliance(clan2.getTag())) {
                return ColorFixer.addColors(fileManager.getConfig().getFormatAlliance().replace("{tag}", String.valueOf(tag)));
            }

            return ColorFixer.addColors(fileManager.getConfig().getFormatNormal().replace("{tag}", String.valueOf(tag)));
        }
        return null;
    }

    private boolean isNumber(String arg) {
        try {
            int a = Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException ignore) {
        }

        return false;
    }

    private String handleTopType(RankType rankType, String identifier, int top) {
        top = top - 1;
        Optional<PlayerStat> rank = plugin.getRankingManager().findTopPlayerByIndex(rankType, top);
        if (rank.isEmpty())
            return "";

        PlayerStat playerStat = rank.get();
        if (identifier.endsWith("_value")) {
            return String.valueOf(playerStat.getValue());
        }
        if (identifier.endsWith("_name")) {
            return String.valueOf(playerStat.getName());
        }
        return "";
    }
}
