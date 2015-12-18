package me.Aubli.ZvP.Translation;

import java.util.ArrayList;


public class MessageKeys {
    
    public enum config {
	reloaded;
    }
    
    public enum commands {
	missing_permission,
	no_commands_allowed,
	only_for_Players;
    }
    
    public enum game {
	waiting_for_players,
	joined,
	left,
	player_left,
	player_joined,
	player_not_found,
	player_died,
	player_bought,
	player_bought_more,
	player_sold,
	player_sold_more,
	player_bought_kit,
	no_item_to_sell,
	not_in_game,
	already_in_game,
	vote_request,
	voted_next_wave,
	already_voted,
	no_voting,
	feature_disabled,
	spawn_protection_enabled,
	spawn_protection_over,
	won,
	won_messages,
	spectator_mode,
	speedTool_enabled,
	speedTool_disabled,
	speedTool_description,
	teleportTool_description,
	teleport_to;
    }
    
    public enum arena {
	stop_all,
	stop,
	offline,
	not_ready;
    }
    
    public enum manage {
	right_saved,
	left_saved,
	position_saved,
	position_saved_poly,
	position_cleared,
	position_not_saved,
	position_not_in_arena,
	tool,
	lobby_saved,
	arena_saved,
	lobby_removed,
	arena_removed,
	arena_status_changed,
	sign_saved,
	sign_removed,
	kit_saved,
	kit_removed,
	record_start,
	record_already_running;
    }
    
    public enum error {
	sign_remove,
	sign_place,
	sign_layout,
	arena_place,
	prelobby_add,
	no_prelobby,
	lobby_not_available,
	arena_not_available,
	kit_already_exists,
	kit_not_exist,
	transaction_failed,
	no_money,
	wrong_inventory,
	record_start_error,
	negative_duration;
    }
    
    public enum category {
	food,
	armor,
	weapon,
	potion,
	misc;
    }
    
    public enum dataType {
	kills,
	kill_record,
	deaths,
	left_money;
    }
    
    public enum status {
	running,
	waiting,
	stoped;
    }
    
    public enum inventory {
	kit_select,
	place_icon,
	select_category,
	select_recordType,
	living_players;
    }
    
    public static ArrayList<Class<?>> getEnums() {
	ArrayList<Class<?>> enums = new ArrayList<Class<?>>();
	enums.add(config.class);
	enums.add(commands.class);
	enums.add(game.class);
	enums.add(arena.class);
	enums.add(manage.class);
	enums.add(error.class);
	enums.add(category.class);
	enums.add(dataType.class);
	enums.add(status.class);
	enums.add(inventory.class);
	return enums;
    }
}
