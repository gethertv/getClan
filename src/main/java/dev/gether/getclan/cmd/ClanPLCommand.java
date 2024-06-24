package dev.gether.getclan.cmd;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.cmd.context.domain.DeputyOwner;
import dev.gether.getclan.cmd.context.domain.Member;
import dev.gether.getclan.cmd.context.domain.Owner;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.upgrade.*;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "getclan", aliases = "klan")
@Permission("getclan.use")
public class ClanPLCommand {

    private final GetClan plugin;
    private final FileManager fileManager;
    private final ClanManager clanManager;
    private final UpgradeManager upgradeManager;

    private final Pattern pattern = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ0-9]+$");

    public ClanPLCommand(GetClan plugin, FileManager fileManager, ClanManager clanManager, UpgradeManager upgradeManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
        this.upgradeManager = upgradeManager;
    }

    private boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("not-possible-via-console"));
            return false;
        }
        return true;
    }

    @Execute(name = "usun")
    public void deleteClan(@Context Owner owner) {
        plugin.getClanManager().deleteClan(owner);
    }

    @Execute(name = "opusc")
    public void leaveClan(@Context Member member) {
        plugin.getClanManager().leaveClan(member);
    }

    @Execute(name = "stworz")
    public void createClan(@Context CommandSender sender, @Arg("tag") String tag) {
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

    @Execute(name = "ustawlidera")
    public void setOwner(@Context Owner owner, @Arg("gracz") Player target) {
        plugin.getClanManager().setOwner(owner, target);
    }

    @Execute(name = "zastepca")
    public void setDeputy(@Context Owner owner, @Arg("gracz") Player target) {
        plugin.getClanManager().setDeputy(owner, target);
    }

    @Execute(name = "usunzastepce")
    public void removeDeputy(@Context CommandSender sender, @Arg Owner owner) {
        plugin.getClanManager().removeDeputy(owner);
    }

    @Execute(name = "zapros")
    public void inviteUser(@Context DeputyOwner deputyOwner, @Arg("gracz") Player target) {
        plugin.getClanManager().inviteUser(deputyOwner, target);
    }

    @Execute(name = "wyrzuc")
    public void kickUser(@Context DeputyOwner deputyOwner, @Arg("nickname") String username) {
        plugin.getClanManager().kickUser(deputyOwner, username);
    }
    @Execute(name = "sojusz")
    public void alliance(@Context DeputyOwner deputyOwner, @Arg("tag") Clan clan) {
        plugin.getAllianceManager().alliance(deputyOwner, clan);
    }

    @Execute(name = "dolacz")
    public void joinClan(@Context Player player, @Arg("tag") Clan clan) {
        plugin.getClanManager().joinClan(player, clan);
    }

    @Execute(name = "reload")
    @Permission("getclan.admin")
    public void reloadConfig(@Context CommandSender sender) {
        plugin.reloadPlugin(sender);
    }

    @Execute(name = "admin dolacz")
    @Permission("getclan.admin")
    public void adminForceJoinUser(@Context CommandSender sender, @Arg("gracz") User user, @Arg("tag") Clan clan) {
        plugin.getClanManager().forceJoin(sender, user, clan);
    }

    @Execute(name = "admin wyrzuc")
    @Permission("getclan.admin")
    public void adminForceKickUser(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getClanManager().forceKickUser(sender, user);
    }

    @Execute(name = "pvp")
    public void changePvpStatusClan(@Context DeputyOwner deputyOwner) {
        plugin.getClanManager().changePvpStatus(deputyOwner);
    }

    @Execute(name = "ulepszenia")
    public void upgradePanel(@Context Player player) {
        Optional<User> userByPlayer = plugin.getUserManager().findUserByPlayer(player);
        if(userByPlayer.isEmpty()) return;

        clanManager.openMenu(player, userByPlayer.get());
    }

    @Execute(name = "admin ulepszenia off")
    @Permission("getclan.admin")
    public void disableUpgrade(@Context CommandSender sender) {
        fileManager.getUpgradesConfig().setUpgradeEnable(false);
        MessageUtil.sendMessage(sender, "&cWylaczono ulepszanie klanow!");
    }

    @Execute(name = "admin ulepszenia on")
    @Permission("getclan.admin")
    public void enableUpgrade(@Context CommandSender sender) {
        fileManager.getUpgradesConfig().setUpgradeEnable(true);
        MessageUtil.sendMessage(sender, "&aWlaczono ulepszanie klanow!");
    }

    @Execute(name = "admin setitem")
    @Permission("getclan.admin")
    public void adminUpgradeItem(@Context Player player, @Arg("typ-ulepszenia") UpgradeType upgradeType, @Arg("poziom") int level) {
        Optional<UpgradeCost> upgradeCostOpt = upgradeManager.findUpgradeCost(upgradeType, level);
        if(upgradeCostOpt.isEmpty()) {
            MessageUtil.sendMessage(player, "&cNie można znaleźć typu ulepszenia na tym poziomie!");
            return;
        }

        ItemStack itemStack = getItemStack(player);
        if(itemStack == null) return;

        UpgradeCost upgradeCost = upgradeCostOpt.get();
        upgradeCost.setItemStack(itemStack);
        upgradeManager.save();
        MessageUtil.sendMessage(player, "&aPomyślnie ustawiono nowy przedmiot!");
    }

    @Execute(name = "admin ustawlidera")
    @Permission("getclan.admin")
    public void adminSetOwner(@Context CommandSender sender, @Arg("nickname") String username) {
        plugin.getClanManager().forceSetOwner(sender, username);
    }

    @Execute(name = "admin resetuj *")
    @Permission("getclan.admin")
    public void adminReset(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getUserManager().resetUser(user);
        MessageUtil.sendMessage(sender, "&aPomyślnie zresetowano użytkownika!");
    }

    @Execute(name = "admin resetuj punkty")
    @Permission("getclan.admin")
    public void adminResetPoints(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getUserManager().resetPoints(user);
        MessageUtil.sendMessage(sender, "&aPunkty pomyślnie zresetowane!");
    }

    @Execute(name = "admin resetuj zabojstwa")
    @Permission("getclan.admin")
    public void adminResetKill(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getUserManager().resetKill(user);
        MessageUtil.sendMessage(sender, "&aZabójstwa pomyślnie zresetowane!");
    }

    @Execute(name = "admin resetuj smierci")
    @Permission("getclan.admin")
    public void adminResetDeath(@Context CommandSender sender, @Arg("gracz") User user) {
        plugin.getUserManager().resetDeath(user);
        MessageUtil.sendMessage(sender, "&aŚmierci pomyślnie zresetowane!");
    }

    @Execute(name = "admin klan resetuj ulepszenia")
    @Permission("getclan.admin")
    public void adminClanReset(@Context CommandSender sender, @Arg("tag") Clan clan) {
        clan.getUpgrades().values().forEach(LevelData::reset);
        clanManager.updateItem(clan);
        MessageUtil.sendMessage(sender, "&aPomyślnie zresetowano ulepszenia klanu");
    }

    @Execute(name = "admin debug")
    @Permission("getclan.admin")
    public void adminDebug(@Context CommandSender sender, @Arg("gracz") Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        if (user == null || !user.hasClan()) {
            MessageUtil.sendMessage(sender, "&cGracz nie należy do żadnego klanu!");
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
        ItemStack itemStack = getItemStack(player);
        if(itemStack == null)
            return;

        fileManager.getConfig().setItemCost(itemStack);
        fileManager.getConfig().save();
        MessageUtil.sendMessage(player, "&aPomyślnie ustawiono przedmiot!");
    }

    @Execute(name = "admin give")
    @Permission("getclan.admin")
    public void giveLevelItem(@Context CommandSender sender, @Arg("typ-ulepszenia") UpgradeType upgradeType, @Arg("gracz") Player target, @Arg("poziom") int level, @Arg("ilosc") int amount) {
        Optional<UpgradeCost> upgradeCostOpt = upgradeManager.findUpgradeCost(upgradeType, level);
        if(upgradeCostOpt.isEmpty()) {
            MessageUtil.sendMessage(sender, "&cNie można znaleźć typu ulepszenia na tym poziomie!");
            return;
        }

        UpgradeCost upgradeCost = upgradeCostOpt.get();
        ItemStack item = upgradeCost.getItemStack().clone();
        item.setAmount(amount);
        PlayerUtil.giveItem(target, item);
        MessageUtil.sendMessage(sender, "&aPomyślnie wręczono przedmiot!");
    }

    @Execute(name = "admin give odlamek")
    @Permission("getclan.admin")
    public void giveDefaultItem(@Context CommandSender sender, @Arg("gracz") Player target, @Arg("ilosc") int amount) {
        ItemStack item = fileManager.getConfig().getItemCost().clone();
        item.setAmount(amount);
        PlayerUtil.giveItem(target, item);
        MessageUtil.sendMessage(sender, "&aPomyślnie wręczono przedmiot!");
    }

    private ItemStack getItemStack(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if(itemInMainHand.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "&cMusisz trzymać przedmiot w ręce!");
            return null;
        }
        ItemStack itemClone = itemInMainHand.clone();
        itemClone.setAmount(1);
        return itemClone;
    }

    @Execute(name = "admin usun klan")
    @Permission("getclan.admin")
    public void adminRemove(@Context CommandSender sender, @Arg("gracz") Owner owner) {
        plugin.getClanManager().deleteClan(owner);
        MessageUtil.sendMessage(sender, "&aKlan pomyślnie usunięty!");
    }

    @Execute(name = "admin ustaw punkty")
    @Permission("getclan.admin")
    public void adminSetPoint(@Context CommandSender sender, @Arg("gracz") User user, @Arg("points") int points) {
        user.setPoints(points);
        MessageUtil.sendMessage(sender, "&aPunkty pomyślnie ustawione!");
    }

    @Execute(name = "admin ustaw ulepszenie")
    @Permission("getclan.admin")
    public void adminSetUpgrade(@Context CommandSender sender, @Arg("tag") Clan clan, @Arg("typ") UpgradeType upgradeType, @Arg("poziom") int level) {
        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(upgradeType);
        if(upgradeByType.isEmpty()) {
            MessageUtil.sendMessage(sender, "&cTakie ulepszenie nie istnieje!");
            return;
        }
        Upgrade upgrade = upgradeByType.get();
        UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(level);
        if(upgradeCost == null) {
            MessageUtil.sendMessage(sender, "&cTakie poziom tego ulepszenia nie istnieje!");
            return;
        }

        LevelData levelData = clan.getUpgrades().get(upgradeType);
        levelData.reset();
        levelData.setLevel(level);
        clan.setUpdate(true);
        MessageUtil.sendMessage(sender, "&aPomyślnie ustawiono poziom ulepszenia dla tego klanu!");
    }
}

