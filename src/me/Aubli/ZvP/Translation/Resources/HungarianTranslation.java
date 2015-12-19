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
	    
	    {config.reloaded.name(), ChatColor.GREEN + "Config successfully reloaded!"},
	    {commands.missing_permission.name(), ChatColor.DARK_RED + "Nincs hozzáférésed!"},
	    {commands.no_commands_allowed.name(), ChatColor.DARK_RED + "You can not execute commands during a ZvP game!"},
	    {commands.only_for_Players.name(), "Játékos nem használhatja!"},
	    
	    {game.waiting_for_players.name(), ChatColor.DARK_GRAY + "Várakozás játékosokra...hamarosan indul"},
	    {game.joined.name(), ChatColor.GREEN + "A következő arénához csatlakoztál " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.left.name(), ChatColor.GREEN + "Ezt az arénát hagytad el " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.player_left.name(), ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " elhagyta a játékot!"},
	    {game.player_joined.name(), ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " csatlakozott a játékhoz!"},
	    {game.player_not_found.name(), ChatColor.RED + "Nem vagy található a játékban (Might be a plugin error)!"},
	    {game.player_died.name(), ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " meghalt!"},
	    {game.player_bought.name(), ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vett " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ennyiért " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_bought_more.name(), ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vett " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "ennyiért " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {game.player_sold.name(), ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " eladva " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ennyiért " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_sold_more.name(), ChatColor.DARK_GRAY + "Játékos " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " eladva " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "ennyiért " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {game.player_bought_kit.name(), ChatColor.GREEN + "You bought the " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Kit for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "! You have " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " left!"},
	    {game.no_item_to_sell.name(), ChatColor.RED + "Nincs ilyen tárgyad!"},
	    {game.not_in_game.name(), ChatColor.RED + "Nem vagy játékban!"},
	    {game.already_in_game.name(), ChatColor.RED + "Még játékban vagy!"},
	    {game.vote_request.name(), ChatColor.DARK_PURPLE + "Írd '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' a chatbe a szavazást a következő fordulóhoz!"},
	    {game.voted_next_wave.name(), ChatColor.GREEN + "Beszavaztak a következő fordulóba!"}, 
	    {game.already_voted.name(), ChatColor.RED + "Már beszavaztak a következő fordulóra!"},
	    {game.no_voting.name(), ChatColor.RED + "Nem lehet most szavazni!"},
	    {game.feature_disabled.name(), ChatColor.RED + "This feature is disabled!"},
	    {game.spawn_protection_enabled.name(), ChatColor.GREEN + "You are protected for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " seconds!"},
	    {game.spawn_protection_over.name(), ChatColor.RED + "ATTENTION! "+ ChatColor.GREEN + "Spawnprotection is over!"},
	    {game.won.name(), ChatColor.GOLD + "" + ChatColor.BOLD + "Gratulálunk!\n" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Nyertél a zombik ellen.\nEnnyi " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombival harcoltál " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " körben és " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " szor haltál meg. A szerzett pénzed maradéka " + ChatColor.GOLD + "(%s)" + ChatColor.DARK_GRAY + " adományozva lett " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Köszönjük a játékot!"},
	    {game.won_messages.name(), "Clonecraft házára;Pankix ebédjére;Új stadionokra;Új pályára;MesterMC Akadémiára"},
	    {game.lost.name(), ChatColor.DARK_GRAY + "You lost the game!" + ChatColor.RED + " All your teammates are dead!" + ChatColor.DARK_GRAY + " You killed " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombies in " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " waves! " + ChatColor.GREEN + "" + ChatColor.BOLD + "Good luck next time!"},
	    {game.spectator_mode.name(), ChatColor.GOLD + "You are now in spectator mode. You can use the tools in your inventory."},
	    {game.speedTool_description.name(), ChatColor.GREEN + "Use this tool to change your flying speed."},
	    {game.speedTool_enabled.name(), ChatColor.GREEN + "You can travel faster now"},
	    {game.speedTool_disabled.name(), ChatColor.RED + "You are now as fast as usuall"},
	    {game.teleportTool_description.name(), ChatColor.GREEN + "Use this tool to teleport to living players."},
	    {game.teleport_to.name(), ChatColor.DARK_GRAY + "Teleport to " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "!"},
	    
	    {arena.stop_all.name(), ChatColor.DARK_GRAY + "Minden arena leallt!"},
	    {arena.stop.name(), ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " leallt!"},
	    {arena.offline.name(), ChatColor.RED + "Az Arena nem elerheto!"},
	    {arena.not_ready.name(), ChatColor.RED + "The Arena nincs kesz vagy tele van!"},
	    
	    {manage.right_saved.name(), ChatColor.GREEN + "Right Click saved!"},
	    {manage.left_saved.name(), ChatColor.GREEN + "Left Click saved!"},
	    {manage.position_saved.name(), ChatColor.GOLD + "%s " + ChatColor.GREEN + "saved!"},
	    {manage.position_saved_poly.name(), ChatColor.GREEN + "Position " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " saved! Use " + ChatColor.DARK_PURPLE + "'/zvp add arena clear'" + ChatColor.GREEN + " to clear the list! Use " + ChatColor.DARK_PURPLE + "'/zvp add arena finish'" + ChatColor.GREEN + " to finish editing!"},
	    {manage.position_cleared.name(), ChatColor.GREEN + "Positions are reseted! You can start again!"},
	    {manage.position_not_saved.name(), ChatColor.RED + "This position can not be saved!"},
	    {manage.position_not_in_arena.name(), ChatColor.RED + "This position is not part of an arena!"},
	    {manage.tool.name(), ChatColor.DARK_PURPLE + "Use this tool to create positions for an arena!"},
	    {manage.lobby_saved.name(), ChatColor.GREEN + "Lobby saved!"},
	    {manage.arena_saved.name(), ChatColor.GREEN + "Arena " + ChatColor.GOLD + "%s"+ ChatColor.GREEN + " saved!"},
	    {manage.lobby_removed.name(), ChatColor.GREEN + "Lobby removed from Config!"},
	    {manage.arena_removed.name(), ChatColor.GREEN + "Arena removed from Config!"},
	    {manage.arena_status_changed.name(), ChatColor.GREEN + "Arena is now " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {manage.sign_saved.name(), ChatColor.GREEN + "Sign successfully placed!"},
	    {manage.sign_removed.name(), ChatColor.GREEN + "Sign successfully removed!"},
	    {manage.kit_saved.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully saved!"},
	    {manage.kit_removed.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully removed!"},
	    {manage.record_start.name(), ChatColor.GREEN + "Separated statistics will be recorded for the next " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " hours!"},
	    {manage.record_already_running.name(), ChatColor.RED + "A different record is currently running. There can only be one at a time!"},
	
	    {error.sign_remove.name(), ChatColor.RED + "An Error occured while removing this Sign!"},
	    {error.sign_place.name(), ChatColor.RED + "An Error occured while placing this Sign!"},
	    {error.sign_layout.name(), ChatColor.RED + "The sign layout is wrong!"},
	    {error.arena_place.name(), ChatColor.RED + "Your positions are not in the same world!"},
	    {error.prelobby_add.name(), ChatColor.RED + "An Error occured while saving PreLobby!"},
	    {error.no_prelobby.name(), ChatColor.RED + "This arena does not have a PreLobby!"},
	    {error.lobby_not_available.name(), ChatColor.RED + "Nincs lobby!"},
	    {error.arena_not_available.name(), ChatColor.RED + "Aréna nem létezik!"},
	    {error.kit_already_exists.name(), ChatColor.RED + "The Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " already exists! Choose another name!"},
	    {error.kit_not_exist.name(), ChatColor.RED + "A Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " nem található!"},
	    {error.transaction_failed.name(), ChatColor.RED + "The transaction failed!"},
	    {error.no_money.name(), ChatColor.RED + "Nincs elég pénzed!"},
	    {error.wrong_inventory.name(), ChatColor.RED + "Rossz helyre kattintottál! Próbáld a felsőt!"},
	    {error.record_start_error.name(), ChatColor.RED + "An internal error occoured! Record was not started!"},
	    {error.negative_duration.name(), ChatColor.RED + "The duration must be greater than 0!"},
		   
	    {category.food.name(), "Kaja"},
	    {category.armor.name(), "Páncél"},
	    {category.weapon.name(), "Fegyver"},
	    {category.potion.name(), "Poti"},
	    {category.misc.name(), "Misc"},
	    
	    {status.running.name(), "Megy"},
	    {status.waiting.name(), "Várakozás"},
	    {status.stoped.name(), "Leállitva"},
	    
	    {dataType.kills.name(), "Kills"},
	    {dataType.kill_record.name(), "Kill Record"},
	    {dataType.deaths.name(), "Deaths"},
	    {dataType.left_money.name(), "Left Money"},
	    
	    {inventory.kit_select.name(), "Kit kiválasztva!"},
	    {inventory.place_icon.name(), "Place Kit icon here!"},
	    {inventory.select_category.name(), "Select Category"},
	    {inventory.select_recordType.name(), "Select Statistic Type"},
	    {inventory.living_players.name(), "Living Players"},
    };    
}
