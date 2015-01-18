package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;


public class DeathListener implements Listener {
    
    private Player eventPlayer;
    private GameManager game = GameManager.getManager();
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
	
	if (event.getEntity().getKiller() != null) {
	    
	    this.eventPlayer = event.getEntity().getKiller();
	    
	    if (this.game.isInGame(this.eventPlayer)) {
		event.setDroppedExp(0);
		final ZvPPlayer player = this.game.getPlayer(this.eventPlayer);
		
		if (event.getEntity() instanceof Zombie) {
		    event.getEntity().remove();
		    
		    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
			
			@Override
			public void run() {
			    player.addKill();
			}
		    }, 5L);
		    
		    return;
		}
	    }
	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	
	this.eventPlayer = event.getEntity();
	
	if (this.game.isInGame(this.eventPlayer)) {
	    ZvPPlayer player = this.game.getPlayer(this.eventPlayer);
	    
	    player.die();
	    
	    event.setDeathMessage("");
	    player.getArena().sendMessage(String.format(MessageManager.getMessage("game:player_died"), player.getName()));
	    return;
	}
    }
}
