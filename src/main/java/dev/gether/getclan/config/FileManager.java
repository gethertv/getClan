package dev.gether.getclan.config;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.domain.Config;
import dev.gether.getclan.config.domain.LangConfig;
import dev.gether.getclan.config.domain.DatabaseConfig;
import dev.gether.getconfig.ConfigManager;
import lombok.Getter;

import java.io.File;


@Getter
public class FileManager {


    private final GetClan getClan;
    // domain
    private Config config;
    private LangConfig langConfig;
    private DatabaseConfig databaseConfig;

    public FileManager(GetClan getClan) {
        this.getClan = getClan;

        this.config = ConfigManager.create(Config.class, it -> {
            it.file(new File(getClan.getDataFolder(), "config.yml"));
            it.load();
        });

        this.langConfig = new LangConfig(getClan, config.getLangType());
        this.databaseConfig = ConfigManager.create(DatabaseConfig.class, it -> {
            it.file(new File(getClan.getDataFolder(), "database.yml"));
            it.load();
        });

    }

    public void reload() {
        this.config.load();
        this.langConfig = new LangConfig(getClan, config.getLangType());
        this.databaseConfig.load();
    }
}
