package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
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
    
    public static final double ZOMBIEINTERACTIONFACTOR = 0.25;
    
    private GameManager game = GameManager.getManager();
    private BukkitTask task;
    
    private static boolean entityInteraction = true;
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
	
	if (event.getEntity() instanceof Player) { // Player is victim
	    ZvPPlayer victim = this.game.getPlayer((Player) event.getEntity());
	    
	    if (victim != null) {
		if (victim.hasProtection()) { // If player has protection. Cancel all damage
		    event.setCancelled(true);
		    return;
		}
		
		if (event.getDamager() instanceof Player) { // Player vs Player actions
		    ZvPPlayer damager = this.game.getPlayer((Player) event.getDamager());
		    
		    if (damager != null) {
			if (damager.hasProtection()) {
			    event.setCancelled(true);
			    return;
			}
			
			if (damager.getArena().equals(victim.getArena())) {
			    Arena arena = victim.getArena();
			    
			    if (!arena.getConfig().isPlayerVsPlayer()) {
				event.setCancelled(true);
				return;
			    }
			    
			}
		    }
		}
		
		if (event.getDamager() instanceof Projectile) { // player got hit by a projectile
		    if (((Projectile) event.getDamager()).getShooter() instanceof Player) { // projectile came from player
			ZvPPlayer shooter = this.game.getPlayer((Player) ((Projectile) event.getDamager()).getShooter());
			
			if (shooter != null) {
			    if (shooter.getArena().equals(victim.getArena())) {
				Arena arena = victim.getArena();
				
				if (!arena.getConfig().isPlayerVsPlayer()) {
				    event.setCancelled(true);
				    return;
				} else {
				    event.setCancelled(false);
				    return;
				}
			    }
			}
			
			// In case of shooting players outside of the arena
			event.setCancelled(true);
			return;
		    } else {
			event.setCancelled(true);
			return;
		    }
		    
		}
		
		if (event.getDamager() instanceof Zombie) {
		    entityInteraction = true;
		    if (victim.getArena().getConfig().isIncreaseDifficulty()) {
			event.setDamage(event.getDamage() * victim.getArena().getDifficultyTool().getZombieStrengthFactor());
		    }
		}
	    }
	}
	
	if (event.getEntity() instanceof Zombie) { // Zombie is victim
	
	    if (event.getDamager() instanceof Player) {
		ZvPPlayer damager = this.game.getPlayer((Player) event.getDamager());
		
		if (damager != null) {
		    if (damager.hasProtection()) { // Don't hurt zombies if player has Spawnprotection
			entityInteraction = true;
			event.setCancelled(true);
			return;
		    }
		    
		    // Zombie interaction timer
		    if (damager.getArena().getLivingZombieAmount() < (damager.getArena().getSpawningZombies() * ZOMBIEINTERACTIONFACTOR)) {
			if (this.task != null) {
			    this.task.cancel();
			    entityInteraction = true;
			    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Zombie Respawn Task killed caused by interaction!", true, true);
			}
			
			final Arena arena = damager.getArena();
			
			this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
			    
			    @Override
			    public void run() {
				
				if (arena.getStatus() == ArenaStatus.RUNNING) {
				    if (arena.getLivingZombieAmount() < (arena.getSpawningZombies() * ZOMBIEINTERACTIONFACTOR)) {
					entityInteraction = false;
					
					for (Zombie zombie : arena.getLivingZombies()) {
					    zombie.teleport(arena.getArea().getNewUnsaveLocation(arena.getConfig().getSaveRadius() * 1.5 + 2.0 * arena.getDifficulty().getLevel()), TeleportCause.PLUGIN);
					    zombie.setTarget(arena.getRandomPlayer().getPlayer());
					}
					
					EntityListener.this.task = Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), this, 50 * 20L);
					ZvP.getPluginLogger().log(EntityListener.class, Level.FINE, "Zombie teleport caused by no interaction!", true, true);
				    }
				}
			    }
			}, getArenaInteractionTime(arena) * 20L); // Time to wait until zombie respawn
		    }
		}
	    }
	    
	    switch (event.getCause()) {
		case FALL:
		case FALLING_BLOCK:
		case SUFFOCATION:
		    event.setCancelled(true);
		    break;
		
		default:
		    break;
	    }
	}
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
	
	if (event.getEntity().getKiller() != null) {
	    
	    Player eventPlayer = event.getEntity().getKiller();
	    if (this.game.isInGame(eventPlayer)) {
		if (event.getEntity() instanceof Zombie) {
		    
		    final ZvPPlayer player = this.game.getPlayer(eventPlayer);
		    
		    if (player.getArena().getConfig().isKeepXP()) {
			// entity.remove() does cancel xp spawn.
			// --> spawn xp
			
			int droppedExp = (int) Math.ceil((event.getDroppedExp() / 2.0) * player.getArena().getDifficultyTool().getExpFactor());
			
			for (int xp = 0; xp < droppedExp; xp++) {
			    event.getEntity().getWorld().spawn(event.getEntity().getLocation().clone(), ExperienceOrb.class).setExperience(1);
			}
		    }
		    
		    // Remove entity is faster than waiting for Server to do it
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
    
    public static boolean hasInteractionTimeout() {
	return !entityInteraction;
    }
    
    private long getArenaInteractionTime(Arena arena) {
	return getArenaInteractionTime(arena.getSpawningZombies());
    }
    
    public static long getArenaInteractionTime(int spawningZombies) {
	return (long) (Math.floor(Math.floor(spawningZombies / 30.0) * 0.6) * 10 + 20);
    }
}
