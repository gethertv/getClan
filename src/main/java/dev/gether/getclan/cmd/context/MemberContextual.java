package dev.gether.getclan.cmd.context;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.core.clan.ClanManager;
import dev.gether.getclan.core.user.User;
import dev.gether.getclan.core.user.UserManager;
import dev.gether.getclan.cmd.context.domain.Member;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemberContextual implements ContextProvider<CommandSender, Member> {


    private final GetClan getClan;
    private final FileManager fileManager;
    private final ClanManager clanManager;

    public MemberContextual(GetClan getClan, FileManager fileManager, ClanManager clanManager) {
        this.getClan = getClan;
        this.fileManager = fileManager;
        this.clanManager = clanManager;
    }

    @Override
    public ContextResult<Member> provide(Invocation<CommandSender> invocation) {
        if(!(invocation.sender() instanceof Player player)) {
            return ContextResult.error(fileManager.getLangConfig().getMessage("player-not-found"));
        }
        UserManager userManager = getClan.getUserManager();
        User user = userManager.getUserData().get(player.getUniqueId());

        Clan clan = clanManager.getClan(user.getTag());
        if(!user.hasClan()) {
            return ContextResult.error(fileManager.getLangConfig().getMessage("player-has-no-clan"));
        }

        return ContextResult.ok(() -> new Member(player, clan));
    }
}