package dev.gether.getclan.model.clan;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;

public class Member {

    private Player player;
    private Clan clan;

    public Member(Player player, Clan clan) {
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
