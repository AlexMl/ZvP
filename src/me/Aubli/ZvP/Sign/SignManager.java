package me.Aubli.ZvP.Sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;

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
    
    private ArrayList<ISign> signs;
    
    public SignManager() {
	instance = this;
	
	this.signFolder = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs");
	reloadConfig();
    }
    
    public void reloadConfig() {
	if (!this.signFolder.exists()) {
	    this.signFolder.mkdirs();
	}
	
	loadSigns();
	updateSigns();
    }
    
    private void loadSigns() {
	
	this.signs = new ArrayList<ISign>();
	
	FileConfiguration conf;
	
	for (File f : this.signFolder.listFiles()) {
	    conf = YamlConfiguration.loadConfiguration(f);
	    SignType t = SignType.valueOf(conf.getString("sign.Type"));
	    
	    switch (t) {
		case INFO_SIGN:
		    InfoSign info = new InfoSign(f);
		    if (info.getWorld() != null) {
			this.signs.add(info);
		    }
		    break;
		    
		case INTERACT_SIGN:
		    InteractSign inter = new InteractSign(f);
		    if (inter.getWorld() != null) {
			this.signs.add(inter);
		    }
		    break;
		    
		case SHOP_SIGN:
		    ShopSign shop = new ShopSign(f);
		    if (shop.getWorld() != null) {
			this.signs.add(shop);
		    }
	    }
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
    
    public boolean isZVPSign(Location loc) {
	if (getSign(loc) != null) {
	    return true;
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
		e.printStackTrace();
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
	    s.update();
	}
    }
    
    public void updateSigns(Lobby lobby) {
	for (ISign s : this.signs) {
	    if (s.getLobby().equals(lobby)) {
		s.update();
	    }
	}
    }
    
    public void updateSigns(Arena arena) {
	for (ISign s : this.signs) {
	    if (s.getArena().equals(arena)) {
		s.update();
	    }
	}
    }
    
}
