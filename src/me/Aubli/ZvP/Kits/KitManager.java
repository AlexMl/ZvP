package me.Aubli.ZvP.Kits;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

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
	
	
	@SuppressWarnings("deprecation")
	public void openSelectKitGUI(ZvPPlayer player) {
		Inventory kitInventory = Bukkit.createInventory(player.getPlayer(), ((int)Math.ceil(((double)getKits()/9.0)))*9, MessageManager.getMessage("inventory:kit_select"));

		for(IZvPKit kit : kits) {
			ItemStack kitItem = kit.getIcon();
			ItemMeta kitMeta = kitItem.getItemMeta();
		
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Content:");
			
			for(ItemStack stack : kit.getContents()) {
				
				lore.add(ChatColor.DARK_GREEN + "" + stack.getAmount() + "x " + stack.getType().toString());
				
				if(stack.getType()==Material.POTION) {
					Potion p = Potion.fromDamage(stack.getDurability());					
					lore.add(ChatColor.DARK_BLUE + "  -" + p.getType() + " L" + p.getLevel());
				}			
				
				Map<Enchantment, Integer> enchs = stack.getEnchantments();						
				if(enchs.size()>0) {	
					for(Enchantment e : enchs.keySet()) {
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
		Inventory kitInv = Bukkit.createInventory(player, 9, ChatColor.DARK_BLUE + "ZvP-Kit: " + ChatColor.RED + kitName);
		player.closeInventory();
		player.openInventory(kitInv);
	}
	
	public void openAddKitIconGUI(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, MessageManager.getMessage("inventory:place_icon"));
		player.closeInventory();
		player.openInventory(inv);
	}
	
} 
