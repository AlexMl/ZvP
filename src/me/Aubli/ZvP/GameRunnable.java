package me.Aubli.ZvP;

import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, Lobby lobby, int startDelay){
		this.arena = arena;
		this.lobby = lobby;	
		
		this.startDelay = startDelay;
	}
	
	private Arena arena;
	private Lobby lobby;
	
	private int startDelay;
	
	
	@Override
	public void run() {
		
	}

}
