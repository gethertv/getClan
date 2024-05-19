package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.model.role.Owner;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class OwnerArgument extends ArgumentResolver<CommandSender, Owner> {

    private final UserManager userManager;
    private final FileManager fileManager;
    private final ClanManager clanManager;

    public OwnerArgument(UserManager userManager, FileManager fileManager, ClanManager clanManager) {
        this.userManager = userManager;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    @Override
    protected ParseResult<Owner> parse(Invocation<CommandSender> invocation, Argument<Owner> context, String argument) {
        Player player = Bukkit.getPlayer(argument);
        if (player == null) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-not-found"));
        }

        User user = userManager.getUserData().get(player.getUniqueId());
        if (!user.hasClan()) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-has-no-clan"));
        }
        Clan clan = clanManager.getClan(user.getTag());
        return ParseResult.success(new Owner(player, clan));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Owner> argument, SuggestionContext context) {
        return SuggestionResult.of(Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));
    }

}
