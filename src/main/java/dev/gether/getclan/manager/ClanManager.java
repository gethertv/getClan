package dev.gether.getclan.manager;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.event.*;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.CostType;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.gether.getclan.model.role.Member;
import dev.gether.getclan.model.role.Owner;
import dev.gether.getclan.service.ClanService;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.ItemUtil;
import dev.gether.getclan.utils.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ClanManager {
    private final GetClan plugin;
    private Config config;

    private ClanService clanService;

    private HashMap<String, Clan> clansData = new HashMap<>();

    public ClanManager(GetClan plugin, ClanService clanService)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
        this.clanService = clanService;
    }

    public void setOwner(Owner owner, Player target) {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();
        if(!isSame(player, target))
        {
            MessageUtil.sendMessage(player, config.langCannotChangeToYourSelf);
            return;
        }
        if(!isYourClan(clan, target))
        {
            MessageUtil.sendMessage(player, config.langPlayerNotYourClan);
            return;
        }
        ChangeOwnerClanEvent event = new ChangeOwnerClanEvent(clan, player, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setOwner(target.getUniqueId());
            MessageUtil.sendMessage(player,
                    config.langChangeOwner
                            .replace("{new-owner}", target.getName())

            );
        }

    }

    public void inviteUser(DeputyOwner deputyOwner, Player target)
    {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();

        // limit clan
        if(isLimitMember(clan))
        {
            MessageUtil.sendMessage(player, config.langLimitMembers);
            return;
        }

        if(hasClan(target))
        {
            MessageUtil.sendMessage(player, config.langInvitedPlayerHasClan);
            return;
        }
        if(clan.hasInvite(target.getUniqueId()))
        {
            // cancel invite to clan
            CancelInviteClanEvent event = new CancelInviteClanEvent(clan, target);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                clan.cancelInvite(target.getUniqueId());
                MessageUtil.sendMessage(player, config.langCancelInvite
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
                    config.langInvitedPlayer
                            .replace("{player}", target.getName())

            );
            MessageUtil.sendMessage(target,
                    config.langGetInvitation
                            .replace("{tag}", clan.getTag())

            );
            return;
        }
    }

    private boolean hasClan(Player target) {
        User user = plugin.getUserManager().getUserData().get(target.getUniqueId());
        return user.hasClan();
    }

    private boolean isDeputyOwner(Clan clan, Player player) {
        return clan.getDeputyOwnerUUID().equals(player.getUniqueId());
    }

    private boolean isLimitMember(Clan clan) {
        return clan.getMembers().size()>=getMaxMember(clan);
    }


    // checking permission and count members
    public int getMaxMember(Clan clan)
    {
        int ownerMax = getUserMaxMember(clan.getOwnerUUID());
        int deputyOwnerMax = getUserMaxMember(clan.getDeputyOwnerUUID());
        return Math.max(ownerMax, deputyOwnerMax);
    }

    private int getUserMaxMember(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player!=null)
        {
            Map<String, Integer> permissionLimitMember = config.permissionLimitMember;
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

        String infoMessage = MessageUtil.joinListToString(config.langInfoClan);
        infoMessage = infoMessage.replace("{tag}", clan.getTag())
                        .replace("{owner}", getPlayerName(clan.getOwnerUUID()))
                        .replace("{deputy-owner}", getPlayerName(clan.getDeputyOwnerUUID()))
                        .replace("{points}", getAveragePoint(clan))
                        .replace("{members-online}", String.valueOf(countOnlineMember(clan)))
                        .replace("{members-size}", String.valueOf(clan.getMembers().size()))
                        .replace("{members}", getClanMembers(clan));

        MessageUtil.sendMessage(player, infoMessage);
    }

    private String getClanMembers(Clan clan) {
        List<UUID> members = clan.getMembers();
        List<String> membersText = new ArrayList<>();

        for(UUID uuid : members)
        {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if(player.isOnline())
                membersText.add(config.colorOnlinePlayer+player.getName());
            else
                membersText.add(config.colorOfflinePlayer+player.getName());

        }
        return String.join(", ", membersText);
    }

    private int countOnlineMember(Clan clan) {
        int online = 0;
        for (UUID uuid : clan.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player!=null)
                online++;
        }
        return online;
    }

    public String getAveragePoint(Player player)
    {
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());
        if(user==null || !user.hasClan())
            return config.noneTag;

        return getAveragePoint(user.getClan());
    }

    public String getAveragePoint(Clan clan)
    {
        List<UUID> members = clan.getMembers();
        int sum = 0;
        int count = 0;

        UserManager userManager = plugin.getUserManager();

        for (UUID uuid : members) {
            User tempUser = userManager.getUserData().get(uuid);
            sum += tempUser.getPoints();
            count++;
        }
        double average = (double) sum / count;
        return String.valueOf((int) average);
    }

    private String getPlayerName(UUID uuid)
    {
        if(uuid==null)
            return config.noneDeputy;

        return Bukkit.getOfflinePlayer(uuid).getName();
    }
    public void joinClan(Player player, Clan clan)
    {
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(user.hasClan())
        {
            // already has clan
            MessageUtil.sendMessage(player, config.langHasClan);
            return;
        }
        if(!clan.hasInvite(player.getUniqueId()))
        {
            // not received invite
            MessageUtil.sendMessage(player, config.langNoInvited);
            return;
        }
        if(isLimitMember(clan))
        {
            MessageUtil.sendMessage(player, config.langLimitMembers);
            return;
        }

        // join to clan
        JoinClanEvent event = new JoinClanEvent(clan, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            user.setClan(clan);
            clan.joinUser(player.getUniqueId());
            MessageUtil.sendMessage(player, config.langSuccessfullyJoined);
            return;
        }
    }
    public void deleteClan(Owner owner)
    {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();

        // delete clan
        DeleteClanEvent event = new DeleteClanEvent(player, clan);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {;
            String tag = clan.getTag();
            ClanManager clansManager = plugin.getClansManager();
            for(UUID uuid : clan.getMembers())
            {
                User member = plugin.getUserManager().getUserData().get(uuid);
                member.setClan(null);
            }
            for(String allianceTag : clan.getAlliances())
            {
                Clan allianceClan = clansManager.getClansData().get(allianceTag.toUpperCase());
                allianceClan.getAlliances().remove(tag.toUpperCase());
                clanService.deleteAlliance(tag);
            }
            clanService.deleteClan(tag);
            deleteClan(tag);
            MessageUtil.broadcast(config.langBroadcastDeleteClan
                    .replace("{tag}", tag)
                    .replace("{player}", player.getName())
            );

        }
    }


    public void createClan(Player player, String tag)
    {
        if(hasClan(player))
        {
            MessageUtil.sendMessage(player, config.langHasClan);
            return;
        }

        // check MIN AND MAX LENGTHS of tag
        if(tag.length()< config.clansTagLengthMin || tag.length()> config.clansTagLengthMax)
        {
            MessageUtil.sendMessage(player, config.langMinMaxTag
                    .replace("{min-length}", String.valueOf(config.clansTagLengthMin))
                    .replace("{max-length}", String.valueOf(config.clansTagLengthMax))
            );
            return;
        }
        // check the name is not busy
        if(tagIsBusy(tag.toUpperCase()))
        {
            MessageUtil.sendMessage(player, config.langTagIsBusy);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(user==null)
            return;


        if(config.enablePayment)
        {
            boolean status = checkPayments(player);
            if(!status)
                return;
        }



        // create clan
        CreateClanEvent event = new CreateClanEvent(player, tag);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Clan clan = new Clan(tag, player.getUniqueId());
            clansData.put(tag.toUpperCase(), clan);
            user.setClan(clan);
            clanService.createClan(clan, player);
            MessageUtil.broadcast(config.langBroadcastCreateClan
                            .replace("{tag}", tag)
                            .replace("{player}", player.getName())
            );

        }
    }

    private boolean checkPayments(Player player) {
        if(config.costType == CostType.VAULT) {
            Economy economy = plugin.getEconomy();
            if(!economy.has(player, config.costCreate))
            {
                MessageUtil.sendMessage(player, config.noMoney.replace("{cost}", String.valueOf(config.costCreate)));
                return false;
            }
            economy.withdrawPlayer(player, config.costCreate);
            return true;
        } else {
            int amount = ItemUtil.calcItemAmount(player, config.itemCost);
            int needAmount = (int) config.costCreate;
            if(amount<needAmount)
            {
                MessageUtil.sendMessage(player, config.noItem
                        .replace("{amount}", String.valueOf(needAmount))
                );
                return false;
            }
            ItemUtil.removeItems(player, config.itemCost, needAmount);
            return true;
        }
    }

    public void kickUser(DeputyOwner deputyOwner, Player target) {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();
        if(!isYourClan(clan, target))
        {
            MessageUtil.sendMessage(player, config.langPlayerNotYourClan);
            return;
        }
        if(isSame(player, target))
        {
            MessageUtil.sendMessage(player, config.langCannotKickYourSelf);
            return;
        }
        if(isOwner(clan, target))
        {
            MessageUtil.sendMessage(player, config.langCannotKickOwner);
            return;
        }
        // kick player from clan
        User kUser = plugin.getUserManager().getUserData().get(target.getUniqueId());
        PlayerKickClanEvent event = new PlayerKickClanEvent(clan, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.removeMember(target.getUniqueId());
            kUser.setClan(null);
            MessageUtil.sendMessage(player, config.langSuccessfullyKicked);
            return;
        }
    }

    private boolean isSame(Player player, Player target) {
        return player.getUniqueId().equals(target.getUniqueId());
    }

    public void leaveClan(Member member) {
        Clan clan = member.getClan();
        Player player = member.getPlayer();
        if(isOwner(clan, player))
        {
            MessageUtil.sendMessage(player, config.langOwnerCannotLeave);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        // left clan
        LeaveClanEvent event = new LeaveClanEvent(player, clan);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.removeMember(player.getUniqueId());
            user.setClan(null);
            MessageUtil.sendMessage(player, config.langSuccessfullyLeaved);
        }
    }
    private boolean isYourClan(Clan clan, Player player) {
        return clan.getMembers().contains(player.getUniqueId());
    }

    private boolean isOwner(Clan clan, Player player) {
        return clan.getOwnerUUID().equals(player.getUniqueId());
    }
    private boolean tagIsBusy(String tag) {
        if(clansData.get(tag)!=null)
            return true;

        return false;
    }

    public void alliance(DeputyOwner deputyOwner, Clan allianceClan) {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();

        if(isYourClan(allianceClan, player))
        {
            MessageUtil.sendMessage(player, config.langCannotAllianceYourClan);
            return;
        }
        if(clan.isAlliance(allianceClan.getTag()))
        {
            DisbandAllianceEvent event = new DisbandAllianceEvent(clan, allianceClan);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // remove alliance from both clan
                clan.removeAlliance(allianceClan.getTag());
                allianceClan.removeAlliance(clan.getTag());
                // add to database
                clanService.deleteAlliance(clan.getTag());

                // message
                MessageUtil.broadcast(config.langBroadcastDisbandAlliance
                        .replace("{first-clan}", clan.getTag())
                        .replace("{second-clan}", allianceClan.getTag())
                );
            }
            return;
        }
        // check limit
        if(isLimitAlliance(clan))
        {
            MessageUtil.sendMessage(player, config.langLimitAlliance);
            return;
        }

        // check if you have already been invited to the alliance.
        if(allianceClan.isSuggestAlliance(clan.getTag()))
        {
            CreateAllianceEvent event = new CreateAllianceEvent(clan, allianceClan);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // add to both clan alliance
                allianceClan.removeSuggestAlliance(clan.getTag());
                clan.addAlliance(allianceClan.getTag());
                allianceClan.addAlliance(clan.getTag());
                // add to database
                clanService.createAlliance(clan.getTag(), allianceClan.getTag());

                // message
                MessageUtil.broadcast(config.langBroadcastCreateAlliance
                        .replace("{first-clan}", clan.getTag())
                        .replace("{second-clan}", allianceClan.getTag())
                );
            }
            return;
        }
        if(!clan.isSuggestAlliance(allianceClan.getTag()))
        {
            clan.inviteAlliance(allianceClan.getTag());
            MessageUtil.sendMessage(player, config.langSuggestAlliance);
            MessageUtil.sendMessage(allianceClan, config.langGetSuggestAlliance
                    .replace("{tag}", clan.getTag())
            );
            return;
        } else {
            clan.removeInviteAlliance(allianceClan.getTag());
            MessageUtil.sendMessage(player, config.langCancelSuggestAlliance
                    .replace("{tag}", allianceClan.getTag())
            );
        }


    }

    public void removeDeputy(Owner owner) {
        Clan clan = owner.getClan();
        Player player = owner.getPlayer();
        if(!deputyIsEmpty(clan))
        {
            MessageUtil.sendMessage(player, config.langPlayerNotYourClan);
            return;
        }
        DeleteDeputyEvent event = new DeleteDeputyEvent(clan, clan.getDeputyOwnerUUID());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setDeputyOwnerUUID(null);
            clanService.updateClan(clan);
            MessageUtil.sendMessage(player, config.langDeputyDelete);
        }
    }

    private boolean deputyIsEmpty(Clan clan) {
         return clan.getDeputyOwnerUUID()==null;
    }

    public void setDeputy(Owner owner, Player target) {
        Player player = owner.getPlayer();
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();
        if(!isYourClan(clan, target))
        {
            MessageUtil.sendMessage(player, config.langPlayerNotYourClan);
            return;
        }
        if(clan.isDeputy(target.getUniqueId()))
        {
            MessageUtil.sendMessage(player, config.langIsDeputy);
            return;
        }
        DeputyChangeClanEvent event = new DeputyChangeClanEvent(user.getClan(), player, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.setDeputyOwnerUUID(target.getUniqueId());
            clanService.updateClan(clan);
            MessageUtil.sendMessage(player, config.langSetDeputyOwner);
        }
    }
    private boolean isLimitAlliance(Clan clan) {
        return clan.getAlliances().size() >= config.limitAlliance;
    }

    public Clan getClan(String tag) {
        return clansData.get(tag.toUpperCase());
    }



    public Clan deleteClan(String tag) {
        return clansData.remove(tag.toUpperCase());
    }

    public HashMap<String, Clan> getClansData() {
        return clansData;
    }

}
