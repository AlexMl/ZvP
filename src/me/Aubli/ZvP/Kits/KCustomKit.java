package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.util.ItemStorageUtil.ItemStorage;


public class KCustomKit implements IZvPKit, Comparable<IZvPKit> {
    
    private final File kitFile;
    
    private final String name;
    
    private final ItemStack icon;
    
    private final ItemStack[] items;
    
    private final boolean enabled;
    
    public KCustomKit(String path, String name, ItemStack icon, ItemStack[] content) {
	this.name = name;
	this.icon = icon;
	this.items = content;
	this.enabled = true;
	
	this.kitFile = new File(path + "/" + name + ".yml");
	FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(this.kitFile);
	
	if (!this.kitFile.exists()) {
	    
	    kitConfig.options().header("This is the config file used in ZvP to store a customm kit.\n\n" + "'name:' The name of the kit\n" + "'icon:' An item used as an icon\n\n" + "'id:' The id describes the item material. A list of all items can be found here: http://jd.bukkit.org/dev/apidocs/org/bukkit/Material.html\n" + "'amount:' The amount of the item (Should be 1!)\n" + "'data:' Used by potions\n" + "'ench: {}' A list of enchantings (ench: {ENCHANTMENT:LEVEL}). A list of enchantments can be found here:\n http://jd.bukkit.org/dev/apidocs/org/bukkit/enchantments/Enchantment.html\n");
	    kitConfig.options().copyHeader(true);
	    
	    kitConfig.set("name", name);
	    kitConfig.set("icon", icon.getType().toString());
	    kitConfig.set("enabled", true);
	    ItemStorage.saveItemsToFile(this.kitFile, "items", content);
	    
	    try {
		kitConfig.load(this.kitFile);
		kitConfig.save(this.kitFile);
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(Level.WARNING, "Error while saving Kit file: " + e.getMessage(), true, false, e);
	    }
	}
    }
    
    public KCustomKit(File kitFile) {
	
	this.kitFile = kitFile;
	FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
	
	this.name = kitConfig.getString("name");
	this.icon = parseIcon(kitConfig.getString("icon"));
	this.items = parseItemStack(kitConfig.getList("items"));
	this.enabled = kitConfig.getBoolean("enabled", true);
    }
    
    private ItemStack[] parseItemStack(List<?> itemList) {
	try {
	    return ItemStorage.getItemsFromFile(itemList);
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while loading Custom Kit: " + getName() + "  Error: " + e.getMessage() + " in File " + this.kitFile.getPath(), true, false, e);
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
    public boolean isEnabled() {
	return this.enabled;
    }
    
    @Override
    public String getName() {
	return this.name;
    }
    
    @Override
    public ItemStack getIcon() {
	return this.icon;
    }
    
    @Override
    public ItemStack[] getContents() {
	return this.items.clone();
    }
    
    @Override
    public int compareTo(IZvPKit o) {
	return getName().compareTo(o.getName());
    }
}
