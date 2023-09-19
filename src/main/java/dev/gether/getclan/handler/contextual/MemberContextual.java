package dev.gether.getclan.handler.contextual;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.clan.Member;
import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

public class MemberContextual implements Contextual<CommandSender, Member> {


    private GetClan plugin;
    private Config config;
    public MemberContextual(GetClan plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
    }
    @Override
    public Result<Member, Object> extract(CommandSender sender, Invocation<CommandSender> invocation) {
        Option<Player> playerOption = Option.of(sender).is(Player.class);
        if(playerOption.isEmpty()) {
            return Result.error(config.langPlayerNotOnline);
        }
        Player player = playerOption.get();
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        if(!user.hasClan()) {
            return Result.error(config.langNoClan);
        }

        return Result.ok(new Member(player, user.getClan()));
    }


}