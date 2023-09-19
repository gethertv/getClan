package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.ColorFixer;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.entity.Player;

@Route(name = "getclan", aliases = "klan")
@Permission("getclan.use")
public class GetClansCmd {

    private final GetClan plugin;
    public GetClansCmd(GetClan plugin) {
        this.plugin = plugin;
    }
    @Execute(route = "usun")
    public void removeClan(Player player) {
        plugin.getClansManager().removeClan(player);
    }

    @Execute(route = "opusc")
    public void leaveClan(Player player) {
        plugin.getClansManager().leaveClan(player);
    }

    @Execute(route = "stworz")
    public void createClan(Player sender, @Arg String tag) {
        plugin.getClansManager().createClan(sender, tag);
    }

    @Execute(route = "zapros")
    public void inviteUser(Player player, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().inviteUser(player, target);
    }
    @Execute(route = "wyrzuc")
    public void kickUser(Player player, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().kickUser(player, target);
    }

    @Execute(route = "sojusz")
    public void alliace(Player player, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().alliance(player, clan);
    }


    @Execute(route = "dolacz")
    public void joinClan(Player player, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().joinClan(player, clan);
    }

    @Execute(route = "admin reset")
    @Permission("getclans.admin")
    public void adminReset(LiteSender sender, @Arg @Name("gracz") Player target) {
        plugin.getUserManager().resetUser(target);
        MessageUtil.sendMessage(sender, "&aPomyslnie zresetowano ranking!");
    }

    @Execute(route = "admin debug")
    @Permission("getclans.admin")
    public void adminDebug(LiteSender sender, @Arg @Name("gracz") Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        if (user == null || user.getClan() == null) {
            MessageUtil.sendMessage(sender, "&cGracz nie posiada klanu!");
            return;
        }
        UserManager userManager = plugin.getUserManager();
        user.getClan().getMembers().forEach(((uuid) -> {
            User userTemp = userManager.getUserData().get(uuid);
            MessageUtil.sendMessage(sender, "&7" + uuid + " ---> " + userTemp.getPoints());
        }));
    }

    @Execute(route = "admin usun")
    @Permission("getclans.admin")
    public void adminRemove(LiteSender sender, @Arg @Name("gracz") Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        if (user == null || user.getClan() == null) {
            MessageUtil.sendMessage(sender, "&cGracz nie posiada klanu!");
            return;
        }
        plugin.getClansManager().removeClan(target);
        MessageUtil.sendMessage(sender, "&aPomyslnie usunieto klan");
    }

    @Execute(route = "admin setpoint")
    @Permission("getclans.admin")
    public void adminSetPoint(LiteSender sender, @Arg @Name("gracz") Player target, @Arg @Name("punkty") int points) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        user.setPoints(points);
        sender.sendMessage(ColorFixer.addColors("&aPomyslnie ustawione nowe punkty dla " + target.getName()));
    }

    private boolean isInt(String input)
    {
        try {
            int a = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored) {}

        return false;
    }

}
