package me.Aubli.ZvP.Game;

import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
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
				if(spawnGoal<arena.getRound()*arena.getWave()*magicSpawnNumber && spawnZombies) {
					double spawn = arena.getRound()*arena.getWave()*magicSpawnNumber;
					ZvP.getPluginLogger().log(Level.FINER, "Arena: " + arena.getID() + " Missing: " + (spawn-arena.getLivingZombies()), true);
						
					if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.17)) && (int)(spawn*0.10)>0) {
						arena.spawnZombies((int)(spawn*0.10));
						spawnGoal += (int)(spawn*0.10);				
					}else if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.12)) && (int)(spawn*0.06)>0) {
						arena.spawnZombies((int)(spawn*0.06));
						spawnGoal += (int)(spawn*0.06);				
					}else if(spawn-arena.getLivingZombies() >= ((int)(spawn*0.08)) && (int)(spawn*0.02)>0) {
						arena.spawnZombies((int)(spawn*0.02));
						spawnGoal += (int)(spawn*0.02);
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
						stop = arena.next(); //Stop checks if the last round is over
					}
					
					if(!stop) {
						arena.setStatus(ArenaStatus.VOTING);
						
						new BukkitRunnable() {
							
							@Override
							public void run() {								
								if(arena.getStatus()==ArenaStatus.VOTING) {
									arena.sendMessage(MessageManager.getMessage("game:vote_request"));
								}else {
									this.cancel();
								}
							}
						}.runTaskTimer(ZvP.getInstance(), 10L, 13*20L);
						this.cancel();
					}else {			//End of Game				
												
						int kills = arena.getKilledZombies();
						int deaths = 0;
						double money = arena.getBalance();
						
						for(ZvPPlayer p : arena.getPlayers()) {
							deaths += p.getDeaths();
						}
						
						String[] donP = MessageManager.getMessage("game:won_messages").split(";");
						int index = new Random().nextInt(donP.length);
						String endMessage = String.format(MessageManager.getMessage("game:won"), kills, (arena.getMaxRounds() * arena.getMaxWaves()), deaths, Math.round(money), donP[index]);
						
						arena.sendMessage(endMessage);
						arena.stop();
					}
				}
			}
		}else {
			arena.stop();
		}		
	
		if(arena.getLivingZombies()>0 && ZvP.getPluginLogger().isDebugMode()) {
			arena.sendMessage("Arena: " + arena.getID() + " : " + ChatColor.RED + arena.getStatus().toString() + "; " + ChatColor.RESET + arena.getRound() + ":" + arena.getWave() + " Z:" + arena.getLivingZombies() + ":" + arena.getKilledZombies());
		}
		arena.getWorld().setTime(15000L);
		seconds++;
	}

}
