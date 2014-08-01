package org.util.ItemStorageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemStorage {
	
	/**
	 * Save an Array of ItemStacks to an specific file in a specific config section.
	 * 
	 * @param saveFile			The file to save
	 * @param configSection		The config section
	 * @param content			The Array of ItemStacks
	 */
	public static void saveItemsToFile(File saveFile, String configSection, ItemStack[] content) {
		// - { id: IRON_SWORD, amount: 1, data: 0, ench: {DAMAGE_ALL:1} }
		
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(saveFile);		
		List<String> itemList = new ArrayList<String>();
		
		for(ItemStack item : content) {
			if(item!=null && item.getType()!=Material.AIR) {
				String enchString = translateEnchantment(item);				
				String itemString = generateItemString(enchString, item);
				//System.out.println(itemString);
				itemList.add(itemString);		
			}
		}
		
		fileConfig.set(configSection, itemList);

		try {
			fileConfig.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save an Array of ShopItems to an specific file in a specific config section.
	 * 
	 * @param saveFile			The file to save
	 * @param configSection		The config section
	 * @param content			The Array of Shopitems
	 */
	public static void saveItemsToFile(File saveFile, String configSection, ShopItem[] content) {
		// - { id: IRON_SWORD, amount: 1, data: 0, ench: {DAMAGE_ALL:1}, Price: 5.0}
		
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(saveFile);		
		List<String> itemList = new ArrayList<String>();
		
		for(ShopItem item : content) {
			if(item!=null && item.getItem()!=null && item.getItem().getType()!=Material.AIR) {
				String enchString = translateEnchantment(item.getItem());				
				String itemString = generateShopItemString(enchString, item);
				//System.out.println(itemString);
				itemList.add(itemString);		
			}
		}
		
		fileConfig.set(configSection, itemList);

		try {
			fileConfig.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get an Array of ItemStacks from an List of items. Used in cooperation with the saveItemsToFile method.
	 *  
	 * @param itemList				The list of items
	 * @return {@link ItemStack[]} 	The array of {@link ItemStack}s with data, {@link Enchantment}, amount
	 * @throws Exception			Throws {@link Exception} if a listentry is not correct.
	 */
	public static ItemStack[] getItemsFromFile(List<?> itemList) throws Exception {
		
		ArrayList<ItemStack> itemArray = new ArrayList<ItemStack>();
		
		for(Object listObject : itemList) {

			try {					
				String itemString = (String) listObject;
				
				Material m = parseMaterial(itemString);
				int amount = parseAmount(itemString);
				short data = parseData(itemString);				
				
				ItemStack item = new ItemStack(m, amount, data);
				applyEnchantment(item, itemString);
				itemArray.add(item);				
			}catch(Exception e) {
				throw new Exception(e.getMessage() + " in Object: " + (String)listObject);
			}
		}		
		return toArray(itemArray);
	}
	
	/**
	 * Get an Array of ShopItems from an List of items. Used in cooperation with the saveItemsToFile method.
	 *  
	 * @param itemList				The list of items
	 * @return {@link ItemStack[]} 	The array of {@link ItemStack}s with data, {@link Enchantment}, amount
	 * @throws Exception			Throws {@link Exception} if a listentry is not correct.
	 */
	public static ShopItem[] getShopItemsFromFile(List<?> itemList) throws Exception {
		
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		
		for(Object listObject : itemList) {

			try {					
				String itemString = (String) listObject;
				
				Material m = parseMaterial(itemString);
				int amount = parseAmount(itemString);
				short data = parseData(itemString);				
				double price = parsePrice(itemString);
				ItemCategory cat = parseCategory(itemString);
				
				ItemStack item = new ItemStack(m, amount, data);
				applyEnchantment(item, itemString);
				
				items.add(toShopItem(item, cat, price));				
			}catch(Exception e) {
				throw new Exception(e.getMessage() + " in Object:\n" + (String)listObject);
			}
		}		
		return toShopArray(items);
	}
	
	private static String generateItemString(String enchString, ItemStack item) {
		return "id: " + item.getType().toString() + ", amount: " + item.getAmount() + ", data: " + item.getDurability() + ", " + enchString;
	}
	
	private static String generateShopItemString(String enchString, ShopItem item) {
		return "id: " + item.getType().toString() + ", amount: " + item.getItem().getAmount() + ", data: " + item.getItem().getDurability() + ", category: " + item.getCategory().getEnumName() + ", price: " + item.getPrice() + ", " + enchString;
	}
	
	private static String translateEnchantment(ItemStack item) {
		
		String enchString = "ench: {";
		if(item.getEnchantments().size()>1) {
			for(int i=0;i<item.getEnchantments().size()-1;i++) {
				Enchantment ench = (Enchantment) item.getEnchantments().keySet().toArray()[i];
				enchString += ench.getName() + ":" + item.getEnchantments().get(ench);
				enchString += ", ";
			}				
			enchString += ((Enchantment)item.getEnchantments().keySet().toArray()[item.getEnchantments().size()-1]).getName() + ":" + item.getEnchantments().get(item.getEnchantments().keySet().toArray()[item.getEnchantments().size()-1]);

		}else if(item.getEnchantments().size()==1) {
			Enchantment ench = (Enchantment) item.getEnchantments().keySet().toArray()[0];
			enchString += ench.getName() + ":" + item.getEnchantments().get(ench);
		}
		enchString += "}";
		
		return enchString;
	}
	
	
	private static Material parseMaterial(String itemString) {
		return Material.getMaterial(itemString.split(", ")[0].split("id: ")[1]);
	}
	
	private static int parseAmount(String itemString) throws NumberFormatException {
		return Integer.parseInt(itemString.split(", ")[1].split("amount: ")[1]);
	}
	
	private static short parseData(String itemString) throws NumberFormatException {
		return Short.parseShort(itemString.split(", ")[2].split("data: ")[1]);
	}
	
	private static ItemCategory parseCategory(String itemString) throws Exception {
		return ItemCategory.valueOf(itemString.split(", ")[3].split("category: ")[1]);
	}
	
	private static double parsePrice(String itemString) throws NumberFormatException {
		return Double.parseDouble(itemString.split(", ")[4].split("price: ")[1]);
	}
	
	private static ItemStack applyEnchantment(ItemStack item, String itemString) throws Exception {
		String enchString = itemString.split("ench: ")[1].replace("{", "").replace("}", "");
		
		String[] enchantments = enchString.split(", ");
		
		if(enchantments.length>0) {
			for(String ench : enchantments) {
				if(!ench.isEmpty()) {
				//	System.out.println("E: " + ench);
					Enchantment itemEnchantment = Enchantment.getByName(ench.split(":")[0]);
					int level = Integer.parseInt(ench.split(":")[1]);
					item.addUnsafeEnchantment(itemEnchantment, level);
				}
			}
		}
		return item;
	}
	
	private static ItemStack[] toArray(ArrayList<ItemStack> items) {
		ItemStack[] returnStack = new ItemStack[items.size()];
		
		for (int i = 0; i < returnStack.length; i++) {
			returnStack[i] = items.get(i);
		}	
			
		return returnStack;
	}

	private static ShopItem toShopItem(ItemStack item, ItemCategory cat, double price) {
		return new ShopItem(item, cat, price);
	}
	
	private static ShopItem[] toShopArray(ArrayList<ShopItem> items) {			
		
		ShopItem[] shopItems = new ShopItem[items.size()];	
		
		for(int i=0;i<items.size();i++) {
			shopItems[i] = items.get(i);		
		}
		
		return shopItems;
	}
}
