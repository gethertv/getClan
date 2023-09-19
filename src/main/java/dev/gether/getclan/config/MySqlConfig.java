package dev.gether.getclans.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

@Header("Konfiguracja MySQL")
@Names(strategy = NameStrategy.IDENTITY)
public class MySqlConfig extends OkaeriConfig {

    @Comment("Host serwera MySQL")
    public String host = "localhost";

    @Comment("Nazwa użytkownika serwera MySQL")
    public String username = "user";

    @Comment("Hasło do serwera MySQL")
    public String password = "pass";

    @Comment("Nazwa bazy danych")
    public String database = "database_name";

    @Comment("Port serwera MySQL")
    public String port = "3306";

    @Comment("Używać SSL?")
    public boolean ssl = false;
}