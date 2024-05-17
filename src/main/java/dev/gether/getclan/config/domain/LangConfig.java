package dev.gether.getclan.config.domain;

import dev.gether.getclan.GetClan;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LangConfig {

    private HashMap<String, String> langData = new HashMap<>();


    public LangConfig(GetClan getClan, LangType langType) {
        try {
            implementLangMessage(new File(getClan.getDataFolder(), langType.name().toUpperCase()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void implementLangMessage(File file) throws IOException, InvalidConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.load(file);

        config.getKeys(true).forEach(key -> {
            String value = config.getString(key);
            langData.put(key, value);
        });
    }


    public String getMessage(String key) {
        String message = langData.get(key);
        return message != null ? message : key;
    }

}
