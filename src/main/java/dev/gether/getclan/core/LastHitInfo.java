package dev.gether.getclan.core;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class LastHitInfo {
    private UUID attackerUUID;
    private long expirationTime;
    private ItemStack itemStack;

    public LastHitInfo(UUID attackerUUID, long expirationTime, ItemStack itemStack) {
        this.attackerUUID = attackerUUID;
        this.expirationTime = expirationTime;
        this.itemStack = itemStack;
    }
}