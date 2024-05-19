package dev.gether.getclan.handler.contextual;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.model.role.DeputyOwner;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeputyOwnerContextual implements ContextProvider<CommandSender, DeputyOwner> {


    private final GetClan plugin;
    private final FileManager fileManager;
    private final ClanManager clanManager;

    public DeputyOwnerContextual(GetClan plugin, FileManager fileManager, ClanManager clanManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    @Override
    public ContextResult<DeputyOwner> provide(Invocation<CommandSender> invocation) {
        if(!(invocation.sender() instanceof Player player)) {
            return ContextResult.error(fileManager.getLangConfig().getMessage("player-not-found"));
        }

        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        if (!user.hasClan()) {
            return ContextResult.error(fileManager.getLangConfig().getMessage("player-has-no-clan"));
        }
        Clan clan = clanManager.getClan(user.getTag());
        if (!clan.isOwner(player.getUniqueId())) {
            if (!clan.isDeputy(player.getUniqueId())) {
                return ContextResult.error(fileManager.getLangConfig().getMessage("not-clan-owner"));
            }
        }

        return ContextResult.ok(() -> new DeputyOwner(player, clan));
    }
}