package me.Aubli.Listeners;

import me.Aubli.zombie.zombie;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class zombieBlockBreakListener implements Listener{

	public zombieBlockBreakListener(zombie plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		Player eventPlayer = event.getPlayer();
		if(plugin.start==true){
			if(plugin.playerVote.contains(eventPlayer)){
				event.setCancelled(true);	
			}			
		}	
	}
	
	private zombie plugin;
}
