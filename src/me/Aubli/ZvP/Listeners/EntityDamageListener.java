package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDamageListener implements Listener {
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
	
	if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
	    
	    Player p1 = (Player) event.getDamager();
	    Player p2 = (Player) event.getEntity();
	    
	    if (p1 != null && p2 != null) {
		if (GameManager.getManager().isInGame(p1) && GameManager.getManager().isInGame(p2)) {
		    if (GameManager.getManager().getPlayer(p1).getArena().equals(GameManager.getManager().getPlayer(p2).getArena())) {
			event.setCancelled(true);
			return;
		    }
		}
	    }
	    
	} else if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
	    Projectile p = (Projectile) event.getDamager();
	    
	    if (p.getShooter() instanceof Player) {
		
		Player p1 = (Player) p.getShooter();
		Player p2 = (Player) event.getEntity();
		
		if (p1 != null && p2 != null) {
		    if (GameManager.getManager().isInGame(p1) && GameManager.getManager().isInGame(p2)) {
			if (GameManager.getManager().getPlayer(p1).getArena().equals(GameManager.getManager().getPlayer(p2).getArena())) {
			    event.setCancelled(true);
			    return;
			}
		    }
		}
	    }
	}
	
    }
}
