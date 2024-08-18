package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.core.user.User;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;


@Command(name = "getplayer", aliases = "player")
@Permission("getclan.use")
public class PlayerENCommand {

    private final GetClan plugin;

    public PlayerENCommand(GetClan plugin) {
        this.plugin = plugin;
    }

    @Execute()
    public void infoClan(@Context Player player, @Arg("user") User user) {
        plugin.getUserManager().infoPlayer(player, user);
    }
}
