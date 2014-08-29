package me.Aubli.ZvP.Kits;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.util.ItemStorageUtil.ItemStorage;

public class KCustomKit implements IZvPKit, Comparable<IZvPKit>{

	
	private final File kitFile;
	
	private final String name;
	
	private final ItemStack icon;
	
	private final ItemStack[] items;
	
	
	public KCustomKit(String path, String name, ItemStack icon, ItemStack[] content) {	
		this.name = name;
		this.icon = icon;
		this.items = content;
	
		kitFile = new File(path + "/" + name + ".yml");		
		FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
			
		kitConfig.set("name", name);
		kitConfig.set("icon", icon.getType().toString());
		ItemStorage.saveItemsToFile(kitFile, "items", content);
		
		try {
			kitConfig.save(kitFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public KCustomKit(File kitFile) {
		
		this.kitFile = kitFile;
		FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
		
		this.name = kitConfig.getString("name");
		this.icon = parseIcon(kitConfig.getString("icon"));
		this.items = parseItemStack(kitConfig.getList("items"));
	}	
	
	private ItemStack[] parseItemStack(List<?> itemList) {
		try {
			return ItemStorage.getItemsFromFile(itemList);
		} catch (Exception e) {
			ZvP.log.log(Level.WARNING, "Error while loading Custom Kit: " + getName() + "  Error: " + e.getMessage() + " in File " + kitFile.getPath(), e);			
		}
		return null;
	}
	
	private ItemStack parseIcon(String itemString) {
		return new ItemStack(Material.getMaterial(itemString));
	}
	
	
	@Override
	public void delete() {
		this.kitFile.delete();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public ItemStack[] getContents() {
		return items.clone();
	}
	
	@Override
	public int compareTo(IZvPKit o) {
		return getName().compareTo(o.getName());
	}
}
