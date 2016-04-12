package me.Aubli.ZvP.Translation.Resources;

import java.util.Locale;

import me.Aubli.ZvP.Translation.LanguageBundle;
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


public class DefaultTranslation extends LanguageBundle {
    
    @Override
    public Locale getLocale() {
	return Locale.ENGLISH;
    }
    
    @Override
    public String getAuthor() {
	return "Aubli";
    }
    
    @Override
    protected Object[][] getContents() {
	return this.contents;
    }
    
    //@formatter:off
    private Object[][] contents = {
	    
	    {config.reloaded.name(), ChatColor.GREEN + "Config successfully reloaded!"},
	    {commands.missing_permission.name(), ChatColor.DARK_RED + "You do not have enough permission for that!"},
	    {commands.no_commands_allowed.name(), ChatColor.DARK_RED + "You can not execute commands during a ZvP game!"},
	    {commands.only_for_Players.name(), "Only Players can use this command!"},
	    
	    {game.waiting_for_players.name(), ChatColor.DARK_GRAY + "Waiting for players ..."},
	    {game.joined.name(), ChatColor.GREEN + "You joined Arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.left.name(), ChatColor.GREEN + "You have left Arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.player_left.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " has left the game!"},
	    {game.player_joined.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " joined the game!"},
	    {game.player_not_found.name(), ChatColor.RED + "You were not found in a game (Might be a plugin error)!"},
	    {game.player_died.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " died!"},
	    {game.player_bought.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " bought " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " for " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_bought_more.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " bought " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "for " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {game.player_sold.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " sold " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " for " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_sold_more.name(), ChatColor.DARK_GRAY + "Player " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " sold " + ChatColor.GOLD + "%s %s " + ChatColor.DARK_GRAY + "for " + ChatColor.GOLD + "%s " + ChatColor.DARK_GRAY + "!"},
	    {game.player_bought_kit.name(), ChatColor.GREEN + "You bought the " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " kit for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "! You have " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " left!"},
	    {game.no_item_to_sell.name(), ChatColor.RED + "You do not have this item!"},
	    {game.not_in_game.name(), ChatColor.RED + "You are not in a game!"},
	    {game.already_in_game.name(), ChatColor.RED + "You are already in a game!"},
	    {game.vote_request.name(), ChatColor.DARK_PURPLE + "Type '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' in the chat to vote for the next wave!"},
	    {game.voted_next_wave.name(), ChatColor.GREEN + "You have voted for the next wave!"},
	    {game.already_voted.name(), ChatColor.RED + "You have already voted for the next round!"},
	    {game.no_voting.name(), ChatColor.RED + "It is not the right time to vote!"},
	    {game.feature_disabled.name(), ChatColor.RED + "This feature is disabled!"},
	    {game.spawn_protection_enabled.name(), ChatColor.GREEN + "You are protected for " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " second(s)!"},
	    {game.spawn_protection_over.name(), ChatColor.RED + "ATTENTION! "+ ChatColor.GREEN + "Spawnprotection is over!"},
	    {game.won.name(), ChatColor.GOLD + "" + ChatColor.BOLD + "Congratulation!\n" + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "You won against the Zombies. You fought against " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombies in " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " wave(s) and have died " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " time(s). The remains of your acquired money " + ChatColor.GOLD + "( %s )" + ChatColor.DARK_GRAY + " will be donated to the " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "." + ChatColor.BOLD + "" + ChatColor.DARK_GREEN + " Thanks for playing!"},
	    {game.won_messages.name(), "Notch Hospital;Minecraft Factory;Creeper farms;Sheep resorts;Jeb Academy"},
	    {game.lost.name(), ChatColor.DARK_GRAY + "You lost the game!" + ChatColor.RED + " All your teammates are dead!" + ChatColor.DARK_GRAY + " You killed " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " Zombies in " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " wave(s)! " + ChatColor.GREEN + "" + ChatColor.BOLD + "Good luck next time!"},
	    {game.spectator_mode.name(), ChatColor.GOLD + "You are now in spectator mode. You can use the tools in your inventory."},
	    {game.speedTool_description.name(), ChatColor.GREEN + "Use this tool to change your flying speed."},
	    {game.speedTool_enabled.name(), ChatColor.GREEN + "You can travel faster now"},
	    {game.speedTool_disabled.name(), ChatColor.RED + "You are now as fast as usual"},
	    {game.teleportTool_description.name(), ChatColor.GREEN + "Use this tool to teleport to living players."},
	    {game.teleport_to.name(), ChatColor.DARK_GRAY + "Teleport to " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "!"},
	    
	    {arena.stop_all.name(), ChatColor.DARK_GRAY + "All arenas halted!"},
	    {arena.stop.name(), ChatColor.DARK_GRAY + "Arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " stoped!"},
	    {arena.offline.name(), ChatColor.RED + "The arena is offline!"},
	    {arena.not_ready.name(), ChatColor.RED + "The arena is not ready or full!"},
	    
	    {manage.right_saved.name(), ChatColor.GREEN + "Right-click saved!"},
	    {manage.left_saved.name(), ChatColor.GREEN + "Left-click saved!"},
	    {manage.position_saved.name(), ChatColor.GOLD + "%s " + ChatColor.GREEN + "saved!"},
	    {manage.position_saved_poly.name(), ChatColor.GREEN + "Position " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " saved! Use " + ChatColor.DARK_PURPLE + "'/zvp add arena clear'" + ChatColor.GREEN + " to clear the list! Use " + ChatColor.DARK_PURPLE + "'/zvp add arena finish'" + ChatColor.GREEN + " to finish editing!"},
	    {manage.position_cleared.name(), ChatColor.GREEN + "Positions are reseted! You can start again!"},
	    {manage.position_not_saved.name(), ChatColor.RED + "This position can not be saved!"},
	    {manage.position_not_in_arena.name(), ChatColor.RED + "This position is not part of an arena!"},
	    {manage.tool.name(), ChatColor.DARK_PURPLE + "Use this tool to create positions for an arena!"},
	    {manage.lobby_saved.name(), ChatColor.GREEN + "Lobby saved!"},
	    {manage.arena_saved.name(), ChatColor.GREEN + "Arena " + ChatColor.GOLD + "%s"+ ChatColor.GREEN + " saved!"},
	    {manage.lobby_removed.name(), ChatColor.GREEN + "Lobby removed from config!"},
	    {manage.arena_removed.name(), ChatColor.GREEN + "Arena removed from config!"},
	    {manage.arena_status_changed.name(), ChatColor.GREEN + "Arena is now " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {manage.sign_saved.name(), ChatColor.GREEN + "Sign successfully placed!"},
	    {manage.sign_removed.name(), ChatColor.GREEN + "Sign successfully removed!"},
	    {manage.kit_saved.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully saved!"},
	    {manage.kit_removed.name(), ChatColor.GREEN + "Kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " successfully removed!"},
	    {manage.record_start.name(), ChatColor.GREEN + "Separated statistics will be recorded for the next " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " hours!"},
	    {manage.record_already_running.name(), ChatColor.RED + "A different record is currently running. There can only be one at a time!"},
			   
	    {error.sign_remove.name(), ChatColor.RED + "An error occured while removing this sign!"},
	    {error.sign_place.name(), ChatColor.RED + "An error occured while placing this sign!"},
	    {error.sign_layout.name(), ChatColor.RED + "The layout of this sign is wrong!"},
	    {error.arena_place.name(), ChatColor.RED + "Your positions are not in the same world!"},
	    {error.prelobby_add.name(), ChatColor.RED + "An error occured while saving PreLobby!"},
	    {error.no_prelobby.name(), ChatColor.RED + "This arena does not have a PreLobby!"},
	    {error.lobby_not_available.name(), ChatColor.RED + "This lobby is not available!"},
	    {error.arena_not_available.name(), ChatColor.RED + "This arena is not available!"},
	    {error.kit_already_exists.name(), ChatColor.RED + "The kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " already exists! Choose another name!"},
	    {error.kit_not_exist.name(), ChatColor.RED + "The kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " does not exist!"},
	    {error.transaction_failed.name(), ChatColor.RED + "The transaction failed!"},
	    {error.no_money.name(), ChatColor.RED + "You do not own enough money!"},
	    {error.wrong_inventory.name(), ChatColor.RED + "You clicked the wrong inventory! Try the top one!"},
	    {error.record_start_error.name(), ChatColor.RED + "An internal error occoured! Record was not started!"},
	    {error.negative_duration.name(), ChatColor.RED + "The duration must be greater than 0!"},
		   
	    {category.food.name(), "Food"},
	    {category.armor.name(), "Armor"},
	    {category.weapon.name(), "Weapons"},
	    {category.potion.name(), "Potions"},
	    {category.misc.name(), "Misc"},
	    
	    {dataType.kills.name(), "Kills"},
	    {dataType.kill_record.name(), "Kill Record"},
	    {dataType.deaths.name(), "Deaths"},
	    {dataType.left_money.name(), "Left Money"},
	    
	    {status.running.name(), "Running"},
	    {status.waiting.name(), "Waiting"},
	    {status.stoped.name(), "Stoped"},
	    
	    {inventory.kit_select.name(), "Select your Kit!"},
	    {inventory.place_icon.name(), "Place Kit icon here!"},
	    {inventory.select_category.name(), "Select Category"},
	    {inventory.select_recordType.name(), "Select Statistic Type"},
	    {inventory.living_players.name(), "Living Players"},
    };
}
