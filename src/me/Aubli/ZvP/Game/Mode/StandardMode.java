package me.Aubli.ZvP.Game.Mode;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;


public class StandardMode extends ZvPMode {
    
    private int seconds = 0;
    private int spawnGoal;
    private boolean firstSpawn;
    private boolean spawnZombies;
    
    protected StandardMode(Arena arena) {
	super(arena, "STANDARD");
    }
    
    @Override
    public ModeType getType() {
	return ModeType.STANDARD;
    }
    
    @Override
    public ZvPMode reInitialize() {
	return new StandardMode(getArena());
    }
    
    @Override
    public boolean allowPlayerInteraction(ZvPPlayer player) {
	return true;
    }
    
    @Override
    public void run() {
	
	if (ZvP.getPluginLogger().isDebugMode() && (ZvP.getPluginLogger().getLogLevel() <= 100)) {
	    getArena().sendMessage("A:" + getArena().getID() + " ;" + ChatColor.RED + getArena().getStatus().toString() + ChatColor.RESET + "; " + getArena().getCurrentRound() + ":" + getArena().getCurrentWave() + " Z:" + getArena().getLivingZombieAmount() + ":" + getArena().getSpawningZombies() + " FS:" + this.firstSpawn + " SZ:" + this.spawnZombies + " T:" + this.seconds);
	}
	
	if (this.seconds < this.startDelay) {	// Waiting for players
	    if (getArena().getCurrentRound() == 0 && getArena().getCurrentWave() == 0) {
		getArena().setStatus(ArenaStatus.WAITING);
	    }
	    
	    if (!getArena().hasKit()) {
		this.seconds = 0;
		getArena().setPlayerLevel(this.startDelay);
		return;
	    }
	    
	    getArena().setPlayerLevel(this.startDelay - this.seconds);
	    
	    this.seconds++;
	    return;
	}
	
	if (this.seconds == this.startDelay) {  // set game settings
	    if (getArena().getCurrentRound() == 0 && getArena().getCurrentWave() == 0) {
		getArena().initArenaScore(false);
		getArena().setRound(1);
		getArena().setWave(1);
	    }
	    getArena().setPlayerLevel(0);
	    getArena().setPlayerBoards();
	    getArena().removePlayerBoards();
	    getArena().updatePlayerBoards();
	    
	    for (ZvPPlayer player : getArena().getPlayers()) {
		player.setSpawnProtected(false);
	    }
	    
	    getArena().setStatus(ArenaStatus.RUNNING);
	    
	    this.firstSpawn = true;
	    this.spawnZombies = false;
	    this.seconds++;
	    return;
	}
	
	if (this.seconds > this.startDelay && getArena().getPlayers().length > 0) { // actuall game start
	
	    if (this.firstSpawn) {
		final int nextZombies = getArena().getSpawningZombies();
		this.spawnGoal = nextZombies - (int) (nextZombies * spawnRate);
		getArena().spawnZombies(this.spawnGoal);
		this.firstSpawn = false;
		this.spawnZombies = true;
	    } else {
		// INFO: More zombies will spawn if player joins while firstSpawn or spawnZombies is true
		// could be fixed by setting a global int in firstspawn. Depends on players opinion
		final int nextZombies = getArena().getSpawningZombies();
		// System.out.println(this.spawnGoal + " < " + nextZombies + " && " + this.spawnZombies);
		if ((this.spawnGoal < nextZombies) && this.spawnZombies) {
		    double missing = nextZombies - this.spawnGoal;
		    ZvP.getPluginLogger().log(this.getClass(), Level.FINER, "Arena: " + getArena().getID() + " Missing: " + (int) missing, true);
		    
		    if (missing >= ((int) (nextZombies * 0.17)) && ((int) (nextZombies * 0.10)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.10));;
		    } else if (missing >= ((int) (nextZombies * 0.12)) && ((int) (nextZombies * 0.06)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.06));
		    } else if (missing >= ((int) (nextZombies * 0.08)) && ((int) (nextZombies * 0.02)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.02));
		    } else if (missing > getArena().getConfig().getSpawnRate() && ((int) (getArena().getConfig().getSpawnRate() * 0.5)) > 0) {
			this.spawnGoal += getArena().spawnZombies(getArena().getConfig().getSpawnRate() / 2);
		    } else {
			this.spawnGoal += getArena().spawnZombies(1);
		    }
		} else {
		    this.spawnZombies = false;
		    this.firstSpawn = false;
		}
	    }
	    
	    if (this.firstSpawn == false && this.spawnZombies == false) {
		
		if (getArena().getConfig().isAutoWaves()) {
		    if (getArena().hasNext() && EntityListener.hasInteractionTimeout()) {
			if (!getArena().getConfig().isVoteSystem()) {
			    if (getArena().getLivingZombieAmount() < (getArena().getSpawningZombies() * EntityListener.ZOMBIEINTERACTIONFACTOR)) {
				getArena().next();
				getArena().updatePlayerBoards();
				
				ZvP.getPluginLogger().log(getClass(), Level.INFO, "Arena " + getArena().getID() + " moved into the next wave cause of no zombie interaction!", true, true);
				
				getArena().reStart(getArena().getConfig().getBreakTime());
				// getArena().setTaskID(new GameRunnable(getArena(), getArena().getConfig().getBreakTime()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
				this.cancel();
			    }
			}
		    }
		}
		
		if (getArena().getLivingZombieAmount() == 0) {
		    
		    getArena().updatePlayerBoards();
		    boolean stop = getArena().next(); // Stop checks if the last round is over
		    
		    if (!stop) {
			if (getArena().getConfig().isVoteSystem()) {
			    getArena().setStatus(ArenaStatus.VOTING);
			    this.taskID = new BukkitRunnable() {
				
				@Override
				public void run() {
				    
				    if (getArena().getStatus() == ArenaStatus.VOTING) {
					getArena().sendMessage(MessageManager.getMessage(game.vote_request));
				    } else {
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 10L, 17 * 20L).getTaskId();
			    
			    this.cancel();
			} else {
			    getArena().setStatus(ArenaStatus.BREAKWAITING);
			    getArena().reStart(getArena().getConfig().getBreakTime());
			    // getArena().setTaskID(new GameRunnable(getArena(), getArena().getConfig().getBreakTime()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
			    this.cancel();
			}
		    } else { // End of Game
			fireFirework();
			
			double money = getArena().getScore().getScoreDiffSum();
			int deaths = 0;
			
			for (ZvPPlayer p : getArena().getPlayers()) {
			    deaths += p.getDeaths();
			}
			
			String[] donP = MessageManager.getMessage(game.won_messages).split(";");
			int index = this.rand.nextInt(donP.length);
			String endMessage = MessageManager.getFormatedMessage(game.won, getArena().getKilledZombies(), (getArena().getConfig().getMaxRounds() * getArena().getConfig().getMaxWaves()), deaths, Math.round(money), donP[index]);
			getArena().sendMessage(endMessage);
			this.cancel();
			return;
		    }
		}
	    }
	} else {
	    getArena().stop();
	    return;
	}
	
	getArena().getWorld().setTime(15000L);
	this.seconds++;
    }
    
}
