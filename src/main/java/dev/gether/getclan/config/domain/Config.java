package dev.gether.getclan.config.domain;

import dev.gether.getclan.core.CostType;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


@Getter
@Setter
public class Config extends GetConfig {

    @Comment({"Language | Język  [PL, EN]", "Language | Language selection [PL, EN]"})
    private LangType langType = LangType.EN;

    @Comment({"Napis na środku ekranu po śmierci", "Show title after death"})
    private boolean titleAlert = true;
    private int fadeIn = 10;
    private int stay = 40;
    private int fadeOut = 10;

    @Comment({"System punktacji", "Points conversion system"})
    private String calcPoints = "{old_rating} + (30 * ({score} - (1 / (1 + pow(10, ({opponent_rating} - {old_rating}) / 400)))))";
    @Comment({"Domyślna ilość punktów dla gracza", "Default points for players"})
    private int defaultPoints = 500;
    @Comment({"Limit sojusznikow dla klanu", "Alliance limit for clans"})
    private int limitAlliance = 2;
    @Comment({"Czy ma być włączone pvp dla klanu", "Enable clan PvP"})
    private boolean pvpClan = true;
    @Comment({"Czy ma być włączone pvp dla sojuszników", "Enable alliance PvP"})
    private boolean pvpAlliance = true;
    @Comment({"Czy ma być włączona wiadomość po śmierci", "Enable broadcast message after the death"})
    private boolean deathMessage = true;
    @Comment({"System Antiabuse (Nabijanie rankingu)", "Anti-abuse system (Prevent ranking abuse)"})
    private boolean systemAntiabuse = true;
    @Comment({"Co ile sekund nadal mozna zabic tego samego gracza", "Cooldown time in seconds for killing the same player"})
    private int cooldown = 300;
    @Comment({"Czy wlaczyc platne tworzenie klanu (true | false)", "Enable paid clan creation (true | false)"})
    private boolean enablePayment = true;
    @Comment({"Jezeli opcja powyzej jest wlaczona to jaka metoda platnosci (ITEM|VAULT)", "If the above option is enabled, specify payment method (ITEM|VAULT)"})
    private CostType costType = CostType.VAULT;
    @Comment({"Koszt - VAULT | ITEM", "Cost - VAULT | ITEM"})
    private double costCreate = 10;
    private ItemStack itemCost = new ItemStack(Material.STONE);
    @Comment("ilość osób potrzebnych do liczenia rankingu klanu")
    private int membersRequiredForRanking = 3;
    @Comment("co ma zwracac placeholder gdy klan nie posida tylu czonkow do liczenia pkt")
    private String placeholderNeedMembers = "&eneed 3 users";
    @Comment({"Limit osób w klanie", "Clan member limit"})
    public Map<String, Integer> permissionLimitMember = Map.of("getclan.default", 5);
    @Comment({"Ograniczenia długości tagu klanu", "Clan tag length restrictions"})
    private int clansTagLengthMin = 2;
    private int clansTagLengthMax = 6;
    private String colorOnlinePlayer = "&a";
    private String colorOfflinePlayer = "&7";

    @Comment({"Gdy zastępca lidera nie jest ustawiony", "When deputy leader is not set"})
    private String noneDeputy = "None";

    @Comment({"Placeholder %getclan_user_has_clan% kiedy nie/posiadasz klanu", "Placeholder %getclan_user_has_clan% when you have/not a clan"})
    private String hasNotClan = "false";
    private String hasClan = "true";

    @Comment({"Placeholder FORMAT kiedy nie posiadasz TAGU klanu", "Placeholder format when you don't have a clan TAG"})
    private String noneTag = "None";
    @Comment({"Placeholder FORMAT kiedy nie posidasz punktów klanu", "Placeholder format when you don't have clan points"})
    private String nonePointsClan = "None";
    @Comment({"[%getclan_user_format_points%] Format dla punktów gracza", "[%getclan_user_format_points%] Format for player points"})
    private String formatUserPoints = "&7[&f{points}&7]";
    @Comment({"[%getclan_clan_format_points%] Format dla punktów klanu", "[%getclan_clan_format_points%] Format for clan points"})
    private String formatClanPoints = "&7[&f{points}&7]";
    @Comment({"[%getclan_clan_format_tag%] Format dla tagu", "[%getclan_clan_format_tag%] Format for clan tag"})
    private String formatTag = "&7[&f{tag}&7]";
    @Comment({"[%rel_getclan_tag%] Format dla sojuszników", "[%rel_getclan_tag%] Format for alliances"})
    private String formatAlliance = "#147aff{tag}";
    @Comment({"[%rel_getclan_tag%] Format dla członka z klanu", "[%rel_getclan_tag%] Format for clan members"})
    private String formatMember = "#48ff05{tag}";

    @Comment({"[%rel_getclan_tag%] Format normalny dla tagu | {tag} {player}", "Normal format for tags | {tag} {player}"})
    private String formatNormal = "&c{tag}";

    @Comment({"Format do wiadomości wysłanej do gildii | {tag} {player}", "Format for messages sent to the clan | {tag} {player}"})
    private String formatClanMessage = "&6{player} -> ⚐ | &e{message}";

    @Comment({"Format do wiadomości wysłanej do sojuszników | {tag} {player}", "Format for messages sent to alliances | {tag} {player}"})
    private String formatAllianceMessage = "#006eff{player} -> ⚐ | #78d4ff{message}";
}

