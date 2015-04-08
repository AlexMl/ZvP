package me.Aubli.ZvP.Translation;

import java.util.ListResourceBundle;

import org.bukkit.ChatColor;


public class DefaultTranslation extends ListResourceBundle {
    
    @Override
    protected Object[][] getContents() {
	return this.contents;
    }
    
    //@formatter:off
    private Object[][] contents = {
	    
	    {"config:saved", ChatColor.GREEN + "Config successfully saved!"},
	    {"config:reloaded", ChatColor.GREEN + "Config successfully reloaded!"},
	    {"commands:missing_Permission", ChatColor.DARK_RED + "You do not have enough permission for that!"},
	    {"commands:only_for_Players", "Only Players can use this command!"},
	    
	    {"game:waiting", ChatColor.DARK_GRAY + "Waiting for players ..."},
	    {"game:joined", ChatColor.GREEN + "You joined Arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {"game:left", ChatColor.GREEN + "You have left Arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {"game:player_left", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " has left the game!"},
	    {"game:player_joined", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " joined the game!"},
	    {"game:player_not_found", ChatColor.RED + "You were not found in a game (Might be a plugin error)!"},
	    {"game:player_died", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " died!"},
	    {"game:player_bought", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " bought " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " for " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {"game:player_bought_more", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " bought " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "for " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {"game:player_sold", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " sold " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " for " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {"game:player_sold_more", ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " sold " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "for " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {"game:no_money", ChatColor.RED + "You do not own enough money!"},
	    {"game:no_item_to_sell", ChatColor.RED + "You do not have this item!"},
	    {"game:wrong_inventory", ChatColor.RED + "You clicked the wrong Inventory! Try the top one!"},
	    {"game:not_in_game", ChatColor.RED + "You are not in a game!"},
	    {"game:already_in_game", ChatColor.RED + "You are already in a game!"},
	    {"game:sign_interaction", ChatColor.DARK_PURPLE + "You need a free Hand to interact with this sign!"},
	    {"game:vote_request", ChatColor.DARK_PURPLE + "Type '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' in the chat to vote for the next round!"},
	    {"game:voted_next_round", ChatColor.GREEN + "You have voted for the next round!"}, {"game:already_voted", ChatColor.RED + "You have already voted for the next round!"},
	    {"game:no_voting", ChatColor.RED + "It is not the Time to vote now!"},
	    {"game:voting_disabled", ChatColor.RED + "The vote system is disabled!"},
	    {"game:spawn_protection_enabled", ChatColor.GREEN + "You are protected for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " seconds!"},
	    {"game:spawn_protection_over", ChatColor.RED + "ATTENTION! "+ ChatColor.GREEN + "Spawnprotection is over!"},
	    {"game:won", ChatColor.GOLD + "" + ChatColor.BOLD + "Grongrats!" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + " You won against the Zombies.\nYou fought against " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombies in " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " waves and have died " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " times. The remains of your acquired money " + ChatColor.GOLD + "( %s )" + ChatColor.DARK_GRAY + " will be donated to the " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Thanks for playing!"},
	    {"game:won_messages", "Notch Hospital;Minecraft Factory;Creeper farms;Sheep resorts;Jeb Academy"},
	    
	    {"arena:stop_all", ChatColor.DARK_GRAY + "All arenas halted!"},
	    {"arena:stop", ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " stoped!"},
	    {"arena:offline", ChatColor.RED + "The Arena is Offline!"},
	    {"arena:not_ready", ChatColor.RED + "The Arena is not Ready or full!"},
	    
	    {"manage:right_saved", ChatColor.GREEN + "Right Click saved!"},
	    {"manage:left_saved", ChatColor.GREEN + "Left Click saved!"},
	    {"manage:position_saved", ChatColor.GOLD + "%s " + ChatColor.GREEN + "saved!"},
	    {"manage:tool", ChatColor.DARK_PURPLE + "Use this tool to create two Positions"},
	    {"manage:lobby_saved", ChatColor.GREEN + "Lobby saved!"},
	    {"manage:arena_saved", ChatColor.GREEN + "Arena saved!"},
	    {"manage:lobby_removed", ChatColor.GREEN + "Lobby removed from Config!"},
	    {"manage:arena_removed", ChatColor.GREEN + "Arena removed from Config!"},
	    {"manage:sign_saved", ChatColor.GREEN + "Sign successfully placed!"},
	    {"manage:sign_removed", ChatColor.GREEN + "Sign successfully removed!"},
	    {"manage:kit_saved", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully saved!"},
	    {"manage:kit_removed", ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully removed!"},
	    
	    {"error:sign_remove", ChatColor.RED + "An Error occured while removing this Sign!"},
	    {"error:sign_place", ChatColor.RED + "An Error occured while placing this Sign!"},
	    {"error:sign_layout", ChatColor.RED + "The sign layout is wrong!"},
	    {"error:lobby_not_available", ChatColor.RED + "This Lobby is not available!"},
	    {"error:arena_not_available", ChatColor.RED + "This Arena is not available!"},
	    {"error:kit_already_exists", ChatColor.RED + "The Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " already exists! Choose another name!"},
	    {"error:kit_does_not_exists", ChatColor.RED + "The Kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " does not exist!"},
	    
	    {"category:food", "Food"},
	    {"category:armor", "Armor"},
	    {"category:weapon", "Weapons"},
	    {"category:potion", "Potions"},
	    {"category:misc", "Misc"},
	    
	    {"status:running", "Running"},
	    {"status:waiting", "Waiting"},
	    {"status:stoped", "Stoped"},
	    
	    {"inventory:kit_select", "Select your Kit!"},
	    {"inventory:place_icon", "Place Kit icon here!"},
	    {"inventory:select_category", "Select Category"},
    };
}
