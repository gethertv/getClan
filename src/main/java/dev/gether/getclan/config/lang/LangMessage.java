package dev.gether.getclan.config.lang;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.exception.OkaeriException;

import java.util.Arrays;
import java.util.List;

public class LangMessage extends OkaeriConfig {

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
    public String langNoDeputy = "&4⚐ | &cTwój klan nie posiada zastępcy lidera!";
    public String langBroadcastDeleteClan = "&4⚐ | &cKlan &8[&4{tag}&8] &czostał usunięty przez &4{player}&c!";
    public String langBroadcastCreateClan = "&2⚐ | &aKlan &8[&2{tag}&8] &azostał utworzony przez &2{player}&a!";
    public String langBroadcastDeathInfo = "&4☠ | &6{victim}&8(&c-{victim-points}&8) &7został zabity przez &6{killer}&8(&a+{killer-points}&8)";
    public String langBroadcastDeathNoVictimInfo = "&4☠ | &6{victim}&7 popełnił samobójstwo!";
    public String langBroadcastCreateAlliance = "#008cff⚐ | #2eb2ffKlan &8[#008cff{first-clan}&8] #2eb2ffzawarł sojusz z &8[#008cff{second-clan}&8]";
    public String langBroadcastDisbandAlliance = "#008cff⚐ | #2eb2ffKlan &8[#008cff{first-clan}&8] #2eb2ffzerwał sojusz z &8[#008cff{second-clan}&8]";
    public String langNoPermission = "&4⚐ | &cBrak uprawnień! &8(&f{permission}&8)";
    public String langClanPvpEnable = "&4⚐ | &cPvp dla klanu zostało włączone";
    public String langClanPvpDisable = "&4⚐ | &cPvp dla klanu zostało wyłączone";
    public String langAdminHasClan = "&4⚐ | &cPodany gracz posiada klan!";
    public String langAdminDeleteClan = "&4⚐ | &cPomyślnie usunięto klan!";

    public String langAdminUserNoClan = "&4⚐ | &cPodany gracz nie posiada klanu!";
    public String langadminSuccessfullySetOwner = "&4⚐ | &cPomyślnie ustawiono gracza liderem klanu!";
    public List<String> langInfoClan = Arrays.asList(
            "&7",
            "&2⚐ | &aKlan: &8[&2{tag}&8]",
            "&2⚐ | &aPunkty: &8[&2{points}&8] &f{rank}",
            "&2⚐ | &aLider: &2{owner}",
            "&2⚐ | &aZastępca: &2{deputy-owner}",
            "&2⚐ | &aCzłonkowie &8(&a{members-online}&8/&a{members-size}&8) - &2{members}",
            "&7"
    );

    public List<String> langInfoUser = Arrays.asList(
            "&7",
            "&2⚐ | &aNazwa: &2{player}",
            "&2⚐ | &aZabójstwa: &2{kills}",
            "&2⚐ | &aŚmierci: &2{deaths}",
            "&2⚐ | &aPunkty: &2{points} &8(&f#{rank}&8)",
            "&2⚐ | &aKlan: {tag}",
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
            "&8» &6/gracz [nazwa] &8- &7Informacje o graczu.",
            "&8",
            "&6!<wiadomosc> &8- &7Czat dla klanu",
            "&6!!<wiadomosc> &8- &7Czat dla sojuszników",
            "&7");

    public OkaeriConfig load() throws OkaeriException {

//        if(GetClan.getInstance().getConfigPlugin().langType != LangType.EN)
//            return this.load(this.getBindFile());

        langMinMaxTag = "&4⚐ | &cx Clan name can have from &4{min-length}&c to &4{max-length} &ccharacters!";
        langTagIsBusy = "&4⚐ | &cA clan with this name already exists!";
        langPlayerNotOnline = "&4⚐ | &cPlayer with this name was not found!";
        langInvitedPlayerHasClan = "&4⚐ | &cThe specified player already has a clan!";
        langCannotKickYourSelf = "&4⚐ | &cYou cannot kick yourself!";
        langCannotKickOwner = "&4⚐ | &cYou cannot kick the clan leader!";
        langLimitMembers = "&4⚐ | &cThe clan has exceeded the member limit!";
        langInvalidCharacter = "&4⚐ | &cThe tag contains illegal characters!";
        langCancelInvite = "&2⚐ | &aInvitation for player &2{player} &ahas been canceled!";
        langInvitedPlayer = "&2⚐ | &aYou have invited player &2{player} &ato the clan!";
        langChangeOwner = "&2⚐ | &aThe clan leader has been transferred!";
        cooldownKill = "&4⚐ | &cYou must wait &4{time} minutes &cbefore killing this player.";
        langLimitAlliance = "&4⚐ | &cYou cannot have more alliances!";
        langNoClan = "&4⚐ | &cYou don't have a clan!";
        langGetInvitation = "&2⚐ | &aYou have received an invitation to clan &8[&2{tag}&8]\n&2⚐ | &aTo accept, type &2/klan join {tag}";
        langNotOwnerClan = "&4⚐ | &cYou are not the clan leader!";
        langCannotAllianceYourClan = "&4⚐ | &cYou cannot form an alliance with your own clan!";
        langHasClan = "&4⚐ | &cYou already have a clan!";
        noMoney = "&4⚐ | &cYou need &4{cost} &cto create a clan!";
        noItem = "&4⚐ | &cYou need &4{amount}x Stone &cto create a clan!";
        langClanNotExists = "&4⚐ | &cThe specified clan does not exist!";
        langNoInvited = "&4⚐ | &cYou don't have an invitation to this clan!";
        langSuccessfullyJoined = "&2⚐ | &aYou have joined the clan!";
        langPlayerNotYourClan = "&4⚐ | &cThe player is not a member of your clan!";
        langSuccessfullyKicked = "&2⚐ | &aThe player has been kicked from the clan.";
        langSuggestAlliance = "&2⚐ | &aYou have sent an alliance proposal.";
        langGetSuggestAlliance = "&2⚐ | &aYou have received an alliance proposal from &8[{tag}&8]\n&2⚐ | &aTo accept, type &2/klan alliance {tag}";
        langCancelSuggestAlliance = "&4⚐ | &cThe alliance proposal for clan &8[{tag}&8] has been canceled.";
        langCannotChangeToYourSelf = "&4⚐ | &cYou cannot use this command on yourself!";
        langSuccessfullyLeaved = "&2⚐ | &aYou have left the clan!";
        langOwnerCannotLeave = "&4⚐ | &cYou are the clan leader, so you cannot leave it!\n&4⚐ | &cTo do that, you must transfer leadership to someone else or disband the clan.";
        langDeputyDelete = "&4⚐ | &cThe deputy leader has been removed!";
        langSetDeputyOwner = "&2⚐ | &aThe deputy leader has been set!";
        langIsDeputy = "&4⚐ | &cThe specified player is already a deputy leader!";
        langBroadcastDeleteClan = "&4⚐ | &cClan &8[{tag}&8] &chas been disbanded by &4{player}&c!";
        langBroadcastCreateClan = "&2⚐ | &aClan &8[{tag}&8] &ahas been created by &2{player}&a!";
        langBroadcastDeathInfo = "&4☠ | &6{victim}&8(&c-{victim-points}&8) &7was killed by &6{killer}&8(&a+{killer-points}&8)";
        langBroadcastDeathNoVictimInfo = "&4☠ | &6{victim}&7 committed suicide!";
        langNoDeputy = "&4☠ | &cYour clan does not have a deputy leader!";
        langBroadcastCreateAlliance = "#008cff⚐ | #2eb2ffClan &8[#008cff{first-clan}&8] #2eb2ffhas formed an alliance with &8[#008cff{second-clan}&8]";
        langBroadcastDisbandAlliance = "#008cff⚐ | #2eb2ffClan &8[#008cff{first-clan}&8] #2eb2ffhas broken the alliance with &8[#008cff{second-clan}&8]";
        langNoPermission = "&4⚐ | &cInsufficient permissions! &8(&f{permission}&8)";
        langClanPvpEnable = "&4⚐ | &cClan PvP has been turned on";
        langClanPvpDisable = "&4⚐ | &cClan PvP has been turned off";
        langAdminUserNoClan = "&4⚐ | &cThe user has no clan!";
        langAdminHasClan = "&4⚐ | &cThis player has the clan";
        langadminSuccessfullySetOwner = "&4⚐ | &cSuccessfully set new player leader!";
        langAdminDeleteClan = "&4⚐ | &cSuccessfully deleted the clan!";
        langInfoClan = Arrays.asList(
                "&7",
                "&2⚐ | &aClan: &8[&2{tag}&8]",
                "&2⚐ | &aPoints: &8[&2{points}&8] &f{rank}",
                "&2⚐ | &aLeader: &2{owner}",
                "&2⚐ | &aDeputy: &2{deputy-owner}",
                "&2⚐ | &aMembers &8(&a{members-online}&8/&a{members-size}&8) - &2{members}",
                "&7"
        );
        langInfoUser = Arrays.asList(
                "&7",
                "&2⚐ | &aName: &2{player}",
                "&2⚐ | &aKills: &2{kills}",
                "&2⚐ | &aDeaths: &2{deaths}",
                "&2⚐ | &aClan: {tag}",
                "&7"
        );
        langUsageCmd = "&7Usage: &6{usage}";
        langUsageList = List.of(
                "&7",
                "&c&lClan Command Center:",
                "&8» &6/clan create [tag] &8- &7Creates a clan.",
                "&8» &6/clan join [tag] &8- &7Joins a clan.",
                "&8» &6/clan leave &8- &7Leaves the current clan.",
                "&8» &6/clan info [tag] &8- &7Shows clan info.",
                "&8» &6/clan alliance [tag] &8- &7Sends an alliance request.",
                "&8» &6/clan invite [nickname] &8- &7Invites a player to the clan.",
                "&8» &6/clan kick [nickname] &8- &7Kicks a player from the clan.",
                "&8» &6/clan setleader [nickname] &8- &7Changes the clan leader.",
                "&8» &6/clan setdeputy [nickname] &8- &7Sets a clan deputy.",
                "&8» &6/clan removedeputy &8- &7Removes the clan deputy.",
                "&8» &6/clan delete &8- &7Dissolves the current clan.",
                "&8",
                "&8» &6/player [name] &8- &7Player information.",
                "&8",
                "&6!<message> &8- &7Clan chat",
                "&6!!<message> &8- &7Alliance chat",
                "&7");




        return this.load(this.getBindFile());

    }



}
