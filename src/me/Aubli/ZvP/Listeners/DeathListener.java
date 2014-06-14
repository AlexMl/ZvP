package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener{
	
	private Player eventPlayer;
	private GameManager game = GameManager.getManager();
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){	
			
		if(event.getEntity() instanceof Zombie) {
			if(event.getEntity().getKiller()!=null) {
				
				eventPlayer = event.getEntity().getKiller();
				
				if(game.isInGame(eventPlayer)) {
					ZvPPlayer player = game.getPlayer(eventPlayer);
					
					player.addKill();
					return;
				}
			}
		}	
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		
		eventPlayer = event.getEntity();
		
		if(game.isInGame(eventPlayer)) {
			ZvPPlayer player = game.getPlayer(eventPlayer);
			
			player.die();
			
			event.setDeathMessage("");
			player.getArena().sendMessage(player.getName() +  " died!"); //TODO message
			return;
		}
	}
}
