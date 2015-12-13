package me.Aubli.ZvP.Game.Mode;

import java.util.Random;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Translation.MessageKeys;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class ZvPMode extends BukkitRunnable implements IZvPMode {
    
    private Arena arena;
    
    private String name;
    
    protected int taskID;
    
    protected int startDelay;
    
    protected static final double spawnRate = 0.45;
    
    protected final Random rand = new Random(System.currentTimeMillis());
    
    public ZvPMode(Arena arena, String name) {
	this.arena = arena;
	this.name = name;
    }
    
    @Override
    public String getName() {
	return this.name;
    }
    
    @Override
    public int getTaskID() {
	return this.taskID;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    @Override
    public void start(int startDelay) {
	this.startDelay = startDelay;
	
	this.taskID = this.runTaskTimer(ZvP.getInstance(), 0L, 1 * 20L).getTaskId();
    }
    
    @Override
    public void stop() {
	Bukkit.getScheduler().cancelTask(getTaskID());
    }
    
    @Override
    public void onJoin(ZvPPlayer player, Arena arena) {
    }
    
    @Override
    public void onLeave(ZvPPlayer player) {
    }
    
    @Override
    public void onDeath(ZvPPlayer player, PlayerDeathEvent event) {
	if (this.arena.getConfig().isKeepXP()) {
	    player.getXPManager().setExp(0);
	}
	
	player.die();
	event.setDeathMessage("");
	this.arena.sendMessage(MessageManager.getFormatedMessage(MessageKeys.game.player_died, player.getName()));
    }
    
    @Override
    public void onRespawn(final ZvPPlayer player, PlayerRespawnEvent event) {
	
	event.setRespawnLocation(this.arena.getArea().getNewRandomLocation(true));
	
	if (this.arena.getConfig().isSpawnProtection()) {
	    player.setSpawnProtected(true);
	    player.sendMessage(MessageManager.getFormatedMessage(MessageKeys.game.spawn_protection_enabled, player.getArena().getConfig().getProtectionDuration()));
	    
	    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		
		@Override
		public void run() {
		    if (GameManager.getManager().isInGame(player.getPlayer())) {
			player.setSpawnProtected(false);
			player.sendMessage(MessageManager.getMessage(MessageKeys.game.spawn_protection_over));
		    }
		}
	    }, this.arena.getConfig().getProtectionDuration() * 20L);
	}
    }
    
    @Override
    public void onZombieKill(final ZvPPlayer attacker, Entity zombie, EntityDeathEvent event) {
	
	if (this.arena.getConfig().isKeepXP()) {
	    // entity.remove() does cancel xp spawn.
	    // --> spawn xp
	    
	    int droppedExp = (int) Math.ceil((event.getDroppedExp() / 2.0) * this.arena.getDifficultyTool().getExpFactor());
	    
	    for (int xp = 0; xp < droppedExp; xp++) {
		zombie.getWorld().spawn(zombie.getLocation().clone(), ExperienceOrb.class).setExperience(1);
	    }
	}
	
	// Remove entity is faster than waiting for Server to do it
	zombie.remove();
	
	// Task is needed because entity.remove() is asyncron and takes longer
	// therefor the scoreboard gets updated to early and needs to wait!
	Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
	    
	    @Override
	    public void run() {
		attacker.addKill();
	    }
	}, 5L);
	
    }
    
    @Override
    public void onPlayerKill(ZvPPlayer attacker, ZvPPlayer victim) {
    }
    
    @Override
    public void onPlayerDamage(ZvPPlayer player, Entity damager, EntityDamageByEntityEvent event) {
	if (player.hasProtection()) { // If player has protection. Cancel all damage
	    event.setCancelled(true);
	    return;
	}
	
	if (damager instanceof Player) { // Player vs Player actions
	    ZvPPlayer attacker = GameManager.getManager().getPlayer((Player) event.getDamager());
	    
	    if (attacker != null) {
		if (attacker.hasProtection()) {
		    event.setCancelled(true);
		    return;
		}
		
		if (attacker.getArena().equals(this.arena)) {
		    
		    if (!this.arena.getConfig().isPlayerVsPlayer()) {
			event.setCancelled(true);
			return;
		    }
		    
		}
	    }
	}
	
	if (damager instanceof Projectile) { // player got hit by a projectile
	    if (((Projectile) damager).getShooter() instanceof Player) { // projectile came from player
		ZvPPlayer shooter = GameManager.getManager().getPlayer((Player) ((Projectile) damager).getShooter());
		
		if (shooter != null) {
		    if (shooter.getArena().equals(this.arena)) {
			
			if (!this.arena.getConfig().isPlayerVsPlayer()) {
			    event.setCancelled(true);
			    return;
			} else {
			    event.setCancelled(false);
			    return;
			}
		    }
		}
		
		// In case that other players shooting players from outside of the arena
		event.setCancelled(true);
		return;
	    } else {
		event.setCancelled(true);
		return;
	    }
	    
	}
	
	if (damager instanceof Zombie) {
	    EntityListener.entityInteraction = true;
	    if (this.arena.getConfig().isIncreaseDifficulty()) {
		event.setDamage(event.getDamage() * this.arena.getDifficultyTool().getZombieStrengthFactor());
	    }
	}
    }
    
    @Override
    public void onZombieDamage(ZvPPlayer damager, Entity victim, EntityDamageByEntityEvent event) {
    }
    
    @Override
    public abstract void run();
    
}
