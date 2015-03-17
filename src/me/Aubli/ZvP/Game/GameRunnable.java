package me.Aubli.ZvP.Game;

import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;


public class GameRunnable extends BukkitRunnable {
    
    public GameRunnable(Arena arena, int startDelay, int magicSpawnRate) {
	this.arena = arena;
	this.startDelay = startDelay;
	this.magicSpawnNumber = magicSpawnRate;
	this.spawnRate = 0.45;
    }
    
    private Arena arena;
    
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
			    
			    new BukkitRunnable() {
				
				int runs = 0;
				
				@Override
				public void run() {
				    if (this.runs < ZvPConfig.getBreakTime()) {
					GameRunnable.this.arena.setPlayerLevel(ZvPConfig.getBreakTime() - this.runs);
					GameRunnable.this.arena.setTaskID(this.getTaskId());
					this.runs++;
				    } else {
					GameRunnable.this.arena.setTaskID(new GameRunnable(GameRunnable.this.arena, ZvPConfig.getStartDelay(), GameRunnable.this.arena.getSpawnRate()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
					GameRunnable.this.arena.setStatus(ArenaStatus.RUNNING);
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 0L, 1 * 20L);
			    this.cancel();
			}
		    } else {			// End of Game
			
			int kills = this.arena.getKilledZombies();
			int deaths = 0;
			double money = this.arena.getBalance();
			
			for (ZvPPlayer p : this.arena.getPlayers()) {
			    deaths += p.getDeaths();
			}
			
			String[] donP = MessageManager.getMessage("game:won_messages").split(";");
			int index = new Random().nextInt(donP.length);
			String endMessage = String.format(MessageManager.getMessage("game:won"), kills, (this.arena.getMaxRounds() * this.arena.getMaxWaves()), deaths, Math.round(money), donP[index]);
			
			this.arena.sendMessage(endMessage);
			this.arena.stop();
		    }
		}
	    }
	} else {
	    this.arena.stop();
	}
	
	if (this.arena.getLivingZombies() > 0 && ZvP.getPluginLogger().isDebugMode()) {
	    this.arena.sendMessage("Arena: " + this.arena.getID() + " : " + ChatColor.RED + this.arena.getStatus().toString() + "; " + ChatColor.RESET + this.arena.getRound() + ":" + this.arena.getWave() + " Z:" + this.arena.getLivingZombies() + ":" + this.arena.getKilledZombies());
	}
	this.arena.getWorld().setTime(15000L);
	this.seconds++;
    }
    
}
