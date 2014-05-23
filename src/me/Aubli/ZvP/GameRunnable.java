package me.Aubli.ZvP;

import me.Aubli.ZvP.GameManager.ArenaStatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay){
		this.arena = arena;
		this.startDelay = 5;//startDelay;
	}
	
	private Arena arena;	
	private int startDelay;
	
	private int seconds = 0;
	
	private int currentRound = 0;
	private int currentWave = 0;
	
	private boolean firstSpawn;
	
	@Override
	public void run() {
		//Bukkit.broadcastMessage("\nTaskID: " + this.getTaskId() + "\nArena: " + arena.getID() + "\nPlayers: " + arena.getPlayers().toString() + "\nSeconds: " + seconds);
		if(seconds<=startDelay){
			arena.setStatus(ArenaStatus.WAITING);
			arena.setPlayerLevel(startDelay-seconds);
			
			firstSpawn = true;
		}else if(seconds > startDelay) {
			currentRound++;
			currentWave++;
			
			arena.setStatus(ArenaStatus.RUNNING);			
			arena.setPlayerLevel(0);
		
			if(firstSpawn) {
				arena.spawnZombies(currentRound*currentWave*30);// - (int)(currentRound*currentWave*30*0.2));
				firstSpawn = false;
			}else {
				arena.spawnZombies(1);
				firstSpawn = true;
			}
		
		}

		arena.sendMessage("Arena: " + arena.getID() + " : " + ChatColor.RED + arena.getStatus().toString() + " ; " + startDelay + ":" + seconds);
		
		seconds++;
	}

}
