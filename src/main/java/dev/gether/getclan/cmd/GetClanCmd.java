package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.clan.DeputyOwner;
import dev.gether.getclan.model.clan.Member;
import dev.gether.getclan.model.clan.Owner;
import dev.gether.getclan.utils.ColorFixer;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Route(name = "getclan", aliases = "klan")
@Permission("getclan.use")
public class GetClanCmd {

    private final GetClan plugin;
    public GetClanCmd(GetClan plugin) {
        this.plugin = plugin;
    }
    @Execute(route = "usun")
    public void deleteClan(Owner owner) {
        plugin.getClansManager().deleteClan(owner);
    }

    @Execute(route = "opusc")
    public void leaveClan(Member member) {
        plugin.getClansManager().leaveClan(member);
    }

    @Execute(route = "stworz")
    public void createClan(Player player, @Arg String tag) {
        plugin.getClansManager().createClan(player, tag);
    }
    @Execute(route = "info")
    public void infoClan(Player player, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().infoClan(player, clan);
    }
    @Execute(route = "ustawlidera")
    public void setOwner(Owner owner, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().setOwner(owner, target);
    }

    @Execute(route = "zastepca")
    public void setDeputy(Owner owner, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().setDeputy(owner, target);
    }
    @Execute(route = "usunzastepce")
    public void removeDeputy(Owner owner) {
        plugin.getClansManager().removeDeputy(owner);
    }

    @Execute(route = "zapros")
    public void inviteUser(DeputyOwner deputyOwner, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().inviteUser(deputyOwner, target);
    }
    @Execute(route = "wyrzuc")
    public void kickUser(DeputyOwner deputyOwner, @Arg @Name("gracz") Player target) {
        plugin.getClansManager().kickUser(deputyOwner, target);
    }

    @Execute(route = "sojusz")
    public void alliace(DeputyOwner deputyOwner, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().alliance(deputyOwner, clan);
    }

    @Execute(route = "dolacz")
    public void joinClan(Player player, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().joinClan(player, clan);
    }

    @Execute(route = "reload")
    @Permission("getclans.admin")
    public void reloadConfig(LiteSender sender)
    {
        plugin.reloadPlugin(sender);
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
            MessageUtil.sendMessage(sender, "&7" + uuid + " -> " + userTemp.getPoints());
        }));
    }
    @Execute(route = "admin setitem")
    @Permission("getclans.admin")
    public void setItemCost(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if(itemInMainHand==null || itemInMainHand.getType()== Material.AIR)
        {
            MessageUtil.sendMessage(player, "&cMusisz trzymac przedmiot w łapce!");
            return;
        }
        ItemStack itemClone = itemInMainHand.clone();
        itemClone.setAmount(1);

        plugin.getConfigPlugin().itemCost = itemClone;
        plugin.getConfigPlugin().save();
        MessageUtil.sendMessage(player, "&aPomyślnie ustawiono item!");
    }
    @Execute(route = "admin usun")
    @Permission("getclans.admin")
    public void adminRemove(LiteSender sender, @Arg @Name("gracz") Owner owner) {
        plugin.getClansManager().deleteClan(owner);
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
