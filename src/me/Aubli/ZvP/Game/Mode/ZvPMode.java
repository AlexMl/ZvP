package me.Aubli.ZvP.Game.Mode;

import java.util.Random;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Translation.MessageKeys;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class ZvPMode extends BukkitRunnable {
    
    private Arena arena;
    
    private String name;
    
    protected int taskID;
    
    protected int startDelay;
    
    protected static final double spawnRate = 0.45;
    
    protected final Random rand = new Random(System.currentTimeMillis());
    
    public ZvPMode(Arena arena, String name) {
	this.arena = arena;
	this.name = name;
	// System.out.println("Initialized new " + name + " mode in " + arena.getID());
    }
    
    public String getName() {
	return this.name;
    }
    
    public int getTaskID() {
	return this.taskID;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public void start(int startDelay) {
	this.startDelay = startDelay;
	this.taskID = this.runTaskTimer(ZvP.getInstance(), 0L, 1 * 20L).getTaskId();
    }
    
    public abstract ZvPMode reInitialize();
    
    public void stop() {
	// System.out.println("stop " + this.arena.getID() + " " + getTaskID());
	Bukkit.getScheduler().cancelTask(getTaskID());
    }
    
    /**
     * Called when {@link ZvPPlayer} joins {@link Arena}
     * 
     * @param player
     *        player who joined
     * @param arena
     *        arena which is joined
     */
    public void onJoin(ZvPPlayer player, Arena arena) {
	// NOT IMPLEMENTED IN STANDARD MODE
	return;
    }
    
    /**
     * Called when {@link ZvPPlayer} leaves the game by command or disconnect
     * 
     * @param player
     *        player who left
     */
    public void onLeave(ZvPPlayer player) {
	// NOT IMPLEMENTED IN STANDARD MODE
	return;
    }
    
    /**
     * Called when {@link ZvPPlayer} dies. //TODO deathcause enum?
     * 
     * @param player
     *        player who died
     */
    public void onDeath(ZvPPlayer player, PlayerDeathEvent event) {
	if (this.arena.getConfig().isKeepXP()) {
	    player.getXPManager().setExp(0);
	}
	
	player.die();
	event.setDeathMessage("");
	this.arena.sendMessage(MessageManager.getFormatedMessage(MessageKeys.game.player_died, player.getName()));
    }
    
    /**
     * Called when {@link ZvPPlayer} respawns.
     * 
     * @param player
     *        player who respawned
     */
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
    
    /**
     * Called when a Zombie is killed by a player
     * 
     * @param attacker
     *        the {@link ZvPPlayer} who killed the zombie
     * @param zombie
     *        the zombie {@link Entity} who is killed
     */
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
    
    /**
     * Called when a Player is killed by another player
     * 
     * @param attacker
     *        the {@link ZvPPlayer} who killed the player
     * @param victim
     *        the {@link ZvPPlayer} who is killed by the attacker
     */
    public void onPlayerKill(ZvPPlayer attacker, ZvPPlayer victim) {
	// NOT IMPLEMENTED IN STANDARD MODE
	return;
    }
    
    /**
     * Called when {@link ZvPPlayer} got damaged by an entity
     * 
     * @param player
     *        the player who is damaged
     * @param damager
     *        the entity who damaged the player
     */
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
    
    /**
     * Called when zombie got damaged by an entity
     * 
     * @param damager
     *        the entity causing the damage
     * @param victim
     *        the entity who is damaged
     */
    public void onZombieDamage(ZvPPlayer damager, Entity victim, EntityDamageByEntityEvent event) {
	// NOT IMPLEMENTED IN STANDARD MODE
	return;
    }
    
    public abstract boolean allowPlayerInteraction(ZvPPlayer player);
    
    public boolean allowFullArena() {
	return false;
    }
    
    protected void fireFirework() {
	this.taskID = new BukkitRunnable() {
	    
	    ZvPPlayer winner = getArena().getBestPlayer();
	    
	    int runs = 0;
	    
	    @Override
	    public void run() {
		if (ZvPConfig.getEnableFirework()) {
		    getArena().setPlayerLevel(6 - this.runs);
		    
		    if (this.runs <= 5) {
			for (int i = 0; i < 10; i++) {
			    Firework fw = (Firework) getArena().getWorld().spawnEntity(this.winner.getLocation().clone().add((ZvPMode.this.rand.nextInt(60) - 2.8 * i), ZvPMode.this.rand.nextInt(15), (ZvPMode.this.rand.nextInt(60) - 2.8 * i)), EntityType.FIREWORK);
			    FireworkMeta fwMeta = fw.getFireworkMeta();
			    
			    // Get the type
			    Type effectType;
			    switch (ZvPMode.this.rand.nextInt(4) + 1) {
				case 1:
				    effectType = Type.BALL;
				    break;
				
				case 2:
				    effectType = Type.BALL_LARGE;
				    break;
				
				case 3:
				    effectType = Type.BURST;
				    break;
				
				case 4:
				    effectType = Type.STAR;
				    break;
				
				default:
				    effectType = Type.BALL;
				    break;
			    }
			    
			    // Get our random colours
			    Color c1 = getColor(ZvPMode.this.rand.nextInt(17) + 1);
			    Color c2 = getColor(ZvPMode.this.rand.nextInt(17) + 1);
			    
			    // Create our effect with this
			    FireworkEffect effect = FireworkEffect.builder().flicker(ZvPMode.this.rand.nextBoolean()).withColor(c1).withFade(c2).with(effectType).trail(ZvPMode.this.rand.nextBoolean()).build();
			    
			    // Then apply the effect to the meta
			    fwMeta.addEffect(effect);
			    
			    // Generate some random power and set it
			    fwMeta.setPower(ZvPMode.this.rand.nextInt(2) + 1);
			    fw.setFireworkMeta(fwMeta);
			}
			this.runs++;
		    } else {
			getArena().stop();
			this.cancel();
		    }
		} else {
		    getArena().stop();
		}
	    }
	}.runTaskTimer(ZvP.getInstance(), 1 * 20L, 2 * 20L).getTaskId();
    }
    
    private static Color getColor(int value) {
	
	switch (value) {
	    case 1:
		return Color.AQUA;
	    case 2:
		return Color.BLACK;
	    case 3:
		return Color.BLUE;
	    case 4:
		return Color.FUCHSIA;
	    case 5:
		return Color.GRAY;
	    case 6:
		return Color.GREEN;
	    case 7:
		return Color.LIME;
	    case 8:
		return Color.MAROON;
	    case 9:
		return Color.NAVY;
	    case 10:
		return Color.OLIVE;
	    case 11:
		return Color.ORANGE;
	    case 12:
		return Color.PURPLE;
	    case 13:
		return Color.RED;
	    case 14:
		return Color.SILVER;
	    case 15:
		return Color.TEAL;
	    case 16:
		return Color.WHITE;
	    case 17:
		return Color.YELLOW;
		
	    default:
		return Color.BLUE;
	}
    }
    
    @Override
    public abstract void run();
    
}
