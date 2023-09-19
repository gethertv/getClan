package dev.gether.getclans.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private String tag;
    private String ownerName;
    private List<UUID> invitedPlayers = new ArrayList<>();

    private List<UUID> members = new ArrayList<>();

    public Clan(String tag, String ownerName) {
        this.tag = tag;
        this.ownerName = ownerName;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public String getTag() {
        return tag;
    }

    public List<UUID> getInvitedPlayers() {
        return invitedPlayers;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
