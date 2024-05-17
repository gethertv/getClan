package dev.gether.getclan;

import dev.gether.getclan.bstats.Metrics;
import dev.gether.getclan.cmd.ClanCommand;
import dev.gether.getclan.cmd.PlayerCommand;
import dev.gether.getclan.cmd.argument.ClanTagArgument;
import dev.gether.getclan.cmd.argument.OwnerArgument;
import dev.gether.getclan.cmd.argument.UserArgument;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.handler.CustomInvalidUsage;
import dev.gether.getclan.handler.PermissionMessage;
import dev.gether.getclan.handler.contextual.DeputyOwnerContextual;
import dev.gether.getclan.handler.contextual.MemberContextual;
import dev.gether.getclan.handler.contextual.OwnerContextual;
import dev.gether.getclan.listener.*;
import dev.gether.getclan.manager.ClanManager;
import dev.gether.getclan.manager.CooldownManager;
import dev.gether.getclan.manager.UserManager;
import dev.gether.getclan.model.Clan;
import dev.gether.getclan.model.User;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.gether.getclan.model.role.Member;
import dev.gether.getclan.model.role.Owner;
import dev.gether.getclan.placeholder.ClanPlaceholder;
import dev.gether.getclan.scheduler.TopRankScheduler;
import dev.gether.getclan.service.ClanService;
import dev.gether.getclan.service.QueueService;
import dev.gether.getclan.service.UserService;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.stream.Stream;

public final class GetClan extends JavaPlugin {


    private static GetClan instance;
    private Economy economy;

    // manager
    private UserManager userManager;
    private ClanManager clansManager;
    private CooldownManager cooldownManager;

    // service
    private ClanService clanService;
    private UserService userService;
    private QueueService queueService;
    private MySQL mySQL;

    private LiteCommands<CommandSender> liteCommands;

    private ClanPlaceholder clanPlaceholder;

    // scheduler TOP
    private TopRankScheduler topRankScheduler;

    private FileManager fileManager;

    @Override
    public void onLoad() {
        instance = this;
        this.fileManager = new FileManager(this);
    }

    @Override
    public void onEnable() {
        // setup the economy plugin VAULT
        if (!setupEconomy() ) {
            getServer().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // initialize mysql
        mySQL = new MySQL(this, fileManager);

        if (!mySQL.isConnected()) {
            getLogger().severe("Cannot connect to the database!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            clanPlaceholder = new ClanPlaceholder(this, fileManager);
            MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully implement the placeholders");
        }

        // serivce
        queueService = new QueueService(this, mySQL);
        // IMPORTANT: first, we must implement clans, and then users
        clanService = new ClanService(mySQL, queueService);
        userService = new UserService(mySQL, queueService);

        // manager
        userManager = new UserManager(userService, this, fileManager);
        clansManager = new ClanManager(this, clanService, fileManager);
        cooldownManager = new CooldownManager();

        // load data form database
        loadDataMySQL();

        // listeners
        Stream.of(
                new PlayerConnectionListener(this, cooldownManager),
                new PlayerDeathListener(this, fileManager),
                new EntityDamageListener(this),
                new AsyncPlayerChatListener(this),
                new PlayerInteractionEntityListener(fileManager, userManager, cooldownManager)
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
            mySQL.disconnect();

        }

        this.liteCommands.unregister();

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin(CommandSender sender)
    {
        fileManager.reload();
        MessageUtil.sendMessage(sender, "&aSuccessfully reloaded plugin!");
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

        this.liteCommands = LiteBukkitFactory.builder("getclan")
                .commands(
                        new ClanCommand(this, fileManager),
                        new PlayerCommand(this)
                )

                // contextual bind
                .argument(Owner.class, new OwnerContextual(this, fileManager))
                .argument(DeputyOwner.class, new DeputyOwnerContextual(this, fileManager))
                .argument(Member.class, new MemberContextual(this, fileManager))

                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, fileManager.getLangConfig().getMessage("player-not-found"))

                .argument(Clan.class, new ClanTagArgument(clansManager, fileManager))
                .argument(Owner.class, new OwnerArgument(userManager, fileManager))
                .argument(User.class, new UserArgument(userManager, fileManager))

                // handlers
                .missingPermission(new PermissionMessage(fileManager))
                .invalidUsage(new CustomInvalidUsage(fileManager))

                .build();

    }

    public TopRankScheduler getTopRankScheduler() {
        return topRankScheduler;
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


    public FileManager getFileManager() {
        return fileManager;
    }

    public ClanService getClanService() {
        return clanService;
    }

    public ClanManager getClansManager() {
        return clansManager;
    }
}
