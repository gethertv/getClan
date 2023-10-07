package dev.gether.getclan.config;

import dev.gether.getclan.config.lang.LangType;
import dev.gether.getclan.model.CostType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Header("Konfiguracja getClan")
public class Config extends OkaeriConfig {

    @Comment("Language | Język  [PL, EN]")
    @Comment("Language | Language selection [PL, EN]")
    public LangType langType = LangType.EN;
    @Comment("Domyślna ilość punktów dla gracza")
    @Comment("Default points for players")
    public int defaultPoints = 500;
    @Comment("Limit sojusznikow dla klanu")
    @Comment("Alliance limit for clans")
    public int limitAlliance = 2;
    @Comment("Czy ma być włączone pvp dla klanu")
    @Comment("Enable clan PvP")
    public boolean pvpClan = true;
    @Comment("Czy ma być włączone pvp dla sojuszników")
    @Comment("Enable alliance PvP")
    public boolean pvpAlliance = true;

    @Comment("Czy ma być włączona wiadomość po śmierci")
    @Comment("Enable broadcast message after the death")
    public boolean deathMessage = true;
    @Comment("Limit osób w klanie ")
    @Comment("Clan member limit")
    public Map<String, Integer> permissionLimitMember = Map.of("getclan.default", 5);
    @Comment("System Antiabuse (Nabijanie rankingu)")
    @Comment("Anti-abuse system (Prevent ranking abuse)")
    public boolean systemAntiabuse = true;
    @Comment("Co ile sekund nadal mozna zabic tego samego gracza")
    @Comment("Cooldown time in seconds for killing the same player")
    public int cooldown = 300;

    @Comment("Czy wlaczyc platne tworzenie klanu (true | false)")
    @Comment("Enable paid clan creation (true | false)")
    public boolean enablePayment = true;
    @Comment("Jezeli opcja powyzej jest wlaczona to jaka metoda platnosci (ITEM|VAULT)")
    @Comment("If the above option is enabled, specify payment method (ITEM|VAULT)")
    public CostType costType = CostType.VAULT;
    @Comment("Koszt - VAULT | ITEM")
    @Comment("Cost - VAULT | ITEM")
    public double costCreate = 10;
    public ItemStack itemCost = new ItemStack(Material.STONE);
    @Comment("Ograniczenia długości tagu klanu")
    @Comment("Clan tag length restrictions")
    public int clansTagLengthMin = 2;
    public int clansTagLengthMax = 6;
    public String colorOnlinePlayer = "&a";
    public String colorOfflinePlayer = "&7";

    @Comment("Gdy zastępca lidera nie jest ustawiony")
    @Comment("When deputy leader is not set")
    public String noneDeputy = "Brak";

    @Comment("Placeholder FORMAT kiedy nie posiadasz TAGU klanu")
    @Comment("Placeholder format when you don't have a clan TAG")
    public String noneTag = "Brak";
    @Comment("Placeholder FORMAT kiedy nie posidasz punktów klanu")
    @Comment("Placeholder format when you don't have clan points")
    public String nonePointsClan = "Brak";
    @Comment("[%getclan_user_format_points%] Format dla punktów gracza")
    @Comment("[%getclan_user_format_points%] Format for player points")
    public String formatUserPoints = "&7[&f{points}&7]";
    @Comment("[%getclan_clan_format_points%] Format dla punktów klanu")
    @Comment("[%getclan_clan_format_points%] Format for clan points")
    public String formatClanPoints = "&7[&f{points}&7]";
    @Comment("[%getclan_clan_format_tag%] Format dla tagu")
    @Comment("[%getclan_clan_format_tag%] Format for clan tag")
    public String formatTag = "&7[&f{tag}&7]";
    @Comment("[%rel_getclan_tag%] Format dla sojuszników")
    @Comment("[%rel_getclan_tag%] Format for alliances")
    public String formatAlliance = "#147aff{tag}";
    @Comment("[%rel_getclan_tag%] Format dla członka z klanu")
    @Comment("[%rel_getclan_tag%] Format for clan members")
    public String formatMember = "#48ff05{tag}";

    @Comment("[%rel_getclan_tag%] Format normalny dla tagu | {tag} {player}")
    @Comment("Normal format for tags | {tag} {player}")
    public String formatNormal = "&c{tag}";

    @Comment("Format do wiadomości wysłanej do gildii | {tag} {player}")
    @Comment("Format for messages sent to the clan | {tag} {player}")
    public String formatClanMessage = "&6{player} -> ⚐ | &e{message}";

    @Comment("Format do wiadomości wysłanej do sojuszników | {tag} {player}")
    @Comment("Format for messages sent to alliances | {tag} {player}")
    public String formatAllianceMessage = "#006eff{player} -> ⚐ | #78d4ff{message}";
}

