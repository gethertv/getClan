package dev.gether.getclan;

import dev.gether.getclan.bstats.Metrics;
import dev.gether.getclan.cmd.ClanENCommand;
import dev.gether.getclan.cmd.ClanPLCommand;
import dev.gether.getclan.cmd.PlayerENCommand;
import dev.gether.getclan.cmd.PlayerPLCommand;
import dev.gether.getclan.cmd.argument.ClanTagArgument;
import dev.gether.getclan.cmd.argument.OwnerArgument;
import dev.gether.getclan.cmd.argument.UserArgument;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.config.domain.LangType;
import dev.gether.getclan.core.alliance.AllianceManager;
import dev.gether.getclan.core.upgrade.UpgradeManager;
import dev.gether.getclan.database.MySQL;
import dev.gether.getclan.handler.CustomInvalidUsage;
import dev.gether.getclan.handler.PermissionMessage;
import dev.gether.getclan.cmd.context.DeputyOwnerContextual;
import dev.gether.getclan.cmd.context.MemberContextual;
import dev.gether.getclan.cmd.context.OwnerContextual;
import dev.gether.getclan.listener.*;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.CooldownManager;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.cmd.context.domain.DeputyOwner;
import dev.gether.getclan.cmd.context.domain.Member;
import dev.gether.getclan.cmd.context.domain.Owner;
import dev.gether.getclan.placeholder.ClanPlaceholder;
import dev.gether.getclan.ranking.RankingManager;
import dev.gether.getclan.core.alliance.AllianceService;
import dev.gether.getclan.core.clan.ClanService;
import dev.gether.getclan.core.user.UserService;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.stream.Stream;

@Getter
public final class GetClan extends JavaPlugin {


    private static GetClan instance;
    private Economy economy;

    // manager
    private UserManager userManager;
    private ClanManager clanManager;
    private CooldownManager cooldownManager;
    private AllianceManager allianceManager;
    private UpgradeManager upgradeManager;
    private MySQL mySQL;
    private LiteCommands<CommandSender> liteCommands;
    private ClanPlaceholder clanPlaceholder;
    private RankingManager rankingManager;
    private FileManager fileManager;

    @Override
    public void onLoad() {
        instance = this;
        this.fileManager = new FileManager(this);
    }


    @Override
    public void onEnable() {
        // setup the economy plugin VAULT
        if (!setupEconomy()) {
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

        // IMPORTANT: first, we must implement clans, and then users
        ClanService clanService = new ClanService(mySQL, fileManager, this);
        UserService userService = new UserService(mySQL, fileManager);
        AllianceService allianceService = new AllianceService(mySQL);

        // manager
        upgradeManager = new UpgradeManager(fileManager);
        userManager = new UserManager(userService, this, fileManager);
        clanManager = new ClanManager(this, clanService, allianceService, fileManager);
        allianceManager = new AllianceManager(this, allianceService, fileManager);
        cooldownManager = new CooldownManager();


        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            clanPlaceholder = new ClanPlaceholder(this, fileManager, clanManager);
            clanPlaceholder.register();
            MessageUtil.logMessage(ConsoleColor.GREEN, "Successfully implement the placeholders");
        }

        // load data form database
        loadDataMySQL();

        // listeners
        Stream.of(
                new PlayerConnectionListener(this, cooldownManager, clanManager),
                new PlayerDeathListener(this, fileManager),
                new EntityDamageListener(this, fileManager, clanManager),
                new AsyncPlayerChatListener(this, fileManager, clanManager),
                new PlayerInteractionEntityListener(fileManager, userManager, cooldownManager),
                new InventoryClickListener(clanManager, userManager),
                new BreakBlockListener(userManager, clanManager, upgradeManager, fileManager)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));


        // ranking
        rankingManager = new RankingManager(clanManager, this );
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            rankingManager.updateAll(userManager.getUserData().values());
            rankingManager.sort();
        }, 0, 20L * 30);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            MessageUtil.logMessage(ConsoleColor.GREEN, "Starting update data to mysql...");
            userManager.updateUsers();
            clanManager.updateClans();
            mySQL.executeQueued();
        }, 20L * 300, 20L * 300);


        // register cmd
        registerCmd();

        Metrics metrics = new Metrics(this, 19808);

    }

    @Override
    public void onDisable() {


        if (clanPlaceholder != null)
            clanPlaceholder.unregister();

        if (mySQL != null) {
            userManager.updateUsers(); // check users need update
            clanManager.updateClans(); // check clans need update
            mySQL.executeQueued();
            mySQL.disconnect();
        }

        if (liteCommands != null) {
            this.liteCommands.unregister();
        }

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin(CommandSender sender) {
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
        return true;
    }

    private void loadDataMySQL() {

        clanManager.loadClans();
        userManager.loadUsers();
        allianceManager.loadAlliances();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(this, () -> Bukkit.getOnlinePlayers().forEach(player -> userManager.loadUser(player)));
    }


    public void registerCmd() {

        this.liteCommands = LiteBukkitFactory.builder("getclan")
                .commands(
                        fileManager.getConfig().getLangType() == LangType.PL ?
                                new ClanPLCommand(this, fileManager, clanManager, upgradeManager) :
                                new ClanENCommand(this, fileManager, clanManager, upgradeManager),
                        fileManager.getConfig().getLangType() == LangType.PL ?
                                new PlayerPLCommand(this) :
                                new PlayerENCommand(this)
                )

                // contextual bind
                .context(Owner.class, new OwnerContextual(this, fileManager, clanManager))
                .context(DeputyOwner.class, new DeputyOwnerContextual(this, fileManager, clanManager))
                .context(Member.class, new MemberContextual(this, fileManager, clanManager))

                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, fileManager.getLangConfig().getMessage("player-not-found"))

                .argument(Clan.class, new ClanTagArgument(clanManager, fileManager))
                .argument(Owner.class, new OwnerArgument(userManager, fileManager, clanManager))
                .argument(User.class, new UserArgument(userManager, fileManager))

                // handlers
                .missingPermission(new PermissionMessage(fileManager))
                .invalidUsage(new CustomInvalidUsage(fileManager))

                .build();

    }

}
