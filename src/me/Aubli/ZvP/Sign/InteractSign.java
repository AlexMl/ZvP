package me.Aubli.ZvP.Sign;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class InteractSign implements ISign, Comparable<ISign> {
    
    private int ID;
    
    private File signFile;
    private FileConfiguration signConfig;
    
    private Location signLoc;
    private Sign sign;
    
    private SignType type;
    
    private Lobby lobby;
    private Arena arena;
    
    public InteractSign(Location signLoc, int ID, String path, Arena arena, Lobby lobby) throws Exception {
	this.ID = ID;
	this.signLoc = signLoc.clone();
	
	this.signFile = new File(path + "/" + ID + ".yml");
	this.signConfig = YamlConfiguration.loadConfiguration(this.signFile);
	
	this.type = SignType.INTERACT_SIGN;
	
	this.lobby = lobby;
	this.arena = arena;
	
	try {
	    this.signFile.createNewFile();
	    this.signConfig.set("sign.ID", ID);
	    this.signConfig.set("sign.Type", getType().toString());
	    this.signConfig.set("sign.Arena", arena.getID());
	    this.signConfig.set("sign.Lobby", lobby.getID());
	    
	    this.signConfig.set("sign.Location.world", signLoc.getWorld().getUID().toString());
	    this.signConfig.set("sign.Location.X", signLoc.getBlockX());
	    this.signConfig.set("sign.Location.Y", signLoc.getBlockY());
	    this.signConfig.set("sign.Location.Z", signLoc.getBlockZ());
	    
	    this.signConfig.save(this.signFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while saving " + getType().toString() + " " + ID + ": " + e.getMessage(), true, false, e);
	}
	
	if (signLoc.getBlock().getState() instanceof Sign) {
	    this.sign = (Sign) signLoc.getBlock().getState();
	} else {
	    delete();
	    throw new Exception("Location is not a Sign!");
	}
	update(SignManager.getManager().getColorMap(getType()));
    }
    
    public InteractSign(File signFile) throws Exception {
	this.signFile = signFile;
	this.signConfig = YamlConfiguration.loadConfiguration(signFile);
	
	this.type = SignType.INTERACT_SIGN;
	
	this.ID = this.signConfig.getInt("sign.ID");
	this.signLoc = new Location(Bukkit.getWorld(UUID.fromString(this.signConfig.getString("sign.Location.world"))), this.signConfig.getDouble("sign.Location.X"), this.signConfig.getDouble("sign.Location.Y"), this.signConfig.getDouble("sign.Location.Z"));
	
	if (this.signLoc.getWorld() != null) {
	    if (this.signLoc.getBlock().getState() instanceof Sign) {
		this.sign = (Sign) this.signLoc.getBlock().getState();
		
		this.sign = (Sign) this.signLoc.getBlock().getState();
		
		this.arena = GameManager.getManager().getArena(this.signConfig.getInt("sign.Arena"));
		this.lobby = GameManager.getManager().getLobby(this.signConfig.getInt("sign.Lobby"));
		update(SignManager.getManager().getColorMap(getType()));
	    } else {
		throw new Exception("Location " + this.signLoc.getBlockX() + ":" + this.signLoc.getBlockY() + ":" + this.signLoc.getBlockZ() + " in World " + this.signLoc.getWorld().getName() + " is not a Sign! (File:" + signFile.getAbsolutePath());
	    }
	}
    }
    
    @Override
    public void delete() {
	this.signFile.delete();
    }
    
    @Override
    public int getID() {
	return this.ID;
    }
    
    @Override
    public World getWorld() {
	return this.signLoc.getWorld();
    }
    
    @Override
    public Location getLocation() {
	return this.signLoc.clone();
    }
    
    @Override
    public Sign getSign() {
	return this.sign;
    }
    
    @Override
    public SignType getType() {
	return this.type;
    }
    
    @Override
    public Arena getArena() {
	return this.arena;
    }
    
    @Override
    public Lobby getLobby() {
	return this.lobby;
    }
    
    @Override
    public void update(Map<String, ChatColor> colorMap) {
	this.sign.setLine(0, ZvP.getPrefix().trim());
	
	if (this.arena != null) {
	    this.sign.setLine(1, "Arena: " + this.arena.getID());
	    
	    if (getArena().getStatus() == ArenaStatus.RUNNING || getArena().getStatus() == ArenaStatus.VOTING || getArena().getStatus() == ArenaStatus.BREAKWAITING) {
		this.sign.setLine(2, colorMap.get("statusLineRunning") + getArena().getStatus().getName());
		this.sign.setLine(3, colorMap.get("inProgressLine") + "In Progress!");
	    }
	    if (ZvPConfig.getAllowDuringGameJoin()) {
		this.sign.setLine(2, colorMap.get("statusLineRunning") + getArena().getStatus().getName());
		this.sign.setLine(3, colorMap.get("joinLine") + "[JOIN]");
	    }
	    if (getArena().getStatus() == ArenaStatus.WAITING || getArena().getStatus() == ArenaStatus.STANDBY) {
		this.sign.setLine(2, colorMap.get("statusLineWaiting") + getArena().getStatus().getName());
		this.sign.setLine(3, colorMap.get("joinLine") + "[JOIN]");
	    }
	    if (getArena().getStatus() == ArenaStatus.STOPED) {
		this.sign.setLine(2, colorMap.get("statusLineStoped") + getArena().getStatus().getName());
		this.sign.setLine(3, colorMap.get("statusLineStoped") + "Arena is offline!");
	    }
	} else {
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, ChatColor.DARK_RED + "Arena is not");
	    this.sign.setLine(3, ChatColor.DARK_RED + "available!");
	}
	this.sign.update(true);
    }
    
    @Override
    public int compareTo(ISign o) {
	return getArena().compareTo(o.getArena());
    }
    
}
