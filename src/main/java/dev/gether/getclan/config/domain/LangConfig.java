package dev.gether.getclan.config.domain;

import dev.gether.getclan.GetClan;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
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
            File file = new File(getClan.getDataFolder() + "/lang/", langType.name().toLowerCase() + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                MessageUtil.logMessage(ConsoleColor.YELLOW, "Creating file... "+file.getName());
                MessageUtil.logMessage(ConsoleColor.RED, "Path: "+file.getAbsolutePath());
                getClan.saveResource( "lang/" + file.getName(), false);

            }
            implementLangMessage(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void implementLangMessage(File file) throws IOException, InvalidConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        config.save(file);

        config.getKeys(true).forEach(key -> {
            String value = config.isList(key) ? String.join("\n", config.getStringList(key)) : config.getString(key);
            langData.put(key, value);
        });
    }


    public String getMessage(String key) {
        String message = langData.get(key);
        return message != null ? message : key;
    }

}
