package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;


public class EntityDamageListener implements Listener {
    
    private BukkitTask task;
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
	
	if (event.getDamager() instanceof Player) {
	    ZvPPlayer damager = GameManager.getManager().getPlayer((Player) event.getDamager());
	    
	    if (damager != null && GameManager.getManager().isInGame(damager.getPlayer())) {
		if (damager.hasProtection()) {
		    event.setCancelled(true);
		    return;
		}
		
		if (event.getEntity() instanceof Zombie) {
		    if (damager.getArena().getLivingZombieAmount() < (damager.getArena().getSpawningZombies() * 0.15)) {
			if (this.task != null) {
			    this.task.cancel();
			    ZvP.getPluginLogger().log(Level.FINE, "Zombie Respawn Task killed caused by interaction!", true, true);
			}
			
			final Arena arena = damager.getArena();
			
			this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
			    
			    @Override
			    public void run() {
				
				if (arena.getLivingZombieAmount() < (arena.getSpawningZombies() * 0.15)) {
				    
				    for (Zombie zombie : arena.getLivingZombies()) {
					zombie.teleport(arena.getNewSaveLocation(), TeleportCause.PLUGIN);
				    }
				    ZvP.getPluginLogger().log(Level.FINE, "Zombie teleport caused by no interaction!", true, true);
				    EntityDamageListener.this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), this, 50 * 20L);
				}
			    }
			}, 50 * 20L); // Time to wait until zombie respawn
		    }
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
