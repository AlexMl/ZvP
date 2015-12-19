package me.Aubli.ZvP.Translation.Resources;

import java.util.ListResourceBundle;
import java.util.Locale;

import me.Aubli.ZvP.Translation.MessageKeys.arena;
import me.Aubli.ZvP.Translation.MessageKeys.category;
import me.Aubli.ZvP.Translation.MessageKeys.commands;
import me.Aubli.ZvP.Translation.MessageKeys.config;
import me.Aubli.ZvP.Translation.MessageKeys.dataType;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageKeys.status;

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
	    
	    {config.reloaded.name(), ChatColor.GREEN + "Konfigurationsdatei erfolgreich neu geladen!"},
	    {commands.missing_permission.name(), ChatColor.DARK_RED + "You do not have enough permission for that!"},
	    {commands.no_commands_allowed.name(), ChatColor.DARK_RED + "Du kannst keine Befehle während eines ZvP Spiels ausführen!"},
	    {commands.only_for_Players.name(), "Only players can use this command!"},
	   	    
	    {game.waiting_for_players.name(), ChatColor.DARK_GRAY + "Auf Spieler warten ..."},
	    {game.joined.name(), ChatColor.GREEN + "Du bist Arena " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " beigetreten!"},
	    {game.left.name(), ChatColor.GREEN + "Du hast Arena " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " verlassen!"},
	    {game.player_left.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat das Spiel verlassen!"},
	    {game.player_joined.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ist dem Spiel beigetreten!"},
	    {game.player_not_found.name(), ChatColor.RED + "Du wurdest in keinem Spiel gefunden! (Wahrscheinlich ein Pluginfehler)!"},
	    {game.player_died.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ist gestorben!"},
	    {game.player_bought.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gekauft!"},
	    {game.player_bought_more.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gekauft!"},
	    {game.player_sold.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " verkauft!"},
	    {game.player_sold_more.name(), ChatColor.DARK_GRAY + "Spieler " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " hat " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " für " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " verkauft!"},
	    {game.player_bought_kit.name(), ChatColor.GREEN + "Du hast das " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Kit für " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " gekauft! Du hast " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " übrig!"},
	    {game.no_item_to_sell.name(), ChatColor.RED + "Du besitzt diesen Gegenstand nicht!"},
	    {game.not_in_game.name(), ChatColor.RED + "Du bist nicht im Spiel!"},
	    {game.already_in_game.name(), ChatColor.RED + "Du bist bereits im Spiel!"},
	    {game.vote_request.name(), ChatColor.DARK_PURPLE + "Tippe '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' in das chat fenster um für die nächste Welle abzustimmen!"},
	    {game.voted_next_wave.name(), ChatColor.GREEN + "Du hast für die nächste Welle gestimmt!"}, 
	    {game.already_voted.name(), ChatColor.RED + "Du hast bereits abgestimmt!"},
	    {game.no_voting.name(), ChatColor.RED + "Das ist der falsche Zeitpunkt um abzustimmen!"},
	    {game.feature_disabled.name(), ChatColor.RED + "Diese Funktion ist deaktiviert!"},
	    {game.spawn_protection_enabled.name(), ChatColor.GREEN + "Du bist für " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Sekunden geschützt!"},
	    {game.spawn_protection_over.name(), ChatColor.RED + "ACHTUNG!"+ ChatColor.GREEN + " Spawnschutz ist abgelaufen!"},
	    {game.won.name(), ChatColor.GOLD + "" + ChatColor.BOLD + "Glückwunsch!\n" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Ihr habt die Zombies zurückgedrängt und dabei " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " von ihnen zur Strecke gebracht!\nEs hat " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Wellen gedauert in denen ihr " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " mal gestorben seid. Euer verdientes Geld " + ChatColor.GOLD + "( %s )" + ChatColor.DARK_GRAY + " wird an " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gespendet." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Danke für's Spielen!"},
	    {game.won_messages.name(), "Notchs Krankenhaus;die Minecraftfabrik;die Creeperfarmen;das Schaf Erholungszentrum;Jebs Akademie"},
	    {game.lost.name(), ChatColor.DARK_GRAY + "Du hast das Spiel verloren!" + ChatColor.RED + " Alle deine Mitspieler sind tot!" + ChatColor.DARK_GRAY + " Ihr habt " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombies in " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Wellen getötet! " + ChatColor.GREEN + "" + ChatColor.BOLD + "Viel Glück beim nächsten mal!"},
	    {game.spectator_mode.name(), ChatColor.GOLD + "Du bist nun im Zuschauer Modus. Du kannst die Tools in deinem Inventar benutzen."},
	    {game.speedTool_description.name(), ChatColor.GREEN + "Benutze das Tool um deine Fluggeschwindigkeit zu ändern."},
	    {game.speedTool_enabled.name(), ChatColor.GREEN + "Du fliegst jetzt schneller."},
	    {game.speedTool_disabled.name(), ChatColor.RED + "Du fliegst wieder langsam."},
	    {game.teleportTool_description.name(), ChatColor.GREEN + "Benutze das Tool um dich zu lebenden Sielern zu teleportieren."},
	    {game.teleport_to.name(), ChatColor.DARK_GRAY + "Zu " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " teleportieren!"},
	    
	    {arena.stop_all.name(), ChatColor.DARK_GRAY + "Alle Arenen gestoppt!"},
	    {arena.stop.name(), ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " gestoppt!"},
	    {arena.offline.name(), ChatColor.RED + "Die Arena ist Offline!"},
	    {arena.not_ready.name(), ChatColor.RED + "Die Arena ist nicht bereit oder bereits voll!"},
	    
	    {manage.right_saved.name(), ChatColor.GREEN + "Rechtsklick gespeichert!"},
	    {manage.left_saved.name(), ChatColor.GREEN + "Linksklick gespeichert!"},
	    {manage.position_saved.name(), ChatColor.GOLD + "%s" + ChatColor.GREEN + " gespeichert!"},
	    {manage.position_saved_poly.name(), ChatColor.GREEN + "Position " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " aufgenommen! Benutze " + ChatColor.DARK_PURPLE + "'/zvp add arena clear'" + ChatColor.GREEN + " um die Positionsliste zu löschen! Bemutze " + ChatColor.DARK_PURPLE + "'/zvp add arena finish'" + ChatColor.GREEN + " um abzuschließen!"},
	    {manage.position_cleared.name(), ChatColor.GREEN + "Positionen wurden zurückgesetzt! Du kannst neu beginnen!"},
	    {manage.position_not_saved.name(), ChatColor.RED + "Die Position konnte nicht gespeichert werden!"},
	    {manage.position_not_in_arena.name(), ChatColor.RED + "Diese Position ist nicht teil einer Arena!"},
	    {manage.tool.name(), ChatColor.DARK_PURPLE + "Benutze das Tool um Positionen für eine Arena zu erstellen."},
	    {manage.lobby_saved.name(), ChatColor.GREEN + "Lobby gespeichert!"},
	    {manage.arena_saved.name(), ChatColor.GREEN + "Arena " + ChatColor.GOLD + "%s"+ ChatColor.GREEN + " gespeichert!"},
	    {manage.lobby_removed.name(), ChatColor.GREEN + "Lobby aus Spiel entfernt!"},
	    {manage.arena_removed.name(), ChatColor.GREEN + "Arena aus Spiel entfernt!"},
	    {manage.arena_status_changed.name(), ChatColor.GREEN + "Arena ist jetzt " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {manage.sign_saved.name(), ChatColor.GREEN + "Schild erfolgreich platziert!"},
	    {manage.sign_removed.name(), ChatColor.GREEN + "Schild erfolgreich entfernt!"},
	    {manage.kit_saved.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " erfolgreich saved!"},
	    {manage.kit_removed.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " erfolgreich entfernt!"},
	    {manage.record_start.name(), ChatColor.GREEN + "Statistiken für die nächsten " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " stunden werden seperat aufgezeichnet!"},
	    {manage.record_already_running.name(), ChatColor.RED + "Es läuft gerade eine andere Aufzeichnung!"},
		
	    {error.sign_remove.name(), ChatColor.RED + "Ein Fehler ist beim Entfernen dieses Schildes aufgetreten!"},
	    {error.sign_place.name(), ChatColor.RED + "Ein Fehler ist beim Platzieren dieses Schildes aufgetreten!"},
	    {error.sign_layout.name(), ChatColor.RED + "Das Schild Layout ist falsch!"},
	    {error.arena_place.name(), ChatColor.RED + "Die Arena Positionen sind nicht in der selben Welt!"},
	    {error.prelobby_add.name(), ChatColor.RED + "Ein Fehler ist beim Speichern der PreLobby aufgetreten!"},
	    {error.no_prelobby.name(), ChatColor.RED + "Diese Arena hat keine PreLobby!"},
	    {error.lobby_not_available.name(), ChatColor.RED + "Diese Lobby ist nicht verfügbar!"},
	    {error.arena_not_available.name(), ChatColor.RED + "Diese Arena ist nicht verfügbar!"},
	    {error.kit_already_exists.name(), ChatColor.RED + "Das Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " existiert bereits! Wähle einen anderen Namen!"},
	    {error.kit_not_exist.name(), ChatColor.RED + "Das Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " existiert nicht!"},
	    {error.transaction_failed.name(), ChatColor.RED + "Die Transaktion ist gescheitert!"},
	    {error.no_money.name(), ChatColor.RED + "Du besitzt nicht genügend Geld!"},
	    {error.wrong_inventory.name(), ChatColor.RED + "Du hast in das falsche Inventar geklickt! Versuchs im obrigen!"},
	    {error.record_start_error.name(), ChatColor.RED + "Ein interner Fehler ist aufgetreten! Keine Aufzeichnung gestartet!"},
	    {error.negative_duration.name(), ChatColor.RED + "Die Dauer muss größer als 0 sein!"},
		   
	    {category.food.name(), "Nahrung"},
	    {category.armor.name(), "Rüstung"},
	    {category.weapon.name(), "Waffen"},
	    {category.potion.name(), "Tränke"},
	    {category.misc.name(), "Sonstiges"},
	    
	    {dataType.kills.name(), "Kills"},
	    {dataType.kill_record.name(), "Kill Rekord"},
	    {dataType.deaths.name(), "Tode"},
	    {dataType.left_money.name(), "Übriges Geld"},
	    
	    {status.running.name(), "Im Spiel"},
	    {status.waiting.name(), "Wartet"},
	    {status.stoped.name(), "Angehalten"},
	    
	    {inventory.kit_select.name(), "Wähle dein Kit!"},
	    {inventory.place_icon.name(), "Platziere ein Icon hier!"},
	    {inventory.select_category.name(), "Wähle eine Kategorie!"},
	    {inventory.select_recordType.name(), "Wähle einen Statistik Typen"},
	    {inventory.living_players.name(), "Lebende Spieler"},
    };    
}
