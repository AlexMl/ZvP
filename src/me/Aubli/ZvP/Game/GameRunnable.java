package me.Aubli.ZvP.Game;

import me.Aubli.ZvP.Game.GameManager.ArenaStatus;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay, int saveTime, int magicSpawnRate){
		this.arena = arena;
		this.startDelay = 15;//startDelay;
		this.magicSpawnNumber = magicSpawnRate;
		this.saveTime = saveTime;
		this.spawnRate = 0.45;
	}
	
	private Arena arena;	
	
	private final int startDelay;
	private final int saveTime;
	
	private final double spawnRate;
	
	private int seconds = 0;
	private int seconds2 = 0;
	private int spawnGoal;
	
	private int magicSpawnNumber;
	
	private boolean firstSpawn;
	private boolean spawnZombies;
	
	@Override
	public void run() {
		//Bukkit.broadcastMessage("\nTaskID: " + this.getTaskId() + "\nArena: " + arena.getID() + "\nPlayers: " + arena.getPlayers().toString() + "\nSeconds: " + seconds);
		if(seconds<=startDelay){
			arena.setStatus(ArenaStatus.WAITING);
			
			if(!arena.isReady()) {
				for(ZvPPlayer p : arena.getPlayers()) {
					if(p.hasKit()) {
						p.sendMessage("waiting for players to choose a kit"); //TODO message
					}
				}
				seconds = 0;
				return;
			}		
			
			arena.setPlayerLevel(startDelay-seconds);
			
			firstSpawn = true;
		}else if(seconds > startDelay) {
			if(arena.getRound()==0 && arena.getWave()==0) { //start
				arena.setPlayerBoards();
				arena.removePlayerBoards();
				arena.updatePlayerBoards();
				
				arena.setStatus(ArenaStatus.RUNNING);			
				arena.setPlayerLevel(0);
				arena.setRound(1);
				arena.setWave(1);
				
				firstSpawn = true;
				spawnZombies = false;
			}
			
			if(firstSpawn) {
				arena.spawnZombies(arena.getRound()*arena.getWave()*magicSpawnNumber - (int)(arena.getRound()*arena.getWave()*magicSpawnNumber*spawnRate));
				spawnGoal = arena.getRound()*arena.getWave()*magicSpawnNumber - (int)(arena.getRound()*arena.getWave()*magicSpawnNumber*spawnRate);
				firstSpawn = false;
				spawnZombies = true;
			}else {
				System.out.println(spawnGoal + " - " + arena.getRound()*arena.getWave()*magicSpawnNumber);
				if(spawnGoal<arena.getRound()*arena.getWave()*magicSpawnNumber && spawnZombies) {
					double spawn = arena.getRound()*arena.getWave()*magicSpawnNumber;
					System.out.println("Missing: " + (spawn-arena.getLivingZombies()));
						
					if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.17)) && (int)(spawn*0.10)>0) {
						arena.spawnZombies((int)(spawn*0.10));
						spawnGoal += (int)(spawn*0.10);
				//		System.out.println("10% " + (int)(spawn*0.10));
					}else if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.12)) && (int)(spawn*0.06)>0) {
						arena.spawnZombies((int)(spawn*0.06));
						spawnGoal += (int)(spawn*0.06);
				//		System.out.println("6% " + (int)(spawn*0.06));						
					}else if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.08)) && (int)(spawn*0.02)>0) {
						arena.spawnZombies((int)(spawn*0.02));
						spawnGoal += (int)(spawn*0.02);
				//		System.out.println("2% " + (int)(spawn*0.02));
					}else if(spawn-arena.getLivingZombies() > magicSpawnNumber) {
						arena.spawnZombies((int)magicSpawnNumber/2);
						spawnGoal += (int)magicSpawnNumber/2;
					}else {
						arena.spawnZombies(1);	
						spawnGoal++;
					//	System.out.println(1);
					}
				}else {
					spawnZombies = false;
					firstSpawn = false;
				}
			}
			
			if(firstSpawn==false && spawnZombies==false) {
				if(arena.getLivingZombies()==0) {
					
					if(seconds2==0) {
						arena.updatePlayerBoards();
					}
					
					if(seconds2<=saveTime) {		
						arena.setPlayerLevel(saveTime-seconds2);
						arena.sendMessage(saveTime-seconds2 + " Seconds left!");
						seconds2++;
					}else {						
						boolean stop = arena.next();
						
						if(!stop) {
							arena.sendMessage("next wave!");
							firstSpawn = true;
							seconds2 = 0;
						}else {							
							//TODO Get the winner
							//End of the event
							arena.stop();
						}
					}
				}
			}
			
			if(arena.getPlayers().length<1) {
				arena.stop();
			}
			
		}

		if(arena.getLivingZombies()>0) {
			arena.sendMessage("Arena: " + arena.getID() + " : " + ChatColor.RED + arena.getStatus().toString() + "; " + ChatColor.RESET + arena.getRound() + ":" + arena.getWave() + " Z:" + arena.getLivingZombies() + ":" + arena.getKilledZombies());
		}
		arena.getWorld().setTime(15000L);
		seconds++;
	}

}
