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
		{"game:left", "You have left arena %s!"},
		{"game:player_left", "Player %s has left the game!"},
		{"game:player_not_found", "You were not found in a game (Might be a plugin error)!"},
		{"game:not_in_game", "You are not in a game!"},
		{"game:vote_request", "Type 'zvp vote' to vote for the next round!"},
		{"game:voted_next_round", "You have voted for the next round!"},
		{"game:already_voted", "You have already voted for the next round!"},
		{"game:no_voting", "It is not the Time to vote now!"},
		
		{"arena:stop_all", "All arenas halted!"},
		{"arena:stop", "Arena %s stoped!"},
		
		{"manage:lobby_saved", "Lobby saved!"},
		{"manage:arena_saved", "Arena saved!"},
		{"manage:lobby_removed", "Lobby removed from Config!"},
		{"manage:arena_removed", "Arena removed from Config!"},
		{"arena_not_available", "This arena is not available!"},		
		
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
