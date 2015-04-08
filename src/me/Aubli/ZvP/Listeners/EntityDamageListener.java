package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDamageListener implements Listener {
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
	
	if (event.getDamager() instanceof Player) {
	    ZvPPlayer damager = GameManager.getManager().getPlayer((Player) event.getDamager());
	    
	    if (damager != null && GameManager.getManager().isInGame(damager.getPlayer())) {
		if (damager.hasProtection()) {
		    event.setCancelled(true);
		    return;
		}
	    }
	}
	
	if (event.getEntity() instanceof Player) {
	    
	    ZvPPlayer victim = GameManager.getManager().getPlayer((Player) event.getEntity());
	    
	    if (victim != null && GameManager.getManager().isInGame(victim.getPlayer())) {
		if (victim.hasProtection()) {
		    event.setCancelled(true);
		    return;
		}
	    }
	    
	    if (event.getDamager() instanceof Projectile) {
		Projectile p = (Projectile) event.getDamager();
		
		if (p.getShooter() instanceof Player) {
		    
		    Player p1 = (Player) p.getShooter();
		    
		    if (p1 != null && victim != null) {
			if (GameManager.getManager().isInGame(p1) && GameManager.getManager().isInGame(victim.getPlayer())) {
			    if (GameManager.getManager().getPlayer(p1).getArena().equals(victim.getArena())) {
				event.setCancelled(true);
				return;
			    }
			}
		    }
		}
	    }
	    
	    if (event.getDamager() instanceof Player) {
		Player p1 = (Player) event.getDamager();
		
		if (p1 != null && victim != null) {
		    if (GameManager.getManager().isInGame(p1) && GameManager.getManager().isInGame(victim.getPlayer())) {
			if (GameManager.getManager().getPlayer(p1).getArena().equals(victim.getArena())) {
			    event.setCancelled(true);
			    return;
			}
		    }
		}
	    }
	}
	
    }
}
