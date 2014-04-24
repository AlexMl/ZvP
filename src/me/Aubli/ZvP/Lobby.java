package me.Aubli.ZvP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.Aubli.ZvP.Sign.InfoSign;

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
	
	private ArrayList<InfoSign> signs;
	
	
	public Lobby(int lobbyID, String lobbyPath, Location loc){
		
		this.lobbyID = lobbyID;
		
		this.centerLoc = loc.clone();
		
		this.lobbyFile = new File(lobbyPath + "/" + lobbyID + ".yml");
		this.lobbyConfig = YamlConfiguration.loadConfiguration(lobbyFile);
		
		this.signs = new ArrayList<InfoSign>();
		
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
		this.signs = new ArrayList<InfoSign>();
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
	
	public InfoSign[] getSigns(){
		InfoSign[] s = new InfoSign[signs.size()];		
		for(int i=0;i<signs.size();i++){
			s[i] = signs.get(i);
		}
		return s;
	}
	
	
	public void addSign(InfoSign sign){
		signs.add(sign);
	}
	
	public void removeSign(InfoSign sign){
		signs.remove(sign);
	}
	
	public void updateSigns(){
		for(InfoSign sign : getSigns()){
			sign.update();
		}
	}
}
