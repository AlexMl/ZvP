package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerConnectionListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	Player eventPlayer = event.getPlayer();
	
	if (eventPlayer.hasPermission("zvp.update") && ZvP.updateAvailable) {
	    eventPlayer.sendMessage(ZvP.getPrefix() + ZvP.newVersion + " was released. Type " + ChatColor.GOLD + "'/zvp update'" + ChatColor.RESET + " to update automatically.");
	    return;
	}
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player eventPlayer = event.getPlayer();
	
	if (GameManager.getManager().isInGame(eventPlayer)) {
	    GameManager.getManager().removePlayer(GameManager.getManager().getPlayer(eventPlayer));
	    return;
	}
    }
}
