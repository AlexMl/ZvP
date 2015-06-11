package me.Aubli.ZvP.Game;

import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class GameRunnable extends BukkitRunnable {
    
    public GameRunnable(Arena arena, int startDelay) {
	this.arena = arena;
	this.startDelay = startDelay;
	this.spawnRate = 0.45;
	
	this.rand = new Random(System.currentTimeMillis());
    }
    
    private Arena arena;
    
    private Random rand;
    
    private final int startDelay;
    
    private final double spawnRate;
    
    private int seconds = 0;
    private int spawnGoal;
    
    private boolean firstSpawn;
    private boolean spawnZombies;
    
    @Override
    public void run() {
	
	if (ZvP.getPluginLogger().isDebugMode()) {
	    this.arena.sendMessage("A:" + this.arena.getID() + " ;" + ChatColor.RED + this.arena.getStatus().toString() + ChatColor.RESET + "; " + this.arena.getRound() + ":" + this.arena.getWave() + " Z:" + this.arena.getLivingZombieAmount() + ":" + this.arena.getSpawningZombies() + " FS:" + this.firstSpawn + " SZ:" + this.spawnZombies + " T:" + this.seconds);
	}
	
	if (this.seconds < this.startDelay) {	// Waiting for players
	    if (this.arena.getRound() == 0 && this.arena.getWave() == 0) {
		this.arena.setStatus(ArenaStatus.WAITING);
	    }
	    
	    if (!this.arena.hasKit()) {
		for (ZvPPlayer p : this.arena.getPlayers()) {
		    if (p.hasKit()) {
			p.sendMessage(MessageManager.getMessage("game:waiting"));
		    }
		}
		this.seconds = 0;
		return;
	    }
	    
	    this.arena.setPlayerLevel(this.startDelay - this.seconds);
	    
	    this.seconds++;
	    return;
	}
	
	if (this.seconds == this.startDelay) {  // set game settings
	    if (this.arena.getRound() == 0 && this.arena.getWave() == 0) {
		this.arena.initArenaScore(false);
		this.arena.setRound(1);
		this.arena.setWave(1);
	    }
	    this.arena.setPlayerLevel(0);
	    this.arena.setPlayerBoards();
	    this.arena.removePlayerBoards();
	    this.arena.updatePlayerBoards();
	    
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
		    } else if (missing > this.arena.getSpawnRate() && ((int) (this.arena.getSpawnRate() * 0.5)) > 0) {
			this.spawnGoal += this.arena.spawnZombies(this.arena.getSpawnRate() / 2);
		    } else {
			this.spawnGoal += this.arena.spawnZombies(1);
		    }
		} else {
		    this.spawnZombies = false;
		    this.firstSpawn = false;
		}
	    }
	    
	    if (this.firstSpawn == false && this.spawnZombies == false) {
		if (this.arena.getLivingZombieAmount() == 0) {
		    
		    this.arena.updatePlayerBoards();
		    boolean stop = this.arena.next(); // Stop checks if the last round is over
		    
		    if (!stop) {
			if (this.arena.useVoteSystem()) {
			    this.arena.setStatus(ArenaStatus.VOTING);
			    this.arena.setTaskID(new BukkitRunnable() {
				
				@Override
				public void run() {
				    
				    if (GameRunnable.this.arena.getStatus() == ArenaStatus.VOTING) {
					GameRunnable.this.arena.sendMessage(MessageManager.getMessage("game:vote_request"));
				    } else {
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 10L, 13 * 20L).getTaskId());
			    
			    this.cancel();
			} else {
			    this.arena.setStatus(ArenaStatus.BREAKWAITING);
			    this.arena.setTaskID(new GameRunnable(GameRunnable.this.arena, this.arena.getArenaBreakTime()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
			    this.cancel();
			}
		    } else { // End of Game
		    
			this.arena.setTaskID(new BukkitRunnable() {
			    
			    ZvPPlayer winner = GameRunnable.this.arena.getBestPlayer();
			    
			    int runs = 0;
			    
			    @Override
			    public void run() {
				if (ZvPConfig.getEnableFirework()) {
				    GameRunnable.this.arena.setPlayerLevel(6 - this.runs);
				    
				    if (this.runs <= 5) {
					for (int i = 0; i < 10; i++) {
					    Firework fw = (Firework) GameRunnable.this.arena.getWorld().spawnEntity(this.winner.getLocation().clone().add((GameRunnable.this.rand.nextInt(60) - 2.8 * i), GameRunnable.this.rand.nextInt(15), (GameRunnable.this.rand.nextInt(60) - 2.8 * i)), EntityType.FIREWORK);
					    FireworkMeta fwMeta = fw.getFireworkMeta();
					    
					    // Get the type
					    Type effectType;
					    switch (GameRunnable.this.rand.nextInt(4) + 1) {
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
					    Color c1 = GameRunnable.getColor(GameRunnable.this.rand.nextInt(17) + 1);
					    Color c2 = GameRunnable.getColor(GameRunnable.this.rand.nextInt(17) + 1);
					    
					    // Create our effect with this
					    FireworkEffect effect = FireworkEffect.builder().flicker(GameRunnable.this.rand.nextBoolean()).withColor(c1).withFade(c2).with(effectType).trail(GameRunnable.this.rand.nextBoolean()).build();
					    
					    // Then apply the effect to the meta
					    fwMeta.addEffect(effect);
					    
					    // Generate some random power and set it
					    fwMeta.setPower(GameRunnable.this.rand.nextInt(2) + 1);
					    fw.setFireworkMeta(fwMeta);
					}
					this.runs++;
				    } else {
					GameRunnable.this.arena.stop();
					this.cancel();
				    }
				} else {
				    GameRunnable.this.arena.stop();
				}
			    }
			}.runTaskTimer(ZvP.getInstance(), 1 * 20L, 2 * 20L).getTaskId());
			
			int kills = this.arena.getKilledZombies();
			double money = this.arena.getScore().getScore(null);
			int deaths = 0;
			
			for (ZvPPlayer p : this.arena.getPlayers()) {
			    deaths += p.getDeaths();
			}
			
			String[] donP = MessageManager.getMessage("game:won_messages").split(";");
			int index = this.rand.nextInt(donP.length);
			String endMessage = MessageManager.getFormatedMessage("game:won", kills, (this.arena.getMaxRounds() * this.arena.getMaxWaves()), deaths, Math.round(money), donP[index]);
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
