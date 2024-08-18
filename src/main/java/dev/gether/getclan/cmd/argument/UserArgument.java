package dev.gether.getclan.cmd.argument;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.user.User;
import dev.gether.getconfig.utils.ColorFixer;
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

public class UserArgument extends ArgumentResolver<CommandSender, User> {

    private final UserManager userManager;
    private final FileManager fileManager;

    public UserArgument(UserManager userManager, FileManager fileManager) {
        this.userManager = userManager;
        this.fileManager = fileManager;
    }

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> invocation, Argument<User> context, String argument) {
        Player player = Bukkit.getPlayer(argument);
        if (player == null) {
            return ParseResult.failure(ColorFixer.addColors(fileManager.getLangConfig().getMessage("player-not-found")));
        }

        User user = userManager.getUserData().get(player.getUniqueId());
        return ParseResult.success(user);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<User> argument, SuggestionContext context) {
        return SuggestionResult.of(Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));
    }
}
