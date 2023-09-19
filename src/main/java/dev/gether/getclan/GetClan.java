package dev.gether.getclans;

import dev.gether.getclans.config.CommandConfigurator;
import dev.gether.getclans.manager.ClansManager;
import dev.gether.getclans.cmd.GetClansCmd;
import dev.gether.getclans.config.Config;
import dev.gether.getclans.database.MySQL;
import dev.gether.getclans.handler.InvalidUsage;
import dev.gether.getclans.handler.PermissionMessage;
import dev.gether.getclans.listener.PlayerConnectionListener;
import dev.gether.getclans.listener.PlayerDeathListener;
import dev.gether.getclans.service.QueueService;
import dev.gether.getclans.placeholder.StatsPoints;
import dev.gether.getclans.scheduler.AutoSave;
import dev.gether.getclans.service.ClanService;
import dev.gether.getclans.service.UserService;
import dev.gether.getclans.manager.UserManager;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.stream.Stream;

public final class GetClans extends JavaPlugin {


    private static GetClans instance;

    // manager
    private UserManager userManager;
    private ClansManager clansManager;

    // service
    private ClanService clanService;
    private UserService userService;
    private QueueService queueService;

    private MySQL mySQL;

    private Config config;

    private LiteCommands<CommandSender> liteCommands;

    private StatsPoints statsPoints;
    @Override
    public void onLoad() {

        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    @Override
    public void onEnable() {

        instance = this;

        // initialize mysql
        mySQL = new MySQL(this);

        if (!mySQL.isConnected()) {
            getLogger().severe("Nie można połączyć się z bazą danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        statsPoints = new StatsPoints(this);

        // serivce
        queueService = new QueueService(this, mySQL);
        // IMPORTANT: first, we must implement clans, and then users
        clanService = new ClanService(mySQL, queueService);
        userService = new UserService(mySQL, queueService);

        // manager
        userManager = new UserManager(this, userService);
        clansManager = new ClansManager(this, clanService);

        // load data form database
        loadDataMySQL();

        // listeners
        Stream.of(
                new PlayerConnectionListener(this),
                new PlayerDeathListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));


        // scheduler
        BukkitScheduler scheduler = Bukkit.getScheduler();

        AutoSave autoSave = new AutoSave(this);
        scheduler.runTaskTimerAsynchronously(this, autoSave, 20L * 300, 20L * 300);


        // register cmd
        registerCmd();


    }

    private void loadDataMySQL() {

        clanService.loadClans();
        userService.loadUsers();

        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                    userManager.loadUser(player);
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {

        this.liteCommands.getPlatform().unregisterAll();

        this.getLogger().info("Disabled!");

        if (mySQL != null) {
            queueService.execute();
            mySQL.closeConnection();

        }


        if(statsPoints!=null)
            statsPoints.unregister();

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void registerCmd()
    {

        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "getClan")
                // Arguments
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("&cKomenda tylko dla gracza!"))
                .commandInstance(
                        new GetClansCmd(this)
                )
                .permissionHandler(new PermissionMessage())
                .invalidUsageHandler(new InvalidUsage())
                .commandGlobalEditor(new CommandConfigurator())
                .register();
    }
    public Config getConfigPlugin()
    {
        return config;
    }
    public static GetClans getInstance() {
        return instance;
    }

    public UserManager getUserManager() {
        return userManager;
    }


    public ClanService getClanService() {
        return clanService;
    }

    public QueueService getQueueService() {
        return queueService;
    }

    public UserService getUserService() {
        return userService;
    }

    public ClansManager getClansManager() {
        return clansManager;
    }
}
