package me.Aubli.ZvP;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay){
		this.arena = arena;
		this.startDelay = startDelay;
	}
	
	private Arena arena;	
	private int startDelay;
	
	private int seconds = 0;
	
	@Override
	public void run() {
		Bukkit.broadcastMessage("Arena: " + arena.getID() + "\nPlayers: " + arena.getPlayers().toString() + "\nSeconds: " + seconds);
		if(seconds>=startDelay){
			
		}
		seconds++;
	}

}
