package me.Aubli.ZvP.Translation.Resources;

import java.util.ListResourceBundle;
import java.util.Locale;

import org.bukkit.ChatColor;


public class HungarianTranslation extends ListResourceBundle {
    
    @Override
    public Locale getLocale() {
	return new Locale("hu");
    }
    
    @Override
    protected Object[][] getContents() {
	return this.contents;
    }
    
    //@formatter:off
    private Object[][] contents = {
	    {"config:reloaded", ChatColor.GREEN + "Config successfully reloaded!"},
	    {"commands:missing_Permission", ChatColor.DARK_RED + "Nincs hozzáférésed!"},
	    {"commands:only_for_Players", "Játékos nem használhatja!"},
	    
	    {"game:waiting", ChatColor.DARK_GRAY + "Várakozás játékosokra...hamarosan indul"},
	    {"game:joined", ChatColor.GREEN + "A következő arénához csatlakoztál " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {"game:left", ChatColor.GREEN + "Ezt az arénát hagytad el " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {"game:player_left", ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " elhagyta a játékot!"},
	    {"game:player_joined", ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " csatlakozott a játékhoz!"},
	    {"game:player_not_found", ChatColor.RED + "Nem vagy található a játékban (Might be a plugin error)!"},
	    {"game:player_died", ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " meghalt!"},
	    {"game:player_bought", ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vett " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ennyiért " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {"game:player_bought_more", ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vett " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "ennyiért " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {"game:player_sold", ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " eladva " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ennyiért " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {"game:player_sold_more", ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " eladva " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "ennyiért " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {"game:player_bought_kit", ChatColor.GREEN + "You bought the " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Kit for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "! You have " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " left!"},
	    {"game:no_money", ChatColor.RED + "Nincs elég pénzed!"},
	    {"game:no_item_to_sell", ChatColor.RED + "Nincs ilyen tárgyad!"},
	    {"game:wrong_inventory", ChatColor.RED + "Rossz helyre kattintottál! Próbáld a felsőt!"},
	    {"game:not_in_game", ChatColor.RED + "Nem vagy játékban!"},
	    {"game:already_in_game", ChatColor.RED + "Még játékban vagy!"},
	    {"game:vote_request", ChatColor.DARK_PURPLE + "Írd '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' a chatbe a szavazást a következő fordulóhoz!"},
	    {"game:voted_next_round", ChatColor.GREEN + "Beszavaztak a következő fordulóba!"}, 
	    {"game:already_voted", ChatColor.RED + "Már beszavaztak a következő fordulóra!"},
	    {"game:no_voting", ChatColor.RED + "Nem lehet most szavazni!"},
	    {"game:voting_disabled", ChatColor.RED + "A szavazó rendszer nem elérhető!"},
	    {"game:spawn_protection_enabled", ChatColor.GREEN + "You are protected for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " seconds!"},
	    {"game:spawn_protection_over", ChatColor.RED + "ATTENTION! "+ ChatColor.GREEN + "Spawnprotection is over!"},
	    {"game:won", ChatColor.GOLD + "" + ChatColor.BOLD + "Gratulálunk!\n" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Nyertél a zombik ellen.\nEnnyi " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombival harcoltál " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " körben és " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " szor haltál meg. A szerzett pénzed maradéka " + ChatColor.GOLD + "(%s)" + ChatColor.DARK_GRAY + " adományozva lett " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Köszönjük a játékot!"},
	    {"game:won_messages", "Clonecraft házára;Pankix ebédjére;Új stadionokra;Új pályára;MesterMC Akadémiára"},
	    
	    {"arena:stop_all", ChatColor.DARK_GRAY + "Minden arena leallt!"},
	    {"arena:stop", ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " leallt!"},
	    {"arena:offline", ChatColor.RED + "Az Arena nem elerheto!"},
	    {"arena:not_ready", ChatColor.RED + "The Arena nincs kesz vagy tele van!"},
	    
	    {"manage:right_saved", ChatColor.GREEN + "Right Click saved!"},
	    {"manage:left_saved", ChatColor.GREEN + "Left Click saved!"},
	    {"manage:position_saved", ChatColor.GOLD + "%s " + ChatColor.GREEN + "saved!"},
	    {"manage:position_not_saved", ChatColor.RED + "This position can not be saved!"},
	    {"manage:position_not_in_arena", ChatColor.RED + "This position is not part of an arena!"},
	    {"manage:tool", ChatColor.DARK_PURPLE + "Use this tool to create two positions"},
	    {"manage:lobby_saved", ChatColor.GREEN + "Lobby saved!"},
	    {"manage:arena_saved", ChatColor.GREEN + "Arena " + ChatColor.GOLD + "%s"+ ChatColor.GREEN + " saved!"},
	    {"manage:lobby_removed", ChatColor.GREEN + "Lobby removed from Config!"},
	    {"manage:arena_removed", ChatColor.GREEN + "Arena removed from Config!"},
	    {"manage:arena_status_changed", ChatColor.GREEN + "Arena is now " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {"manage:sign_saved", ChatColor.GREEN + "Sign successfully placed!"},
	    {"manage:sign_removed", ChatColor.GREEN + "Sign successfully removed!"},
	    {"manage:kit_saved", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully saved!"},
	    {"manage:kit_removed", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully removed!"},
	    
	    {"error:sign_remove", ChatColor.RED + "An Error occured while removing this Sign!"},
	    {"error:sign_place", ChatColor.RED + "An Error occured while placing this Sign!"},
	    {"error:sign_layout", ChatColor.RED + "The sign layout is wrong!"},
	    {"error:arena_place", ChatColor.RED + "Your positions are not in the same world!"},
	    {"error:prelobby_add", ChatColor.RED + "An Error occured while saving PreLobby!"},
	    {"error:no_prelobby", ChatColor.RED + "This arena does not have a PreLobby!"},
	    {"error:lobby_not_available", ChatColor.RED + "Nincs lobby!"},
	    {"error:arena_not_available", ChatColor.RED + "Aréna nem létezik!"},
	    {"error:kit_already_exists", ChatColor.RED + "The Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " already exists! Choose another name!"},
	    {"error:kit_does_not_exists", ChatColor.RED + "A Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " nem található!"},
	    {"error:transaction_failed", ChatColor.RED + "The transaction failed!"},
	    
	    {"category:food", "Kaja"},
	    {"category:armor", "Páncél"},
	    {"category:weapon", "Fegyver"},
	    {"category:potion", "Poti"},
	    {"category:misc", "Misc"},
	    
	    {"status:running", "Megy"},
	    {"status:waiting", "Várakozás"},
	    {"status:stoped", "Leállitva"},
	    
	    {"inventory:kit_select", "Kit kiválasztva!"},
	    {"inventory:place_icon", "Place Kit icon here!"},
	    {"inventory:select_category", "Select Category"},
    };
    
}
