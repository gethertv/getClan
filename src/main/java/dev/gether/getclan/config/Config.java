package dev.gether.getclan.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dev.gether.getclan.model.CostType;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Header("Konfiguracja getClan")
public class Config extends OkaeriConfig {

    @Comment("Domyślna ilość punktów dla gracza")
    public int defaultPoints = 500;
    @Comment("Limit sojusznikow dla klanu")
    public int limitAlliance = 2;
    @Comment("Czy ma być włączone pvp dla klanu")
    public boolean pvpClan = true;
    @Comment("Czy ma być włączone pvp dla sojuszników")
    public boolean pvpAlliance = true;

    @Comment("Limit osób w klanie ")
    public Map<String, Integer> permissionLimitMember = Map.of("getclan.default", 5);
    @Comment("System Antiabuse (Nabijanie rankingu)")
    public boolean systemAntiabuse = true;
    @Comment("Co ile sekund nadal mozna zabic tego samego gracza")
    public int cooldown = 300;
    @Comment("Czy wlaczyc platne tworzenie klanu (true | false)")
    public boolean enablePayment = true;
    @Comment("Jezeli opcja powyzej jest wlaczona to jaka metoda platnosci (ITEM|VAULT)")
    public CostType costType = CostType.VAULT;
    @Comment("Koszt - VAULT | ITEM")
    public double costCreate = 10;
    public ItemStack itemCost = new ItemStack(Material.STONE);
    @Comment("Ograniczenia długości tagu klanu")
    public int clansTagLengthMin = 2;
    public int clansTagLengthMax = 6;
    public String colorOnlinePlayer = "&a";
    public String colorOfflinePlayer = "&7";

    @Comment("Placeholder gdy zastępca lidera nie jest ustawiony")
    public String noneDeputy = "Brak";

    @Comment("Placeholder kiedy nie posiadasz TAGU klanu")
    public String noneTag = "Brak";
    @Comment("[%getclan_user_points%] Format dla punktów gracza")
    public String formatUserPoints = "&7[&f{points}&7]";
    @Comment("[%getclan_clan_points%] Format dla punktów klanu")
    public String formatClanPoints = "&7[&f{points}&7]";
    @Comment("[%getclan_clan_tag%] Format dla tagu")
    public String formatTag = "&7[&f{tag}&7]";
    @Comment("[%rel_getclan_tag%] Format dla sojuszników")
    public String formatAlliance = "#147aff{tag}";
    @Comment("[%rel_getclan_tag%] Format dla członka z klanu")
    public String formatMember = "#48ff05{tag}";

    @Comment("[%rel_getclan_tag%] Format normalny dla tagu | {tag} {player}")
    public String formatNormal = "{tag}";

    @Comment("Format do wiadomosci wyslanej do gildii | {tag} {player}")
    public String formatClanMessage = "&6{player} -> ⚐ | &e{message}";

    @Comment("Format do wiadomosci wyslanej do sojusznikow")
    public String formatAllianceMessage = "#006eff{player} -> ⚐ | #78d4ff{message}";
    @Comment("Komunikaty językowe")
    public String langMinMaxTag = "&4⚐ | &cNazwa klanu może mieć od &4{min-length}&c do &4{max-length} &cliter!";
    public String langTagIsBusy = "&4⚐ | &cKlan o podanej nazwie już istnieje!";
    public String langPlayerNotOnline = "&4⚐ | &cGracz o podanej nazwie nie został odnaleziony!";
    public String langInvitedPlayerHasClan = "&4⚐ | &cPodany gracz posiada już klan!";
    public String langCannotKickYourSelf = "&4⚐ | &cNie możesz wyrzucić samego siebie!";
    public String langCannotKickOwner = "&4⚐ | &cNie możesz wyrzucić lidera klanu!";
    public String langLimitMembers = "&4⚐ | &cPrzekroczono limit członków w klanie!";
    public String langInvalidCharacter = "&4⚐ | &cTag zawiera niedozwolone znaki!";
    public String langCancelInvite = "&2⚐ | &aZaproszenie dla gracza &2{player} &azostało anulowane!";
    public String langInvitedPlayer = "&2⚐ | &aZaprosiłeś gracza &2{player} &ado klanu!";
    public String langChangeOwner = "&2⚐ | &aPrzekazano lidera!";
    public String cooldownKill = "&4⚐ | &cMusisz odczekać &4{time}min &caby zabić tego gracza.";
    public String langLimitAlliance = "&4⚐ | &cNie możesz mieć więcej sojuszników!";
    public String langNoClan = "&4⚐ | &cNie posiadasz klanu!";
    public String langGetInvitation = "&2⚐ | &aOtrzymałeś zaproszenie do klanu &8[&2{tag}&8]\n&2⚐ | &aAby zaakceptować wpisz &2/klan dolacz {tag}";
    public String langNotOwnerClan = "&4⚐ | &cNie jesteś liderem klanu!";
    public String langCannotAllianceYourClan = "&4⚐ | &cNie możesz zawrzeć sojuszu ze swoim klanem!";
    public String langHasClan = "&4⚐ | &cPosiadasz już klan!";
    public String noMoney = "&4⚐ | &cPotrzebujesz &4{cost}$ &caby zalożyć klan!";
    public String noItem = "&4⚐ | &cPotrzebujesz &4{amount}x Kamień &caby zalożyć klan!";
    public String langClanNotExists = "&4⚐ | &cPodany klan nie istnieje!";
    public String langNoInvited = "&4⚐ | &cNie posiadasz zaproszenia do tego klanu!";
    public String langSuccessfullyJoined = "&2⚐ | &aDołączyłeś do klanu!";
    public String langPlayerNotYourClan = "&4⚐ | &cGracz nie jest członkiem twojego klanu!";
    public String langSuccessfullyKicked = "&2⚐ | &aWyrzucono gracza z klanu.";
    public String langSuggestAlliance = "&2⚐ | &aWysłano propozycje o sojusz.";
    public String langGetSuggestAlliance = "&2⚐ | &aOtrzymano propozycje o sojusz od &8[{tag}&8]\n&2⚐ | &aAby zaakceptować sojusz wpisz &2/klan sojusz {tag}";
    public String langCancelSuggestAlliance = "&4⚐ | &cAnulowano propozycje o sojusz dla klanu &8[&4{tag}&8]";
    public String langCannotChangeToYourSelf = "&4⚐ | &cNie możesz tej komendy zastosować na sobie!";
    public String langSuccessfullyLeaved = "&2⚐ | &aOpuściłeś klan!";
    public String langOwnerCannotLeave = "&4⚐ | &cJesteś liderem klanu, więc nie możesz go opuścić!\n&4⚐ | &cAby to zrobić musisz do przekazać innej osobie, albo usunąć.";
    public String langDeputyDelete = "&4⚐ | &cUsunięto zestępce lidera!";
    public String langSetDeputyOwner = "&2⚐ | &aUstawiono zestępce lidera!";
    public String langIsDeputy = "&4⚐ | &cPodany gracz jest już zastępcą lidera!";
    public String langBroadcastDeleteClan = "&4⚐ | &cKlan &8[&4{tag}&8] &czostał usunięty przez &4{player}&c!";
    public String langBroadcastCreateClan = "&2⚐ | &aKlan &8[&2{tag}&8] &azostał utworzony przez &2{player}&a!";
    public String langBroadcastDeathInfo = "&4☠ | &6{victim}&8(&c-{victim-points}&8) &7został zabity przez &6{killer}&8(&a+{killer-points}&8)";
    public String langBroadcastCreateAlliance = "#008cff⚐ | #2eb2ffKlan &8[#008cff{first-clan}&8] #2eb2ffzawarł sojusz z &8[#008cff{second-clan}&8]";
    public String langBroadcastDisbandAlliance = "#008cff⚐ | #2eb2ffKlan &8[#008cff{first-clan}&8] #2eb2ffzerwał sojusz z &8[#008cff{second-clan}&8]";
    public String langNoPermission = "&4⚐ | &cBrak uprawnień! &8(&f{permission}&8)";
    public List<String> langInfoClan = Arrays.asList(
                                                "&7",
                                                "&2⚐ | &aKlan: &8[&2{tag}&8]",
                                                "&2⚐ | &aPunkty: &8[&2{points}&8] &f{rank}",
                                                "&2⚐ | &aLider: &2{owner}",
                                                "&2⚐ | &aZastępca: &2{deputy-owner}",
                                                "&2⚐ | &aCzłonkowie &8(&a{members-online}&8/&a{members-size}&8) - &2{members}",
                                                "&7"
    );

    @Comment("Komunikaty dotyczące użycia komendy")
    public String langUsageCmd  = "&7Prawidłowe użycie: &6{usage}";
    @Comment("Komunikaty dotyczące użycia komend")
    public List<String> langUsageList = List.of(
                                            "&7",
                                            "&c&lCentrum pomocy klanów:",
                                            "&8» &6/klan stworz [tag] &8- &7Tworzy klan.",
                                            "&8» &6/klan dolacz [tag] &8- &7Dołącza do klanu",
                                            "&8» &6/klan opusc &8- &7Opuszcza aktualny klan.",
                                            "&8» &6/klan info [tag] &8- &7Pokazuje info o klanie.",
                                            "&8» &6/klan sojusz [tag] &8- &7Wysyła prośbę o sojusz.",
                                            "&8» &6/klan zapros [nick] &8- &7Zaprasza gracza do klanu.",
                                            "&8» &6/klan wyrzuc [nick] &8- &7Wyrzuca gracza z klanu.",
                                            "&8» &6/klan ustawlidera [nick] &8- &7Zmienia lidera klanu.",
                                            "&8» &6/klan ustawzestepce [nick] &8- &7Ustawia zastępce klanu.",
                                            "&8» &6/klan usunzastepce &8- &7Usuwa zastępce klanu.",
                                            "&8» &6/klan usun &8- &7Rozwiązuje aktualny klan.",
                                            "&8",
                                            "&6!<wiadomosc> &8- &7Czat dla klanu",
                                            "&6!!<wiadomosc> &8- &7Czat dla sojuszników",
                                            "&7");


}

