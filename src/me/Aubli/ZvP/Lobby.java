package me.Aubli.ZvP;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lobby {

	private File lobbyFile;
	private FileConfiguration lobbyConfig;
	
	private int lobbyID;
	
	private Location centerLoc;
	
	public Lobby(int lobbyID, String lobbyPath, Location loc){
		
		this.lobbyID = lobbyID;
		
		this.centerLoc = loc.clone();
		
		this.lobbyFile = new File(lobbyPath + "/" + lobbyID + ".yml");
		this.lobbyConfig = YamlConfiguration.loadConfiguration(lobbyFile);
		
		try {
			lobbyFile.createNewFile();
			save();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public Lobby(File lobbyFile){
		this.lobbyFile = lobbyFile;
		this.lobbyConfig = YamlConfiguration.loadConfiguration(lobbyFile);
		
		this.lobbyID = lobbyConfig.getInt("lobby.ID");
		this.centerLoc = new Location(
				Bukkit.getWorld(lobbyConfig.getString("lobby.Location.world")),
				lobbyConfig.getInt("lobby.Location.X"),
				lobbyConfig.getInt("lobby.Location.Y"),
				lobbyConfig.getInt("lobby.Location.Z"));		
	}
	
	
	void save() throws IOException{		
		lobbyConfig.set("lobby.ID", lobbyID);
			
		lobbyConfig.set("lobby.Location.world", centerLoc.getWorld().getName());
		lobbyConfig.set("lobby.Location.X", centerLoc.getBlockX());
		lobbyConfig.set("lobby.Location.Y", centerLoc.getBlockY());
		lobbyConfig.set("lobby.Location.Z", centerLoc.getBlockZ());
			
		lobbyConfig.save(lobbyFile);			
	}
	
	void delete(){
		this.lobbyFile.delete();
	}

	
	public int getID(){
		return lobbyID;
	}
	
	public World getWorld(){
		return centerLoc.getWorld();
	}
	
	public Location getLocation(){
		return centerLoc;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Lobby){
			Lobby otherLobby = (Lobby) obj;
			if(otherLobby.getID()==this.getID()){
				if(otherLobby.getLocation().equals(this.getLocation())){
					return true;
				}
			}
		}
		return false;
	}
}
