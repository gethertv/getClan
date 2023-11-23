package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

    private final GetClan plugin;
    private Config config;
    public EntityDamageListener(GetClan plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigPlugin();
    }
    @EventHandler()
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        User victimUserData = plugin.getUserManager().getUserData().get(victim.getUniqueId());
        if (victimUserData == null || victimUserData.getClan() == null) {
            return;
        }

        Clan victimClan = victimUserData.getClan();

        if (victimClan.isMember(attacker.getUniqueId())) {
            if (!victimClan.isPvpEnable()) {
                event.setCancelled(true);
            }
            return;
        }

        User attackerUserData = plugin.getUserManager().getUserData().get(attacker.getUniqueId());
        if (attackerUserData == null || attackerUserData.getClan() == null) {
            return;
        }

        Clan attackerClan = attackerUserData.getClan();

        if (victimClan.isAlliance(attackerClan.getTag())) {
            if (!config.pvpAlliance) {
                event.setCancelled(true);
            }
        }

    }

}
