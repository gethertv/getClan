package dev.gether.getclan;

import dev.gether.getclan.bstats.Metrics;
import dev.gether.getclan.cmd.GetClanENCmd;
import dev.gether.getclan.cmd.GetGraczPLCmd;
import dev.gether.getclan.cmd.GetUserENCmd;
import dev.gether.getclan.cmd.argument.ClanTagArgument;
import dev.gether.getclan.cmd.argument.OwnerArgument;
import dev.gether.getclan.cmd.argument.UserArgument;
import dev.gether.getclan.config.lang.LangMessage;
import dev.gether.getclan.config.lang.LangType;
import dev.gether.getclan.handler.*;
import dev.gether.getclan.handler.contextual.DeputyOwnerContextual;
import dev.gether.getclan.handler.contextual.MemberContextual;
import dev.gether.getclan.handler.contextual.OwnerContextual;
import dev.gether.getclan.listener.AsyncPlayerChatListener;
import dev.gether.getclan.listener.EntityDamageListener;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.cmd.GetClanPLCmd;
import dev.gether.getclan.config.Config;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.listener.PlayerConnectionListener;
import dev.gether.getclan.listener.PlayerDeathListener;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.gether.getclan.model.role.Member;
import dev.gether.getclan.model.role.Owner;
import dev.gether.getclan.scheduler.TopRankScheduler;
import dev.gether.getclan.service.QueueService;
import dev.gether.getclan.placeholder.ClanPlaceholder;
import dev.gether.getclan.service.ClanService;
import dev.gether.getclan.service.UserService;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.utils.MessageUtil;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.platform.LiteSender;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.stream.Stream;

public final class GetClan extends JavaPlugin {


    private static GetClan instance;
    private Economy economy;

    // manager
    private UserManager userManager;
    private ClanManager clansManager;

    // service
    private ClanService clanService;
    private UserService userService;
    private QueueService queueService;
    private MySQL mySQL;

    private Config config;
    public LangMessage lang;
    private LiteCommands<CommandSender> liteCommands;

    private ClanPlaceholder clanPlaceholder;

    // scheduler TOP
    private TopRankScheduler topRankScheduler;

    public void loadConfig() {

        config = ConfigManager.create(Config.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        lang = ConfigManager.create(LangMessage.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "lang/"+config.langType.name()+".yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    @Override
    public void onEnable() {

        instance = this;

        // implement configuration
        loadConfig();

        if (!setupEconomy() ) {
            getServer().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // initialize mysql
        mySQL = new MySQL(this);

        if (!mySQL.isConnected()) {
            getLogger().severe("Nie można połączyć się z bazą danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        clanPlaceholder = new ClanPlaceholder(this);

        // serivce
        queueService = new QueueService(this, mySQL);
        // IMPORTANT: first, we must implement clans, and then users
        clanService = new ClanService(mySQL, queueService);
        userService = new UserService(mySQL, queueService);

        // manager
        userManager = new UserManager(this, userService, lang);
        clansManager = new ClanManager(this, clanService);

        // load data form database
        loadDataMySQL();

        // listeners
        Stream.of(
                new PlayerConnectionListener(this),
                new PlayerDeathListener(this),
                new EntityDamageListener(this),
                new AsyncPlayerChatListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));



        // scheduler
        BukkitScheduler scheduler = Bukkit.getScheduler();
        // rank/top scheduler
        topRankScheduler = new TopRankScheduler(userManager, clansManager);
        // save data (5MIN)
        scheduler.runTaskTimerAsynchronously(this, () -> queueService.execute(),20L * 60 * 5, 20L * 60 * 5);
        topRankScheduler.runTaskTimerAsynchronously(this, 0L, 20L * 60 * 2);

        // register cmd
        registerCmd();


        Metrics metrics = new Metrics(this, 19808);

    }


    @Override
    public void onDisable() {


        if(clanPlaceholder!=null)
            clanPlaceholder.unregister();

        if (mySQL != null) {
            queueService.execute();
            mySQL.closeConnection();

        }

        this.liteCommands.getPlatform().unregisterAll();

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin(LiteSender sender)
    {
        config.load();
        lang.load();
        MessageUtil.sendMessage(sender, "&aPomyslnie przeladowano plugin!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
    private void loadDataMySQL() {

        clanService.loadClans();
        userService.loadUsers();
        clanService.loadAlliances();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskAsynchronously(this, () -> Bukkit.getOnlinePlayers().forEach(player -> userManager.loadUser(player)));
    }


    public void registerCmd()
    {

       this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "getclan")
                .commandInstance(
                        (config.langType == LangType.PL ? new GetClanPLCmd(this) : new GetClanENCmd(this)),
                        (config.langType == LangType.PL ? new GetGraczPLCmd(this) : new GetUserENCmd(this))
                )

                // contextual bind
                .contextualBind(Owner.class, new OwnerContextual(this))
                .contextualBind(DeputyOwner.class, new DeputyOwnerContextual(this))
                .contextualBind(Member.class, new MemberContextual(this))
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(lang.langPlayerNotOnline))


                // args
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), lang.langPlayerNotOnline))
                .argument(Clan.class, new ClanTagArgument(lang, clansManager))
                .argument(Owner.class, new OwnerArgument(lang, userManager))
                .argument(User.class, new UserArgument(lang, userManager))

                // handlers
                .permissionHandler(new PermissionMessage(lang))
                .invalidUsageHandler(new InvalidUsage(lang))

                .register();

    }

    public TopRankScheduler getTopRankScheduler() {
        return topRankScheduler;
    }

    public Config getConfigPlugin()
    {
        return config;
    }
    public static GetClan getInstance() {
        return instance;
    }

    public UserManager getUserManager() {
        return userManager;
    }


    public UserService getUserService() {
        return userService;
    }

    public Economy getEconomy() {
        return economy;
    }

    public LangMessage getLang() {
        return lang;
    }

    public ClanService getClanService() {
        return clanService;
    }

    public ClanManager getClansManager() {
        return clansManager;
    }
}
