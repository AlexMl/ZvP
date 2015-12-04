package me.Aubli.ZvP.Sign;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Statistic.DataRecordType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class StatisticListSign implements ISign, Comparable<ISign> {
    
    private int ID;
    
    private File signFile;
    private FileConfiguration signConfig;
    
    private Location signLoc;
    private Sign sign;
    
    private SignType type;
    private StatisticSign mainSign;
    
    private Arena arena;
    private Lobby lobby;
    
    public StatisticListSign(Location signLoc, int ID, String path, Arena arena, Lobby lobby, ISign mainSign) throws Exception {
	this.ID = ID;
	this.signLoc = signLoc.clone();
	
	this.signFile = new File(path + "/" + ID + ".yml");
	this.signConfig = YamlConfiguration.loadConfiguration(this.signFile);
	
	this.arena = arena;
	this.lobby = lobby;
	
	this.mainSign = (StatisticSign) mainSign;
	this.type = SignType.STATISTIC_LIST_SIGN;
	
	try {
	    this.signFile.createNewFile();
	    this.signConfig.set("sign.ID", ID);
	    this.signConfig.set("sign.MainSign", this.mainSign.getID());
	    this.signConfig.set("sign.Type", getType().toString());
	    this.signConfig.set("sign.Arena", arena.getID());
	    this.signConfig.set("sign.Lobby", lobby.getID());
	    
	    this.signConfig.set("sign.Location.world", this.signLoc.getWorld().getUID().toString());
	    this.signConfig.set("sign.Location.X", this.signLoc.getBlockX());
	    this.signConfig.set("sign.Location.Y", this.signLoc.getBlockY());
	    this.signConfig.set("sign.Location.Z", this.signLoc.getBlockZ());
	    
	    this.signConfig.save(this.signFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving " + getType().toString() + " " + ID + ": " + e.getMessage(), true, false, e);
	}
	
	if (this.signLoc.getBlock().getState() instanceof Sign) {
	    this.sign = (Sign) this.signLoc.getBlock().getState();
	} else {
	    delete();
	    throw new Exception("Location is not a Sign!");
	}
	update(SignManager.getManager().getColorMap(getType()));
    }
    
    public StatisticListSign(File signFile) throws Exception {
	this.signFile = signFile;
	this.signConfig = YamlConfiguration.loadConfiguration(signFile);
	
	this.type = SignType.STATISTIC_LIST_SIGN;
	
	this.ID = this.signConfig.getInt("sign.ID");
	this.mainSign = (StatisticSign) SignManager.getManager().getSign(this.signConfig.getInt("sign.MainSign"));
	this.signLoc = new Location(Bukkit.getWorld(UUID.fromString(this.signConfig.getString("sign.Location.world"))), this.signConfig.getDouble("sign.Location.X"), this.signConfig.getDouble("sign.Location.Y"), this.signConfig.getDouble("sign.Location.Z"));
	
	if (this.signLoc.getWorld() != null) {
	    if (this.signLoc.getBlock().getState() instanceof Sign) {
		this.sign = (Sign) this.signLoc.getBlock().getState();
		
		this.arena = GameManager.getManager().getArena(this.signConfig.getInt("sign.Arena"));
		this.lobby = GameManager.getManager().getLobby(this.signConfig.getInt("sign.Lobby"));
		update(SignManager.getManager().getColorMap(getType()));
	    } else {
		throw new Exception("Location " + this.signLoc.getBlockX() + ":" + this.signLoc.getBlockY() + ":" + this.signLoc.getBlockZ() + " in World " + this.signLoc.getWorld().getName() + " is not a Sign! (File:" + signFile.getAbsolutePath());
	    }
	}
	
	if (this.mainSign == null) {
	    System.err.println("Main statistic sign (" + this.signConfig.getInt("sign.MainSign") + ") could not be found!");
	}
    }
    
    @Override
    public void delete() {
	for (int i = 0; i < 4; i++) {
	    this.sign.setLine(i, "");
	}
	this.sign.update(true);
	
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
    public SignType getType() {
	return this.type;
    }
    
    public StatisticSign getMainSign() {
	return this.mainSign;
    }
    
    public DataRecordType getRecordType() {
	return this.mainSign.getRecordType();
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
	
	if (this.arena != null && getMainSign() != null && getRecordType() != null && getRecordType() != DataRecordType.NULL) {
	    Map<UUID, Integer> valueMap = getRecordType().getValueMap();
	    
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, "");
	    this.sign.setLine(3, "");
	    
	    if (valueMap != null && !valueMap.isEmpty() && valueMap.size() > 2) {
		
		int line = 1;
		for (Entry<UUID, Integer> entry : valueMap.entrySet()) {
		    if (line < 3) {
			line++;
			continue;
		    }
		    
		    String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
		    String entryString = format(entry.getValue());
		    String signLine = "";
		    
		    int charsLeft = SIGN_SPACE - (line + ".: " + entryString).length();
		    // System.out.println("charsLeft: " + charsLeft);
		    
		    if (charsLeft >= playerName.length()) {
			signLine = colorMap.get("rankNumber") + "" + line + "." + colorMap.get("playerName") + playerName + ChatColor.RESET + ": " + colorMap.get("value") + entryString;
		    } else {
			signLine = colorMap.get("rankNumber") + "" + line + "." + colorMap.get("playerName") + playerName.substring(0, charsLeft) + ChatColor.RESET + ".: " + colorMap.get("value") + entryString;
		    }
		    
		    this.sign.setLine(line - 2, signLine);
		    line++;
		    
		    if (line > 5) {
			break;
		    }
		}
	    } else {
		this.sign.setLine(2, ChatColor.DARK_RED + "Not enough data");
		this.sign.setLine(3, ChatColor.DARK_RED + "available!");
	    }
	} else if (getMainSign() == null) {
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, ChatColor.DARK_RED + "No main sign");
	    this.sign.setLine(3, ChatColor.DARK_RED + "found!");
	} else if (getRecordType() == null || getRecordType() == DataRecordType.NULL) {
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, ChatColor.DARK_RED + "No data type");
	    this.sign.setLine(3, ChatColor.DARK_RED + "set!");
	} else if (this.arena == null) {
	    this.sign.setLine(1, "");
	    this.sign.setLine(2, ChatColor.DARK_RED + "Arena is not");
	    this.sign.setLine(3, ChatColor.DARK_RED + "available!");
	}
	this.sign.update(true);
    }
    
    private String format(Object o) {
	if (o instanceof Double) {
	    return "" + Math.round((double) o);
	} else {
	    return o.toString();
	}
    }
    
    @Override
    public int compareTo(ISign o) {
	return getArena().compareTo(o.getArena());
    }
    
}
