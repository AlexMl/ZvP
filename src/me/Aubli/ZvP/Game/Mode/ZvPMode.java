package me.Aubli.ZvP.Game.Mode;

import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Translation.MessageKeys;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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


public class StandardMode extends BukkitRunnable implements IZvPMode {
    
    private Arena arena;
    
    private String name;
    
    private int taskID;
    
    private Random rand;
    private int startDelay;
    private double spawnRate;
    private int seconds = 0;
    private int spawnGoal;
    private boolean firstSpawn;
    private boolean spawnZombies;
    
    public StandardMode(Arena arena, String name) {
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
    
    @Override
    public void start(int startDelay) {
	this.startDelay = startDelay;
	this.spawnRate = 0.45;
	
	this.rand = new Random(System.currentTimeMillis());
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
    public void run() {
	
	if (ZvP.getPluginLogger().isDebugMode() && (ZvP.getPluginLogger().getLogLevel() <= 100)) {
	    this.arena.sendMessage("A:" + this.arena.getID() + " ;" + ChatColor.RED + this.arena.getStatus().toString() + ChatColor.RESET + "; " + this.arena.getCurrentRound() + ":" + this.arena.getCurrentWave() + " Z:" + this.arena.getLivingZombieAmount() + ":" + this.arena.getSpawningZombies() + " FS:" + this.firstSpawn + " SZ:" + this.spawnZombies + " T:" + this.seconds);
	}
	
	if (this.seconds < this.startDelay) {	// Waiting for players
	    if (this.arena.getCurrentRound() == 0 && this.arena.getCurrentWave() == 0) {
		this.arena.setStatus(ArenaStatus.WAITING);
	    }
	    
	    if (!this.arena.hasKit()) {
		this.seconds = 0;
		this.arena.setPlayerLevel(this.startDelay);
		return;
	    }
	    
	    this.arena.setPlayerLevel(this.startDelay - this.seconds);
	    
	    this.seconds++;
	    return;
	}
	
	if (this.seconds == this.startDelay) {  // set game settings
	    if (this.arena.getCurrentRound() == 0 && this.arena.getCurrentWave() == 0) {
		this.arena.initArenaScore(false);
		this.arena.setRound(1);
		this.arena.setWave(1);
	    }
	    this.arena.setPlayerLevel(0);
	    this.arena.setPlayerBoards();
	    this.arena.removePlayerBoards();
	    this.arena.updatePlayerBoards();
	    
	    for (ZvPPlayer player : this.arena.getPlayers()) {
		player.setSpawnProtected(false);
	    }
	    
	    this.arena.setStatus(ArenaStatus.RUNNING);
	    
	    this.firstSpawn = true;
	    this.spawnZombies = false;
	    this.seconds++;
	    return;
	}
	
	if (this.seconds > this.startDelay && this.arena.getPlayers().length > 0) { // actuall game start
	
	    if (this.firstSpawn) {
		final int nextZombies = this.arena.getSpawningZombies();
		this.spawnGoal = nextZombies - (int) (nextZombies * this.spawnRate);
		this.arena.spawnZombies(this.spawnGoal);
		this.firstSpawn = false;
		this.spawnZombies = true;
	    } else {
		// INFO: More zombies will spawn if player joins while firstSpawn or spawnZombies is true
		// could be fixed by setting a global int in firstspawn. Depends on players opinion
		final int nextZombies = this.arena.getSpawningZombies();
		// System.out.println(this.spawnGoal + " < " + nextZombies + " && " + this.spawnZombies);
		if ((this.spawnGoal < nextZombies) && this.spawnZombies) {
		    double missing = nextZombies - this.spawnGoal;
		    ZvP.getPluginLogger().log(this.getClass(), Level.FINER, "Arena: " + this.arena.getID() + " Missing: " + (int) missing, true);
		    
		    if (missing >= ((int) (nextZombies * 0.17)) && ((int) (nextZombies * 0.10)) > 0) {
			this.spawnGoal += this.arena.spawnZombies((int) (nextZombies * 0.10));;
		    } else if (missing >= ((int) (nextZombies * 0.12)) && ((int) (nextZombies * 0.06)) > 0) {
			this.spawnGoal += this.arena.spawnZombies((int) (nextZombies * 0.06));
		    } else if (missing >= ((int) (nextZombies * 0.08)) && ((int) (nextZombies * 0.02)) > 0) {
			this.spawnGoal += this.arena.spawnZombies((int) (nextZombies * 0.02));
		    } else if (missing > this.arena.getConfig().getSpawnRate() && ((int) (this.arena.getConfig().getSpawnRate() * 0.5)) > 0) {
			this.spawnGoal += this.arena.spawnZombies(this.arena.getConfig().getSpawnRate() / 2);
		    } else {
			this.spawnGoal += this.arena.spawnZombies(1);
		    }
		} else {
		    this.spawnZombies = false;
		    this.firstSpawn = false;
		}
	    }
	    
	    if (this.firstSpawn == false && this.spawnZombies == false) {
		
		if (this.arena.getConfig().isAutoWaves()) {
		    if (this.arena.hasNext() && EntityListener.hasInteractionTimeout()) {
			if (!this.arena.getConfig().isVoteSystem()) {
			    if (this.arena.getLivingZombieAmount() < (this.arena.getSpawningZombies() * EntityListener.ZOMBIEINTERACTIONFACTOR)) {
				this.arena.next();
				this.arena.updatePlayerBoards();
				
				ZvP.getPluginLogger().log(getClass(), Level.INFO, "Arena " + this.arena.getID() + " moved into the next wave cause of no zombie interaction!", true, true);
				
				start(this.arena.getConfig().getBreakTime());
				// this.arena.setTaskID(new GameRunnable(this.arena, this.arena.getConfig().getBreakTime()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
				this.cancel();
			    }
			}
		    }
		}
		
		if (this.arena.getLivingZombieAmount() == 0) {
		    
		    this.arena.updatePlayerBoards();
		    boolean stop = this.arena.next(); // Stop checks if the last round is over
		    
		    if (!stop) {
			if (this.arena.getConfig().isVoteSystem()) {
			    this.arena.setStatus(ArenaStatus.VOTING);
			    this.taskID = new BukkitRunnable() {
				
				@Override
				public void run() {
				    
				    if (StandardMode.this.arena.getStatus() == ArenaStatus.VOTING) {
					StandardMode.this.arena.sendMessage(MessageManager.getMessage(game.vote_request));
				    } else {
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 10L, 17 * 20L).getTaskId();
			    
			    this.cancel();
			} else {
			    this.arena.setStatus(ArenaStatus.BREAKWAITING);
			    start(this.arena.getConfig().getBreakTime());
			    // this.arena.setTaskID(new GameRunnable(this.arena, this.arena.getConfig().getBreakTime()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
			    this.cancel();
			}
		    } else { // End of Game
		    
			this.taskID = new BukkitRunnable() {
			    
			    ZvPPlayer winner = StandardMode.this.arena.getBestPlayer();
			    
			    int runs = 0;
			    
			    @Override
			    public void run() {
				if (ZvPConfig.getEnableFirework()) {
				    StandardMode.this.arena.setPlayerLevel(6 - this.runs);
				    
				    if (this.runs <= 5) {
					for (int i = 0; i < 10; i++) {
					    Firework fw = (Firework) StandardMode.this.arena.getWorld().spawnEntity(this.winner.getLocation().clone().add((StandardMode.this.rand.nextInt(60) - 2.8 * i), StandardMode.this.rand.nextInt(15), (StandardMode.this.rand.nextInt(60) - 2.8 * i)), EntityType.FIREWORK);
					    FireworkMeta fwMeta = fw.getFireworkMeta();
					    
					    // Get the type
					    Type effectType;
					    switch (StandardMode.this.rand.nextInt(4) + 1) {
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
					    Color c1 = getColor(StandardMode.this.rand.nextInt(17) + 1);
					    Color c2 = getColor(StandardMode.this.rand.nextInt(17) + 1);
					    
					    // Create our effect with this
					    FireworkEffect effect = FireworkEffect.builder().flicker(StandardMode.this.rand.nextBoolean()).withColor(c1).withFade(c2).with(effectType).trail(StandardMode.this.rand.nextBoolean()).build();
					    
					    // Then apply the effect to the meta
					    fwMeta.addEffect(effect);
					    
					    // Generate some random power and set it
					    fwMeta.setPower(StandardMode.this.rand.nextInt(2) + 1);
					    fw.setFireworkMeta(fwMeta);
					}
					this.runs++;
				    } else {
					StandardMode.this.arena.stop();
					this.cancel();
				    }
				} else {
				    StandardMode.this.arena.stop();
				}
			    }
			}.runTaskTimer(ZvP.getInstance(), 1 * 20L, 2 * 20L).getTaskId();
			
			int kills = this.arena.getKilledZombies();
			double money = this.arena.getScore().getScore(null);
			int deaths = 0;
			
			for (ZvPPlayer p : this.arena.getPlayers()) {
			    deaths += p.getDeaths();
			}
			
			String[] donP = MessageManager.getMessage(game.won_messages).split(";");
			int index = this.rand.nextInt(donP.length);
			String endMessage = MessageManager.getFormatedMessage(game.won, kills, (this.arena.getConfig().getMaxRounds() * this.arena.getConfig().getMaxWaves()), deaths, Math.round(money), donP[index]);
			// TODO change message. Econ doesnt make much sense here
			this.arena.sendMessage(endMessage);
			this.cancel();
			return;
		    }
		}
	    }
	} else {
	    this.arena.stop();
	    return;
	}
	
	this.arena.getWorld().setTime(15000L);
	this.seconds++;
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
}
