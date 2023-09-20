package dev.gether.getclan.model.role;

import dev.gether.getclan.model.Clan;
import org.bukkit.entity.Player;

public class Member extends ClanMember{

    public Member(Player player, Clan clan) {
        super(player, clan);
    }
}
