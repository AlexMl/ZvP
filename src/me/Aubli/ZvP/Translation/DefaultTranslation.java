package me.Aubli.ZvP.Translation;

import java.util.ListResourceBundle;

public class DefaultTranslation extends ListResourceBundle{

	@Override
	protected Object[][] getContents() {
		return contents;
	}

	private Object[][] contents = {
		{"missing_Permission", "You do not have enough permission for that!"},
		{"config_saved", "Config successfully saved!"},
		{"config_reloaded", "Config successfully reloaded!"},
		{"leave_game", "You have left arena %arena%!"},
		{"player_left", "Player %player% has left the game!"},
		{"not_in_game", "You are not in a game!"},
		{"arena_stop_all", "All arenas halted!"},
		{"arena_stop", "Arena %arena% stoped!"},
		{"lobby_created", "Lobby saved!"},
		{"arena_created", "Arena saved!"},
		{"lobby_removed", "Lobby removed from Config!"},
		{"arena_removed", "Arena removed from Config!"},
		{"arena_not_available", "This arena is not available!"},
		{"", ""},
		{"", ""},
		
	};
	
}
