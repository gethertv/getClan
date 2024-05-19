package dev.gether.getclan.core.alliance;

import dev.gether.getclan.GetClan;
import dev.gether.getclan.config.FileManager;
import dev.gether.getclan.core.clan.Clan;

import java.util.Map;

public class AllianceManager {
    private final GetClan plugin;
    private final AllianceService allianceService;
    public AllianceManager(GetClan plugin, AllianceService allianceService) {
        this.plugin = plugin;
        this.allianceService = allianceService;
    }

    public void loadAlliances() {
        Map<String, String> alliances = allianceService.loadAlliances();
        alliances.forEach((tag1, tag2) -> {
            Clan clan1 = plugin.getClanManager().getClan(tag1);
            Clan clan2 = plugin.getClanManager().getClan(tag2);

            clan1.addAlliance(tag2);
            clan2.addAlliance(tag1);
        });
    }
}
