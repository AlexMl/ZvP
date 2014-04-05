package me.Aubli.ZvP;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ZombieRunnableLogic implements Runnable {	
	
	private zombie plugin;
	public ArrayList<String> player = new ArrayList<String>();
	private Player Sender,victim, messagePlayer;
	private World welt;
	private Location zombieLoc;
	private Location zombieLocEye;
	private int Runden,userIndex;
	public int Runde, Welle, zombieCount;
	private boolean voteZeit, welle;
	
	Random randInt = new Random();
	
	public ZombieRunnableLogic(Player Sender, World welt, int Runden,zombie plugin) {
		this.plugin = plugin;
		this.Sender = Sender;
		this.welt = welt;
		this.Runden = Runden;
		this.Runde = 1;
		System.out.println(Runde);
		plugin.Runde = 1;
		System.out.println(plugin.Runde);
		Runde = plugin.Runde;
		plugin.Welle = Welle;
		plugin.zombieCount = zombieCount;
		plugin.voteZeit = voteZeit;
		plugin.welle = welle;
		
		Runde = 1;
		Welle = 1;
		voteZeit = false;
		zombieCount = 0;
		welle = false;
	}
	
	FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
	
	String broadcastMessage = messageFileConfiguration.getString("config.messages.starting_zombie_event"); 
	String zombieKillmessage = messageFileConfiguration.getString("config.messages.zombie_killed_message");
	String nextWave = messageFileConfiguration.getString("config.messages.zombie_event_nextwave");
	String timeLeft = messageFileConfiguration.getString("config.messages.zombie_event_timeleft");
	String waveSurvived = messageFileConfiguration.getString("config.messages.wave_survived");
	
	String[] broadcastMessageResult = broadcastMessage.split("%seconds%");
	String[] broadcastMessageJoin = broadcastMessageResult[1].split("<newLine>");
	String[] zombieKillMessageArray = zombieKillmessage.split("%zombie_kills%");
	String[] nextWaveArray = nextWave.split("%seconds%");
	String[] timeLeftArray = timeLeft.split("%seconds%");
	String[] waveSurvivedArray = waveSurvived.split("%round%");
	String[] waveSurvivedArrayWaves = waveSurvivedArray[1].split("%wave%");
	
	
	int x, z;
	int sekunden = 30;
	int counter = 0;
	int count = 0;
	int zombies = 0;
	int rest = 0;
	
	boolean status = false;
	boolean zombieSpawn = true;
	boolean firstSpawn = true;	
	
	@Override
	public void run() {

		// Sender.sendMessage(zombieCount + " : " + zombie.gesammtKill);
				
		if(status==false){
		
			plugin.voteZeit=true;
			
			if(sekunden==30){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "30" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
			}
			if(sekunden==20){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "20" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
			}
			if(sekunden==10){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "10" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
			}
			if(sekunden<6&&sekunden>1){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + sekunden + ChatColor.GOLD + broadcastMessageJoin[0]);
				plugin.voteZeit=false;
			}
			if(sekunden==1){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "1" + ChatColor.GOLD + broadcastMessageJoin[0]);
			}
			if(sekunden==0){
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.starting_zombie_event_now"));
				status=true;
				welle = true;
				
				if(plugin.playerVote.size()==0){
					victim = Sender;
				}else{
					userIndex = randInt.nextInt(plugin.playerVote.size());
					victim = plugin.playerVote.get(userIndex);
				}
			}

			sekunden--;
		}else{
			
			Bukkit.getServer().getWorld(welt.getName()).setTime(15000);
			
			if(firstSpawn==true){
				this.zombieLoc = victim.getLocation();
				zombieLoc.add(3, 0, 2);
				for(int i=0; i<(Runde*Welle*30 - (int)(Runde*Welle*30*0.18));i++){					
					welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
					zombieCount++;
					firstSpawn = false;
					zombieSpawn = true;
				}
				
			}else{
			if(zombieSpawn==true){
				this.zombieLoc = victim.getLocation();
				
				if(randInt.nextBoolean()){
					x = randInt.nextInt(7)-randInt.nextInt(5)*-1;
					z = (randInt.nextInt(7)*-1)-randInt.nextInt(4)*-1;
				}else{
					x = (randInt.nextInt(7)*-1)-randInt.nextInt(4)*-1;
					z = randInt.nextInt(7)-randInt.nextInt(5)*-1;
				}
				
				this.zombieLoc.add(x, 0, z);
				this.zombieLocEye = zombieLoc.clone();
				this.zombieLocEye.setY(zombieLoc.getY()+1);
				Block b1 = zombieLocEye.getBlock();
				
				if(b1.getType().equals(Material.AIR)){
					welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
					zombieCount++;
				}else{
					welt.spawnEntity(victim.getLocation(), EntityType.ZOMBIE);
					zombieCount++;
					
				}
								
				
				if(zombieCount>=Runde*Welle*30){
					zombieSpawn=false;
					firstSpawn = false;
					Sender.sendMessage("Wir sind voll! " + zombieCount);					
				}
			}
			
			zombies = 0;
			
			for(int i=0; i<welt.getEntities().size();i++){
				if(welt.getEntities().get(i).toString().equalsIgnoreCase("craftzombie")){
					zombies++;
				}
			}
			
			if(zombies == 7 && zombieSpawn == false && welle == true){
				rest = (Runde*Welle*30)-(plugin.gesammtKill+7);
				
				if(rest<zombies&&rest>0){
					Bukkit.getServer().broadcastMessage("Zombieausgleich 7. Stufe! Es spawnen " + rest);
				
					if(plugin.playerVote.size()==0){
						victim = Sender;
					}else{
						userIndex = randInt.nextInt(plugin.playerVote.size());
						victim = plugin.playerVote.get(userIndex);
					}
					zombieLoc = victim.getLocation();
					zombieLoc.add(2,0,-3);
					
					this.zombieLocEye = zombieLoc.clone();
					this.zombieLocEye.setY(zombieLoc.getY()+1);
					Block b1 = zombieLocEye.getBlock();
					
					if(b1.getType().equals(Material.AIR)){
						for(int i=0;i<rest;i++){
							welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
						}
					}else{
						for(int i=0;i<rest;i++){
							welt.spawnEntity(victim.getLocation(), EntityType.ZOMBIE);
						}					
					}
				}
			}
			
			if(zombies == 3 && zombieSpawn == false && welle == true){
				rest = (Runde*Welle*30)-(plugin.gesammtKill+3);
				
				if(rest<zombies&&rest>0){
					Bukkit.getServer().broadcastMessage("Zombieausgleich 3. Stufe! Es spawnen " + rest);
				
					if(plugin.playerVote.size()==0){
						victim = Sender;
					}else{
						userIndex = randInt.nextInt(plugin.playerVote.size());
						victim = plugin.playerVote.get(userIndex);
					}
					zombieLoc = victim.getLocation();
					zombieLoc.add(2,0,-3);
					
					this.zombieLocEye = zombieLoc.clone();
					this.zombieLocEye.setY(zombieLoc.getY()+1);
					Block b1 = zombieLocEye.getBlock();
					
					if(b1.getType().equals(Material.AIR)){
						for(int i=0;i<rest;i++){
							welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
						}
					}else{
						for(int i=0;i<rest;i++){
							welt.spawnEntity(victim.getLocation(), EntityType.ZOMBIE);
						}					
					}
				}
			}
			
			if(zombies == 0 && zombieSpawn == false && welle == true){
				rest = (Runde*Welle*30)-plugin.gesammtKill;
								
				Bukkit.getServer().broadcastMessage("Zombies werden ausgeglichen! Es spawnen " + rest + "!");
				
				if(plugin.playerVote.size()==0){
					victim = Sender;
					this.zombieLoc = victim.getLocation();
				}else{
					userIndex = randInt.nextInt(plugin.playerVote.size());
					victim = plugin.playerVote.get(userIndex);
					this.zombieLoc = victim.getLocation();
				}
				
				zombieLoc = victim.getLocation();
				zombieLoc.add(2,0,-3);
				
				this.zombieLocEye = zombieLoc.clone();
				this.zombieLocEye.setY(zombieLoc.getY()+1);
				Block b1 = zombieLocEye.getBlock();
				
				if(b1.getType().equals(Material.AIR)){
					for(int i=0;i<rest;i++){
						welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
					}
				}else{
					for(int i=0;i<rest;i++){
						welt.spawnEntity(victim.getLocation(), EntityType.ZOMBIE);
					}					
				}				
			}
			
			
			if(plugin.gesammtKill >= zombieCount){
				
				welle = false;
				Bukkit.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.PEACEFUL);
				
				if(count == 1){										
					if(Welle == 3){
						if(Runde>=Runden){
							//Ende des Events
							// Runde 1 in Welle 2 überstanden
							
							for(int y=0; y<plugin.playerVote.size();y++){
								messagePlayer = plugin.playerVote.get(y);
								messagePlayer.sendMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
								messagePlayer.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.zombie_event_won"));
							}
							
							//plugin.getServer().broadcastMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
							//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.zombie_event_won"));

							World welt = Sender.getWorld();
							
							plugin.start=false;
							plugin.Konto = 0;
							plugin.kills.clear();
							plugin.deaths.clear();
							plugin.imSpiel.clear();
							plugin.gesammtKill = 0;
							plugin.playerVote.clear();
							
							Runde = 1;
							Welle = 1;
							counter = 0;
							zombieCount = 0;
							count = 0;
							zombies = 0;
							rest = 0;
							
							status = false;
							zombieSpawn = true;
							firstSpawn =true;	
							welle = false;
							
							welt.setMonsterSpawnLimit(-1);
							Bukkit.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.PEACEFUL);
							Bukkit.getServer().getWorld(welt.getName()).setTime(0);
							Bukkit.getServer().getWorld(welt.getName()).setWeatherDuration(0);
							Bukkit.getScheduler().cancelAllTasks();
							//Das Zombie event wurde gestoppt
							Bukkit.getServer().broadcastMessage(ChatColor.AQUA + messageFileConfiguration.getString("config.messages.zombie_event_stopped"));
						}else{
						plugin.getServer().getWorld(welt.getName()).setTime(15000);
						
						// Runde 1 in Welle 2 überstanden
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
						}	
						//plugin.getServer().broadcastMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
						
						//plugin.getServer().broadcastMessage(ChatColor.GOLD + "Runde "+ ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + " in Welle " + ChatColor.DARK_PURPLE + (Welle) + ChatColor.GOLD + " überstanden!" );
						
						//Kontostand beträgt:
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " +  ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$");
						}	
						
						//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " +  ChatColor.DARK_PURPLE + (int) zombie.Konto + ChatColor.DARK_GREEN + "$");
						
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							plugin.kills.get(messagePlayer);
							//Zombie Kill ausgabe
							messagePlayer.sendMessage(ChatColor.DARK_PURPLE + zombieKillMessageArray[0] + ChatColor.GRAY + (plugin.kills.get(messagePlayer)) + ChatColor.GOLD + zombieKillMessageArray[1]);
						}			
						
						//Der kampf geht in die nächste Runde
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.zombie_event_nextround"));
						}	
						//plugin.getServer().broadcastMessage(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.zombie_event_nextround"));
						
						
						//Die nächste Welle startet in ...
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1]);
						}
						//plugin.getServer().broadcastMessage(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1]);
						
						Runde++;
						Welle=1;
						}
					}else{
						plugin.getServer().getWorld(welt.getName()).setTime(15000);
						// Runde 1 in Welle 2 überstanden
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
						}
						//plugin.getServer().broadcastMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
					
						//Kontostand beträgt:
						for(int y=0; y<plugin.playerVote.size();y++){
							messagePlayer = plugin.playerVote.get(y);
							messagePlayer.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$");
						}
						//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) zombie.Konto + ChatColor.DARK_GREEN + "$");
						
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						plugin.kills.get(messagePlayer);
						//Zombie Kill ausgabe
						messagePlayer.sendMessage(ChatColor.DARK_PURPLE + zombieKillMessageArray[0] + ChatColor.GRAY + (plugin.kills.get(messagePlayer)) + ChatColor.GOLD + zombieKillMessageArray[1]);
					}			
					//Die nächste Welle startet in ...
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						messagePlayer.sendMessage(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1]);
					}	
					//plugin.getServer().broadcastMessage(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1]);
					Welle ++;
					}	
					//count = 59;
					}

				if(count == 40){
					//nur noch 20 sec
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						messagePlayer.sendMessage(ChatColor.GRAY + timeLeftArray[0] + "20" + timeLeftArray[1]);
					}
					//plugin.getServer().broadcastMessage(ChatColor.GRAY + timeLeftArray[0] + "20" + timeLeftArray[1]);
				}
				
				if(count == 50){
					//nur noch 10 sec
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						messagePlayer.sendMessage(ChatColor.GRAY + timeLeftArray[0] + "10" + timeLeftArray[1]);
					}
					//plugin.getServer().broadcastMessage(ChatColor.GRAY + timeLeftArray[0] + "10" + timeLeftArray[1]);
				}
				
				if(count > 54&& count < 60){
					//nur noch Sekunden
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						messagePlayer.sendMessage(ChatColor.GRAY + timeLeftArray[0] + (60-count) + timeLeftArray[1]);
					}
					//plugin.getServer().broadcastMessage(ChatColor.GRAY + timeLeftArray[0] + (60-count) + timeLeftArray[1]);
				}
				
				if(count == 60){
					
					if(plugin.playerVote.size()==0){
						victim=Sender;
					}else{
						userIndex = randInt.nextInt(plugin.playerVote.size());
						victim = plugin.playerVote.get(userIndex);
					}										
					zombieCount=0;
					plugin.gesammtKill = 0;
					zombieSpawn = true;
					firstSpawn = true;
					welle = true;
					count =0;
					Bukkit.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.NORMAL);
					
					for(int y=0; y<plugin.playerVote.size();y++){
						messagePlayer = plugin.playerVote.get(y);
						messagePlayer.sendMessage(ChatColor.GRAY + messageFileConfiguration.getString("config.messages.zombie_event_nextwavearrived"));
					}
					
					//plugin.getServer().broadcastMessage(ChatColor.GRAY + messageFileConfiguration.getString("config.messages.zombie_event_nextwavearrived"));
				}
				count++;
			}			
			counter++;
			}
		}	
	}
}
