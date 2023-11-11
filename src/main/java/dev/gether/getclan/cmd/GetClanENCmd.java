package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
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

@Route(name = "getclan", aliases = "clan")
@Permission("getclan.use")
public class GetClanENCmd {

    private final GetClan plugin;
    private Pattern pattern = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ0-9]+$");

    public GetClanENCmd(GetClan plugin) {
        this.plugin = plugin;
    }
    @Execute(route = "delete")
    public void deleteClan(Owner owner) {
        plugin.getClansManager().deleteClan(owner);
    }

    @Execute(route = "leave")
    public void leaveClan(Member member) {
        plugin.getClansManager().leaveClan(member);
    }

    @Execute(route = "create")
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
    @Execute(route = "setowner")
    public void setOwner(Owner owner, @Arg @Name("player") Player target) {
        plugin.getClansManager().setOwner(owner, target);
    }

    @Execute(route = "deputy")
    public void setDeputy(Owner owner, @Arg @Name("player") Player target) {
        plugin.getClansManager().setDeputy(owner, target);
    }
    @Execute(route = "removedeputy")
    public void removeDeputy(Owner owner) {
        plugin.getClansManager().removeDeputy(owner);
    }

    @Execute(route = "invite")
    public void inviteUser(DeputyOwner deputyOwner, @Arg @Name("player") Player target) {
        plugin.getClansManager().inviteUser(deputyOwner, target);
    }
    @Execute(route = "kick")
    public void kickUser(DeputyOwner deputyOwner, @Arg @Name("nickname") String username) {
        plugin.getClansManager().kickUser(deputyOwner, username);
    }

    @Execute(route = "alliance")
    public void alliace(DeputyOwner deputyOwner, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().alliance(deputyOwner, clan);
    }

    @Execute(route = "join")
    public void joinClan(Player player, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().joinClan(player, clan);
    }

    @Execute(route = "reload")
    @Permission("getclan.admin")
    public void reloadConfig(LiteSender sender)
    {
        plugin.reloadPlugin(sender);
    }

    @Execute(route = "admin join")
    @Permission("getclan.admin")
    public void adminForceJoinUser(LiteSender sender, @Arg @Name("player") User user, @Arg @Name("tag") Clan clan) {
        plugin.getClansManager().forceJoin(sender, user, clan);
    }

    @Execute(route = "admin kick")
    @Permission("getclan.admin")
    public void adminForceKickUser(LiteSender sender, @Arg @Name("gracz") User user) {
        plugin.getClansManager().forceKickUser(sender, user);
    }

    @Execute(route = "pvp")
    public void changePvpStatusClan(DeputyOwner deputyOwner) {
        plugin.getClansManager().changePvpStatus(deputyOwner);
    }


    @Execute(route = "admin setleader")
    @Permission("getclan.admin")
    public void adminSetOwner(LiteSender sender, @Arg @Name("nickname") String username) {
        plugin.getClansManager().forceSetOwner(sender, username);
    }


    @Execute(route = "admin reset all")
    @Permission("getclan.admin")
    public void adminReset(LiteSender sender, @Arg @Name("player") User user) {
        plugin.getUserManager().resetUser(user);
        MessageUtil.sendMessage(sender, "&aSuccessfully reset!");
    }

    @Execute(route = "admin reset points")
    @Permission("getclan.admin")
    public void adminResetPoints(LiteSender sender, @Arg @Name("player") User user) {
        plugin.getUserManager().resetPoints(user);
        MessageUtil.sendMessage(sender, "&aPoints reset successfully!");
    }

    @Execute(route = "admin reset kill")
    @Permission("getclan.admin")
    public void adminResetKill(LiteSender sender, @Arg @Name("player") User user) {
        plugin.getUserManager().resetKill(user);
        MessageUtil.sendMessage(sender, "&aKills reset successfully!");
    }
    @Execute(route = "admin reset death")
    @Permission("getclan.admin")
    public void adminResetDeath(LiteSender sender, @Arg @Name("player") User user) {
        plugin.getUserManager().resetDeath(user);
        MessageUtil.sendMessage(sender, "&aDeaths reset successfully!");
    }

    @Execute(route = "admin debug")
    @Permission("getclan.admin")
    public void adminDebug(LiteSender sender, @Arg @Name("player") Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        if (user == null || user.getClan() == null) {
            MessageUtil.sendMessage(sender, "&cPlayer doesn't have a clan!");
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
            MessageUtil.sendMessage(player, "&cYou need to hold the item in your hand!");
            return;
        }
        ItemStack itemClone = itemInMainHand.clone();
        itemClone.setAmount(1);

        plugin.getConfigPlugin().itemCost = itemClone;
        plugin.getConfigPlugin().save();
        MessageUtil.sendMessage(player, "&aItem set successfully!");
    }
    @Execute(route = "admin delete clan")
    @Permission("getclan.admin")
    public void adminRemove(LiteSender sender, @Arg @Name("player") Owner owner) {
        plugin.getClansManager().deleteClan(owner);
        MessageUtil.sendMessage(sender, "&aClan successfully removed!");
    }

    @Execute(route = "admin setpoints")
    @Permission("getclan.admin")
    public void adminSetPoint(LiteSender sender, @Arg @Name("player") User user, @Arg @Name("points") int points) {
        user.setPoints(points);
        sender.sendMessage(ColorFixer.addColors("&aNew points set successfully!"));
    }
}
