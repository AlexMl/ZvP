package me.Aubli.ZvP.Kits;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.util.File.Converter.FileConverter.FileType;


public class KitManager {
    
    private static KitManager instance;
    
    private boolean enabled;
    
    private File kitPath;
    private FilenameFilter filter;
    
    private ArrayList<IZvPKit> kits;
    
    public KitManager(boolean enableKits) {
	
	instance = this;
	
	this.enabled = enableKits;
	this.kits = new ArrayList<IZvPKit>();
	this.kitPath = new File(ZvP.getInstance().getDataFolder().getPath() + "/Kits");
	
	this.filter = new FilenameFilter() {
	    
	    @Override
	    public boolean accept(File dir, String name) {
		if (name.contains(".old")) {
		    return false;
		}
		return true;
	    }
	};
	
	loadKits();
    }
    
    public static KitManager getManager() {
	return instance;
    }
    
    public void loadKits() {
	
	this.kits = new ArrayList<IZvPKit>();
	this.kitPath.mkdirs();
	
	new KBowKit();
	new KSwordKit();
	new KNullKit();
	
	for (File f : this.kitPath.listFiles(this.filter)) {
	    // Version 2.5 needs converted kit files
	    // Version 2.8 needs permissionNode in Kit file
	    ZvP.getConverter().convert(FileType.KITFILE, f, 280.0);
	    IZvPKit kit = new KCustomKit(f);
	    if (kit.isEnabled()) {
		this.kits.add(kit);
		ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "Loaded " + kit.getName() + " from " + f.getPath(), true);
	    } else {
		ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "Kit " + kit.getName() + " is disabled through config " + f.getPath(), true);
	    }
	}
	
	if (ZvPConfig.getUseEssentialsKits()) {
	    ZvP.getPluginLogger().log(getClass(), Level.FINE, "Attempting Essentials Kit load!", true, true);
	    
	    File essFile = new File(Bukkit.getPluginManager().getPlugin("Essentials").getDataFolder(), "config.yml");
	    FileConfiguration essConfig = YamlConfiguration.loadConfiguration(essFile);
	    
	    for (String kitName : essConfig.getConfigurationSection("kits").getValues(false).keySet()) {
		System.out.println("eKit " + kitName);
		IZvPKit kit = new KEssentialsKit(kitName, essConfig.getConfigurationSection("kits." + kitName));
		this.kits.add(kit);
		// TODO log
	    }
	    
	}
    }
    
    public File getKitPath() {
	return this.kitPath;
    }
    
    public IZvPKit getKit(String kitName) {
	for (IZvPKit k : this.kits) {
	    if (k.getName().equals(kitName)) {
		return k;
	    }
	}
	return null;
    }
    
    public IZvPKit[] getKits() {
	List<IZvPKit> kitList = new ArrayList<IZvPKit>();
	
	for (IZvPKit kit : this.kits) {
	    if (!kit.getName().equals("No Kit")) {
		kitList.add(kit);
	    }
	}
	
	IZvPKit[] kitArray = new IZvPKit[kitList.size()];
	for (IZvPKit kit : kitList) {
	    kitArray[kitList.indexOf(kit)] = kit;
	}
	
	Arrays.sort(kitArray);
	return kitArray;
    }
    
    private int getKitAmount() {
	return this.kits.size();
    }
    
    public boolean isEnabled() {
	return this.enabled;
    }
    
    public void addKit(String kitName, ItemStack icon, ItemStack[] items) {
	
	// splash portion of healing I causes illegal argument exception -> replace it with splash potion of healing II
	for (ItemStack stack : items) {
	    if (stack != null && stack.getType() != Material.AIR) {
		if (stack.getType() == Material.POTION) {
		    if (stack.getDurability() == 16453) {
			stack.setDurability((short) 16421);
		    }
		}
	    }
	}
	
	IZvPKit kit = new KCustomKit(this.kitPath.getAbsolutePath(), kitName, icon, 0.0, items);
	this.kits.add(kit);
	loadKits();
    }
    
    public void removeKit(String kitName) {
	getKit(kitName).delete();
	this.kits.remove(getKit(kitName));
    }
    
    public void openSelectKitGUI(ZvPPlayer player) {
	Inventory kitInventory = Bukkit.createInventory(player.getPlayer(), ((int) Math.ceil((getKitAmount() / 9.0))) * 9, MessageManager.getMessage(inventory.kit_select));
	
	for (IZvPKit kit : this.kits) {
	    if (player.getPlayer().hasPermission(kit.getPermissionNode())) {
		ItemStack kitItem = kit.getIcon();
		ItemMeta kitMeta = kitItem.getItemMeta();
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Price: " + kit.getPrice());
		lore.add(ChatColor.GOLD + "Content:");
		
		for (ItemStack stack : kit.getContents()) {
		    
		    lore.add(ChatColor.DARK_GREEN + "" + stack.getAmount() + "x " + stack.getType().toString());
		    
		    if (stack.getType() == Material.POTION) {
			Potion p = Potion.fromItemStack(stack);
			lore.add(ChatColor.DARK_BLUE + "  -" + p.getType() + " L" + p.getLevel());
		    }
		    
		    Map<Enchantment, Integer> enchs = stack.getEnchantments();
		    if (enchs.size() > 0) {
			for (Enchantment e : enchs.keySet()) {
			    lore.add(ChatColor.DARK_RED + "  -" + e.getName() + " L" + enchs.get(e));
			}
		    }
		}
		
		kitMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		kitMeta.setDisplayName(ChatColor.DARK_GRAY + kit.getName());
		kitMeta.setLore(lore);
		kitItem.setItemMeta(kitMeta);
		kitInventory.addItem(kitItem);
		// System.out.println(player.getName() + " hasPermission " + kit.getPermissionNode() + " for " + kit.getName());
	    }
	}
	
	player.openInventory(kitInventory);
    }
    
    public void openAddKitGUI(Player player, String kitName) {
	Inventory inv = Bukkit.createInventory(player, 9, ChatColor.DARK_BLUE + "ZvP-Kit: " + ChatColor.RED + kitName);
	player.closeInventory();
	player.openInventory(inv);
    }
    
    public void openAddKitIconGUI(Player player) {
	Inventory inv = Bukkit.createInventory(player, 9, MessageManager.getMessage(inventory.place_icon));
	player.closeInventory();
	player.openInventory(inv);
    }
    
}
