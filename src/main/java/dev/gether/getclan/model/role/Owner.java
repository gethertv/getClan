package dev.gether.getclan.model.role;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;

public class Owner extends ClanMember {
    public Owner(Player player, Clan clan) {
        super(player, clan);
    }
}
