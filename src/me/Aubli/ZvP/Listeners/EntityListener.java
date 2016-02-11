package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.Arena.ArenaStatus;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;


public class EntityListener implements Listener {
    
    public static final double ZOMBIEINTERACTIONFACTOR = 0.25;
    
    private GameManager game = GameManager.getManager();
    private BukkitTask task;
    
    public static boolean entityInteraction = true;
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
	if (event.getEntity() instanceof Player) { // Player is victim
	    ZvPPlayer victim = this.game.getPlayer((Player) event.getEntity());
	    
	    if (victim != null) {
		victim.getArena().getArenaMode().onPlayerDamage(victim, null, event);
	    }
	}
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
	
	if (event.getEntity() instanceof Player) { // Player is victim
	    ZvPPlayer victim = this.game.getPlayer((Player) event.getEntity());
	    
	    if (victim != null) {
		victim.getArena().getArenaMode().onPlayerDamage(victim, event.getDamager(), event);
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
	    Player killer = event.getEntity().getKiller();
	    
	    if (this.game.isInGame(killer)) {
		ZvPPlayer zvpKiller = this.game.getPlayer(killer);
		if (event.getEntity().getType() == EntityType.ZOMBIE) {
		    zvpKiller.getArena().getArenaMode().onZombieKill(zvpKiller, event.getEntity(), event);
		    return;
		} else if (event.getEntity().getType() == EntityType.PLAYER) {
		    Player victim = (Player) event.getEntity();
		    
		    if (this.game.isInGame(victim)) {
			ZvPPlayer zvpVictim = this.game.getPlayer(victim);
			if (zvpKiller.getArena().equals(zvpVictim.getArena())) {
			    zvpKiller.getArena().getArenaMode().onPlayerKill(zvpKiller, zvpVictim);
			}
		    }
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
