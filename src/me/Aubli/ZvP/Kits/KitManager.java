package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.ZvPPlayer;

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
		
		IZvPKit bowKit = new KBowKit();
		IZvPKit swordKit = new KSwordKit();
		IZvPKit nullKit = new KNullKit();
		
		kits.add(bowKit);
		kits.add(swordKit);
		
		for(File f : kitPath.listFiles()) {
			IZvPKit kit = new KCustomKit(f);
			kits.add(kit);
		}
		kits.add(nullKit);
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
	
	
	public void addKit(String kitName, ItemStack icon, ItemStack[] items) {
		IZvPKit kit = new KCustomKit(kitPath.getAbsolutePath(), kitName, icon, items);
		kits.add(kit);
	}
	
	public void removeKit(String kitName) {
		getKit(kitName).delete();
		kits.remove(getKit(kitName));
	}
	
	
	public void openSelectKitGUI(ZvPPlayer player) {
		Inventory kitInventory = Bukkit.createInventory(player.getPlayer(), ((int)Math.ceil(((double)getKits()/9.0)))*9, "Select your Kit!");

		for(IZvPKit kit : kits) {
			ItemStack kitItem = kit.getIcon();
			ItemMeta kitMeta = kitItem.getItemMeta();
			
			kitMeta.setDisplayName(kit.getName());
			kitMeta.addEnchant(Enchantment.DURABILITY, 1, true);
			kitItem.setItemMeta(kitMeta);
						
			kitInventory.addItem(kitItem);
		}
		
		player.openInventory(kitInventory);		
	}
	
	public void openAddKitGUI(Player player, String kitName) {
		Inventory kitInv = Bukkit.createInventory(player, 9, ChatColor.DARK_BLUE + "ZvP-Kit: " + ChatColor.RED + kitName);
		player.closeInventory();
		player.openInventory(kitInv);
	}
	
	public void openAddKitIconGUI(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, "Place Kit icon here");
		player.closeInventory();
		player.openInventory(inv);
	}
	
} 
