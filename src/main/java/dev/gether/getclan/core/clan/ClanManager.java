package dev.gether.getclan.core.clan;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.upgrade.LevelData;
import dev.gether.getclan.core.upgrade.Upgrade;
import dev.gether.getclan.core.upgrade.UpgradeCost;
import dev.gether.getclan.core.upgrade.UpgradeType;
import dev.gether.getclan.event.*;
import dev.gether.getclan.core.CostType;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.cmd.context.domain.DeputyOwner;
import dev.gether.getclan.cmd.context.domain.Member;
import dev.gether.getclan.cmd.context.domain.Owner;
import dev.gether.getclan.core.alliance.AllianceService;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.incognito.IncognitoAddon;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ClanManager {
    private final GetClan plugin;
    private final ClanService clanService;
    private final AllianceService allianceService;
    private final HashMap<String, Clan> clansData = new HashMap<>();
    private final FileManager fileManager;
    private final IncognitoAddon incognitoAddon;

    public ClanManager(GetClan plugin, ClanService clanService, AllianceService allianceService, FileManager fileManager) {
        this.plugin = plugin;
        this.clanService = clanService;
        this.allianceService = allianceService;
        this.fileManager = fileManager;
        this.incognitoAddon = new IncognitoAddon();
    }

    public void setOwner(Owner owner, Player target) {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();
        if (isSame(player, target.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("cannot-change-to-yourself"));
            return;
        }
        if (!isYourClan(clan, target.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-not-in-your-clan"));
            return;
        }

        // check event and set new owner
        boolean success = handleSetOwner(clan, target.getUniqueId());
        if (success) {
            MessageUtil.sendMessage(owner.getPlayer(),
                    fileManager.getLangConfig().getMessage("change-leader")
                            .replace("{new-owner}", target.getName())

            );
        }

    }

    private boolean handleSetOwner(Clan clan, UUID newOwnerUUID) {
        ChangeOwnerClanEvent event = new ChangeOwnerClanEvent(clan, clan.getOwnerUUID(), newOwnerUUID);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setOwner(newOwnerUUID);
            return true;
        }
        return false;
    }

    public void changePvpStatus(DeputyOwner deputyOwner) {
        Clan clan = deputyOwner.getClan();
        clan.togglePvp();
        clan.broadcast(fileManager.getLangConfig().getMessage(clan.isPvpEnable() ? "pvp-enabled" : "pvp-disabled"));
    }

    public void forceSetOwner(CommandSender sender, String username) {
        Optional<UUID> uuidOptional = getPlayerUUIDByNickname(username);
        if (uuidOptional.isEmpty()) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("player-not-found"));
            return;
        }
        UUID newOwnerUUID = uuidOptional.get();
        User user = plugin.getUserManager().getUserData().get(newOwnerUUID);
        if (!user.hasClan()) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("admin-player-no-clan"));
            return;
        }
        String tag = user.getTag();
        Clan clan = getClan(tag);
        // check event and set new owner
        boolean success = handleSetOwner(clan, newOwnerUUID);
        if (success)
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("admin-set-leader"));
    }

    public void inviteUser(DeputyOwner deputyOwner, Player target) {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();

        // limit clan
        if (isLimitMember(clan)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-member-limit-reached"));
            return;
        }

        if (hasClan(target)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-already-in-clan"));
            return;
        }
        if (clan.hasInvite(target.getUniqueId())) {
            // cancel invite to clan
            CancelInviteClanEvent event = new CancelInviteClanEvent(clan, target);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                clan.cancelInvite(target.getUniqueId());
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("invite-cancelled")
                        .replace("{player}", target.getName())
                );
            }
            return;
        }

        // invite player
        InviteClanEvent event = new InviteClanEvent(clan, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.invite(target.getUniqueId());
            MessageUtil.sendMessage(player,
                    fileManager.getLangConfig().getMessage("player-invited")
                            .replace("{player}", target.getName())

            );
            MessageUtil.sendMessage(target,
                    fileManager.getLangConfig().getMessage("clan-invitation-received")
                            .replace("{tag}", clan.getTag())

            );
        }
    }

    public void openMenu(Player player, User user) {
        if(!fileManager.getUpgradesConfig().isUpgradeEnable()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("upgrade-clan-disabled"));
            return;
        }
        if(!user.hasClan()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-has-no-clan"));
            return;
        }
        Clan clan = getClan(user.getTag());
        player.openInventory(clan.getInventory());
    }
    private boolean hasClan(Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        return user.hasClan();
    }

    private boolean isLimitMember(Clan clan) {
        if(!fileManager.getUpgradesConfig().isUpgradeEnable())
            return clan.getMembers().size()>=getMaxMember(clan);

        LevelData levelData = clan.getUpgrades().get(UpgradeType.MEMBERS);
        if(levelData == null)
            return true;

        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(UpgradeType.MEMBERS);
        if(upgradeByType.isEmpty())
            return true;

        Upgrade upgrade = upgradeByType.get();
        if(!upgrade.isEnabled()) {
            return clan.getMembers().size()>=getMaxMember(clan);
        }
        UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(levelData.getLevel());
        if(upgradeCost == null)
            return true;

        return (int) upgradeCost.getBoostValue() < clan.getMembers().size();
    }

    // checking permission and count members
    public int getMaxMember(Clan clan) {
        int ownerMax = getUserMaxMember(clan.getOwnerUUID());
        int deputyOwnerMax = getUserMaxMember(clan.getDeputyOwnerUUID());
        return Math.max(ownerMax, deputyOwnerMax);
    }

    private int getUserMaxMember(UUID uuid) {
        if(uuid==null)
            return 0;

        Player player = Bukkit.getPlayer(uuid);
        if(player!=null) {
            Map<String, Integer> permissionLimitMember = fileManager.getConfig().permissionLimitMember;
            for(Map.Entry<String, Integer> permissionData : permissionLimitMember.entrySet())
            {
                String permission = permissionData.getKey();
                int max = permissionData.getValue();
                if(player.hasPermission(permission))
                    return max;
            }
        }
        return 0;
    }


    public void infoClan(Player player, Clan clan) {
        int index = plugin.getRankingManager().findTopClan(clan);

        String infoMessage = String.join("\n", fileManager.getLangConfig().getMessage("info-clan"));
        infoMessage = infoMessage.replace("{tag}", clan.getTag())
                .replace("{owner}", getPlayerName(clan.getOwnerUUID()))
                .replace("{deputy-owner}", getPlayerName(clan.getDeputyOwnerUUID()))
                .replace("{points}", getAveragePoint(player))
                .replace("{members-online}", String.valueOf(countOnlineMember(clan)))
                .replace("{members-size}", String.valueOf(clan.getMembers().size()))
                .replace("{rank}", String.valueOf(index))
                .replace("{members}", getClanMembers(clan));

        MessageUtil.sendMessage(player, infoMessage);
    }


    private String getClanMembers(Clan clan) {
        List<UUID> members = clan.getMembers();
        return members.stream()
                .map(uuid -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    boolean incognito = incognitoAddon.isIncognito(player.getUniqueId());
                    String color = fileManager.getConfig().getColorOfflinePlayer();
                    if(player.isOnline() && !incognito) {
                        color = fileManager.getConfig().getColorOnlinePlayer();
                    }
                    return color + player.getName();
                })
                .collect(Collectors.joining(", "));
    }


    public int countOnlineMember(Clan clan) {
        int online = 0;
        for (UUID uuid : clan.getMembers()) {
            if(incognitoAddon.isIncognito(uuid))
                continue;

            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                online++;
        }
        return online;
    }

    public String getAveragePoint(Player player) {
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());
        if (user == null || !user.hasClan())
            return fileManager.getConfig().getNonePointsClan();

        Clan clan = getClan(user.getTag());
        if (!doesClanFulfillThreshold(clan)) {
            return ColorFixer.addColors(fileManager.getConfig().getPlaceholderNeedMembers());
        }
        return getAveragePoint(clan);
    }

    public String getAveragePoint(Clan clan) {
        List<UUID> members = clan.getMembers();

        int sum = 0;
        int count = 0;

        UserManager userManager = plugin.getUserManager();

        for (UUID uuid : members) {
            User tempUser = userManager.getUserData().get(uuid);
            if (tempUser == null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                plugin.getLogger().info(ConsoleColor.RED + "Bląd - Gracz o nazwie " + player.getName() + "  nalezy do klanu " + clan.getTag() + " ale nie znajduje go jako obiekt User");
                continue;
            }
            sum += tempUser.getPoints();
            count++;
        }
        if(count == 0) {
            throw new RuntimeException("Cannot division through 0");
        }
        double average = (double) sum / count;
        return String.valueOf((int) average);
    }

    private String getPlayerName(UUID uuid) {
        if (uuid == null)
            return fileManager.getConfig().getNoneDeputy();

        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public void joinClan(Player player, Clan clan) {
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if (user.hasClan()) {
            // already has clan
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-already-has-clan"));
            return;
        }
        if (!clan.hasInvite(player.getUniqueId())) {
            // not received invite
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("no-invitation"));
            return;
        }
        if (isLimitMember(clan)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-member-limit-reached"));
            return;
        }

        // join to clan - call event
        joinClanCheckEvent(player, user, clan);
    }

    public void forceJoin(CommandSender sender, User user, Clan clan) {
        Player player = Bukkit.getPlayer(user.getUuid());
        if (user.hasClan()) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("admin-player-has-clan"));
            return;
        }
        // join to clan - call event
        joinClanCheckEvent(player, user, clan);
    }

    public String getIncognitoName(Player player) {
        return incognitoAddon.getIncognitoName(player);
    }
    private void joinClanCheckEvent(Player player, User user, Clan clan) {
        JoinClanEvent event = new JoinClanEvent(clan, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            user.setTag(clan.getTag());
            clan.joinUser(player.getUniqueId());
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-joined-clan"));
            return;
        }
    }

    public void deleteClan(Owner owner) {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();

        // call event and delete clan
        handleDeleteClan(clan, player);
    }

    public void deleteClanByAdmin(CommandSender sender, Clan clan) {
        // call event and delete clan
        boolean isDeleted = handleDeleteClan(clan, null);
        if (isDeleted)
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("admin-clan-deleted"));

    }

    private boolean handleDeleteClan(Clan clan, Player player) {
        // null mean the player deleted clan is admin
        if (player == null) {
            return deleteClan(clan, null);
        }

        DeleteClanEvent event = new DeleteClanEvent(player, clan);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return deleteClan(clan, player);
        }
        return false;
    }

    private boolean deleteClan(Clan clan, Player player) {
        String tag = clan.getTag();
        ClanManager clansManager = plugin.getClanManager();
        for (UUID uuid : clan.getMembers()) {
            User member = plugin.getUserManager().getUserData().get(uuid);
            member.setTag(null);
        }
        for (String allianceTag : clan.getAlliances()) {
            Clan allianceClan = clansManager.getClansData().get(allianceTag);
            allianceClan.getAlliances().remove(tag);
            allianceService.deleteAlliance(tag);
        }
        clanService.deleteClan(tag);
        deleteClan(tag);
        // remove clan to system ranking
        plugin.getRankingManager().removeClan(clan);
        // if player is null that mean clan is removed by admin
        if (player != null) {
            MessageUtil.broadcast(fileManager.getLangConfig().getMessage("clan-deleted")
                    .replace("{tag}", tag)
                    .replace("{player}", player.getName())
            );
        }
        return true;
    }

    public void createClan(Player player, String tag) {
        if (hasClan(player)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-already-has-clan"));
            return;
        }

        // check MIN AND MAX LENGTHS of tag
        int min = fileManager.getConfig().getClansTagLengthMin();
        int max = fileManager.getConfig().getClansTagLengthMax();
        if (tag.length() < min || tag.length() > max) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-name-length-info")
                    .replace("{min-length}", String.valueOf(min))
                    .replace("{max-length}", String.valueOf(max))
            );
            return;
        }
        // check the name is not busy
        if (tagIsBusy(tag)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-name-exists"));
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if (user == null)
            return;


        if (fileManager.getConfig().isEnablePayment()) {
            boolean status = checkPayments(player);
            if (!status)
                return;

        }

        // create clan
        CreateClanEvent event = new CreateClanEvent(player, tag);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Clan clan = new Clan(tag, UUID.randomUUID(),  player.getUniqueId(), fileManager.getConfig().isPvpClan(), fileManager.getUpgradesConfig());
            plugin.getClanManager().updateItem(clan);

            clansData.put(tag, clan);
            user.setTag(clan.getTag());
            plugin.getUserManager().update(user);
            clanService.createClan(clan, player);
            // add clan to system ranking
            plugin.getRankingManager().addClan(clan);
            MessageUtil.broadcast(fileManager.getLangConfig().getMessage("clan-created")
                    .replace("{tag}", tag)
                    .replace("{player}", player.getName())
            );

        }

    }
    public void updateItem(Clan clan) {
        fileManager.getUpgradesConfig().getUpgrades().forEach(upgrade -> {
            if(!upgrade.isEnabled())
                return;

            LevelData levelData = clan.getUpgrades().get(upgrade.getUpgradeType());
            if(levelData==null)
                return;

            UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(levelData.getLevel());
            if(upgradeCost==null)
                return;

            double needAmount = 0;
            UpgradeCost nextLevel = upgrade.getUpgradesCost().get(levelData.getLevel() + 1);
            if(nextLevel != null) {
                needAmount = nextLevel.getCost();
            }

            plugin.getClanManager().updateItem(clan, upgradeCost, levelData, needAmount, upgrade.getSlot());
        });

    }

    private boolean checkPayments(Player player) {
        if (fileManager.getConfig().getCostType() == CostType.VAULT) {
            Economy economy = plugin.getEconomy();
            if (!economy.has(player, fileManager.getConfig().getCostCreate())) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-cost-vault")
                        .replace("{cost}", String.valueOf(fileManager.getConfig().getCostCreate())));
                return false;
            }
            economy.withdrawPlayer(player, fileManager.getConfig().getCostCreate());
            return true;
        } else {
            int amount = ItemUtil.calcItem(player, fileManager.getConfig().getItemCost());
            int needAmount = (int) fileManager.getConfig().getCostCreate();
            if (amount < needAmount) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-cost-item")
                        .replace("{amount}", String.valueOf(needAmount))
                );
                return false;
            }
            ItemUtil.removeItem(player, fileManager.getConfig().getItemCost(), needAmount);
            return true;
        }
    }

    // kick user from clan by admin
    public void forceKickUser(CommandSender sender, User user) {
        if (!user.hasClan()) {
            MessageUtil.sendMessage(sender, fileManager.getLangConfig().getMessage("admin-player-no-clan"));
            return;
        }

        // get user clan
        Clan clan = getClan(user.getTag());
        // kick player from clan
        handleKickUser(null, user, clan);

    }

    public void kickUser(DeputyOwner deputyOwner, String nickname) {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();

        Optional<UUID> optionalUUID = getPlayerUUIDByNickname(nickname);
        if (optionalUUID.isEmpty()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-not-found"));
            return;
        }
        UUID targetUUID = optionalUUID.get();
        if (!isYourClan(clan, targetUUID)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-not-in-your-clan"));
            return;
        }
        if (isSame(player, targetUUID)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("cannot-kick-yourself"));
            return;
        }
        if (isOwner(clan, targetUUID)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("cannot-kick-owner"));
            return;
        }
        // kick player from clan
        User kUser = plugin.getUserManager().getUserData().get(targetUUID);
        handleKickUser(player, kUser, clan);
    }

    private void handleKickUser(Player player, User kickedUser, Clan clan) {
        PlayerKickClanEvent event = new PlayerKickClanEvent(clan, kickedUser.getUuid());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.removeMember(kickedUser.getUuid());
            kickedUser.setTag(null);
            // if its null then mean the kicked user is from the console
            if (player != null)
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-kicked-from-clan"));
        }
    }

    private Optional<UUID> getPlayerUUIDByNickname(String nickname) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(nickname);
        if (offlinePlayer != null) {
            return Optional.of(offlinePlayer.getUniqueId());
        }
        Player player = Bukkit.getPlayer(nickname);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(player.getUniqueId());
    }

    private boolean isSame(Player player, UUID targetUUID) {
        return player.getUniqueId().equals(targetUUID);
    }

    public void leaveClan(Member member) {
        Clan clan = member.getClan();
        Player player = member.getPlayer();
        if (isOwner(clan, player.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("owner-cannot-leave"));
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        // left clan
        LeaveClanEvent event = new LeaveClanEvent(player, clan);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.removeMember(player.getUniqueId());
            user.setTag(null);
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-left-clan"));
        }
    }

    public boolean isYourClan(Clan clan, UUID uuid) {
        return clan.getMembers().contains(uuid);
    }

    private boolean isOwner(Clan clan, UUID playerUUID) {
        return clan.getOwnerUUID().equals(playerUUID);
    }

    private boolean tagIsBusy(String tag) {
        return clansData.get(tag) != null;
    }



    public void removeDeputy(Owner owner) {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();
        if (deputyIsEmpty(clan)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("clan-has-no-deputy"));
            return;
        }
        DeleteDeputyEvent event = new DeleteDeputyEvent(clan, clan.getDeputyOwnerUUID());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setDeputyOwnerUUID(null);
            //clanService.updateClan(clan);
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("deputy-removed"));
        }
    }

    private boolean deputyIsEmpty(Clan clan) {
        return clan.getDeputyOwnerUUID() == null;
    }

    public void setDeputy(Owner owner, Player target) {
        Player player = owner.getPlayer();
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = getClan(user.getTag());
        if (!isYourClan(clan, target.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-not-in-your-clan"));
            return;
        }
        if (clan.isDeputy(target.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("player-is-deputy"));
            return;
        }
        DeputyChangeClanEvent event = new DeputyChangeClanEvent(clan, player, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setDeputyOwnerUUID(target.getUniqueId());
            //clanService.updateClan(clan);
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("deputy-set"));
        }
    }

    public void updateClans() {
        clansData.values().forEach(clan -> {
            if(clan.isUpdate()) {
                clanService.updateClan(clan);
                clan.setUpdate(false);
            }
        });
    }

    public boolean doesClanFulfillThreshold(Clan clan) {
        return clan.getMembers().size() >= fileManager.getConfig().getMembersRequiredForRanking();
    }

    public boolean isLimitAlliance(Clan clan) {
        return clan.getAlliances().size() >= fileManager.getConfig().getLimitAlliance();
    }

    public Clan getClan(String tag) {
        return clansData.get(tag);
    }


    public Clan deleteClan(String tag) {
        return clansData.remove(tag);
    }

    public Map<String, Clan> getClansData() {
        return clansData;
    }


    public void loadClans() {
        Set<Clan> clans = clanService.loadClans();
        clans.forEach(clan -> clansData.put(clan.getTag(), clan));
    }

    public void clickInv(Player player, Clan clan, int slot, @NotNull ClickType clickType) {
        Optional<Upgrade> upgradeTypeBySlot = fileManager.getUpgradesConfig().findUpgradeTypeBySlot(slot);
        if(upgradeTypeBySlot.isEmpty())
            return;

        Upgrade upgrade = upgradeTypeBySlot.get();
        if(!upgrade.isEnabled())
            return;


        LevelData levelData = clan.getUpgrades().get(upgrade.getUpgradeType());
        if(levelData==null)
            return;

        UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(levelData.getLevel() + 1);
        if(upgradeCost == null) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("upgrade-max-level"));
            return;
        }

        double need = upgradeCost.getCost() - levelData.getDepositAmount();
        boolean update = false;
        if(clickType == ClickType.LEFT) {
            update = deposit(player, levelData, upgradeCost, need > 1 ? 1 : need, upgradeCost.getCostType());
        } else if(clickType == ClickType.SHIFT_LEFT) {
            update = deposit(player, levelData, upgradeCost, need, upgradeCost.getCostType());
        }

        if(update)
            clan.setUpdate(true);

        if(nextLevel(upgradeCost, levelData)) {
            levelData.nextLevel();
            updateItem(clan, upgradeCost, levelData, upgradeCost.getCost(), upgrade.getSlot());
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("upgrade-successful-upgrade"));

        } else {
            UpgradeCost actuallyItem = upgrade.getUpgradesCost().get(levelData.getLevel());
            updateItem(clan, actuallyItem, levelData, upgradeCost.getCost(), upgrade.getSlot());
        }

    }
    private boolean nextLevel(UpgradeCost upgradeCost, LevelData levelData) {
        return upgradeCost.getCost() <= levelData.getDepositAmount();
    }

    private void updateItem(Clan clan, UpgradeCost upgradeCost, LevelData levelData, double needAmount, int slot) {
        Inventory inventory = clan.getInventory();
        ItemStack itemStack = upgradeCost.getItem().getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(itemMeta.hasLore())
            lore.addAll(itemMeta.getLore());

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i)
                    .replace("{amount}", getFormattedNumber(levelData.getDepositAmount()))
                    .replace("{need-amount}", getFormattedNumber(needAmount))
            );
        }
        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(slot, itemStack);
    }

    private String getFormattedNumber(double number) {
        String formattedNumber;
        if (number % 1 == 0) {
            formattedNumber = String.format("%.0f", number);
        } else {
            formattedNumber = String.format("%.2f", number);
        }
        return formattedNumber;
    }

    private boolean deposit(Player player, LevelData levelData, UpgradeCost upgradeCost, double amount, CostType costType) {
        if (costType == CostType.VAULT) {
            Economy economy = plugin.getEconomy();
            if (!economy.has(player, amount)) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("upgrade-cost-vault")
                        .replace("{cost}", String.valueOf(amount)));
                return false;
            }
            economy.withdrawPlayer(player, amount);
            levelData.deposit(amount);
            return true;
        } else {
            int calcAmount = ItemUtil.calcItem(player, upgradeCost.getItemStack());
            int needAmount = (int) amount;
            if (calcAmount < needAmount) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("upgrade-cost-item")
                        .replace("{amount}", String.valueOf(needAmount))
                        .replace("{item}", ItemUtil.getItemName(upgradeCost.getItemStack()))
                );
                return false;
            }
            ItemUtil.removeItem(player, upgradeCost.getItemStack(), needAmount);
            levelData.deposit(amount);
            return true;
        }
    }


}
