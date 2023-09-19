package dev.gether.getclan.handler;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.ConfigPlugin;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.clan.DeputyOwner;
import dev.gether.getclan.model.clan.Owner;
import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

public class DeputyOwnerContextual implements Contextual<CommandSender, DeputyOwner>, ConfigPlugin {


    private GetClan plugin;
    private Config config;
    public DeputyOwnerContextual(GetClan plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
    }
    @Override
    public Result<DeputyOwner, Object> extract(CommandSender sender, Invocation<CommandSender> invocation) {
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
        if(!user.getClan().isOwner(player.getUniqueId())) {
            if(!user.getClan().isDeputy(player.getUniqueId()))
            {
                return Result.error(config.langNotOwnerClan);
            }
        }

        return Result.ok(new DeputyOwner(player, user.getClan()));
    }


    @Override
    public void setConfig(Config config) {
        this.config = config;
    }
}