package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.gether.getclan.model.role.Member;
import dev.gether.getclan.model.role.Owner;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Route(name = "getclan", aliases = "klan")
@Permission("getclan.use")
public class GetClanPLCmd {

    private final GetClan plugin;
    private Pattern pattern = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ0-9]+$");

    public GetClanPLCmd(GetClan plugin) {
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
        Matcher matcher = pattern.matcher(tag);
        if(!matcher.matches())
        {
            MessageUtil.sendMessage(player, plugin.lang.langInvalidCharacter);
            return;
        }
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
    @Permission("getclan.admin")
    public void reloadConfig(LiteSender sender)
    {
        plugin.reloadPlugin(sender);
    }
    @Execute(route = "admin reset all")
    @Permission("getclan.admin")
    public void adminReset(LiteSender sender, @Arg @Name("gracz") User user) {
        plugin.getUserManager().resetUser(user);
        MessageUtil.sendMessage(sender, "&aPomyslnie zresetowano!");
    }

    @Execute(route = "admin reset punkty")
    @Permission("getclan.admin")
    public void adminResetPoints(LiteSender sender, @Arg @Name("gracz") User user) {
        plugin.getUserManager().resetPoints(user);
        MessageUtil.sendMessage(sender, "&aPomyslnie zresetowano punkty!");
    }

    @Execute(route = "admin reset zabojstwa")
    @Permission("getclan.admin")
    public void adminResetKill(LiteSender sender, @Arg @Name("gracz") User user) {
        plugin.getUserManager().resetKill(user);
        MessageUtil.sendMessage(sender, "&aPomyslnie zresetowano zabójstwa!");
    }
    @Execute(route = "admin reset smierci")
    @Permission("getclan.admin")
    public void adminResetDeath(LiteSender sender, @Arg @Name("gracz") User user) {
        plugin.getUserManager().resetDeath(user);
        MessageUtil.sendMessage(sender, "&aPomyslnie zresetowano śmierci!");
    }

    @Execute(route = "admin debug")
    @Permission("getclan.admin")
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
    @Permission("getclan.admin")
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
    @Execute(route = "admin usun klan")
    @Permission("getclan.admin")
    public void adminRemove(LiteSender sender, @Arg @Name("gracz") Owner owner) {
        plugin.getClansManager().deleteClan(owner);
        MessageUtil.sendMessage(sender, "&aPomyslnie usunieto klan");
    }

    @Execute(route = "admin ustawpunkty")
    @Permission("getclan.admin")
    public void adminSetPoint(LiteSender sender, @Arg @Name("gracz") User user, @Arg @Name("punkty") int points) {
        user.setPoints(points);
        sender.sendMessage(ColorFixer.addColors("&aPomyslnie ustawiono nowe punkty!"));
    }
}
