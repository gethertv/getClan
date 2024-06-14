package dev.gether.getclan.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getclan.core.CostType;
import dev.gether.getclan.core.upgrade.Upgrade;
import dev.gether.getclan.core.upgrade.UpgradeCost;
import dev.gether.getclan.core.upgrade.UpgradeType;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.InventoryBase;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpgradesConfig extends GetConfig {

    private InventoryBase inventoryBase = InventoryBase.builder()
            .title("&8&lᴜʟᴇᴘꜱᴢᴇɴɪᴀ ᴋʟᴀɴᴜ")
            .size(45)
            .itemsDecoration(new HashSet<>(Set.of(
                    ItemDecoration.builder()
                            .slots(new HashSet<>(Set.of(0,8,36,44)))
                            .item(Item.builder()
                                    .material(Material.LIME_STAINED_GLASS_PANE)
                                    .displayname("")
                                    .lore(new ArrayList<>())
                                    .build())
                            .build(),
                    ItemDecoration.builder()
                            .slots(new HashSet<>(Set.of(2,3,5,6,18,26,36,37,39,40)))
                            .item(Item.builder()
                                    .material(Material.WHITE_STAINED_GLASS_PANE)
                                    .displayname("")
                                    .lore(new ArrayList<>())
                                    .build())
                            .build(),
                    ItemDecoration.builder()
                            .slots(new HashSet<>(Set.of(1,7,9,17,27,35,37,43)))
                            .item(Item.builder()
                                    .material(Material.GREEN_STAINED_GLASS_PANE)
                                    .displayname("")
                                    .lore(new ArrayList<>())
                                    .build())
                            .build()
            ))).build();
    private Set<Upgrade> upgrades = new HashSet<>(
            Set.of(Upgrade.builder()
                                    .slot(20)
                                    .upgradeType(UpgradeType.DROP_BOOST)
                                    .upgradesCost(new HashMap<>(Map.of(
                                            0, UpgradeCost.builder()
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 1")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX0.5",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .build(),
                                            1, UpgradeCost.builder()
                                                    .boostValue(0.5)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 2")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX1.0",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            2, UpgradeCost.builder()
                                                    .boostValue(1.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 2")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX1.5",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            3, UpgradeCost.builder()
                                                    .boostValue(1.5)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 3")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX2.0",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            4, UpgradeCost.builder()
                                                    .boostValue(2.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 4")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX2.5",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            5, UpgradeCost.builder()
                                                    .boostValue(2.5)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 5")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Zwiększony drop &eX3.0",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            6, UpgradeCost.builder()
                                                    .boostValue(1.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.DIAMOND_PICKAXE)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 6 &7- #e80023&Lᴍᴀx")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Aktualna wartość ulepszenia&8:",
                                                                            "&8→ &7Zwiększony drop &eX3",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(20)
                                                    .costType(CostType.ITEM)
                                                    .build())

                                    ))
                            .build(),
                    Upgrade.builder()
                                    .slot(22)
                                    .upgradeType(UpgradeType.MEMBERS)
                                    .upgradesCost(new HashMap<>(Map.of(
                                            0, UpgradeCost.builder()
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 1")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+1",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .build(),
                                            1, UpgradeCost.builder()
                                                    .boostValue(1.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 2")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+1",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            2, UpgradeCost.builder()
                                                    .boostValue(2.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 3")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+3",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            3, UpgradeCost.builder()
                                                    .boostValue(3.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 4")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+4",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            4, UpgradeCost.builder()
                                                    .boostValue(4.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 5")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+5",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            5, UpgradeCost.builder()
                                                    .boostValue(5.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 6")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Ilość członków&8: &e+6",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            6, UpgradeCost.builder()
                                                    .boostValue(6.0)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.PLAYER_HEAD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 6 &7- #e80023&Lᴍᴀx")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Aktualna wartość ulepszenia&8:",
                                                                            "&8→ &7Ilość członków&8: &e+6",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(20)
                                                    .costType(CostType.ITEM)
                                                    .build())

                                    ))
                            .build(),
                    Upgrade.builder()
                                    .slot(24)
                                    .upgradeType(UpgradeType.POINTS_BOOST)
                                    .upgradesCost(new HashMap<>(Map.of(
                                            0, UpgradeCost.builder()
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 1")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e5%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .build(),
                                            1, UpgradeCost.builder()
                                                    .boostValue(0.1)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 2")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e10%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            2, UpgradeCost.builder()
                                                    .boostValue(0.2)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 2")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e20%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            3, UpgradeCost.builder()
                                                    .boostValue(0.3)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 3")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e30%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            4, UpgradeCost.builder()
                                                    .boostValue(0.4)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 4")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e40%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            5, UpgradeCost.builder()
                                                    .boostValue(0.5)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 5")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Co otrzymasz&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e50%",
                                                                            "&7",
                                                                            "&7Aby awansować na kolejny poziom&8:",
                                                                            "&8→ &f{amount}&8/&7{need-amount}",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(10)
                                                    .costType(CostType.ITEM)
                                                    .build(),
                                            6, UpgradeCost.builder()
                                                    .boostValue(0.6)
                                                    .itemStack(ItemBuilder.create(Material.BOOK, "XYZ", true))
                                                    .item(Item.builder()
                                                            .material(Material.IRON_SWORD)
                                                            .displayname("&8⬛ #ffd573ᴘᴏᴢɪᴏᴍ 6 &7- #e80023&Lᴍᴀx")
                                                            .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "",
                                                                            "&7Aktualna wartość ulepszenia&8:",
                                                                            "&8→ &7Dodatkowe punkty&8: &e60%",
                                                                            "&7"
                                                                    )
                                                            ))
                                                            .glow(false)
                                                            .build())
                                                    .cost(20)
                                                    .costType(CostType.ITEM)
                                                    .build())

                                    ))
                            .build()
            )
    );

    @JsonIgnore
    public Optional<Upgrade> findUpgradeByType(UpgradeType upgradeType) {
        return upgrades.stream().filter(upgrade -> upgrade.getUpgradeType() == upgradeType).findAny();
    }

    @JsonIgnore
    public Optional<Upgrade> findUpgradeTypeBySlot(int slot) {
        return upgrades.stream().filter(upgrade -> upgrade.getSlot() == slot).findAny();
    }
}
