package me.Aubli.ZvP;

import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay){
		this.arena = arena;
		this.startDelay = startDelay;
	}
	
	private Arena arena;
	
	private int startDelay;
	
	
	@Override
	public void run() {
		
	}

}
