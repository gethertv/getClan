package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;


@Route(name = "getgracz", aliases = "gracz")
@Permission("getclan.use")
public class GetGraczPLCmd {

    private final GetClan plugin;

    public GetGraczPLCmd(GetClan plugin) {
        this.plugin = plugin;
    }

    @Execute()
    public void infoClan(Player player, @Arg @Name("gracz") User user) {
        plugin.getUserManager().infoPlayer(player, user);
    }
}
