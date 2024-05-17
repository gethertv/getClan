package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
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
import panda.std.Result;

import java.util.stream.Collectors;

public class OwnerArgument extends ArgumentResolver<CommandSender, Owner> {

    private final UserManager userManager;
    private final FileManager fileManager;

    public OwnerArgument(UserManager userManager, FileManager fileManager) {
        this.userManager = userManager;
        this.fileManager = fileManager;
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
        return ParseResult.success(new Owner(player, user.getClan()));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Owner> argument, SuggestionContext context) {
        return SuggestionResult.of(Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));
    }

}
