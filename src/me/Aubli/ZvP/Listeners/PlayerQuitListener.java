package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitListener implements Listener {
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player eventPlayer = event.getPlayer();
	
	if (GameManager.getManager().isInGame(eventPlayer)) {
	    GameManager.getManager().removePlayer(GameManager.getManager().getPlayer(eventPlayer));
	    return;
	}
    }
}
