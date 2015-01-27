package me.Aubli.ZvP.Sign;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class InfoSign implements ISign, Comparable<ISign> {
    
    private int ID;
    
    private File signFile;
    private FileConfiguration signConfig;
    
    private Location signLoc;
    private Sign sign;
    
    private SignType type;
    
    private Arena arena;
    private Lobby lobby;
    
    public InfoSign(Location signLoc, int ID, String path, Arena arena, Lobby lobby) throws Exception {
	this.ID = ID;
	this.signLoc = signLoc.clone();
	
	this.signFile = new File(path + "/" + ID + ".yml");
	this.signConfig = YamlConfiguration.loadConfiguration(this.signFile);
	
	this.type = SignType.INFO_SIGN;
	
	this.arena = arena;
	this.lobby = lobby;
	
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
	update();
    }
    
    public InfoSign(File signFile) {
	this.signFile = signFile;
	this.signConfig = YamlConfiguration.loadConfiguration(signFile);
	
	this.type = SignType.INFO_SIGN;
	
	this.ID = this.signConfig.getInt("sign.ID");
	this.signLoc = new Location(Bukkit.getWorld(UUID.fromString(this.signConfig.getString("sign.Location.world"))), this.signConfig.getDouble("sign.Location.X"), this.signConfig.getDouble("sign.Location.Y"), this.signConfig.getDouble("sign.Location.Z"));
	
	if (this.signLoc.getWorld() != null) {
	    this.sign = (Sign) this.signLoc.getBlock().getState();
	    
	    this.arena = GameManager.getManager().getArena(this.signConfig.getInt("sign.Arena"));
	    this.lobby = GameManager.getManager().getLobby(this.signConfig.getInt("sign.Lobby"));
	    update();
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
	return (Sign) getLocation().getBlock().getState();
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
    public void update() {
	if (this.arena != null) {
	    this.sign.setLine(0, ZvP.getPrefix());
	    this.sign.setLine(1, "Arena: " + this.arena.getID());
	    this.sign.setLine(2, ChatColor.AQUA + "" + this.arena.getPlayers().length + ChatColor.RESET + " / " + ChatColor.DARK_RED + this.arena.getMaxPlayers());
	    this.sign.setLine(3, ChatColor.BLUE + "" + this.arena.getRound() + ":" + this.arena.getWave() + ChatColor.RESET + " / " + ChatColor.DARK_RED + this.arena.getMaxRounds() + ":" + this.arena.getMaxWaves());
	    this.sign.update(true);
	} else {
	    this.sign.setLine(0, ZvP.getPrefix());
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, ChatColor.DARK_RED + "Arena is not");
	    this.sign.setLine(3, ChatColor.DARK_RED + "available!");
	    this.sign.update(true);
	}
    }
    
    @Override
    public int compareTo(ISign o) {
	return getArena().compareTo(o.getArena());
    }
    
}
