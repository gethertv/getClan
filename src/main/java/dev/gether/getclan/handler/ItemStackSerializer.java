package dev.gether.getclan.handler;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackFormat;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackSpecData;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.ItemMetaSerializer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.experimental.StringBase64ItemStackTransformer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {
    private static final ItemMetaSerializer ITEM_META_SERIALIZER = new ItemMetaSerializer();
    private static final StringBase64ItemStackTransformer ITEM_STACK_TRANSFORMER = new StringBase64ItemStackTransformer();
    private boolean failsafe = false;

    public boolean supports(@NonNull Class<? super ItemStack> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        } else {
            return ItemStack.class.isAssignableFrom(type);
        }
    }

    public void serialize(@NonNull ItemStack itemStack, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemStack == null) {
            throw new NullPointerException("itemStack is marked non-null but is null");
        } else if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        } else if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        } else {
            data.add("material", itemStack.getType());
            if (itemStack.getAmount() != 1) {
                data.add("amount", itemStack.getAmount());
            }

            if (itemStack.getDurability() != 0) {
                data.add("durability", itemStack.getDurability());
            }

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null && meta.isUnbreakable()) {
                data.add("unbreakable", true);
            }

            ItemStackFormat format = (ItemStackFormat) data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
            if (itemStack.hasItemMeta()) {
                switch (format) {
                    case NATURAL:
                        data.add("item-meta", meta, ItemMeta.class);
                        break;
                    case COMPACT:
                        ITEM_META_SERIALIZER.serialize(meta, data, generics);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown format: " + format);
                }

                if (this.failsafe) {
                    DeserializationData deserializationData = new DeserializationData(data.asMap(), data.getConfigurer(), data.getContext());
                    ItemStack deserializedStack = this.deserialize(deserializationData, generics);
                    if (!deserializedStack.equals(itemStack)) {
                        data.clear();
                        String base64Stack = ITEM_STACK_TRANSFORMER.leftToRight(itemStack, data.getContext());
                        data.add("base64", base64Stack);
                    }
                }
            }
        }
    }
    public ItemStack deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        } else if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        } else {
            String materialName;
            if (data.containsKey("base64")) {
                materialName = (String) data.get("base64", String.class);
                return ITEM_STACK_TRANSFORMER.rightToLeft(materialName, data.getContext());
            } else {
                materialName = (String) data.get("material", String.class);
                Material material = Material.valueOf(materialName);
                int amount = data.containsKey("amount") ? (Integer) data.get("amount", Integer.class) : 1;
                short durability = data.containsKey("durability") ? (Short) data.get("durability", Short.class) : 0;
                ItemStackFormat format = (ItemStackFormat) data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
                ItemMeta itemMeta;
                switch (format) {
                    case NATURAL:
                        if (data.containsKey("display-name")) {
                            itemMeta = ITEM_META_SERIALIZER.deserialize(data, generics);
                        } else {
                            itemMeta = data.containsKey("item-meta") ? (ItemMeta) data.get("item-meta", ItemMeta.class) : null;
                        }
                        break;
                    case COMPACT:
                        if (data.containsKey("item-meta")) {
                            itemMeta = (ItemMeta) data.get("item-meta", ItemMeta.class);
                        } else {
                            itemMeta = ITEM_META_SERIALIZER.deserialize(data, generics);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown format: " + format);
                }

                ItemStack itemStack = new ItemStack(material, amount);
                itemStack.setDurability(durability);

                if (itemMeta != null) {
                    // Check and set unbreakable property
                    if (data.containsKey("unbreakable") && (Boolean) data.get("unbreakable", Boolean.class)) {
                        itemMeta.setUnbreakable(true);
                    }
                    itemStack.setItemMeta(itemMeta);
                }

                return itemStack;
            }
        }
    }


    public ItemStackSerializer() {
    }

    public ItemStackSerializer(final boolean failsafe) {
        this.failsafe = failsafe;
    }
}
