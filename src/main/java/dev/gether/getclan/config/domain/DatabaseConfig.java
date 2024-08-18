package dev.gether.getclan.config.domain;

import dev.gether.getclan.database.DatabaseType;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.annotation.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseConfig extends GetConfig {

    @Comment("MYSQL, SQLITE")
    private DatabaseType databaseType = DatabaseType.SQLITE;
    @Comment("Host serwera MySQL")
    private String host = "localhost";

    @Comment("Nazwa użytkownika serwera MySQL")
    private String username = "user";

    @Comment("Hasło do serwera MySQL")
    private String password = "pass";

    @Comment("Nazwa bazy danych")
    private String database = "database_name";

    @Comment("Port serwera MySQL")
    private String port = "3306";

    @Comment("Używać SSL?")
    private boolean ssl = false;
}