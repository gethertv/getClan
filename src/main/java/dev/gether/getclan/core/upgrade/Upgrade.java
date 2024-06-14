package dev.gether.getclan.core.upgrade;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Upgrade {
    private int slot;
    private UpgradeType upgradeType;
    private Map<Integer, UpgradeCost> upgradesCost;

}
