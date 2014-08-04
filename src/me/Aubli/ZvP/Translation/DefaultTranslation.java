package me.Aubli.ZvP.Translation;

import java.util.ListResourceBundle;

public class DefaultTranslation extends ListResourceBundle{

	@Override
	protected Object[][] getContents() {
		return contents;
	}

	private Object[][] contents = {
		
		{"config:saved", "Config successfully saved!"},
		{"config:reloaded", "Config successfully reloaded!"},
		
		{"commands:missing_Permission", "You do not have enough permission for that!"},
		{"commands:only_for_Players", "Only Players can use this command!"},
		
		{"game:waiting", "Waiting for players ..."},
		{"game:joined", "You joined Arena %s"},
		{"game:left", "You have left arena %s!"},
		{"game:player_left", "Player %s has left the game!"},
		{"game:player_not_found", "You were not found in a game (Might be a plugin error)!"},
		{"game:player_died", "Player %s died!"},
		{"game:no_money", "You do not own enough money!"},
		{"game:not_in_game", "You are not in a game!"},
		{"game:already_in_game", "You are already in a game!"},
		{"game:sign_interaction", "You need a free Hand for that!"},
		{"game:vote_request", "Type 'zvp vote' to vote for the next round!"},
		{"game:voted_next_round", "You have voted for the next round!"},
		{"game:already_voted", "You have already voted for the next round!"},
		{"game:no_voting", "It is not the Time to vote now!"},
		
		{"arena:stop_all", "All arenas halted!"},
		{"arena:stop", "Arena %s stoped!"},
		{"arena:offline", "The Arena is Offline!"},
		{"arena:not_ready", "The Arena is not Ready or full!"},
		
		{"manage:right_saved", "Right Click saved!"},
		{"manage:left_saved", "Left Click saved!"},
		{"manage:position_saved", "%s saved!"},
		{"manage:tool", "Use this tool to create two Positions"},
		{"manage:lobby_saved", "Lobby saved!"},
		{"manage:arena_saved", "Arena saved!"},
		{"manage:lobby_removed", "Lobby removed from Config!"},
		{"manage:arena_removed", "Arena removed from Config!"},
		{"manage:sign_saved", "Sign successfully placed!"},
		{"manage:sign_removed", "Sign successfully removed!"},
				
		{"error:sign_remove", "An Error occured while removing this Sign!"},
		{"error:sign_place", "An Error occured while placing this Sign!"},
		{"error:sign_layout", "The sign layout is wrong!"},
		{"error:lobby_not_available", "This Lobby is not available!"},
		{"error:arena_not_available", "This Arena is not available!"},
		
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
