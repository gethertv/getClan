package dev.gether.getclan.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Objects;

public class ItemUtil {

    public static int calcItemAmount(Player player, ItemStack targetItem) {
        int totalAmount = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && itemStack.isSimilar(targetItem)) {
                totalAmount += itemStack.getAmount();
            }
        }
        return totalAmount;
    }

    public static void removeItems(Player player, ItemStack targetItem, int amountToRemove) {
        for (ItemStack itemStack : player.getInventory()) {
            if (amountToRemove <= 0) {
                break;
            }

            if (itemStack != null && itemStack.isSimilar(targetItem) && itemStack.getType() != Material.AIR) {
                int currentAmount = itemStack.getAmount();

                if (currentAmount <= amountToRemove) {
                    amountToRemove -= currentAmount;
                    itemStack.setAmount(0);
                } else {
                    itemStack.setAmount(currentAmount - amountToRemove);
                    amountToRemove = 0;
                }
            }
        }
    }

    public boolean isSame(ItemStack item, ItemStack stack) {
        if (stack == null) return false;

        if (!item.hasItemMeta() || !stack.hasItemMeta()) return false;

        ItemMeta thisMeta = item.getItemMeta();
        ItemMeta stackMeta = stack.getItemMeta();

        if (!Objects.equals(thisMeta.getDisplayName(), stackMeta.getDisplayName())) {
            return false;
        }

        if (!Objects.equals(thisMeta.getLore(), stackMeta.getLore())) {
            return false;
        }

        if (!thisMeta.hasEnchants() || !stackMeta.hasEnchants()) {
            return false;
        }
        for (Map.Entry<Enchantment, Integer> entry : thisMeta.getEnchants().entrySet()) {
            if (!stackMeta.hasEnchant(entry.getKey()) || !(stackMeta.getEnchantLevel(entry.getKey()) == entry.getValue())) {
                return false;
            }
        }

        if (!Objects.equals(thisMeta.getAttributeModifiers(), stackMeta.getAttributeModifiers())) {
            return false;
        }

        return true;
    }
}
