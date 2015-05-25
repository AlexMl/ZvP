package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;


public class EntityListener implements Listener {
    
    private GameManager game = GameManager.getManager();
    private BukkitTask task;
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
	
	if (event.getDamager() instanceof Player) {
	    ZvPPlayer damager = this.game.getPlayer((Player) event.getDamager());
	    
	    if (damager != null && this.game.isInGame(damager.getPlayer())) {
		if (damager.hasProtection()) {
		    event.setCancelled(true);
		    return;
		}
		
		if (event.getEntity() instanceof Zombie) {
		    if (damager.getArena().getLivingZombieAmount() < (damager.getArena().getSpawningZombies() * 0.25)) {
			if (this.task != null) {
			    this.task.cancel();
			    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Zombie Respawn Task killed caused by interaction!", true, true);
			}
			
			final Arena arena = damager.getArena();
			
			this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
			    
			    @Override
			    public void run() {
				
				if (arena.getLivingZombieAmount() < (arena.getSpawningZombies() * 0.25)) {
				    
				    for (Zombie zombie : arena.getLivingZombies()) {
					zombie.teleport(arena.getArea().getNewUnsaveLocation(arena.getSaveRadius() * 1.5 + 2.0 * arena.getDifficulty().getLevel()), TeleportCause.PLUGIN);
					zombie.setTarget(arena.getRandomPlayer().getPlayer());
				    }
				    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Zombie teleport caused by no interaction!", true, true);
				    EntityListener.this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), this, 50 * 20L);
				}
			    }
			}, 50 * 20L); // Time to wait until zombie respawn
		    }
		}
	    }
	}
	
	if (event.getEntity() instanceof Player) {
	    
	    ZvPPlayer victim = this.game.getPlayer((Player) event.getEntity());
	    
	    if (victim != null && this.game.isInGame(victim.getPlayer())) {
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
			if (this.game.isInGame(p1) && this.game.isInGame(victim.getPlayer())) {
			    if (this.game.getPlayer(p1).getArena().equals(victim.getArena())) {
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
		    if (this.game.isInGame(p1) && this.game.isInGame(victim.getPlayer())) {
			if (this.game.getPlayer(p1).getArena().equals(victim.getArena())) {
			    event.setCancelled(true);
			    return;
			}
		    }
		}
	    }
	}
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
	
	if (event.getEntity().getKiller() != null) {
	    
	    Player eventPlayer = event.getEntity().getKiller();
	    if (this.game.isInGame(eventPlayer)) {
		
		final ZvPPlayer player = this.game.getPlayer(eventPlayer);
		
		if (player.getArena().keepExp()) {
		    // entity.remove() does cancel xp spawn.
		    // --> spawn xp
		    
		    int droppedExp = (int) Math.ceil((event.getDroppedExp() / 2.0) * player.getArena().getDifficultyTool().getExpFactor());
		    
		    for (int xp = 0; xp < droppedExp; xp++) {
			event.getEntity().getWorld().spawn(event.getEntity().getLocation().clone(), ExperienceOrb.class).setExperience(1);
		    }
		}
		
		if (event.getEntity() instanceof Zombie) {
		    event.getEntity().remove();
		    
		    // Task is needed because entity.remove() is asyncron and takes longer
		    // therefor the scoreboard gets updated to early!
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
}
