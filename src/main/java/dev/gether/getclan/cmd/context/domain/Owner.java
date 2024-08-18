package dev.gether.getclan.cmd.context.domain;

import dev.gether.getclan.core.clan.Clan;
import org.bukkit.entity.Player;

public class Owner extends ClanMember {
    public Owner(Player player, Clan clan) {
        super(player, clan);
    }
}
