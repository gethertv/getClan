package dev.gether.getclan.handler.contextual;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.role.Member;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;

public class MemberContextual extends ArgumentResolver<CommandSender, Member> {


    private final GetClan getClan;
    private final FileManager fileManager;

    public MemberContextual(GetClan getClan, FileManager fileManager) {
        this.getClan = getClan;
        this.fileManager = fileManager;
    }

    @Override
    protected ParseResult<Member> parse(Invocation<CommandSender> invocation, Argument<Member> argument, String s) {
        Option<Player> playerOption = Option.of(invocation).is(Player.class);
        if(playerOption.isEmpty()) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-not-found"));
        }
        Player player = playerOption.get();
        UserManager userManager = getClan.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        if(!user.hasClan()) {
            return ParseResult.failure(fileManager.getLangConfig().getMessage("player-has-no-clan"));
        }

        return ParseResult.success(new Member(player, user.getClan()));

    }
}