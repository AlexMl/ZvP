package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.zombie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerDeathListener implements Listener{
	
	public PlayerDeathListener(zombie plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		
		FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		String diedByZombie = messageFileConfiguration.getString("config.messages.die_by_zombie");
		String playerDied = messageFileConfiguration.getString("config.messages.player_died");
		
		String[] diedByZombieArray = diedByZombie.split("%deaths%");
		String[] playerDiedArray = playerDied.split("%player%");
		
		Location specZvpLoc = null;
		Location zombieZvpLoc = null;
		World specZvpLocWorld, zombieZvpLocWorld;
		double specZvpLocX, specZvpLocY, specZvpLocZ, zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ;
		float specZvpLocYaw, specZvpLocPitch, zombieZvpLocYaw, zombieZvpLocPitch; 
		
		if(plugin.changeToSpectator == true){
			if(plugin.getConfig().getString("config.mem.spectatorZvPLocation")!=null){			
				specZvpLocWorld = Bukkit.getServer().getWorld(plugin.getConfig().getString("config.mem.spectatorZvPLocation.world"));
				specZvpLocX = plugin.getConfig().getDouble("config.mem.spectatorZvPLocation.x");
				specZvpLocY = plugin.getConfig().getDouble("config.mem.spectatorZvPLocation.y");
				specZvpLocZ = plugin.getConfig().getDouble("config.mem.spectatorZvPLocation.z");
				specZvpLocPitch = (float) plugin.getConfig().getDouble("config.mem.spectatorZvPLocation.pitch");
				specZvpLocYaw = (float) plugin.getConfig().getDouble("config.mem.spectatorZvPLocation.yaw");
			
				specZvpLoc = new Location(specZvpLocWorld, specZvpLocX, specZvpLocY, specZvpLocZ);
				specZvpLoc.setYaw(specZvpLocYaw);
				specZvpLoc.setPitch(specZvpLocPitch);
			}
		}else{
			if(plugin.getConfig().getString("config.mem.zombieZvPlocation")!=null){	
				
				zombieZvpLocWorld = Bukkit.getServer().getWorld(plugin.getConfig().getString("config.mem.zombieZvPlocation.world"));
				zombieZvpLocX = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.x");
				zombieZvpLocY = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.y");
				zombieZvpLocZ = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.z");
				zombieZvpLocPitch = (float) plugin.getConfig().getDouble("config.mem.zombieZvPlocation.pitch");
				zombieZvpLocYaw = (float) plugin.getConfig().getDouble("config.mem.zombieZvPlocation.yaw");
				
				zombieZvpLoc = new Location(zombieZvpLocWorld, zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ);
				zombieZvpLoc.setYaw(zombieZvpLocYaw);
				zombieZvpLoc.setPitch(zombieZvpLocPitch);
			}else{
				zombieZvpLoc = plugin.zombieZvpStartLoc;
			}
		}
		
		if(plugin.start==true){		
			Player eventPlayer = event.getEntity();
			int deaths;

			if(eventPlayer.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK) && eventPlayer.getKiller()==null){
				
				if(plugin.playerVote.contains(eventPlayer)){	
					
					event.setDeathMessage("");
					
					if(plugin.changeToSpectator==true && plugin.imSpiel.size()!=0){
						plugin.deaths.put(eventPlayer, (Integer.toString(1)));
						if(plugin.Konto < plugin.playerCash){
							plugin.Konto = 0;
						}else{
							plugin.Konto -= plugin.playerCash;
						}
						
						plugin.playerVote.remove(eventPlayer);
						plugin.sendMessageJoinedPlayers(ChatColor.DARK_BLUE + playerDiedArray[0] + ChatColor.GRAY + eventPlayer.getName() + ChatColor.DARK_BLUE + playerDiedArray[1], eventPlayer);
						plugin.playerVote.add(eventPlayer);
						
						event.setDroppedExp(0);
						event.getDrops().clear();
						/*
						plugin.imSpiel.remove(eventPlayer);
						event.setDroppedExp(0);
						//event.getDrops().clear();
						
						Vector tpVelo = new Vector(0, 0, 0);
						
						eventPlayer.setHealth(20);
						eventPlayer.setVelocity(tpVelo);
						eventPlayer.teleport(specZvpLoc);
						
					/*	eventPlayer.getInventory().clear();
						eventPlayer.getInventory().setHelmet(null);
						eventPlayer.getInventory().setChestplate(null);
						eventPlayer.getInventory().setLeggings(null);
						eventPlayer.getInventory().setBoots(null);
						eventPlayer.updateInventory();						
						
						if(plugin.imSpiel.size()==0){
							plugin.sendMessageJoinedPlayers(ChatColor.DARK_GRAY + messageFileConfiguration.getString("config.messages.zombie_event_lost"), eventPlayer);
							plugin.zomStop(eventPlayer);
							plugin.imSpiel.add(eventPlayer);
							
						}
						*/
					}else{					
						if(plugin.deaths.containsKey(eventPlayer)){
							deaths = Integer.parseInt(plugin.deaths.get(eventPlayer));
							deaths++;
							plugin.deaths.put(eventPlayer, (Integer.toString(deaths)));
							//sooft gestorben
							eventPlayer.sendMessage(ChatColor.GOLD + diedByZombieArray[0] + deaths + diedByZombieArray[1]);
							
							if(plugin.Konto < plugin.playerCash){
								plugin.Konto = 0;
							}else{
								plugin.Konto -= plugin.playerCash;
							}
							eventPlayer.setHealth(20);
							if(zombieZvpLoc!=null){
								eventPlayer.teleport(zombieZvpLoc);
							}else{
								eventPlayer.teleport(plugin.zombieZvpStartLoc);
							}
						}else{
							deaths = 1;
							plugin.deaths.put(eventPlayer, (Integer.toString(deaths)));
							
							if(plugin.Konto < plugin.playerCash){
								plugin.Konto = 0;
							}else{
								plugin.Konto -= plugin.playerCash;
							}
							
							eventPlayer.setHealth(20);
							if(zombieZvpLoc!=null){
								eventPlayer.teleport(zombieZvpLoc);
							}else{
								eventPlayer.teleport(plugin.zombieZvpStartLoc);
							}
						}
					}
				}
			}			
		}		
	}	
	private zombie plugin;
}
