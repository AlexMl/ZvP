package org.util.ItemStorageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(saveFile);
		
		List<String> itemList = new ArrayList<String>();
		
		for(ItemStack item : content) {
			if(item!=null && item.getType()!=Material.AIR) {
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
				
				String itemString = "id: " + item.getType().toString() + ", amount: " + item.getAmount() + ", data: " + item.getDurability() + ", " + enchString;
				System.out.println(itemString);
				itemList.add(itemString);		
			}
		}
		
		// - { id: IRON_SWORD, amount: 1, data: 0, ench: {DAMAGE_ALL:1} }
		
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
				
				Material m = Material.getMaterial(itemString.split(", ")[0].split("id: ")[1]);
				
				int amount = Integer.parseInt(itemString.split(", ")[1].split("amount: ")[1]);
				
				short data = Short.parseShort(itemString.split(", ")[2].split("data: ")[1]);
				
				String enchString = itemString.split("ench: ")[1].replace("{", "").replace("}", "");
				
				String[] enchantments = enchString.split(", ");
				
				ItemStack item = new ItemStack(m, amount, data);
				
				if(enchantments.length>0) {
					for(String ench : enchantments) {
						if(!ench.isEmpty()) {
							System.out.println("E: " + ench);
							Enchantment itemEnchantment = Enchantment.getByName(ench.split(":")[0]);
							int level = Integer.parseInt(ench.split(":")[1]);
							item.addUnsafeEnchantment(itemEnchantment, level);
						}
					}
				}
				
				System.out.println(item);
				
				itemArray.add(item);
				
			}catch(Exception e) {
				throw new Exception(e.getMessage() + " in Object: " + (String)listObject);
			}
		}
			
		// - { id: IRON_SWORD, amount: 1, data: 0, ench: {DAMAGE_ALL:1} }
			
		ItemStack[] returnStack = new ItemStack[itemArray.size()];
			
		for (int i = 0; i < returnStack.length; i++) {
			returnStack[i] = itemArray.get(i);
		}	
			
		return returnStack;
	}
	
	
}
