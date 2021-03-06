package me.Aubli.ZvP.Sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Statistic.DataRecordType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class SignManager {
    
    public enum SignType {
	INFO_SIGN("info"),
	INTERACT_SIGN("interact"),
	SHOP_SIGN("shop"),
	STATISTIC_SIGN("statistics"),
	STATISTIC_LIST_SIGN("STATISTICLIST"), ;
	
	private String name;
	
	private SignType(String name) {
	    this.name = name;
	}
	
	private String getName() {
	    return this.name;
	}
	
	public static SignType fromString(String name) {
	    for (SignType signType : values()) {
		if (signType.getName().equalsIgnoreCase(name)) {
		    return signType;
		}
	    }
	    return null;
	}
	
    }
    
    private static SignManager instance;
    
    private File signFolder;
    
    private Map<SignType, Map<String, ChatColor>> colorMap;
    
    private ArrayList<ISign> signs;
    
    private SignManager() {
	this.signFolder = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs");
	
	this.colorMap = new HashMap<SignManager.SignType, Map<String, ChatColor>>();
	for (SignType type : SignType.values()) {
	    this.colorMap.put(type, new HashMap<String, ChatColor>());
	}
	
	Bukkit.getScheduler().runTask(ZvP.getInstance(), new Runnable() {
	    
	    @Override
	    public void run() {
		reloadConfig();
	    }
	});
    }
    
    public static synchronized SignManager init() {
	if (instance == null) {
	    instance = new SignManager();
	}
	return instance;
    }
    
    public static SignManager getManager() {
	return init();
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
	
	List<File> listStatFiles = new ArrayList<File>();
	try {
	    
	    for (File f : this.signFolder.listFiles()) {
		conf = YamlConfiguration.loadConfiguration(f);
		SignType t = SignType.valueOf(conf.getString("sign.Type"));
		
		switch (t) {
		    case INFO_SIGN:
			ISign info = new InfoSign(f);
			if (info.getWorld() != null) {
			    this.signs.add(info);
			}
			break;
		    
		    case INTERACT_SIGN:
			ISign inter = new InteractSign(f);
			if (inter.getWorld() != null) {
			    this.signs.add(inter);
			}
			break;
		    
		    case SHOP_SIGN:
			ISign shop = new ShopSign(f);
			if (shop.getWorld() != null) {
			    this.signs.add(shop);
			}
			break;
		    
		    case STATISTIC_SIGN:
			ISign stat = new StatisticSign(f);
			if (stat.getWorld() != null) {
			    this.signs.add(stat);
			}
			break;
		    
		    case STATISTIC_LIST_SIGN:
			listStatFiles.add(f);
			break;
		    
		    default:
			throw new IllegalArgumentException(t.name() + " is not supported!");
		}
		
	    }
	    
	    for (File f : listStatFiles) {
		ISign listStat = new StatisticListSign(f);
		if (listStat.getWorld() != null) {
		    this.signs.add(listStat);
		}
	    }
	    
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, e.getMessage(), true, false, e);
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
	    
	    mapConf.addDefault(SignType.INTERACT_SIGN.name() + ".statusLineRunning", 2);
	    mapConf.addDefault(SignType.INTERACT_SIGN.name() + ".statusLineWaiting", 6);
	    mapConf.addDefault(SignType.INTERACT_SIGN.name() + ".statusLineStoped", 4);
	    mapConf.addDefault(SignType.INTERACT_SIGN.name() + ".joinLine", 'a');
	    mapConf.addDefault(SignType.INTERACT_SIGN.name() + ".inProgressLine", 4);
	    
	    mapConf.addDefault(SignType.SHOP_SIGN.name() + ".header", 1);
	    mapConf.addDefault(SignType.SHOP_SIGN.name() + ".category", 0);
	    mapConf.addDefault(SignType.SHOP_SIGN.name() + ".categoryName", 4);
	    
	    mapConf.addDefault(SignType.INFO_SIGN.name() + ".currentAmountOfPlayers", 'b');
	    mapConf.addDefault(SignType.INFO_SIGN.name() + ".minAmountOfPlayers", 9);
	    mapConf.addDefault(SignType.INFO_SIGN.name() + ".maxAmountOfPlayers", 4);
	    mapConf.addDefault(SignType.INFO_SIGN.name() + ".currentWave", 1);
	    mapConf.addDefault(SignType.INFO_SIGN.name() + ".maxWave", 4);
	    
	    mapConf.addDefault(SignType.STATISTIC_SIGN.name() + ".header", 1);
	    mapConf.addDefault(SignType.STATISTIC_SIGN.name() + ".rankNumber", 'a');
	    mapConf.addDefault(SignType.STATISTIC_SIGN.name() + ".playerName", 'e');
	    mapConf.addDefault(SignType.STATISTIC_SIGN.name() + ".value", 'f');
	    
	    mapConf.addDefault(SignType.STATISTIC_LIST_SIGN.name() + ".rankNumber", 'a');
	    mapConf.addDefault(SignType.STATISTIC_LIST_SIGN.name() + ".playerName", 'e');
	    mapConf.addDefault(SignType.STATISTIC_LIST_SIGN.name() + ".value", 'f');
	    
	    mapConf.options().copyDefaults(true);
	    mapConf.options().copyHeader(false);
	    mapConf.save(colorMapFile);
	    
	    for (SignType type : SignType.values()) {
		Map<String, ChatColor> signColorMap = this.colorMap.get(type);
		
		for (String confSectionKey : mapConf.getConfigurationSection(type.name()).getValues(false).keySet()) {
		    ChatColor confColor = ChatColor.getByChar(mapConf.getString(type.name() + "." + confSectionKey).trim().toLowerCase().replace("&", "").replace("§", ""));
		    signColorMap.put(confSectionKey, confColor != null ? confColor : ChatColor.RESET);
		}
		this.colorMap.put(type, signColorMap);
	    }
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving color map: " + e.getMessage(), true, false, e);
	}
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
    
    public ISign getAttachedSign(Location signBlock) {
	for (ISign sign : getSigns()) {
	    if (sign.getAttachedBlock().getLocation().equals(signBlock)) {
		return sign;
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
    
    public ISign[] getSigns(Arena arena) {
	ArrayList<ISign> arenaSigns = new ArrayList<ISign>();
	
	for (ISign sign : getSigns()) {
	    if (sign.getArena().equals(arena)) {
		arenaSigns.add(sign);
	    }
	}
	
	ISign[] signArray = new ISign[arenaSigns.size()];
	
	for (ISign sign : arenaSigns) {
	    signArray[arenaSigns.indexOf(sign)] = sign;
	}
	return signArray;
    }
    
    public ISign[] getSigns(Lobby lobby) {
	ArrayList<ISign> lobbySigns = new ArrayList<ISign>();
	
	for (ISign sign : getSigns()) {
	    if (sign.getLobby().equals(lobby)) {
		lobbySigns.add(sign);
	    }
	}
	
	ISign[] signArray = new ISign[lobbySigns.size()];
	
	for (ISign sign : lobbySigns) {
	    signArray[lobbySigns.indexOf(sign)] = sign;
	}
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
    
    public boolean isSignBlock(Location loc) {
	return getAttachedSign(loc) != null;
    }
    
    public ISign createSign(SignType type, Location signLoc, Arena arena, Lobby lobby) {
	return createSign(type, signLoc, arena, lobby, null, null, null);
    }
    
    public ISign createSign(SignType type, Location signLoc, Arena arena, Lobby lobby, ISign mainSign) {
	return createSign(type, signLoc, arena, lobby, null, null, mainSign);
    }
    
    public ISign createSign(SignType type, Location signLoc, Arena arena, Lobby lobby, DataRecordType recordType) {
	return createSign(type, signLoc, arena, lobby, null, recordType, null);
    }
    
    public ISign createSign(SignType type, Location signLoc, Arena arena, Lobby lobby, ItemCategory category) {
	return createSign(type, signLoc, arena, lobby, category, null, null);
    }
    
    private ISign createSign(SignType type, Location signLoc, Arena arena, Lobby lobby, ItemCategory category, DataRecordType recordType, ISign mainSign) {
	if (signLoc.getBlock().getState() instanceof Sign) {
	    
	    String path = this.signFolder.getPath();
	    
	    try {
		switch (type) {
		    case INFO_SIGN:
			ISign info = new InfoSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
			this.signs.add(info);
			return info;
			
		    case INTERACT_SIGN:
			ISign inter = new InteractSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
			this.signs.add(inter);
			return inter;
			
		    case SHOP_SIGN:
			if (category == null) {
			    category = ItemCategory.NULL;
			}
			ISign shop = new ShopSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby, category);
			this.signs.add(shop);
			return shop;
			
		    case STATISTIC_SIGN:
			if (recordType == null) {
			    recordType = DataRecordType.NULL;
			}
			
			ISign stat = new StatisticSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby, recordType);
			this.signs.add(stat);
			return stat;
			
		    case STATISTIC_LIST_SIGN:
			if (mainSign != null) {
			    ISign listStat = new StatisticListSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby, mainSign);
			    this.signs.add(listStat);
			    return listStat;
			}
			return null;
			
		    default:
			throw new IllegalArgumentException(type.name() + " is not supported!");
		}
		
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while creating new " + type.toString() + ": " + e.getMessage(), true, false, e);
		return null;
	    }
	}
	return null;
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
    
    public void updateSigns(SignType type) {
	for (ISign s : this.signs) {
	    if (s.getType() == type) {
		s.update(getColorMap(type));
	    }
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
