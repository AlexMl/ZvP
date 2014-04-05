package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener{

	public PlayerQuitListener(ZvP plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player eventPlayer = event.getPlayer();
		
		FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		if(plugin.start==true){
			if(plugin.playerVote.contains(eventPlayer)){
				plugin.imSpiel.remove(eventPlayer);
				plugin.playerVote.remove(eventPlayer);
			
				plugin.sendMessageJoinedPlayers(ChatColor.DARK_BLUE + messageFileConfiguration.getString("config.messages.player_leaves_during_game"), eventPlayer);
			}
		}
	}
	private ZvP plugin;
}
