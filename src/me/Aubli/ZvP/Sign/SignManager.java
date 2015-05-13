package me.Aubli.ZvP.Sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class SignManager {
    
    public enum SignType {
	INFO_SIGN,
	INTERACT_SIGN,
	SHOP_SIGN, ;
    }
    
    private static SignManager instance;
    
    private File signFolder;
    
    private Map<SignType, Map<String, ChatColor>> colorMap;
    
    private ArrayList<ISign> signs;
    
    public SignManager() {
	instance = this;
	
	this.signFolder = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs");
	
	this.colorMap = new HashMap<SignManager.SignType, Map<String, ChatColor>>();
	for (SignType type : SignType.values()) {
	    this.colorMap.put(type, new HashMap<String, ChatColor>());
	}
	
	reloadConfig();
    }
    
    public void reloadConfig() {
	if (!this.signFolder.exists()) {
	    this.signFolder.mkdirs();
	}
	
	loadColorMap();
	loadSigns();
	updateSigns();
    }
    
    private void loadSigns() {
	
	this.signs = new ArrayList<ISign>();
	
	FileConfiguration conf;
	
	for (File f : this.signFolder.listFiles()) {
	    conf = YamlConfiguration.loadConfiguration(f);
	    SignType t = SignType.valueOf(conf.getString("sign.Type"));
	    
	    try {
		
		switch (t) {
		    case INFO_SIGN:
			InfoSign info = new InfoSign(f);
			if (info.getWorld() != null) {// && info.getArena() != null && info.getLobby() != null) {
			    this.signs.add(info);
			}
			break;
		    
		    case INTERACT_SIGN:
			InteractSign inter = new InteractSign(f);
			if (inter.getWorld() != null) {// && inter.getArena() != null && inter.getLobby() != null) {
			    this.signs.add(inter);
			}
			break;
		    
		    case SHOP_SIGN:
			ShopSign shop = new ShopSign(f);
			if (shop.getWorld() != null) {// && shop.getArena() != null && shop.getLobby() != null) {
			    this.signs.add(shop);
			}
			break;
		}
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(Level.WARNING, e.getMessage(), true, false, e);
	    }
	}
    }
    
    private void loadColorMap() {
	
	try {
	    File colorMapFile = new File(ZvP.getInstance().getDataFolder(), "SignColor.map");
	    
	    if (!colorMapFile.exists()) {
		colorMapFile.createNewFile();
	    }
	    
	    FileConfiguration mapConf = YamlConfiguration.loadConfiguration(colorMapFile);
	    mapConf.options().header("This file contains color codes for the signs used by zvp!\nYou have to provide the color codes in lower case and !without the color indicator('&' or '§').\nChange them like you want.\n");
	    
	    mapConf.addDefault("map." + SignType.INTERACT_SIGN.name() + ".statusLineRunning", 2);
	    mapConf.addDefault("map." + SignType.INTERACT_SIGN.name() + ".statusLineWaiting", 6);
	    mapConf.addDefault("map." + SignType.INTERACT_SIGN.name() + ".statusLineStoped", 4);
	    mapConf.addDefault("map." + SignType.INTERACT_SIGN.name() + ".joinLine", 'a');
	    mapConf.addDefault("map." + SignType.INTERACT_SIGN.name() + ".inProgressLine", 4);
	    
	    mapConf.addDefault("map." + SignType.SHOP_SIGN.name() + ".header", 1);
	    mapConf.addDefault("map." + SignType.SHOP_SIGN.name() + ".category", 0);
	    mapConf.addDefault("map." + SignType.SHOP_SIGN.name() + ".categoryName", 4);
	    
	    mapConf.addDefault("map." + SignType.INFO_SIGN.name() + ".currentAmountOfPlayers", 'b');
	    mapConf.addDefault("map." + SignType.INFO_SIGN.name() + ".maxAmountOfPlayers", 4);
	    mapConf.addDefault("map." + SignType.INFO_SIGN.name() + ".currentWave", 1);
	    mapConf.addDefault("map." + SignType.INFO_SIGN.name() + ".maxWave", 4);
	    
	    mapConf.options().copyDefaults(true);
	    mapConf.options().copyHeader(false);
	    mapConf.save(colorMapFile);
	    
	    for (SignType type : SignType.values()) {
		Map<String, ChatColor> signColorMap = this.colorMap.get(type);
		
		for (String confSectionKey : mapConf.getConfigurationSection("map." + type.name()).getValues(false).keySet()) {
		    ChatColor confColor = ChatColor.getByChar(mapConf.getString("map." + type.name() + "." + confSectionKey).trim().toLowerCase().replace("&", "").replace("§", ""));
		    signColorMap.put(confSectionKey, confColor != null ? confColor : ChatColor.RESET);
		}
		this.colorMap.put(type, signColorMap);
	    }
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(Level.WARNING, e.getMessage(), true, false, e);
	}
    }
    
    public static SignManager getManager() {
	return instance;
    }
    
    public SignType getType(Location signLoc) {
	
	for (ISign s : this.signs) {
	    if (s.getLocation().equals(signLoc)) {
		return s.getType();
	    }
	}
	return null;
    }
    
    public ISign getSign(int ID) {
	for (ISign s : this.signs) {
	    if (s.getID() == ID) {
		return s;
	    }
	}
	return null;
    }
    
    public ISign getSign(Location signLoc) {
	for (ISign s : this.signs) {
	    if (s.getLocation().equals(signLoc)) {
		return s;
	    }
	}
	return null;
    }
    
    public ISign[] getSigns() {
	ISign[] signArray = new ISign[this.signs.size()];
	
	for (int i = 0; i < signArray.length; i++) {
	    signArray[i] = this.signs.get(i);
	}
	
	Arrays.sort(signArray);
	return signArray;
    }
    
    public Map<String, ChatColor> getColorMap(SignType type) {
	return this.colorMap.get(type);
    }
    
    public boolean isZVPSign(Location loc) {
	if (loc.getBlock().getState() instanceof Sign) {
	    Sign sign = (Sign) loc.getBlock().getState();
	    
	    boolean hasPrefix = ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[zvp]");
	    if ((getSign(loc) != null && hasPrefix) || hasPrefix) {
		return true;
	    }
	}
	return false;
    }
    
    public boolean createSign(SignType type, Location signLoc, Arena arena, Lobby lobby, ItemCategory category) {
	if (signLoc.getBlock().getState() instanceof Sign) {
	    
	    String path = this.signFolder.getPath();
	    
	    try {
		switch (type) {
		
		    case INFO_SIGN:
			ISign info = new InfoSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
			this.signs.add(info);
			return true;
			
		    case INTERACT_SIGN:
			ISign inter = new InteractSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
			this.signs.add(inter);
			return true;
			
		    case SHOP_SIGN:
			if (category == null) {
			    category = ItemCategory.NULL;
			}
			ISign shop = new ShopSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby, category);
			this.signs.add(shop);
			return true;
		}
		
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(Level.WARNING, "Error while creating new " + type.toString() + ": " + e.getMessage(), true, false, e);
		return false;
	    }
	}
	return false;
    }
    
    public boolean removeSign(Location signLoc) {
	if (getSign(signLoc) != null) {
	    getSign(signLoc).delete();
	    this.signs.remove(getSign(signLoc));
	    return true;
	}
	return false;
    }
    
    public boolean removeSign(int signID) {
	if (getSign(signID) != null) {
	    getSign(signID).delete();
	    this.signs.remove(getSign(signID));
	    return true;
	}
	return false;
    }
    
    public void updateSigns() {
	for (ISign s : this.signs) {
	    s.update(getColorMap(s.getType()));
	}
    }
    
    public void updateSigns(Lobby lobby) {
	for (ISign s : this.signs) {
	    if (s.getLobby() != null) {
		if (s.getLobby().equals(lobby)) {
		    s.update(getColorMap(s.getType()));
		}
	    }
	}
    }
    
    public void updateSigns(Arena arena) {
	for (ISign s : this.signs) {
	    if (s.getArena() != null) {
		if (s.getArena().equals(arena)) {
		    s.update(getColorMap(s.getType()));
		}
	    }
	}
    }
    
}
