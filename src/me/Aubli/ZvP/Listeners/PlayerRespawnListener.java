package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerRespawnListener implements Listener {
    
    private GameManager game = GameManager.getManager();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
	
	Player eventPlayer = event.getPlayer();
	
	if (this.game.isInGame(eventPlayer)) {
	    final ZvPPlayer player = this.game.getPlayer(eventPlayer);
	    
	    event.setRespawnLocation(player.getArena().getNewRandomLocation());
	    
	    if (player.getArena().getSpawnProtection()) {
		player.setSpawnProtected(true);
		player.sendMessage(MessageManager.getFormatedMessage("game:spawn_protection_enabled", player.getArena().getProtectionDuration()));
		
		Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		    
		    @Override
		    public void run() {
			player.setSpawnProtected(false);
			player.sendMessage(MessageManager.getMessage("game:spawn_protection_over"));
		    }
		}, player.getArena().getProtectionDuration() * 20L);
	    }
	    return;
	}
    }
}
