package dev.gether.getclan.listener;

import dev.gether.getclan.config.Config;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.manager.CooldownManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getclan.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerInteractionEntityListener implements Listener {

    private final LangMessage lang;
    private final UserManager userManager;
    private final CooldownManager cooldownManager;

    public PlayerInteractionEntityListener(LangMessage lang, UserManager userManager, CooldownManager cooldownManager) {
        this.lang = lang;
        this.userManager = userManager;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if(event.getRightClicked() instanceof Player clickPlayer) {
            if(!player.isSneaking())
                return;
            // disable double action
            if(event.getHand() == EquipmentSlot.OFF_HAND) return;
            // check user has cooldown
            if(cooldownManager.hasCooldown(player)) {
                MessageUtil.sendMessage(player, lang.langSlowDown);
                return;
            }
            // add cooldown
            cooldownManager.addCooldown(player);

            // find clicked user data
            Optional<User> userByPlayer = userManager.findUserByPlayer(clickPlayer);
            if(userByPlayer.isPresent()) {
                User user = userByPlayer.get();
                // send player message about clicked user
                userManager.infoPlayer(player, user);
            }

        }
    }



}
