package dev.gether.getclan.handler;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackAttachmentResolver;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.*;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringEnchantmentTransformer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringPotionEffectTypeTransformer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringTagTransformer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringWorldTransformer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SerdesBukkit implements OkaeriSerdesPack {

    public SerdesBukkit() {
    }
    public void register(@NonNull SerdesRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("registry is marked non-null but is null");
        } else {
            registry.register(new ItemMetaSerializer());
            registry.register(new ItemStackSerializer());
            registry.register(new ItemStackAttachmentResolver());
            registry.register(new LocationSerializer());
            registry.register(new PotionEffectSerializer());
            registry.register(new VectorSerializer());
            registry.register(new StringEnchantmentTransformer());
            registry.register(new StringPotionEffectTypeTransformer());
            whenClass("org.bukkit.Tag", () -> {
                registry.register(new StringTagTransformer());
            });
            registry.register(new StringWorldTransformer());
        }
    }

    private static void whenClass(@NonNull String name, @NonNull Runnable runnable) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else if (runnable == null) {
            throw new NullPointerException("runnable is marked non-null but is null");
        } else {
            try {
                Class.forName(name);
                runnable.run();
            } catch (ClassNotFoundException var3) {
            }
        }
    }
}
