package dev.gether.getclan.cmd.context.domain;

import dev.gether.getclan.core.clan.Clan;
import org.bukkit.entity.Player;

public class Member extends ClanMember{

    public Member(Player player, Clan clan) {
        super(player, clan);
    }
}
