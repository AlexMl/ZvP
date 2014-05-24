package me.Aubli.ZvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.Aubli.ZvP.Listeners.BlockBreakListener;
import me.Aubli.ZvP.Listeners.PlayerInteractListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Sign.SignManager;

import org.util.Metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ZvP extends JavaPlugin{

	public static final Logger log = Bukkit.getLogger();
	private static ZvP instance;
	
	public Location zombieZvpStartLoc;	
	
	public static ItemStack tool;
	
	private static String pluginPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_RED + "Z" + ChatColor.DARK_GRAY + "v" + ChatColor.DARK_RED + "P" + ChatColor.DARK_GREEN + "]"  + ChatColor.RESET + " ";
	
	private static int maxPlayers;
	private static int DEFAULT_ROUNDS;
	private static int DEFAULT_WAVES;
	private static int START_DELAY;
	private static int TIME_BETWEEN_WAVES;
	private static int ZOMBIE_SPAWN_RATE;
	
	public HashMap<Player, String> kills = new HashMap<Player, String>();
	public HashMap<Player, String> deaths = new HashMap<Player, String>();
	public HashMap<Player, Location> playerLoc = new HashMap<Player, Location>();
	public HashMap<Player, Integer> experience = new HashMap<Player, Integer>();
	public HashMap<Player, ItemStack[]> playerInventory = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, ItemStack> playerHelmet = new HashMap<Player, ItemStack>();
	public HashMap<Player, ItemStack> playerChestplate = new HashMap<Player, ItemStack>();
	public HashMap<Player, ItemStack> playerLeggings = new HashMap<Player, ItemStack>();
	public HashMap<Player, ItemStack> playerBoots = new HashMap<Player, ItemStack>();
	public ArrayList<Player> imSpiel = new ArrayList<Player>();
	public ArrayList<Player> playerVote = new ArrayList<Player>(); 
		
	public boolean voteZeit = false;
	public boolean start, portOnJoin, storeInventory, changeToSpectator, storeExp, enableKit;
	public boolean welle = false;
	private boolean useMetrics = false;
	private boolean scoreboardSet = false;
	
	public double zombieCash, playerCash, Konto;
	public int gesammtKill =0;
	public int Runde = 1;
	public int Welle = 1;
	public int zombieCount = 0;
	public Scoreboard board;
	public Objective Obj;
	
	@Override	
	public void onDisable() {
		
		for(Player p: Bukkit.getOnlinePlayers()){
			removeTool(p);
		}		
		GameManager.getManager().shutdown();
		
		log.info("[ZombieVsPlayer] Plugin is disabled!");
	}
	
	@Override
	public void onEnable() {
		
		initialize();		
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		Obj = board.getObjective("customm");
		if(Obj==null){
			Obj = board.registerNewObjective("showkills", "customm");
		}		
		log.info("[ZombieVsPlayer] Plugin is enabled!");
	}
	
	
	private void initialize(){
		instance = this;
		loadConfig();		
		setTool();
		
		new MessageManager();
		new GameManager();
		new SignManager();	
		
		registerListeners();
		getCommand("zvp").setExecutor(new ZvPCommands());
		getCommand("test").setExecutor(new ZvPCommands());
		
		if(useMetrics==true){
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();			   
			} catch (IOException e) {
			   log.info("[ZombieVsPlayer] Can't start Metrics! Skip!");
			}
		}
	}	
	
	private void registerListeners(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new SignChangelistener(), this);		
		pm.registerEvents(new BlockBreakListener(), this);				
	}
	
	private void setTool(){
		tool = new ItemStack(Material.STICK);

		List<String> lore = new ArrayList<String>();
		
		ItemMeta toolMeta = tool.getItemMeta();
		toolMeta.setDisplayName(pluginPrefix + ChatColor.BOLD + "Tool");
		toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
		lore.add("Use this tool to add an Arena!");
		toolMeta.setLore(lore);
		
		tool.setItemMeta(toolMeta);
	}
	
	public boolean removeTool(Player player){		
		if(player.getInventory().contains(tool)){
			player.getInventory().removeItem(tool);
			return true;
		}
		return false;		
	}
	
	
	public static ZvP getInstance(){
		return instance;
	}
	
	public static String getPrefix(){
		return pluginPrefix;
	}
	
	public static int getMaxPlayers(){
		return maxPlayers;
	}
	
	public static int getDefaultRounds(){
		return DEFAULT_ROUNDS;
	}
	
	public static int getDefaultWaves(){
		return DEFAULT_WAVES;
	}
	
	public static int getStartDelay(){
		return START_DELAY;
	}
	
	public static int getSpawnRate() {
		return ZOMBIE_SPAWN_RATE;
	}
	
 	public static int getSaveTime() {
 		return TIME_BETWEEN_WAVES;
 	}
	
	
 	public void zomStart(final Player Sender,final int Runden){
		
		Bukkit.getScheduler().cancelAllTasks();

		zombieZvpStartLoc = Sender.getLocation();
		
		start=true;
		Konto = 0;
		
		kills.clear();
		deaths.clear();
		gesammtKill = 0;
		playerVote.clear();
		imSpiel.clear();
		experience.clear();
		playerInventory.clear();
		playerHelmet.clear();
		playerChestplate.clear();
		playerLeggings.clear();
		playerBoots.clear();
		World welt = Sender.getWorld();
		
		welt.setMonsterSpawnLimit(0);
		this.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.NORMAL);
		this.getServer().getWorld(welt.getName()).setTime(15000);		
	
		Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
							
			private Player victim, messagePlayer;
			private World welt = Sender.getWorld();
			
			private Location zombieLoc;
			
			Random randInt = new Random();
			
			int x, z;
			int sekunden = 30;
			
			int count = 0;
			int zombies = 0;
			int rest = 0;
			int userIndex = 0;
			
			boolean status = false;
			boolean zombieSpawn = true;
			boolean firstSpawn = true;	
						
			@Override
			public void run() {		
				if(status==false){
				
					voteZeit=true;
					
					if(sekunden==30){
					//	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "30" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
					}
					if(sekunden==20){
					//	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "20" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
					}
					if(sekunden==10){
					//	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "10" + ChatColor.GOLD + broadcastMessageJoin[0] + "\n" + ChatColor.GREEN + broadcastMessageJoin[1]);
					}
					if(sekunden<6&&sekunden>1){
//						Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + sekunden + ChatColor.GOLD + broadcastMessageJoin[0]);
						voteZeit=false;
					}
					if(sekunden==1){
//						Bukkit.getServer().broadcastMessage(ChatColor.GOLD + broadcastMessageResult[0] + ChatColor.DARK_PURPLE + "1" + ChatColor.GOLD + broadcastMessageJoin[0]);
					}
					if(sekunden==0){
//						Bukkit.getServer().broadcastMessage(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.starting_zombie_event_now"));
						status = true;
						welle = true;
						
						setScoreboard();
						if(imSpiel.size()==0){							
							zomStop(Sender);						
						}else{
							userIndex = randInt.nextInt(imSpiel.size());
							victim = imSpiel.get(userIndex);
						}						
					}
					
					sekunden--;
				}else{
					
					Bukkit.getServer().getWorld(welt.getName()).setTime(15000);
					
					if(firstSpawn==true){
						zombieLoc = victim.getLocation();
						zombieLoc.add(3, 0, 2);
						for(int i=0; i<(Runde*Welle*30 - (int)(Runde*Welle*30*0.15));i++){					
							welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
							zombieCount++;
							firstSpawn = false;
							zombieSpawn = true;
						}
						
					}else{
					if(zombieSpawn==true){
						this.zombieLoc = victim.getLocation();
						
						x = randInt.nextInt(7)-randInt.nextInt(5)*-1;
						z = (randInt.nextInt(7)*-1)-randInt.nextInt(4)*-1;
						
						this.zombieLoc.add(x, 0, z);
						zombieLoc.setY(welt.getHighestBlockYAt(zombieLoc));
						
						welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
						zombieCount++;
						
						if(zombieCount>=Runde*Welle*30){
							zombieSpawn = false;
							firstSpawn = false;
							Sender.sendMessage(ChatColor.DARK_GRAY + "//DEBUG// Limit reached: " + ChatColor.DARK_PURPLE + zombieCount);					
						}
					}
					
					zombies = 0;
					
					for(int i=0; i<welt.getEntities().size();i++){
						if(welt.getEntities().get(i).toString().equalsIgnoreCase("craftzombie")){
							zombies++;
						}
					}					
										
					if(zombies == 0 && zombieSpawn == false && welle == true){
						rest = (Runde*Welle*30)-gesammtKill;
						if(imSpiel.size()==0){
							victim = Sender;
							this.zombieLoc = victim.getLocation();
						}else{
							userIndex = randInt.nextInt(imSpiel.size());
							victim = imSpiel.get(userIndex);
							this.zombieLoc = victim.getLocation();
						}
						
						zombieLoc = victim.getLocation();
						zombieLoc.add(2,0,-3);
						zombieLoc.setY(welt.getHighestBlockYAt(zombieLoc));
						
						for(int i=0;i<rest;i++){
							welt.spawnEntity(zombieLoc, EntityType.ZOMBIE);
						}										
					}
					
					
					if(gesammtKill >= zombieCount){						
						welle = false;
						Bukkit.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.PEACEFUL);
						
						if(count == 1){										
							if(Welle == 3){
								if(Runde>=Runden){
									//Ende des Events
									// Runde 1 in Welle 2 überstanden
									
									for(int y=0; y<playerVote.size();y++){
										messagePlayer = playerVote.get(y);
//										messagePlayer.sendMessage(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1]);
//										messagePlayer.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.zombie_event_won"));
									}
									
									welt.setMonsterSpawnLimit(-1);
									welt.setDifficulty(Difficulty.PEACEFUL);
									welt.setTime(0);
									welt.setWeatherDuration(0);
									Bukkit.getScheduler().cancelAllTasks();
									zomStop(Sender);
									//Das Zombie event wurde gestoppt
									//Bukkit.getServer().broadcastMessage(ChatColor.AQUA + messageFileConfiguration.getString("config.messages.zombie_event_stopped"));
								}else{
									Bukkit.getServer().getWorld(welt.getName()).setTime(15000);
									
									// Runde 1 in Welle 2 überstanden
//									sendMessageJoinedPlayers(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1], Sender);
									
									//Kontostand beträgt:
//									sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " +  ChatColor.DARK_PURPLE + (int) Konto + ChatColor.DARK_GREEN + "$", Sender);
									
									//Zombie Kill ausgabe
									for(int y=0; y<playerVote.size();y++){
										messagePlayer = playerVote.get(y);
										kills.get(messagePlayer);
//										messagePlayer.sendMessage(ChatColor.GOLD + zombieKillMessageArray[0] + ChatColor.GRAY + (kills.get(messagePlayer)) + ChatColor.GOLD + zombieKillMessageArray[1]);
									}			
									
									//Der kampf geht in die nächste Runde
//									sendMessageJoinedPlayers(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.zombie_event_nextround"), Sender);
									
									//Die nächste Welle startet in ...
//									sendMessageJoinedPlayers(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1], Sender);
									
									Runde++;
									Welle=1;
									}
								}else{
									Bukkit.getServer().getWorld(welt.getName()).setTime(15000);
									// Runde 1 in Welle 2 überstanden
//									sendMessageJoinedPlayers(ChatColor.GOLD + waveSurvivedArray[0] + ChatColor.DARK_PURPLE + Runde + ChatColor.GOLD + waveSurvivedArrayWaves[0] + ChatColor.DARK_PURPLE + Welle + ChatColor.GOLD + waveSurvivedArrayWaves[1], Sender);
									
									//Kontostand beträgt:
//									sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) Konto + ChatColor.DARK_GREEN + "$", Sender);
									
									//Zombie Kill ausgabe
								for(int y=0; y<playerVote.size();y++){
									playerVote.get(y).setHealth(20);
									messagePlayer = playerVote.get(y);
									kills.get(messagePlayer);
//									messagePlayer.sendMessage(ChatColor.GOLD + zombieKillMessageArray[0] + ChatColor.GRAY + (kills.get(messagePlayer)) + ChatColor.GOLD + zombieKillMessageArray[1]);
								}			
								
								//Die nächste Welle startet in ...
//								sendMessageJoinedPlayers(ChatColor.GRAY + nextWaveArray[0] + 60 + nextWaveArray[1], Sender);	
								Welle ++;
								}	
							//count = 59;
							}

						if(count == 40){
							//nur noch 20 sec
//							sendMessageJoinedPlayers(ChatColor.GRAY + timeLeftArray[0] + "20" + timeLeftArray[1], Sender);
						}
						
						if(count == 50){
							//nur noch 10 sec
//							sendMessageJoinedPlayers(ChatColor.GRAY + timeLeftArray[0] + "10" + timeLeftArray[1], Sender);
						}
						
						if(count > 54&& count < 60){
							//nur noch Sekunden
//							sendMessageJoinedPlayers(ChatColor.GRAY + timeLeftArray[0] + (60-count) + timeLeftArray[1], Sender);
						}
						
						if(count == 60){
							
							if(imSpiel.size()==1){
								victim=Sender;
							}else{
								userIndex = randInt.nextInt(imSpiel.size());
								victim = imSpiel.get(userIndex);
							}										
							zombieCount=0;
							gesammtKill = 0;
							zombieSpawn = true;
							firstSpawn = true;
							welle = true;
							count =0;
							welt.setDifficulty(Difficulty.NORMAL);
							
//							sendMessageJoinedPlayers(ChatColor.GRAY + messageFileConfiguration.getString("config.messages.zombie_event_nextwavearrived"), Sender);
							
						}
					count++;
					}			
				//counter++;
				}
			}				
		}
	}, 2L, 20L);
		
		//Das Zombie event wurde gestartet
//		this.getServer().broadcastMessage(ChatColor.AQUA + messageFileConfiguration.getString("config.messages.zombie_event_started"));
	}
	
	public void zomStop(Player Sender){
	
		Bukkit.getScheduler().cancelAllTasks();
		
		World welt = Sender.getWorld();
		
		//INTRESTING
		if(scoreboardSet==true){
			board.clearSlot(DisplaySlot.SIDEBAR);
			board.getTeam("zvpteam").unregister();
	
			if(playerVote.size()==1){
				Sender.setScoreboard(board);
			}else{
				for(int i=0;i<playerVote.size();i++){
					playerVote.get(i).setScoreboard(board);
				}
			}
		}
		
		start=false;
		Konto = 0;
		kills.clear();
		deaths.clear();
		imSpiel.clear();
		gesammtKill = 0;
		playerVote.clear();
		playerInventory.clear();
		playerHelmet.clear();
		playerChestplate.clear();
		playerLeggings.clear();
		playerBoots.clear();
		experience.clear();
		zombieCount = 0;
		
		Runde = 1;
		Welle = 1;
		
		welt.setMonsterSpawnLimit(-1);
		this.getServer().getWorld(welt.getName()).setDifficulty(Difficulty.PEACEFUL);
		this.getServer().getWorld(welt.getName()).setTime(0);
		this.getServer().getWorld(welt.getName()).setWeatherDuration(0);
		
		//Das Zombie event wurde gestoppt
//		this.getServer().broadcastMessage(ChatColor.AQUA + messageFileConfiguration.getString("config.messages.zombie_event_stopped"));	
	}
	
	//INTRESTING
	public void setScoreboard(){

		//Scoreboard setzen
		board.clearSlot(DisplaySlot.SIDEBAR);
		
		Obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Obj.setDisplayName(ChatColor.RED + "Kills");
		
		Team team = board.registerNewTeam("zvpteam");
		
		
		for(int i=0;i<playerVote.size();i++){
			if(playerVote.get(i)!=null){
				team.addPlayer(playerVote.get(i));
				Obj.getScore(playerVote.get(i)).setScore(0);				
			}
		}	
		for(int i=0;i<playerVote.size();i++){
			if(playerVote.get(i)!=null){
				playerVote.get(i).setScoreboard(board);				
			}
		}	
		
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		team.setPrefix(ChatColor.DARK_RED + "");	
		
		scoreboardSet = true;
	}
	
	public void zomjoin(Player playerSender){
		
		Location zombiePvpLoc = null;
		World zombieZvpLocWorld;
		double zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ;
		float zombieZvpLocYaw, zombieZvpLocPitch; 
		
		if(this.getConfig().getString("config.mem.zombieZvPlocation")!=null){	
			
			zombieZvpLocWorld = Bukkit.getServer().getWorld(this.getConfig().getString("config.mem.zombieZvPlocation.world"));
			zombieZvpLocX = this.getConfig().getDouble("config.mem.zombieZvPlocation.x");
			zombieZvpLocY = this.getConfig().getDouble("config.mem.zombieZvPlocation.y");
			zombieZvpLocZ = this.getConfig().getDouble("config.mem.zombieZvPlocation.z");
			zombieZvpLocPitch = (float) this.getConfig().getDouble("config.mem.zombieZvPlocation.pitch");
			zombieZvpLocYaw = (float) this.getConfig().getDouble("config.mem.zombieZvPlocation.yaw");
			
			zombiePvpLoc = new Location(zombieZvpLocWorld, zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ);
			zombiePvpLoc.setYaw(zombieZvpLocYaw);
			zombiePvpLoc.setPitch(zombieZvpLocPitch);
		}else{
			zombiePvpLoc = zombieZvpStartLoc;
		}

		if(imSpiel.contains(playerSender)){
			//Du bist bereits im Spiel
//			playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_already_loged_in"));
		}else{						
			if(playerVote.contains(playerSender)){
				//Du bist bereits im Spiel
//				playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_already_loged_in"));
			
			}else{
				playerVote.add(playerSender);
				imSpiel.add(playerSender);
				
				playerSender.setHealth(20);	
				playerSender.setFoodLevel(20);			
				
				if(storeInventory==true){	
					if(playerSender.getInventory().getContents()!=null){
						
						if(playerSender.getInventory().getHelmet()!=null){
							playerHelmet.put(playerSender, playerSender.getInventory().getHelmet());
							playerSender.getInventory().setHelmet(null);
						}
						if(playerSender.getInventory().getChestplate()!=null){
							playerChestplate.put(playerSender, playerSender.getInventory().getChestplate());
							playerSender.getInventory().setChestplate(null);
						}
						if(playerSender.getInventory().getLeggings()!=null){
							playerLeggings.put(playerSender, playerSender.getInventory().getLeggings());
							playerSender.getInventory().setLeggings(null);
						}
						if(playerSender.getInventory().getBoots()!=null){
							playerBoots.put(playerSender, playerSender.getInventory().getBoots());
							playerSender.getInventory().setBoots(null);
						}
						
						ItemStack[] is = playerSender.getInventory().getContents();
						playerInventory.put(playerSender, is);
						playerSender.getInventory().clear();
						playerSender.updateInventory();
					}
				}
				
				if(portOnJoin == true){
					if(zombiePvpLoc != null){
						if(playerSender.getLocation() != zombiePvpLoc){
							playerLoc.put(playerSender, playerSender.getLocation());
							playerSender.teleport(zombiePvpLoc);
							// Du wurdest teleportiert
//							playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.porting_message"));
						}
					}
				}
				
				if(storeExp==true){
					experience.put(playerSender, playerSender.getLevel());
					playerSender.setExp(0);
					playerSender.setLevel(0);									
				}
								
				//Du bist im Spiel
//				playerSender.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.successfull_joined"));
			}
		}
	}
	
	public void leave(Player playerSender){
		
		if(playerVote.size()==1&&playerVote.get(0).equals(playerSender)){
			zomStop(playerSender);
			return;
		}	
		
		if(storeExp==true){
			if(experience!=null){				
				playerSender.setLevel(0);
				playerSender.setLevel(experience.get(playerSender));				
			}
		}
		
		if(storeInventory==true){
			if(playerInventory!=null){				
				
				playerSender.getInventory().clear();
				playerSender.getInventory().setHelmet(null);
				playerSender.getInventory().setChestplate(null);
				playerSender.getInventory().setLeggings(null);
				playerSender.getInventory().setBoots(null);
					
				playerSender.getInventory().setContents(playerInventory.get(playerSender));
					
				if(playerHelmet.get(playerSender)!=null){
					playerSender.getInventory().setHelmet(playerHelmet.get(playerSender));
				}
				if(playerChestplate.get(playerSender)!=null){
					playerSender.getInventory().setChestplate(playerChestplate.get(playerSender));
				}
				if(playerLeggings.get(playerSender)!=null){
					playerSender.getInventory().setLeggings(playerLeggings.get(playerSender));
				}
				if(playerBoots.get(playerSender)!=null){
					playerSender.getInventory().setBoots(playerBoots.get(playerSender));
				}
				playerSender.updateInventory();
//				playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.inventory_back"));
			}
		}
		
		
		playerSender.removePotionEffect(PotionEffectType.HEAL);
		playerSender.removePotionEffect(PotionEffectType.SPEED);
		playerSender.removePotionEffect(PotionEffectType.REGENERATION);
		playerSender.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			
		playerSender.setHealth(20);
		playerSender.setFoodLevel(20);
		
		
		if(portOnJoin==true){
			if(playerLoc.size()!=0){				
				if(playerLoc.get(playerSender)!=null){
					playerSender.teleport(playerLoc.get(playerSender));
				}				
			}
		}				
		
		if(scoreboardSet==true){
			board.clearSlot(DisplaySlot.SIDEBAR);
			if(board.getTeam("zvpteam").hasPlayer(playerSender)){
				board.getTeam("zvpteam").removePlayer(playerSender);
			}
			
			playerSender.setScoreboard(board);			
		}
		
		playerVote.remove(playerSender);
		playerBoots.remove(playerSender);
		playerChestplate.remove(playerSender);
		playerLeggings.remove(playerSender);
		playerHelmet.remove(playerSender);
		playerLoc.remove(playerSender);
		playerInventory.remove(playerSender);
		kills.remove(playerSender);
		deaths.remove(playerSender);
		experience.remove(playerSender);
		imSpiel.remove(playerSender);
		
//		playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.player_leave"));
	}
	
	public void addKit(Player playerSender, String kitName){
		
		if(this.getConfig().getBoolean("config.starterkit.enable")==false){
			this.getConfig().set("config.starterkit.enable", true);
			enableKit = true;
			this.saveConfig();	
		}	
		
		if(this.getConfig().get("config.starterkit." + kitName)==null){			
			Inventory kitInv = Bukkit.createInventory(playerSender, 9,ChatColor.DARK_BLUE + "ZvP-Kit: " + kitName);
			playerSender.openInventory(kitInv);		
		}else{
			//Kit schon vorhanden
//			playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_kit_already exists"));
		}
	}

	public void sendMessageJoinedPlayers(String message, Player Sender){
		
		Player messagePlayer;
		
		if(playerVote.size()==0){
			Sender.sendMessage(message);
		}
		
		if(playerVote.size()>0){
			for(int y=0; y<playerVote.size();y++){
				messagePlayer = playerVote.get(y);
				messagePlayer.sendMessage(message);
			}
		}			
	}

	private void loadConfig(){
		
		this.getConfig().options().header("\n" +
				"This is the main config file for PlayerVsZombies.\n" +
				"If you want more items that you can sell or buy. Write a comemnt or an ticket on the bukkit-dev site:\n" +
				"http://dev.bukkit.org/bukkit-mods/zombievsplayer/\n");
		
		this.getConfig().addDefault("config.enableMetrics", true);
		this.getConfig().addDefault("config.maximal_Players", 20);
		
		this.getConfig().addDefault("config.rounds", 3);
		this.getConfig().addDefault("config.waves", 5);
		
		this.getConfig().addDefault("config.joinTime", 15);
		this.getConfig().addDefault("config.saveTime", 30);
		
		this.getConfig().addDefault("config.spawnRate", 30);
		
		
		this.getConfig().addDefault("config.misc.ZombieCash", 0.2);
		this.getConfig().addDefault("config.misc.PlayerCash", 2.5);
		this.getConfig().addDefault("config.misc.language", "en");
		this.getConfig().addDefault("config.misc.portOnJoinGame", true);
		this.getConfig().addDefault("config.misc.storeInventory", true);
		this.getConfig().addDefault("config.misc.storeEXPLevel", true);
		this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
		
		
		this.getConfig().addDefault("config.starterkit.enable", true);
		this.getConfig().addDefault("config.starterkit.whichkit", "standardKit");
		this.getConfig().addDefault("config.starterkit.standardKit", "1xSTONE_SWORD, 2xGOLDEN_APPLE, 1xLEATHER_HELMET, 1xLEATHER_LEGGINGS, 1xLEATHER_BOOTS");
		this.getConfig().addDefault("config.starterkit.armorKit", "1xWOOD_SWORD, 1xIRON_CHESTPLATE, 1xIRON_LEGGINGS, 1xIRON_BOOTS, 2xGOLDEN_APPLE");
		this.getConfig().addDefault("config.starterkit.bowKit", "1xBOW, 64xARROW, 64xARROW, 1xLEATHER_HELMET, 1xLEATHER_CHESTPLATE, 1xLEATHER_LEGGINGS, 1xLEATHER_BOOTS, 2xGOLDEN_APPLE");		
		
		this.getConfig().addDefault("config.price.sell.potato", 8);
		this.getConfig().addDefault("config.price.sell.carrot", 7);
		this.getConfig().addDefault("config.price.sell.ironingot", 4);
		this.getConfig().addDefault("config.price.sell.arrow64", 5); 
		this.getConfig().addDefault("config.price.sell.arrow32", 2);
		this.getConfig().addDefault("config.price.sell.rottenflesh64", 1);
		
		this.getConfig().addDefault("config.price.buy.ironSword", 13);
		this.getConfig().addDefault("config.price.buy.stoneSword", 8);
		this.getConfig().addDefault("config.price.buy.woodenSword", 5);
		this.getConfig().addDefault("config.price.buy.stoneAxe", 7); 
		this.getConfig().addDefault("config.price.buy.bow", 12);
		this.getConfig().addDefault("config.price.buy.arrow64", 10);
		this.getConfig().addDefault("config.price.buy.arrow32", 5);
		
		this.getConfig().addDefault("config.price.buy.brewingStand", 20);
		
		this.getConfig().addDefault("config.price.buy.potionStrenght", 15);
		this.getConfig().addDefault("config.price.buy.potionRegeneration", 15);
		this.getConfig().addDefault("config.price.buy.potionHealing", 15);
		this.getConfig().addDefault("config.price.buy.potionSpeed", 15);
		
		this.getConfig().addDefault("config.price.buy.leatherHelmet", 4);
		this.getConfig().addDefault("config.price.buy.leatherChestplate", 8);
		this.getConfig().addDefault("config.price.buy.leatherLeggings", 7);
		this.getConfig().addDefault("config.price.buy.leatherBoots", 4);
		this.getConfig().addDefault("config.price.buy.ironHelmet", 6);
		this.getConfig().addDefault("config.price.buy.ironChestplate", 11);
		this.getConfig().addDefault("config.price.buy.ironLeggings", 9);
		this.getConfig().addDefault("config.price.buy.ironBoots", 6);
		
		maxPlayers = getConfig().getInt("config.maximal_Players");
		useMetrics = getConfig().getBoolean("config.enableMetrics");
		
		DEFAULT_ROUNDS = getConfig().getInt("config.rounds");
		DEFAULT_WAVES = getConfig().getInt("config.waves");
		
		START_DELAY = getConfig().getInt("config.joinTime");
		TIME_BETWEEN_WAVES = getConfig().getInt("config.saveTime");
		
		ZOMBIE_SPAWN_RATE = getConfig().getInt("config.spawnRate");		
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}		
}