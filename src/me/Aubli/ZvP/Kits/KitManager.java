package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;


public class KitManager {
    
    private static KitManager instance;
    
    private File kitPath;
    
    private ArrayList<IZvPKit> kits;
    
    public KitManager() {
	
	instance = this;
	
	this.kits = new ArrayList<IZvPKit>();
	this.kitPath = new File(ZvP.getInstance().getDataFolder().getPath() + "/Kits");
	
	loadKits();
    }
    
    public static KitManager getManager() {
	return instance;
    }
    
    public void loadKits() {
	
	this.kits = new ArrayList<IZvPKit>();
	this.kitPath.mkdirs();
	
	IZvPKit bowKit = new KBowKit();
	IZvPKit swordKit = new KSwordKit();
	IZvPKit nullKit = new KNullKit();
	
	this.kits.add(bowKit);
	this.kits.add(swordKit);
	
	for (File f : this.kitPath.listFiles()) {
	    IZvPKit kit = new KCustomKit(f);
	    this.kits.add(kit);
	}
	this.kits.add(nullKit);
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
	IZvPKit[] kitArray = new IZvPKit[getKitAmount() - 1];
	
	for (int i = 0; i < getKitAmount() - 1; i++) {
	    kitArray[i] = this.kits.get(i);
	}
	
	Arrays.sort(kitArray);
	return kitArray;
    }
    
    private int getKitAmount() {
	return this.kits.size();
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
	
	IZvPKit kit = new KCustomKit(this.kitPath.getAbsolutePath(), kitName, icon, items);
	this.kits.add(kit);
	loadKits();
    }
    
    public void removeKit(String kitName) {
	getKit(kitName).delete();
	this.kits.remove(getKit(kitName));
    }
    
    public void openSelectKitGUI(ZvPPlayer player) {
	Inventory kitInventory = Bukkit.createInventory(player.getPlayer(), ((int) Math.ceil((getKitAmount() / 9.0))) * 9, MessageManager.getMessage("inventory:kit_select"));
	
	for (IZvPKit kit : this.kits) {
	    ItemStack kitItem = kit.getIcon();
	    ItemMeta kitMeta = kitItem.getItemMeta();
	    
	    ArrayList<String> lore = new ArrayList<String>();
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
	    
	    kitMeta.setDisplayName(ChatColor.DARK_GRAY + kit.getName());
	    kitMeta.setLore(lore);
	    kitItem.setItemMeta(kitMeta);
	    kitInventory.addItem(kitItem);
	}
	
	player.openInventory(kitInventory);
    }
    
    public void openAddKitGUI(Player player, String kitName) {
	Inventory inv = Bukkit.createInventory(player, 9, ChatColor.DARK_BLUE + "ZvP-Kit: " + ChatColor.RED + kitName);
	player.closeInventory();
	player.openInventory(inv);
    }
    
    public void openAddKitIconGUI(Player player) {
	Inventory inv = Bukkit.createInventory(player, 9, MessageManager.getMessage("inventory:place_icon"));
	player.closeInventory();
	player.openInventory(inv);
    }
    
}
