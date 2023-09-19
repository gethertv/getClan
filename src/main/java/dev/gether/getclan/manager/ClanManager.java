package dev.gether.getclan.manager;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.event.*;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.service.ClanService;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClansManager {
    private final GetClan plugin;
    private Config config;

    private ClanService clanService;

    private HashMap<String, Clan> clansData = new HashMap<>();

    public ClansManager(GetClan plugin, ClanService clanService)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
        this.clanService = clanService;
    }

    public void inviteUser(Player player, Player target)
    {
        if(!hasClan(player))
        {
            // no clan
            MessageUtil.sendMessage(player, config.langNoClan);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();

        if(!isOnwer(clan, player))
        {
            // not owner of the clan
            MessageUtil.sendMessage(player, config.langNotOwnerClan);
            return;
        }

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
            MessageUtil.sendMessage(player, config.langIsInvited);
            return;
        }

        // invite player
        InviteClanEvent event = new InviteClanEvent(user.getClan(), target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.invite(target.getUniqueId());
            MessageUtil.sendMessage(player,
                    config.langInvitedPlayer
                            .replace("{username}", target.getName())

            );
            MessageUtil.sendMessage(target,
                    config.langGetInvitation
                            .replace("{username}", player.getName())
                            .replace("{tag}", user.getClan().getTag())

            );
            return;
        }
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
            HashMap<String, Integer> permissionLimitMember = config.permissionLimitMember;
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
            MessageUtil.sendMessage(clan, config.langInfoJoinedPlayer.replace("{username}", player.getName()));
            user.setClan(clan);
            clan.joinUser(player.getUniqueId());
            MessageUtil.sendMessage(player, config.langSuccessfullyJoined);
            return;
        }
    }
    public void removeClan(Player player)
    {
        if(!hasClan(player))
        {
            // no clan
            MessageUtil.sendMessage(player, config.langNoClan);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();

        if(!isOnwer(clan, player))
        {
            MessageUtil.sendMessage(player, config.langNotOwnerClan);
            return;
        }

        // delete clan
        DeleteClanEvent event = new DeleteClanEvent(player, user.getClan());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {;
            MessageUtil.sendMessage(clan, config.langInfoDestroyedClan);
            String tag = clan.getTag();
            for(UUID uuid : clan.getMembers())
            {
                User member = plugin.getUserManager().getUserData().get(uuid);
                member.setClan(null);
            }
            clanService.deleteClan(clan.getTag());
            deleteClan(clan.getTag());
            MessageUtil.sendMessage(player, config.langSuccessfullyDeleted);
            MessageUtil.broadcast(config.langBroadcastDeleteClan.replace("{tag}", tag));

        }
    }


    public void createClan(Player player, String tag)
    {
        if(hasClan(player))
        {
            MessageUtil.sendMessage(player, config.langAlreadyHasClan);
            return;
        }

        // check MIN LENGTHS of tag
        if(tag.length()< config.clansTagLengthMin)
        {
            MessageUtil.sendMessage(player, config.langMinTag.replace("{length}", String.valueOf(config.clansTagLengthMin)));
            return;
        }
        // check MAX LENGTHS of tag
        if(tag.length()> config.clansTagLengthMax)
        {
            MessageUtil.sendMessage(player, config.langMaxTag.replace("{length}", String.valueOf(config.clansTagLengthMax)));
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

        // create clan
        CreateClanEvent event = new CreateClanEvent(player, tag);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Clan clan = new Clan(tag, player.getUniqueId());
            clan.addMember(player.getUniqueId());
            clansData.put(tag, clan);
            user.setClan(clan);
            clanService.createClan(clan, player);
            MessageUtil.sendMessage(player, config.langSuccessfullyCreated);
            MessageUtil.broadcast(config.langBroadcastCreateClan.replace("{tag}", tag));

        }
    }

    public void kickUser(Player player, Player target) {
        if(!hasClan(player))
        {
            MessageUtil.sendMessage(player, config.langNoClan);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();
        if(!isYourClan(user.getClan(), target))
        {
            MessageUtil.sendMessage(player, config.langPlayerNotYourClan);
            return;
        }
        if(!isOnwer(clan, target))
        {
            MessageUtil.sendMessage(player, config.langOwnerCannotBeKicked);
            return;
        }

        // kick player from clan
        User kUser = plugin.getUserManager().getUserData().get(target.getUniqueId());
        PlayerKickClanEvent event = new PlayerKickClanEvent(user.getClan(), target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            clan.getMembers().remove(target.getUniqueId());
            MessageUtil.sendMessage(target , config.langAreKicked);
            kUser.setClan(null);
            MessageUtil.sendMessage(player, config.langSuccessfullyKicked);
            MessageUtil.sendMessage(clan, config.langInfoKickedPlayer.replace("{username}", target.getName()));
            return;
        }
    }

    public void leaveClan(Player player) {
        if(!hasClan(player))
        {
            MessageUtil.sendMessage(player, config.langNoClan);
            return;
        }
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();
        if(isOnwer(clan, player))
        {
            MessageUtil.sendMessage(player, config.langOwnerCannotLeave);
            return;
        }

        // left clan
        LeaveClanEvent event = new LeaveClanEvent(player, user.getClan());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            user.getClan().getMembers().remove(player.getUniqueId());
            user.setClan(null);
            MessageUtil.sendMessage(player, config.langSuccessfullyLeaved);
            MessageUtil.sendMessage(clan, config.langInfoLeavedPlayer.replace("{username}", player.getName()));
        }
    }
    private boolean isYourClan(Clan clan, Player player) {
        return clan.getMembers().contains(player.getUniqueId());
    }

    private boolean isOnwer(Clan clan, Player player) {
        return clan.getOwnerUUID().equals(player.getUniqueId());
    }
    private boolean tagIsBusy(String tag) {
        if(clansData.get(tag)!=null)
            return true;

        return false;
    }

    public void alliance(Player player, Clan allianceClan) {
        if(!hasClan(player))
        {
            MessageUtil.sendMessage(player, config.langNoClan);
            return;
        }

        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        Clan clan = user.getClan();

        // check is owner
        if(!isOnwer(clan, player))
        {
            MessageUtil.sendMessage(player, config.langNotOwnerClan);
            return;
        }

        if(clan.isAlliance(allianceClan.getTag()))
        {
            clan.removeAlliance(allianceClan.getTag());
            return;
        }

    }


    public boolean hasClan(Player player) {
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        return user.getClan() != null;
    }

    public Clan getClan(String tag) {
        return clansData.get(tag.toUpperCase());
    }



    public Clan deleteClan(String tag) {
        return clansData.remove(tag);
    }

    public HashMap<String, Clan> getClansData() {
        return clansData;
    }



}
