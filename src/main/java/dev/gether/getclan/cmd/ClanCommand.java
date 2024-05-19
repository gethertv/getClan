package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.gether.getclan.model.role.Member;
import dev.gether.getclan.model.role.Owner;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "name", aliases = "aliases")
@Permission("default-permission")
public class ClanCommand {

    private final GetClan plugin;
    private final FileManager fileManager;
    private final ClanManager clanManager;
    private final Pattern pattern = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ0-9]+$");

    public ClanCommand(GetClan plugin, FileManager fileManager, ClanManager clanManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    private boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("not-possible-via-console"));
            return false;
        }
        return true;
    }

    @Execute(name = "delete")
    public void deleteClan(@Context Owner owner) {
        plugin.getClanManager().deleteClan(owner);
    }

    @Execute(name = "leave")
    public void leaveClan(@Context Member member) {
        plugin.getClanManager().leaveClan(member);
    }

    @Execute(name = "create")
    public void createClan(@Context CommandSender sender, @Arg String tag) {
        if(!isPlayer(sender)) return;
        Player player = (Player) sender;

        Matcher matcher = pattern.matcher(tag);
        if(!matcher.matches()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("invalid-tag-characters"));
            return;
        }
        plugin.getClanManager().createClan(player, tag);
    }
    @Execute(name = "info")
    public void infoClan(@Context CommandSender sender, @Arg("tag") Clan clan) {
        if(!isPlayer(sender)) return;
        Player player = (Player) sender;

        plugin.getClanManager().infoClan(player, clan);
    }
    @Execute(name = "setowner")
    public void setOwner(@Context CommandSender sender, @Arg Owner owner, @Arg("player") Player target) {
        plugin.getClanManager().setOwner(owner, target);
    }

    @Execute(name = "deputy")
    public void setDeputy(@Context CommandSender sender, @Arg Owner owner, @Arg("player") Player target) {
        plugin.getClanManager().setDeputy(owner, target);
    }
    @Execute(name = "removedeputy")
    public void removeDeputy(@Context CommandSender sender, @Arg Owner owner) {
        plugin.getClanManager().removeDeputy(owner);
    }

    @Execute(name = "invite")
    public void inviteUser(@Context CommandSender sender, @Arg DeputyOwner deputyOwner, @Arg("player") Player target) {
        plugin.getClanManager().inviteUser(deputyOwner, target);
    }
    @Execute(name = "kick")
    public void kickUser(@Context CommandSender sender, @Arg DeputyOwner deputyOwner, @Arg("nickname") String username) {
        plugin.getClanManager().kickUser(deputyOwner, username);
    }

    @Execute(name = "alliance")
    public void alliace(@Context CommandSender sender, @Arg DeputyOwner deputyOwner, @Arg("tag") Clan clan) {
        plugin.getClanManager().alliance(deputyOwner, clan);
    }

    @Execute(name = "join")
    public void joinClan(@Context Player player, @Arg("tag") Clan clan) {
        plugin.getClanManager().joinClan(player, clan);
    }

    @Execute(name = "reload")
    @Permission("getclan.admin")
    public void reloadConfig(@Context CommandSender sender)
    {
        plugin.reloadPlugin(sender);
    }

    @Execute(name = "admin join")
    @Permission("getclan.admin")
    public void adminForceJoinUser(@Context CommandSender sender, @Arg("player") User user, @Arg("tag") Clan clan) {
        plugin.getClanManager().forceJoin(sender, user, clan);
    }

    @Execute(name = "admin kick")
    @Permission("getclan.admin")
    public void adminForceKickUser(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getClanManager().forceKickUser(sender, user);
    }

    @Execute(name = "pvp")
    public void changePvpStatusClan(@Context CommandSender sender, @Arg DeputyOwner deputyOwner) {
        plugin.getClanManager().changePvpStatus(deputyOwner);
    }


    @Execute(name = "admin setleader")
    @Permission("getclan.admin")
    public void adminSetOwner(@Context CommandSender sender, @Arg("nickname") String username) {
        plugin.getClanManager().forceSetOwner(sender, username);
    }


    @Execute(name = "admin reset all")
    @Permission("getclan.admin")
    public void adminReset(@Context CommandSender sender, @Arg("player") User user) {
        plugin.getUserManager().resetUser(user);
        MessageUtil.sendMessage(sender, "&aSuccessfully reset!");
    }

    @Execute(name = "admin reset points")
    @Permission("getclan.admin")
    public void adminResetPoints(@Context CommandSender sender, @Arg("player") User user) {
        plugin.getUserManager().resetPoints(user);
        MessageUtil.sendMessage(sender, "&aPoints reset successfully!");
    }

    @Execute(name = "admin reset kill")
    @Permission("getclan.admin")
    public void adminResetKill(@Context CommandSender sender, @Arg("player") User user) {
        plugin.getUserManager().resetKill(user);
        MessageUtil.sendMessage(sender, "&aKills reset successfully!");
    }
    @Execute(name = "admin reset death")
    @Permission("getclan.admin")
    public void adminResetDeath(@Context CommandSender sender, @Arg("player") User user) {
        plugin.getUserManager().resetDeath(user);
        MessageUtil.sendMessage(sender, "&aDeaths reset successfully!");
    }

    @Execute(name = "admin debug")
    @Permission("getclan.admin")
    public void adminDebug(@Context CommandSender sender, @Arg("player") Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        if (user == null || !user.hasClan()) {
            MessageUtil.sendMessage(sender, "&cPlayer doesn't have a clan!");
            return;
        }
        UserManager userManager = plugin.getUserManager();
        Clan clan = clanManager.getClan(user.getTag());
        clan.getMembers().forEach((memberUUID -> {
            User userTemp = userManager.getUserData().get(memberUUID);
            MessageUtil.sendMessage(sender, "&7" + memberUUID + " -> " + userTemp.getPoints());
        }));
    }
    @Execute(name = "admin setitem")
    @Permission("getclan.admin")
    public void setItemCost(@Context Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if(itemInMainHand.getType()== Material.AIR) {
            MessageUtil.sendMessage(player, "&cYou need to hold the item in your hand!");
            return;
        }
        ItemStack itemClone = itemInMainHand.clone();
        itemClone.setAmount(1);

        fileManager.getConfig().setItemCost(itemClone);
        fileManager.getConfig().save();
        MessageUtil.sendMessage(player, "&aItem set successfully!");
    }
    @Execute(name = "admin delete clan")
    @Permission("getclan.admin")
    public void adminRemove(@Context CommandSender sender, @Arg("player") Owner owner) {
        plugin.getClanManager().deleteClan(owner);
        MessageUtil.sendMessage(sender, "&aClan successfully removed!");
    }

    @Execute(name = "admin setpoints")
    @Permission("getclan.admin")
    public void adminSetPoint(@Context CommandSender sender, @Arg("player") User user, @Arg("points") int points) {
        user.setPoints(points);
        MessageUtil.sendMessage(sender, "New points set successfully!");
    }
}
