package dev.gether.getclan.listener;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.manager.CooldownManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.User;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class PlayerInteractionEntityListener implements Listener {

    private final FileManager fileManager;
    private final UserManager userManager;
    private final CooldownManager cooldownManager;

    public PlayerInteractionEntityListener(FileManager fileManager, UserManager userManager, CooldownManager cooldownManager) {
        this.fileManager = fileManager;
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
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("slow-down"));
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
