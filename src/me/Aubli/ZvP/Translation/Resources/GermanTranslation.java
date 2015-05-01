package me.Aubli.ZvP.Translation.Resources;

import java.util.ListResourceBundle;
import java.util.Locale;

import org.bukkit.ChatColor;


public class GermanTranslation extends ListResourceBundle {
    
    @Override
    public Locale getLocale() {
	return Locale.GERMAN;
    }
    
    @Override
    protected Object[][] getContents() {
	return this.contents;
    }
    
    //@formatter:off
    private Object[][] contents = {
	    
	    {"config:reloaded", ChatColor.GREEN + "Konfigurationsdatei erfolgreich neu geladen!"},
	    {"commands:missing_Permission", ChatColor.DARK_RED + "You do not have enough permission for that!"},
	    {"commands:only_for_Spielers", "Only Spielers can use this command!"},
	    
	    {"game:waiting", ChatColor.DARK_GRAY + "Auf Spieler warten ..."},
	    {"game:joined", ChatColor.GREEN + "Du bist Arena " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " beigetreten!"},
	    {"game:left", ChatColor.GREEN + "Du hast Arena " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " verlassen!"},
	    {"game:player_left", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat das Spiel verlassen!"},
	    {"game:player_joined", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ist dem Spiel beigetreten!"},
	    {"game:player_not_found", ChatColor.RED + "Du wurdest in keinem Spiel gefunden! (Wahrscheinlich ein Pluginfehler)!"},
	    {"game:player_died", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ist gestorben!"},
	    {"game:player_bought", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gekauft!"},
	    {"game:player_bought_more", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gekauft!"},
	    {"game:player_sold", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " verkauft!"},
	    {"game:player_sold_more", ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " verkauft!"},
	    {"game:player_bought_kit", ChatColor.GREEN + "Du hast das " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Kit für " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "gekauft! Du hast " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " übrig!"},
	    {"game:no_money", ChatColor.RED + "Du besitzt nicht genügend Geld!"},
	    {"game:no_item_to_sell", ChatColor.RED + "Du besitzt diesen Gegenstand nicht!"},
	    {"game:wrong_inventory", ChatColor.RED + "Du hast in das falsche Inventar geklickt! Versuchs im obrigen!"},
	    {"game:not_in_game", ChatColor.RED + "Du bist nicht im Spiel!"},
	    {"game:already_in_game", ChatColor.RED + "Du bist bereits im Spiel!"},
	    {"game:vote_request", ChatColor.DARK_PURPLE + "Tippe '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' in das chat fenster um für die nächste Welle abzustimmen!"},
	    {"game:voted_next_round", ChatColor.GREEN + "Du hast für die nächste Welle gestimmt!"}, 
	    {"game:already_voted", ChatColor.RED + "Du hast bereits abgestimmt!"},
	    {"game:no_voting", ChatColor.RED + "Das ist der falsche Zeitpunkt um abzustimmen!"},
	    {"game:voting_disabled", ChatColor.RED + "Das Abstimm System ist deaktiviert!"},
	    {"game:spawn_protection_enabled", ChatColor.GREEN + "Du bist für " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Sekunden geschützt!"},
	    {"game:spawn_protection_over", ChatColor.RED + "ACHTUNG!"+ ChatColor.GREEN + " Spawnschutz ist abgelaufen!"},
	    {"game:won", ChatColor.GOLD + "" + ChatColor.BOLD + "Glückwunsch!\n" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Ihr habt die Zombies zurückgedrängt und dabei " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " von ihnen zur Strecke gebracht!\nEs hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Wellen gedauert in denen ihr " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " mal gestorben seid. Euer verdientes Geld " + ChatColor.GOLD + "( %s )" + ChatColor.DARK_GRAY + " wird an " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gespendet." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Danke für's Spielen!"},
	    {"game:won_messages", "Notchs Krankenhaus;die Minecraftfabrik;die Creeperfarmen;das Schaf Erholungszentrum;Jebs Akademie"},
	    
	    {"arena:stop_all", ChatColor.DARK_GRAY + "Alle Arenen gestoppt!"},
	    {"arena:stop", ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gestoppt!"},
	    {"arena:offline", ChatColor.RED + "Die Arena ist Offline!"},
	    {"arena:not_ready", ChatColor.RED + "Die Arena ist nicht bereit oder bereits voll!"},
	    
	    {"manage:right_saved", ChatColor.GREEN + "Rechtsklick gespeichert!"},
	    {"manage:left_saved", ChatColor.GREEN + "Linksklick gespeichert!"},
	    {"manage:position_saved", ChatColor.GOLD + "%s" + ChatColor.GREEN + " gespeichert!"},
	    {"manage:position_not_saved", ChatColor.RED + "Die Position konnte nicht gespeichert werden!"},
	    {"manage:position_not_in_arena", ChatColor.RED + "Diese Position ist nicht teil einer Arena!"},
	    {"manage:tool", ChatColor.DARK_PURPLE + "Benutze das Tool um zwei Positionen zu erstellen."},
	    {"manage:lobby_saved", ChatColor.GREEN + "Lobby gespeichert!"},
	    {"manage:arena_saved", ChatColor.GREEN + "Arena gespeichert!"},
	    {"manage:lobby_removed", ChatColor.GREEN + "Lobby aus Spiel entfernt!"},
	    {"manage:arena_removed", ChatColor.GREEN + "Arena aus Spiel entfernt!"},
	    {"manage:arena_status_changed", ChatColor.GREEN + "Arena ist jetzt " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {"manage:sign_saved", ChatColor.GREEN + "Schild erfolgreich platziert!"},
	    {"manage:sign_removed", ChatColor.GREEN + "Schild erfolgreich entfernt!"},
	    {"manage:kit_saved", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " erfolgreich saved!"},
	    {"manage:kit_removed", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " erfolgreich entfernt!"},
	    
	    {"error:sign_remove", ChatColor.RED + "Ein Fehler ist beim Entfernen dieses Schildes aufgetreten!"},
	    {"error:sign_place", ChatColor.RED + "Ein Fehler ist beim Platzieren dieses Schildes aufgetreten!"},
	    {"error:sign_layout", ChatColor.RED + "Das Schild Layout ist falsch!"},
	    {"error:lobby_not_available", ChatColor.RED + "Diese Lobby ist nicht verfügbar!"},
	    {"error:arena_not_available", ChatColor.RED + "Diese Arena ist nicht verfügbar!"},
	    {"error:kit_already_exists", ChatColor.RED + "Das Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " existiert bereits! Wähle einen anderen Namen!"},
	    {"error:kit_does_not_exists", ChatColor.RED + "Das Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " existiert nicht!"},
	    {"error:transaction_failed", ChatColor.RED + "Die Transaktion ist gescheitert!"},
	    
	    {"category:food", "Nahrung"},
	    {"category:armor", "Rüstung"},
	    {"category:weapon", "Waffen"},
	    {"category:potion", "Tränke"},
	    {"category:misc", "Sonstiges"},
	    
	    {"status:running", "Im Spiel"},
	    {"status:waiting", "Wartet"},
	    {"status:stoped", "Angehalten"},
	    
	    {"inventory:kit_select", "Wähle dein Kit!"},
	    {"inventory:place_icon", "Platziere ein Icon hier!"},
	    {"inventory:select_category", "Wähle eine Kategorie!"},
    };
    
}
