package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.GameManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener{	
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player eventPlayer = event.getPlayer();		
		GameManager game = GameManager.getManager();
		
		if(game.isInGame(eventPlayer)){
			event.setCancelled(true);
			return;
		}
	}

}
