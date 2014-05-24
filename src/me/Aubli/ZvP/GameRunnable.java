package me.Aubli.ZvP;

import me.Aubli.ZvP.GameManager.ArenaStatus;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay, int saveTime, int magicSpawnRate){
		this.arena = arena;
		this.startDelay = 5;//startDelay;
		this.spawnRate = magicSpawnRate;
		this.saveTime = saveTime;
	}
	
	private Arena arena;	
	
	private final int startDelay;
	private final int saveTime;
	
	private int seconds = 0;
	private int seconds2 = 0;
	
	private int spawnRate;
	
	private boolean firstSpawn;
	private boolean spawnZombies;
	
	@Override
	public void run() {
		//Bukkit.broadcastMessage("\nTaskID: " + this.getTaskId() + "\nArena: " + arena.getID() + "\nPlayers: " + arena.getPlayers().toString() + "\nSeconds: " + seconds);
		if(seconds<=startDelay){
			arena.setStatus(ArenaStatus.WAITING);
			arena.setPlayerLevel(startDelay-seconds);
			
			firstSpawn = true;
		}else if(seconds > startDelay) {
			if(arena.getRound()==0 && arena.getWave()==0) { //start
				arena.setStatus(ArenaStatus.RUNNING);			
				arena.setPlayerLevel(0);
				arena.setRound(1);
				arena.setWave(1);
				
				firstSpawn = true;
				spawnZombies = false;
			}
			
			if(firstSpawn) {
				arena.spawnZombies(arena.getRound()*arena.getWave()*spawnRate - (int)(arena.getRound()*arena.getWave()*spawnRate*0.3));
				firstSpawn = false;
				spawnZombies = true;
			}else {
				if(arena.getLivingZombies()<arena.getRound()*arena.getWave()*spawnRate && spawnZombies) {
					double spawn = arena.getRound()*arena.getWave()*spawnRate;
					System.out.println("Missing: " + (spawn-arena.getLivingZombies()));
						
					if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.17)) && (int)(spawn*0.10)>0) {
						arena.spawnZombies((int)(spawn*0.10));
						System.out.println("10% " + (int)(spawn*0.10));
					}else if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.12)) && (int)(spawn*0.06)>0) {
						arena.spawnZombies((int)(spawn*0.06));
						System.out.println("6% " + (int)(spawn*0.06));						
					}else  if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.08)) && (int)(spawn*0.02)>0) {
						arena.spawnZombies((int)(spawn*0.02));
						System.out.println("2% " + (int)(spawn*0.02));
					}else {
						arena.spawnZombies(1);	
						System.out.println(1);
					}
				}else {
					spawnZombies = false;
					firstSpawn = false;
				}
			}
			
			if(firstSpawn==false && spawnZombies==false) {
				if(arena.getLivingZombies()==0) {
					if(seconds2<=saveTime) {					
						arena.sendMessage(saveTime-seconds2 + " Seconds left!");
						seconds2++;
					}else {
						arena.sendMessage("next wave!");
						arena.next();
						firstSpawn = true;
						seconds2 = 0;
					}
				}
			}
			
		}

		if(arena.getLivingZombies()>0)arena.sendMessage("Arena: " + arena.getID() + " : " + ChatColor.RED + arena.getStatus().toString() + "; " + ChatColor.RESET + arena.getRound() + ":" + arena.getWave() + " Z:" + arena.getLivingZombies());
		arena.getWorld().setTime(15000L);
		seconds++;
	}

}
