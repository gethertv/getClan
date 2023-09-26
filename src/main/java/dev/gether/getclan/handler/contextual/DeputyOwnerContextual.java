package dev.gether.getclan.handler.contextual;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

public class DeputyOwnerContextual implements Contextual<CommandSender, DeputyOwner> {


    private GetClan plugin;
    private LangMessage lang;
    public DeputyOwnerContextual(GetClan plugin) {
        this.plugin = plugin;
        this.lang = plugin.lang;
    }
    @Override
    public Result<DeputyOwner, Object> extract(CommandSender sender, Invocation<CommandSender> invocation) {
        Option<Player> playerOption = Option.of(sender).is(Player.class);
        if(playerOption.isEmpty()) {
            return Result.error(lang.langPlayerNotOnline);
        }

        Player player = playerOption.get();
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        if(!user.hasClan()) {
            return Result.error(lang.langNoClan);
        }
        if(!user.getClan().isOwner(player.getUniqueId())) {
            if(!user.getClan().isDeputy(player.getUniqueId()))
            {
                return Result.error(lang.langNotOwnerClan);
            }
        }

        return Result.ok(new DeputyOwner(player, user.getClan()));
    }


}