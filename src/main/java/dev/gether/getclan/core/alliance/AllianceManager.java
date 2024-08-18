package dev.gether.getclan.core.alliance;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.cmd.context.domain.DeputyOwner;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;
import dev.gether.getclan.event.CreateAllianceEvent;
import dev.gether.getclan.event.DisbandAllianceEvent;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class AllianceManager {
    private final GetClan plugin;
    private final AllianceService allianceService;
    private final FileManager fileManager;

    public AllianceManager(GetClan plugin, AllianceService allianceService, FileManager fileManager) {
        this.plugin = plugin;
        this.allianceService = allianceService;
        this.fileManager = fileManager;
    }

    public void loadAlliances() {
        Map<String, String> alliances = allianceService.loadAlliances();
        alliances.forEach((tag1, tag2) -> {
            Clan clan1 = plugin.getClanManager().getClan(tag1);
            Clan clan2 = plugin.getClanManager().getClan(tag2);
            if(clan1 == null || clan2 == null) {
                MessageUtil.logMessage(ConsoleColor.RED, "Something is wrong! Alliance clan doesn't exists! " +tag1 + " or "+tag2);
                return;
            }
            clan1.addAlliance(tag2);
            clan2.addAlliance(tag1);
        });
    }


    public void alliance(DeputyOwner deputyOwner, Clan allianceClan) {
        Clan clan = deputyOwner.getClan();
        Player player = deputyOwner.getPlayer();

        if (plugin.getClanManager().isYourClan(allianceClan, player.getUniqueId())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("cannot-alliance-own-clan"));
            return;
        }
        if (clan.isAlliance(allianceClan.getTag())) {
            DisbandAllianceEvent event = new DisbandAllianceEvent(clan, allianceClan);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // remove alliance from both clan
                clan.removeAlliance(allianceClan.getTag());
                allianceClan.removeAlliance(clan.getTag());
                // add to database
                allianceService.deleteAlliance(clan.getTag());

                // message
                MessageUtil.broadcast(fileManager.getLangConfig().getMessage("alliance-disbanded")
                        .replace("{first-clan}", clan.getTag())
                        .replace("{second-clan}", allianceClan.getTag())
                );
            }
            return;
        }
        // check limit
        if (plugin.getClanManager().isLimitAlliance(clan)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("alliance-limit-reached"));
            return;
        }

        // check if you have already been invited to the alliance.
        if (allianceClan.isSuggestAlliance(clan.getTag())) {
            CreateAllianceEvent event = new CreateAllianceEvent(clan, allianceClan);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // add to both clan alliance
                allianceClan.removeSuggestAlliance(clan.getTag());
                clan.addAlliance(allianceClan.getTag());
                allianceClan.addAlliance(clan.getTag());
                // add to database
                allianceService.createAlliance(clan.getTag(), allianceClan.getTag());

                // message
                MessageUtil.broadcast(fileManager.getLangConfig().getMessage("alliance-formed")
                        .replace("{first-clan}", clan.getTag())
                        .replace("{second-clan}", allianceClan.getTag())
                );
            }
            return;
        }
        if (!clan.isSuggestAlliance(allianceClan.getTag())) {
            clan.inviteAlliance(allianceClan.getTag());
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("suggest-alliance"));
            allianceClan.broadcast(fileManager.getLangConfig().getMessage("get-suggest-alliance")
                    .replace("{tag}", clan.getTag())
            );
        } else {
            clan.removeInviteAlliance(allianceClan.getTag());
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getMessage("cancel-suggest-alliance")
                    .replace("{tag}", allianceClan.getTag())
            );
        }

    }

}
