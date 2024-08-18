package dev.gether.getclan.listener;

import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class InventoryClickListener implements Listener {

    private final ClanManager clanManager;
    private final UserManager userManager;

    public InventoryClickListener(ClanManager clanManager, UserManager userManager) {
        this.clanManager = clanManager;
        this.userManager = userManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        Optional<User> userByPlayer = userManager.findUserByPlayer(player);
        if(userByPlayer.isEmpty())
            return;

        User user = userByPlayer.get();
        if(!user.hasClan())
            return;

        Clan clan = clanManager.getClan(user.getTag());
        if(inventory.equals(clan.getInventory())) {
            event.setCancelled(true);

            clanManager.clickInv(player, clan, event.getRawSlot(), event.getClick());
        }


    }


}
