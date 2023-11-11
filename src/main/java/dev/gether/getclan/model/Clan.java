package dev.gether.getclan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private String tag;
    private UUID ownerUUID;
    private UUID deputyOwnerUUID;
    private List<UUID> invitedPlayers = new ArrayList<>();

    private List<UUID> members = new ArrayList<>();

    private List<String> alliances = new ArrayList<>();
    private List<String> inviteAlliances = new ArrayList<>();

    private boolean pvpEnable;

    public Clan(String tag, UUID ownerUUID, UUID deputyOwnerUUID, boolean pvpEnable) {
        this(tag, ownerUUID, pvpEnable);
        this.deputyOwnerUUID = deputyOwnerUUID;
    }

    public Clan(String tag, UUID ownerUUID, boolean pvpEnable) {
        this.tag = tag;
        this.ownerUUID = ownerUUID;
        this.members.add(ownerUUID);
        this.pvpEnable = pvpEnable;
    }
    public boolean isAlliance(String tag)
    {
        return alliances.contains(tag.toUpperCase());
    }

    public boolean isMember(UUID uuid)
    {
        return members.contains(uuid);
    }
    public boolean removeAlliance(String tag)
    {
        return alliances.remove(tag.toUpperCase());
    }
    public void addAlliance(String tag)
    {
        alliances.add(tag.toUpperCase());
    }
    public boolean hasInvite(UUID uuid)
    {
        return invitedPlayers.contains(uuid);
    }

    public boolean isPvpEnable() {
        return pvpEnable;
    }

    public void joinUser(UUID uuid) {
        invitedPlayers.remove(uuid);
        members.add(uuid);
    }

    public void resetInvite()
    {
        inviteAlliances.clear();
        invitedPlayers.clear();
    }
    public void removeMember(UUID uuid) {
        if(deputyOwnerUUID!=null && deputyOwnerUUID.equals(uuid))
            deputyOwnerUUID = null;

        members.remove(uuid);
    }
    public void invite(UUID uuid) {
        invitedPlayers.add(uuid);
    }
    public void cancelInvite(UUID uuid) {
        invitedPlayers.remove(uuid);
    }
    public List<UUID> getMembers() {
        return members;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public UUID getDeputyOwnerUUID() {
        return deputyOwnerUUID;
    }

    public List<String> getAlliances() {
        return alliances;
    }


    public String getTag() {
        return tag;
    }

    public void setDeputyOwnerUUID(UUID deputyOwnerUUID) {
        this.deputyOwnerUUID = deputyOwnerUUID;
    }

    public void setOwner(UUID newOwnerUUID) {
        ownerUUID = newOwnerUUID;
    }


    public boolean isSuggestAlliance(String tag)
    {
        return inviteAlliances.contains(tag);
    }
    public boolean inviteAlliance(String tag)
    {
        return inviteAlliances.add(tag);
    }

    public void removeInviteAlliance(String tag) {
        inviteAlliances.remove(tag);
    }

    public void removeSuggestAlliance(String tag)
    {
        inviteAlliances.remove(tag);
    }
    public boolean isOwner(UUID uuid) {
        return ownerUUID.equals(uuid);
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public boolean isDeputy(UUID uniqueId) {
       return (deputyOwnerUUID != null && deputyOwnerUUID.equals(uniqueId));
    }


    public void togglePvp() {
        pvpEnable = !pvpEnable;
    }
}
