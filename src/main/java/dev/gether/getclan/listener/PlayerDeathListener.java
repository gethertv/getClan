package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.LastHitInfo;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.upgrade.LevelData;
import dev.gether.getclan.core.upgrade.Upgrade;
import dev.gether.getclan.core.upgrade.UpgradeCost;
import dev.gether.getclan.core.upgrade.UpgradeType;
import dev.gether.getclan.event.PlayerNameEvent;
import dev.gether.getclan.event.PointsChangeUserEvent;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.AntySystemRank;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.utils.SystemPoint;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.MessageUtil;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final GetClan plugin;
    private final FileManager fileManager;
    private final HashMap<UUID, AntySystemRank> antySystem = new HashMap<>();
    private final HashMap<UUID, LastHitInfo> lastHits = new HashMap<>();

    private final Function powFunction = new Function("pow", 2) {
        @Override
        public double apply(double... args) {
            return Math.pow(args[0], args[1]);
        }
    };

    public PlayerDeathListener(GetClan plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player attacker)) {
            return;
        }

        lastHits.put(victim.getUniqueId(), new LastHitInfo(attacker.getUniqueId(),
                System.currentTimeMillis() + (fileManager.getConfig().getKillCountDuration() * 1000L),
                attacker.getInventory().getItemInMainHand()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        UserManager userManager = plugin.getUserManager();
        User victimUser = userManager.getUserData().get(victim.getUniqueId());

        if (victimUser == null) return;

        victimUser.increaseDeath();

        LastHitInfo lastHitInfo = lastHits.get(victim.getUniqueId());
        if (killer == null && (lastHitInfo == null || System.currentTimeMillis() >= lastHitInfo.getExpirationTime())) {
            handleSelfInflictedDeath(victim, event);
            return;
        }

        UUID killerUUID = (killer != null) ? killer.getUniqueId() : lastHitInfo.getAttackerUUID();
        User killerUser = userManager.getUserData().get(killerUUID);
        if (killerUser == null) return;

        killerUser.increaseKill();

        OfflinePlayer offlineKiller = Bukkit.getOfflinePlayer(killerUUID);
        if (offlineKiller.isOnline() && fileManager.getConfig().isSystemAntiabuse()) {
            if (handleAntiAbuse(victim, offlineKiller.getPlayer(), event)) return;
        }

        handlePointsCalculation(victim, offlineKiller, victimUser, killerUser, lastHitInfo, event);

        lastHits.remove(victim.getUniqueId());
    }

    private void handleSelfInflictedDeath(Player player, PlayerDeathEvent event) {
        if (!fileManager.getConfig().isDeathMessage()) return;

        if (fileManager.getConfig().isTitleAlert()) {
            sendTitle(player,
                    fileManager.getLangConfig().getMessage("death-self-inflicted-title"),
                    fileManager.getLangConfig().getMessage("death-self-inflicted-subtitle")
            );
        }
        event.setDeathMessage(
                ColorFixer.addColors(
                        fileManager.getLangConfig().getMessage("death-self-inflicted")
                                .replace("{victim}", player.getName())
                )
        );
    }

    private boolean handleAntiAbuse(Player victim, Player killer, PlayerDeathEvent event) {
        String victimIp = victim.getAddress().getAddress().getHostAddress();
        String killerIp = killer.getAddress().getAddress().getHostAddress();

        AntySystemRank antySystemRank = antySystem.computeIfAbsent(killer.getUniqueId(),
                k -> new AntySystemRank(killerIp));

        boolean isKillable = antySystemRank.isPlayerKillable(victimIp, killer);

        if (!isKillable) {
            if (fileManager.getConfig().isDeathMessage()) {
                event.setDeathMessage("");
            }
            int second = SystemPoint.roundUpToMinutes(antySystemRank.getRemainingCooldown(victimIp));

            if (fileManager.getConfig().isTitleAlert()) {
                sendTitle(victim,
                        fileManager.getLangConfig().getMessage("abuse-victim-title"),
                        fileManager.getLangConfig().getMessage("abuse-victim-subtitle")
                );
                sendTitle(killer,
                        fileManager.getLangConfig().getMessage("abuse-killer-title"),
                        fileManager.getLangConfig().getMessage("abuse-killer-subtitle")
                );
            }

            MessageUtil.sendMessage(killer, fileManager.getLangConfig().getMessage("cooldown-kill").replace("{time}", String.valueOf(second)));
            return true;
        } else {
            antySystemRank.addCooldown(victimIp, fileManager.getConfig().getCooldown());
            return false;
        }
    }

    private void handlePointsCalculation(Player victim, OfflinePlayer killer, User victimUser, User killerUser, LastHitInfo lastHitInfo, PlayerDeathEvent event) {
        int pointsDeath = calculatePoints(victimUser.getPoints(), killerUser.getPoints(), 0);
        int pointsKiller = calculatePoints(killerUser.getPoints(), victimUser.getPoints(), 1);

        int deathPointTake = victimUser.getPoints() - pointsDeath;
        int killerPointAdd = pointsKiller - killerUser.getPoints();

        killerPointAdd = getPointsBoosted(killerUser, killerPointAdd);

        PointsChangeUserEvent pointsChangeUserEvent = new PointsChangeUserEvent(killer.getPlayer(), victim, killerPointAdd, deathPointTake);
        Bukkit.getPluginManager().callEvent(pointsChangeUserEvent);
        if (pointsChangeUserEvent.isCancelled()) return;

        if (pointsDeath >= 0) {
            killerUser.addPoint(pointsChangeUserEvent.getPointKiller());
            victimUser.takePoint(pointsChangeUserEvent.getPointVictim());
        }

        if (!fileManager.getConfig().isDeathMessage()) return;

        updateDeathMessage(victim, killer, killerUser, lastHitInfo, pointsChangeUserEvent, event);

        if (fileManager.getConfig().isTitleAlert()) {
            updateTitles(victim, killer, pointsChangeUserEvent);
        }
    }

    private int calculatePoints(int oldRating, int opponentRating, int score) {
        String expression = fileManager.getConfig().getCalcPoints()
                .replace("{old_rating}", String.valueOf(oldRating))
                .replace("{opponent_rating}", String.valueOf(opponentRating))
                .replace("{score}", String.valueOf(score));

        Expression pointExpression = new ExpressionBuilder(expression).functions(powFunction).build();

        try {
            return (int) pointExpression.evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating points", e);
        }
    }

    private void updateDeathMessage(Player victim, OfflinePlayer killer, User killerUser, LastHitInfo lastHitInfo, PointsChangeUserEvent pointsChangeUserEvent, PlayerDeathEvent event) {
        PlayerNameEvent victimEvent = new PlayerNameEvent(victim.getName(), victim.getUniqueId());
        PlayerNameEvent killerEvent = new PlayerNameEvent(killerUser.getName(), killerUser.getUuid());

        Bukkit.getPluginManager().callEvent(victimEvent);
        Bukkit.getPluginManager().callEvent(killerEvent);

        String killerName = killer.isOnline() ? killerEvent.getPlayerName() : killerUser.getName();

        ItemStack itemStack = lastHitInfo.getItemStack();
        String weaponName = getWeaponName(itemStack);

        event.setDeathMessage(
                ColorFixer.addColors(
                        fileManager.getLangConfig().getMessage("death-info")
                                .replace("{victim}", victimEvent.getPlayerName())
                                .replace("{killer}", killerName)
                                .replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim()))
                                .replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller()))
                                .replace("{weapon}", weaponName)
                )
        );
    }

    private String getWeaponName(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        } else if (itemStack != null) {
            return fileManager.getConfig().getTranslate().getOrDefault(itemStack.getType(), itemStack.getType().name());
        }
        return "";
    }

    private void updateTitles(Player victim, OfflinePlayer killer, PointsChangeUserEvent pointsChangeUserEvent) {
        if (killer.isOnline()) {
            sendTitle(killer.getPlayer(),
                    fileManager.getLangConfig().getMessage("killer-title").replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller())),
                    fileManager.getLangConfig().getMessage("killer-subtitle").replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller()))
            );
        }

        sendTitle(victim,
                fileManager.getLangConfig().getMessage("victim-title").replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim())),
                fileManager.getLangConfig().getMessage("victim-subtitle").replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim()))
        );
    }

    private void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(
                ColorFixer.addColors(title),
                ColorFixer.addColors(subtitle),
                fileManager.getConfig().getFadeIn(),
                fileManager.getConfig().getStay(),
                fileManager.getConfig().getFadeOut()
        );
    }

    private int getPointsBoosted(User userKiller, int killerPointAdd) {
        if (!userKiller.hasClan()) return killerPointAdd;

        Clan clan = plugin.getClanManager().getClan(userKiller.getTag());
        LevelData levelData = clan.getUpgrades().get(UpgradeType.POINTS_BOOST);
        if (levelData == null) return killerPointAdd;

        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(UpgradeType.POINTS_BOOST);
        if (upgradeByType.isEmpty() || !upgradeByType.get().isEnabled()) return killerPointAdd;

        UpgradeCost upgradeCost = upgradeByType.get().getUpgradesCost().get(levelData.getLevel());
        if (upgradeCost == null) return killerPointAdd;

        return (int) ((1 + upgradeCost.getBoostValue()) * killerPointAdd);
    }
}