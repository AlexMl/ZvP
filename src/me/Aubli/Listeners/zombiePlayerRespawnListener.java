package me.Aubli.Listeners;

import me.Aubli.zombie.zombie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class zombiePlayerRespawnListener implements Listener{
	public zombiePlayerRespawnListener(zombie plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		
		Player eventPlayer = event.getPlayer();
		FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		if(plugin.start == true){
			
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
			if(plugin.changeToSpectator==true){
				if(plugin.playerVote.contains(eventPlayer)){
					
					plugin.imSpiel.remove(eventPlayer);
					
					event.setRespawnLocation(specZvpLoc);
					
					eventPlayer.getInventory().clear();
					eventPlayer.getInventory().setHelmet(null);
					eventPlayer.getInventory().setChestplate(null);
					eventPlayer.getInventory().setLeggings(null);
					eventPlayer.getInventory().setBoots(null);
					eventPlayer.updateInventory();						
					
					if(plugin.imSpiel.size()==0){
						plugin.sendMessageJoinedPlayers(ChatColor.DARK_GRAY + messageFileConfiguration.getString("config.messages.zombie_event_lost"), eventPlayer);
						
						if(plugin.portOnJoin==true){
							if(plugin.playerLoc.size()!=0){
								if(plugin.playerLoc.get(eventPlayer)!=null){
									event.setRespawnLocation(plugin.playerLoc.get(eventPlayer));
								}							
							}
						}					
						plugin.zomStop(eventPlayer);		
					}
				}	
			}else{
				event.setRespawnLocation(zombieZvpLoc);
			}
		}
	}
	private zombie plugin;
}
