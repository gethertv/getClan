package dev.gether.getclan.model.role;

import dev.gether.getclan.model.Clan;
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
