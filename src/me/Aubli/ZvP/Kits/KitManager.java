package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import me.Aubli.ZvP.ZvP;

public class KitManager {

	private static KitManager instance;
	
	private File kitPath;
	
	private ArrayList<ZvPKit> kits;
	
	public KitManager() {
		
		instance = this;
		
		kits = new ArrayList<ZvPKit>();
		kitPath = new File(ZvP.getInstance().getDataFolder().getPath() + "/Kits");
				
		loadKits();
		
	}
	
	public static KitManager getManager() {
		return instance;
	}
	
	
	private void loadKits() {
		
		kits = new ArrayList<ZvPKit>();
		kitPath.mkdirs();			
		
		ZvPKit bowKit = new BowKit(kitPath.getAbsolutePath());
		ZvPKit swordKit = new SwordKit(kitPath.getAbsolutePath());
		
		kits.add(bowKit);
		kits.add(swordKit);
		
		for(File f : kitPath.listFiles()) {
			ZvPKit kit = new CustomKit(f);
			kits.add(kit);
		}
		
	}
	
	
	
	public ZvPKit getKit(String kitName) {
		for(ZvPKit k : kits) {
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
		ZvPKit kit = new CustomKit(kitPath.getAbsolutePath(), kitName, items);
		kits.add(kit);
	}
	
	public void removeKit(String kitName) {
		getKit(kitName).delete();
		kits.remove(getKit(kitName));
	}
} 
