package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import me.Aubli.ZvP.ZvP;

public class KitManager {

	private static KitManager instance;
	
	private File kitPath;
	
	private ArrayList<IZvPKit> kits;
	
	public KitManager() {
		
		instance = this;
		
		kits = new ArrayList<IZvPKit>();
		kitPath = new File(ZvP.getInstance().getDataFolder().getPath() + "/Kits");
				
		loadKits();
		
	}
	
	public static KitManager getManager() {
		return instance;
	}
	
	
	public void loadKits() {
		
		kits = new ArrayList<IZvPKit>();
		kitPath.mkdirs();			
		
		IZvPKit bowKit = new BowKit(kitPath.getAbsolutePath());
		IZvPKit swordKit = new SwordKit(kitPath.getAbsolutePath());
		
		kits.add(bowKit);
		kits.add(swordKit);
		
		for(File f : kitPath.listFiles()) {
			IZvPKit kit = new CustomKit(f);
			kits.add(kit);
		}
		
	}
	
	
	
	public IZvPKit getKit(String kitName) {
		for(IZvPKit k : kits) {
			if(k.getName().equals(kitName)) {
				return k;
			}
		}
		return null;
	}
	
	public int getKits() {
		return kits.size();
	}
	
	
	public void addKit(String kitName, ItemStack[] items) {
		IZvPKit kit = new CustomKit(kitPath.getAbsolutePath(), kitName, items);
		kits.add(kit);
	}
	
	public void removeKit(String kitName) {
		getKit(kitName).delete();
		kits.remove(getKit(kitName));
	}
} 
