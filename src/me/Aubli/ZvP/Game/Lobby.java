package me.Aubli.ZvP.Game;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Lobby implements Comparable<Lobby> {
    
    private File lobbyFile;
    private FileConfiguration lobbyConfig;
    
    private int lobbyID;
    
    private Location centerLoc;
    
    public Lobby(int lobbyID, String lobbyPath, Location loc) {
	
	this.lobbyID = lobbyID;
	
	this.centerLoc = loc.clone();
	
	this.lobbyFile = new File(lobbyPath + "/" + lobbyID + ".yml");
	this.lobbyConfig = YamlConfiguration.loadConfiguration(this.lobbyFile);
	
	try {
	    this.lobbyFile.createNewFile();
	    save();
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Lobby config: " + e.getMessage(), true, false, e);
	}
    }
    
    public Lobby(File lobbyFile) {
	this.lobbyFile = lobbyFile;
	this.lobbyConfig = YamlConfiguration.loadConfiguration(lobbyFile);
	
	this.lobbyID = this.lobbyConfig.getInt("lobby.ID");
	this.centerLoc = new Location(Bukkit.getWorld(UUID.fromString(this.lobbyConfig.getString("lobby.Location.world"))), this.lobbyConfig.getInt("lobby.Location.X"), this.lobbyConfig.getInt("lobby.Location.Y"), this.lobbyConfig.getInt("lobby.Location.Z"));
    }
    
    private void save() throws IOException {
	this.lobbyConfig.set("lobby.ID", this.lobbyID);
	
	this.lobbyConfig.set("lobby.Location.world", this.centerLoc.getWorld().getUID().toString());
	this.lobbyConfig.set("lobby.Location.X", this.centerLoc.getBlockX());
	this.lobbyConfig.set("lobby.Location.Y", this.centerLoc.getBlockY());
	this.lobbyConfig.set("lobby.Location.Z", this.centerLoc.getBlockZ());
	
	this.lobbyConfig.save(this.lobbyFile);
    }
    
    void delete() {
	this.lobbyFile.delete();
    }
    
    public int getID() {
	return this.lobbyID;
    }
    
    public World getWorld() {
	return this.centerLoc.getWorld();
    }
    
    public Location getLocation() {
	return this.centerLoc;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Lobby) {
	    Lobby otherLobby = (Lobby) obj;
	    if (otherLobby.getID() == this.getID()) {
		if (otherLobby.getLocation().equals(this.getLocation())) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    @Override
    public int compareTo(Lobby o) {
	
	if (getID() == o.getID()) {
	    return 0;
	} else if (getID() > o.getID()) {
	    return 1;
	} else if (getID() < o.getID()) {
	    return -1;
	}
	
	return 0;
    }
}
