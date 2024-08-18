package dev.gether.getclan.cmd.context.domain;

import dev.gether.getclan.core.clan.Clan;
import org.bukkit.entity.Player;

public class ClanMember {

    private Player player;
    private Clan clan;

    public ClanMember(Player player, Clan clan) {
        this.player = player;
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    public Player getPlayer() {
        return player;
    }
}
