package dev.gether.getclan.core.upgrade;

import dev.gether.getclan.config.FileManager;

import java.util.Optional;

public class UpgradeManager {


    private final FileManager fileManager;

    public UpgradeManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Optional<UpgradeCost> findUpgradeCost(UpgradeType upgradeType, int level) {
        Optional<Upgrade> upgradeByType = fileManager.getUpgradesConfig().findUpgradeByType(upgradeType);
        if(upgradeByType.isEmpty())
            return Optional.empty();

        Upgrade upgrade = upgradeByType.get();
        UpgradeCost upgradeCost = upgrade.getUpgradesCost().get(level);
        if(upgradeCost == null)
            return Optional.empty();

        return Optional.of(upgradeCost);
    }

    public void save() {
        fileManager.getUpgradesConfig().save();
    }

}
