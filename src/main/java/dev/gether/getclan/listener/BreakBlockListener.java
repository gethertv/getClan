package dev.gether.getclan.listener;

import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.upgrade.*;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getconfig.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BreakBlockListener implements Listener {

    private final UserManager userManager;
    private final ClanManager clanManager;
    private final UpgradeManager upgradeManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public BreakBlockListener(UserManager userManager, ClanManager clanManager, UpgradeManager upgradeManager, FileManager fileManager) {
        this.userManager = userManager;
        this.clanManager = clanManager;
        this.upgradeManager = upgradeManager;
        this.fileManager = fileManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent event) {
        if(event.isCancelled())
            return;

        if(!fileManager.getUpgradesConfig().isUpgradeEnable())
            return;

        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(UpgradeType.DROP_BOOST);
        if(upgradeByType.isEmpty())
            return;

        Upgrade upgrade = upgradeByType.get();
        if(!upgrade.isEnabled())
            return;

        if(fileManager.getUpgradesConfig().getWhitelistMaterial().isEmpty())
            return;

        // if not exists at the whitelist than ignore this blok
        if(!fileManager.getUpgradesConfig().getWhitelistMaterial().contains(event.getBlock().getType()))
            return;


        Player player = event.getPlayer();
        Optional<User> userByPlayer = userManager.findUserByPlayer(player);
        if(userByPlayer.isEmpty())
            return;

        User user = userByPlayer.get();
        if(!user.hasClan())
            return;

        Clan clan = clanManager.getClan(user.getTag());
        if(clan == null)
            return;

        LevelData levelData = clan.getUpgrades().get(UpgradeType.DROP_BOOST);
        if(levelData == null)
            return;

        Optional<UpgradeCost> upgradeCostTemp = upgradeManager.findUpgradeCost(UpgradeType.DROP_BOOST, levelData.getLevel());
        if(upgradeCostTemp.isEmpty())
            return;


        UpgradeCost upgradeCost = upgradeCostTemp.get();
        double boostValue = upgradeCost.getBoostValue();

        double multiply = 1 + boostValue;
        double restChance = multiply % 1;

        List<ItemStack> drops = new ArrayList<>(event.getBlock().getDrops(player.getInventory().getItemInMainHand()));
        if(drops.isEmpty())
            return;

        event.setDropItems(false);
        drops.forEach(itemStack -> {
            int basicAmount = itemStack.getAmount();
            int recieveAmount = basicAmount * (int) multiply;

            double winTicket = random.nextDouble();
            if(winTicket <= restChance)
                recieveAmount += basicAmount;

            itemStack.setAmount(recieveAmount);

            PlayerUtil.giveItem(player, itemStack);
        });

    }
}
