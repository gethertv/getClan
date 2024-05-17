package dev.gether.getclan.handler.contextual;


import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.role.Owner;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;

public class OwnerContextual extends ArgumentResolver<CommandSender, Owner> {


    private final GetClan plugin;
    private final FileManager fileManager;

    public OwnerContextual(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @Override
    protected ParseResult<Owner> parse(Invocation<CommandSender> invocation, Argument<Owner> argument, String s) {
        Option<Player> playerOption = Option.of(invocation).is(Player.class);
        if (playerOption.isEmpty()) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-not-found"));
        }

        Player player = playerOption.get();
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        if (!user.hasClan()) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-has-no-clan"));
        }
        if (!user.getClan().isOwner(player.getUniqueId())) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("not-clan-owner"));
        }

        return ParseResult.success(new Owner(player, user.getClan()));

    }
}
