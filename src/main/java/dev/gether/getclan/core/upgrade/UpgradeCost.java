package dev.gether.getclan.core.upgrade;

import dev.gether.getclan.core.CostType;
import dev.gether.getconfig.domain.Item;
import lombok.*;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpgradeCost {

    private Item item;
    private double cost;
    private ItemStack itemStack;
    private CostType costType;
    private double boostValue;


}
