package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener{
	
	private GameManager game = GameManager.getManager();
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		
		Player eventPlayer = event.getPlayer();
	
		if(game.isInGame(eventPlayer)) {
			ZvPPlayer player = game.getPlayer(eventPlayer);
			
			event.setRespawnLocation(player.getArena().getNewRandomLocation());
			return;
		}
	}
}
