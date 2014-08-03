package me.Aubli.ZvP.Game;

import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable{
	
	public GameRunnable(Arena arena, int startDelay, int magicSpawnRate){
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
		
		if(seconds<=startDelay){	//Waiting for players
			if(arena.getRound()==0 && arena.getWave()==0) {
				arena.setStatus(ArenaStatus.WAITING);
			}
			
			if(!arena.hasKit()) {
				for(ZvPPlayer p : arena.getPlayers()) {
					if(p.hasKit()) {
						p.sendMessage(MessageManager.getMessage("game:waiting"));
					}
				}
				seconds = 0;
				return;
			}		
			
			arena.setPlayerLevel(startDelay-seconds);
			
			firstSpawn = true;
			spawnZombies = false;
			seconds++;
			return;
		}
		
		if(seconds > startDelay && arena.getPlayers().length>0) {	//game start
			if(arena.getRound()==0 && arena.getWave()==0) { //set game settings
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
					}
				}else {
					spawnZombies = false;
					firstSpawn = false;
				}
			}
			
			if(firstSpawn==false && spawnZombies==false) {
				if(arena.getLivingZombies()==0) {
					boolean stop = false;
					
					if(seconds2==0) {
						arena.updatePlayerBoards();
						stop = arena.next();
					}
					
					if(!stop) {
						arena.sendMessage(MessageManager.getMessage("game:vote_request")); // TODO message
						arena.setStatus(ArenaStatus.VOTING);
						this.cancel();
					}else {			//End of Game				
												
						int kills = arena.getKilledZombies();
						int deaths = 0;
						double money = arena.getBalance();
						
						for(ZvPPlayer p : arena.getPlayers()) {
							deaths += p.getDeaths();
						}
						
						//TODO Message
						arena.sendMessage("Grongrats! You won against the Zombies.");
						arena.sendMessage("You fought against " + kills + " Zombies in " + (arena.getMaxRounds() * arena.getMaxWaves()) + " rounds and have died " + deaths + " times.");					
						arena.sendMessage("The remains of your acquired money (" + Math.round(money) + ") will be donated to ...");
						arena.sendMessage("Thanks for playing!");
						
						arena.stop();
					}
				}
			}
		}else {
			arena.stop();
		}		
	
		if(arena.getLivingZombies()>0) {
			arena.sendMessage("Arena: " + arena.getID() + " : " + ChatColor.RED + arena.getStatus().toString() + "; " + ChatColor.RESET + arena.getRound() + ":" + arena.getWave() + " Z:" + arena.getLivingZombies() + ":" + arena.getKilledZombies());
		}
		arena.getWorld().setTime(15000L);
		seconds++;
	}

}
