package dev.gether.getclans.config;

import java.util.List;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.OkaeriConfig;

@Header("Konfiguracja GetClans")
public class Config extends OkaeriConfig {

    @Comment("Limit członków w klanie")
    public int limitMemberClan = 50;

    @Comment("Ograniczenia długości tagu klanu")
    public int clansTagLengthMin = 2;
    public int clansTagLengthMax = 5;

    @Comment("Ograniczenia długości nazwy klanu")
    public int clansNameLengthMin = 3;
    public int clansNameLengthMax = 15;

    @Comment("Komunikaty językowe")
    public String langAlreadyHasClan = "Już masz klan.";
    public String langMinTag = "Tag jest za krótki.";
    public String langMaxTag = "Tag jest za długi.";
    public String langTagIsBusy = "Ten tag jest już zajęty.";
    public String langNameIsBusy = "Ta nazwa jest już zajęta.";
    public String langSuccessfullyCreated = "Klan został pomyślnie utworzony.";
    public String langSuccessfullyDeleted = "Klan został pomyślnie usunięty.";
    public String langPlayerNotOnline = "Gracz nie jest online.";
    public String langInvitedPlayerHasClan = "Zaproszony gracz już ma klan.";
    public String langIsInvited = "Zostałeś zaproszony.";
    public String langInvitedPlayer = "Zaprosiłeś gracza.";
    public String langNoClan = "Nie masz klanu.";
    public String langGetInvitation = "Otrzymałeś zaproszenie.";
    public String langNotOwnerClan = "Nie jesteś właścicielem klanu.";
    public String langHasClan = "Masz już klan.";
    public String langClanNotExists = "Taki klan nie istnieje.";
    public String langNoInvited = "Nie otrzymałeś zaproszenia.";
    public String langSuccessfullyJoined = "Dołączyłeś do klanu.";
    public String langPlayerNotYourClan = "Gracz nie należy do twojego klanu.";
    public String langPlayerHasNoClan = "Gracz nie posiada klanu!";
    public String langSuccessfullyKicked = "Gracz został wyrzucony z klanu.";
    public String langAreKicked = "Zostałeś wyrzucony z klanu.";
    public String langSuccessfullyLeaved = "Opuściłeś klan.";
    public String langOwnerCannotLeave = "Właściciel nie może opuścić klanu.";
    public String langOwnerCannotBeKicked = "Właściciel nie może być wyrzucony z klanu.";
    public String langBroadcastDeleteClan = "Klan został usunięty.";
    public String langBroadcastCreateClan = "Nowy klan został utworzony.";
    public String langInfoKickedPlayer = "Gracz został wyrzucony.";
    public String langInfoJoinedPlayer = "Gracz dołączył do klanu.";
    public String langInfoLeavedPlayer = "Gracz opuścił klan.";
    public String langInfoDestroyedClan = "Klan został zniszczony.";
    public String langBroadcastDeathInfo = "Informacja o śmierci w klanie.";

    @Comment("Komunikaty dotyczące użycia")
    public List<String> langUsage = List.of("#1 LINIA", "#2 LINIA", "#3 LINIA");

    @Comment("Domyślna ilość punktów dla gracza")
    public int defaultPoints = 0;
}

