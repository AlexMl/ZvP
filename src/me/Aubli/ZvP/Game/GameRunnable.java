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
    
    public GameRunnable(Arena arena, int startDelay, int magicSpawnRate) {
	this.arena = arena;
	this.startDelay = startDelay;
	this.magicSpawnNumber = magicSpawnRate;
	this.spawnRate = 0.45;
	
	this.rand = new Random(System.currentTimeMillis());
    }
    
    private Arena arena;
    
    private Random rand;
    
    private final int startDelay;
    
    private final double spawnRate;
    
    private int seconds = 0;
    private int seconds2 = 0;
    private int spawnGoal;
    
    private int magicSpawnNumber;
    
    private boolean firstSpawn;
    private boolean spawnZombies;
    
    @Override
    public void run() {
	
	if (this.seconds <= this.startDelay) {	// Waiting for players
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
	    
	    this.firstSpawn = true;
	    this.spawnZombies = false;
	    this.seconds++;
	    return;
	}
	
	if (this.seconds > this.startDelay && this.arena.getPlayers().length > 0) {	// game start
	    if (this.arena.getRound() == 0 && this.arena.getWave() == 0) { // set game settings
		this.arena.setPlayerBoards();
		this.arena.removePlayerBoards();
		this.arena.updatePlayerBoards();
		
		this.arena.setStatus(ArenaStatus.RUNNING);
		this.arena.setPlayerLevel(0);
		this.arena.setRound(1);
		this.arena.setWave(1);
		
		this.firstSpawn = true;
		this.spawnZombies = false;
	    }
	    
	    if (this.arena.getStatus() != ArenaStatus.RUNNING) {
		this.arena.setStatus(ArenaStatus.RUNNING);
	    }
	    
	    if (this.firstSpawn) {
		this.arena.spawnZombies(this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber - (int) (this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber * this.spawnRate));
		this.spawnGoal = this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber - (int) (this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber * this.spawnRate);
		this.firstSpawn = false;
		this.spawnZombies = true;
	    } else {
		if (this.spawnGoal < this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber && this.spawnZombies) {
		    double spawn = this.arena.getRound() * this.arena.getWave() * this.magicSpawnNumber;
		    ZvP.getPluginLogger().log(Level.FINER, "Arena: " + this.arena.getID() + " Missing: " + (spawn - this.arena.getLivingZombies()), true);
		    
		    if (spawn - this.arena.getLivingZombies() >= ((int) (spawn * 0.17)) && (int) (spawn * 0.10) > 0) {
			this.arena.spawnZombies((int) (spawn * 0.10));
			this.spawnGoal += (int) (spawn * 0.10);
		    } else if (spawn - this.arena.getLivingZombies() >= ((int) (spawn * 0.12)) && (int) (spawn * 0.06) > 0) {
			this.arena.spawnZombies((int) (spawn * 0.06));
			this.spawnGoal += (int) (spawn * 0.06);
		    } else if (spawn - this.arena.getLivingZombies() >= ((int) (spawn * 0.08)) && (int) (spawn * 0.02) > 0) {
			this.arena.spawnZombies((int) (spawn * 0.02));
			this.spawnGoal += (int) (spawn * 0.02);
		    } else if (spawn - this.arena.getLivingZombies() > this.magicSpawnNumber) {
			this.arena.spawnZombies(this.magicSpawnNumber / 2);
			this.spawnGoal += this.magicSpawnNumber / 2;
		    } else {
			this.arena.spawnZombies(1);
			this.spawnGoal++;
		    }
		} else {
		    this.spawnZombies = false;
		    this.firstSpawn = false;
		}
	    }
	    
	    if (this.firstSpawn == false && this.spawnZombies == false) {
		if (this.arena.getLivingZombies() == 0) {
		    boolean stop = false;
		    
		    if (this.seconds2 == 0) {
			this.arena.updatePlayerBoards();
			stop = this.arena.next(); // Stop checks if the last round is over
		    }
		    
		    if (!stop) {
			if (ZvPConfig.getUseVoteSystem()) {
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
			    this.arena.setTaskID(new GameRunnable(GameRunnable.this.arena, ZvPConfig.getBreakTime(), GameRunnable.this.arena.getSpawnRate()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
			    this.cancel();
			}
		    } else {			// End of Game
		    
			new BukkitRunnable() {
			    
			    ZvPPlayer winner = GameRunnable.this.arena.getBestPlayer();
			    
			    int runs = 0;
			    
			    @Override
			    public void run() {
				
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
				    this.cancel();
				}
			    }
			}.runTaskTimer(ZvP.getInstance(), 2 * 20L, 2 * 20L);
			
			int kills = this.arena.getKilledZombies();
			int deaths = 0;
			double money = this.arena.getScore().getScore(null);
			
			for (ZvPPlayer p : this.arena.getPlayers()) {
			    deaths += p.getDeaths();
			}
			
			String[] donP = MessageManager.getMessage("game:won_messages").split(";");
			int index = this.rand.nextInt(donP.length);
			String endMessage = String.format(MessageManager.getMessage("game:won"), kills, (this.arena.getMaxRounds() * this.arena.getMaxWaves()), deaths, Math.round(money), donP[index]);
			
			this.arena.sendMessage(endMessage);
			this.arena.stop();
			return;
		    }
		}
	    }
	} else {
	    this.arena.stop();
	    return;
	}
	
	if (this.arena.getLivingZombies() > 0 && ZvP.getPluginLogger().isDebugMode()) {
	    this.arena.sendMessage("Arena: " + this.arena.getID() + " : " + ChatColor.RED + this.arena.getStatus().toString() + "; " + ChatColor.RESET + this.arena.getRound() + ":" + this.arena.getWave() + " Z:" + this.arena.getLivingZombies() + ":" + this.arena.getKilledZombies());
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
