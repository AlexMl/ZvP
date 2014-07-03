package me.Aubli.ZvP.Shop;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.util.ItemStorageUtil.ItemStorage;

public class ShopManager {

	private File itemFile;
	private FileConfiguration itemConfig;
	
	private ShopItem[] items;
	
	
	public ShopManager() {
		
		itemFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Shop/items.yml");
		itemConfig = YamlConfiguration.loadConfiguration(itemFile);
		
		if(!itemFile.exists()) {
			try {
				itemFile.getParentFile().mkdirs();			
				itemFile.createNewFile();
				writeDefaults();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
		items = loadItems();
	}
	
	
	private void writeDefaults() {
		
	}
	
	
	private ShopItem[] loadItems(){
		try {
			return ItemStorage.getShopItemsFromFile(itemConfig.getList("items"));
		} catch (Exception e) {
			ZvP.log.log(Level.WARNING, "Error while loading Item from shop configuration!\nError: " + e.getMessage() + " in File " + itemFile.getPath(), e);			
		}
		return null;
	}
	
	
	//Shop control
	public ItemStack[] getItems() {
		
		return null;
	}
	
	
	
	public double getPrice(ItemStack item) {
		return 0;
	}
	
	
}
