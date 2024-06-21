package dev.gether.getclan.listener;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.upgrade.LevelData;
import dev.gether.getclan.core.upgrade.Upgrade;
import dev.gether.getclan.core.upgrade.UpgradeCost;
import dev.gether.getclan.core.upgrade.UpgradeType;
import dev.gether.getclan.event.PointsChangeUserEvent;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.AntySystemRank;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.utils.SystemPoint;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


public class PlayerDeathListener implements Listener {

    private final GetClan plugin;
    private final FileManager fileManager;
    private final ClanManager clanManager;
    private HashMap<UUID, AntySystemRank> antySystem = new HashMap<>();

    Function powFunction = new Function("pow", 2) {
        @Override
        public double apply(double... args) {
            return Math.pow(args[0], args[1]);
        }
    };


    public PlayerDeathListener(GetClan plugin, FileManager fileManager, ClanManager clanManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Player killer = player.getKiller();

        UserManager userManager = plugin.getUserManager();
        User userDeath = userManager.getUserData().get(player.getUniqueId());

        if (userDeath == null)
            return;

        // increase death
        userDeath.increaseDeath();

        if (killer == null) {
            // message after the death
            if (!fileManager.getConfig().isDeathMessage())
                return;

            if(fileManager.getConfig().isTitleAlert()) {
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
            return;
        }

        User userKiller = userManager.getUserData().get(killer.getUniqueId());
        if (userKiller == null)
            return;


        userKiller.increaseKill();

        if (fileManager.getConfig().isSystemAntiabuse()) {
            String playerIp = player.getAddress().getAddress().getHostAddress();
            AntySystemRank antySystemRank = antySystem.get(killer.getUniqueId());

            if (antySystemRank != null) {
                if (!antySystemRank.isPlayerKillable(playerIp)) {
                    if (fileManager.getConfig().isDeathMessage()) {
                        event.setDeathMessage("");
                    }
                    int second = SystemPoint.roundUpToMinutes(antySystemRank.getRemainingCooldown(playerIp));

                    // title alert
                    if(fileManager.getConfig().isTitleAlert()) {
                        sendTitle(player,
                                fileManager.getLangConfig().getMessage("abuse-victim-title"),
                                fileManager.getLangConfig().getMessage("abuse-victim-subtitle")
                        );
                        sendTitle(killer,
                                fileManager.getLangConfig().getMessage("abuse-killer-title"),
                                fileManager.getLangConfig().getMessage("abuse-killer-subtitle")
                        );
                    }

                    MessageUtil.sendMessage(killer, fileManager.getLangConfig().getMessage("cooldown-kill").replace("{time}", String.valueOf(second)));
                    return;
                }
                antySystemRank.addCooldown(playerIp, fileManager.getConfig().getCooldown());
            } else {
                antySystem.put(killer.getUniqueId(), new AntySystemRank(
                        killer.getAddress().getAddress().getHostAddress(),
                        playerIp,
                        fileManager.getConfig().getCooldown()
                ));
            }
        }

        String newPointDeath = fileManager.getConfig().getCalcPoints();
        String newPointKiller = fileManager.getConfig().getCalcPoints();

        String expressionDeath = newPointDeath
                .replace("{old_rating}", String.valueOf(userDeath.getPoints()))
                .replace("{opponent_rating}", String.valueOf(userKiller.getPoints()))
                .replace("{score}", String.valueOf(0));

        String expressionKiller = newPointKiller
                .replace("{old_rating}", String.valueOf(userKiller.getPoints()))
                .replace("{opponent_rating}", String.valueOf(userDeath.getPoints()))
                .replace("{score}", String.valueOf(1));

        Expression deathPoint = new ExpressionBuilder(expressionDeath).functions(powFunction).build();
        Expression killerPoint = new ExpressionBuilder(expressionKiller).functions(powFunction).build();

        try {
            int pointsDeath = (int) deathPoint.evaluate();
            int pointsKiller = (int) killerPoint.evaluate();

            int deathPointTake = userDeath.getPoints() - pointsDeath;
            int killerPointAdd = pointsKiller - userKiller.getPoints();

            // modify points if he has a clan && boost raking perk
            killerPointAdd = getPointsBoosted(userKiller, killerPointAdd);

            PointsChangeUserEvent pointsChangeUserEvent = new PointsChangeUserEvent(killer, player, killerPointAdd, deathPointTake);
            Bukkit.getPluginManager().callEvent(pointsChangeUserEvent);
            if (pointsChangeUserEvent.isCancelled())
                return;

            if (pointsDeath >= 0) {
                userKiller.addPoint(pointsChangeUserEvent.getPointKiller());
                userDeath.takePoint(pointsChangeUserEvent.getPointVictim());
            }

            // message after the death
            if (!fileManager.getConfig().isDeathMessage())
                return;

            event.setDeathMessage(
                    ColorFixer.addColors(
                            fileManager.getLangConfig().getMessage("death-info")
                                    .replace("{victim}", clanManager.getIncognitoName(player))
                                    .replace("{killer}", clanManager.getIncognitoName(killer))
                                    .replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim()))
                                    .replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller()))
                    )
            );

            if(fileManager.getConfig().isTitleAlert()) {
                sendTitle(killer,
                        fileManager.getLangConfig().getMessage("killer-title").replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller())),
                        fileManager.getLangConfig().getMessage("killer-subtitle").replace("{killer-points}", String.valueOf(pointsChangeUserEvent.getPointKiller()))
                );

                sendTitle(player,
                        fileManager.getLangConfig().getMessage("victim-title").replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim())),
                        fileManager.getLangConfig().getMessage("victim-subtitle").replace("{victim-points}", String.valueOf(pointsChangeUserEvent.getPointVictim()))
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        int points = killerPointAdd;
        if (!userKiller.hasClan())
            return points;

        Clan clan = plugin.getClanManager().getClan(userKiller.getTag());
        LevelData levelData = clan.getUpgrades().get(UpgradeType.POINTS_BOOST);
        if (levelData == null)
            return points;

        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(UpgradeType.POINTS_BOOST);
        if (upgradeByType.isEmpty())
            return points;

        Upgrade upgrade = upgradeByType.get();
        if(!upgrade.isEnabled())
            return points;

        UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(levelData.getLevel());
        if (upgradeCost == null)
            return points;

        return (int) ((1 + upgradeCost.getBoostValue()) * points);

    }

}
