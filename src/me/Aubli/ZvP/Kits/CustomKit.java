package me.Aubli.ZvP.Kits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CustomKit implements ZvPKit{

	
	private final File kitFile;
	
	private final String name;
	
	private final ItemStack[] items;
	
	
	public CustomKit(String path, String name, ItemStack[] content) {	
		this.name = name;
		this.items = content;
	
		kitFile = new File(path + "/" + name + ".yml");
		FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
		
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
		
		kitConfig.set("name", name);
		kitConfig.set("items", itemList);

		try {
			kitConfig.save(kitFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public CustomKit(File kitFile) {
		
		this.kitFile = kitFile;
		FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
		
		this.name = kitConfig.getString("name");
		
		List<?> items = kitConfig.getList("items"); 
		
		this.items = parseItemStack(items);
	}	
	
	private ItemStack[] parseItemStack(List<?> itemList) {
		
		ArrayList<ItemStack> itemArray = new ArrayList<ItemStack>();
		
		for(Object listObject : itemList) {
			
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
		}
		
		// - { id: IRON_SWORD, amount: 1, data: 0, ench: {DAMAGE_ALL:1} }
		
		
		
		ItemStack[] returnStack = new ItemStack[itemArray.size()];
		
		for (int i = 0; i < returnStack.length; i++) {
			returnStack[i] = itemArray.get(i);
		}				
		return returnStack;
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
	public ItemStack[] getContents() {
		return items.clone();
	}
}
