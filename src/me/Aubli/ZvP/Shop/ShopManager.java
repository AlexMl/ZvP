package me.Aubli.ZvP.Shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.util.ItemStorageUtil.ItemStorage;

public class ShopManager {

	public enum ItemCategory {		//TODO names
		FOOD(new ItemStack(Material.APPLE), "Food"),
		ARMOR(new ItemStack(Material.IRON_HELMET), "Armor"),
		WEAPON(new ItemStack(Material.STONE_SWORD), "Weapons"),
		POTION(new Potion(PotionType.INSTANT_HEAL, 2).toItemStack(1), "Potions"),
		MISC(new ItemStack(Material.BUCKET), "Misc"),
		NULL(null, ""),
		;		
		
		private ItemStack icon;
		private String name;
		
		private ItemCategory(ItemStack icon, String name) {
			this.icon = icon;
			this.name = name;
		}
		
		public ItemStack getIcon() {
			return this.icon;
		}
		
		
		public static ItemCategory getEnum(String string) {

			for(ItemCategory cat : ItemCategory.values()) {
				if(cat.toString().equals(string)) {
					return cat;
				}
			}
			throw new IllegalArgumentException("String '" + string + "' has no enum!");			
		}
		
		public String getEnumName() {
			return super.toString();
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}	
	
	private static ShopManager instance;
	
	private File itemFile;
	private FileConfiguration itemConfig;
	
	private ShopItem[] items;
	
	
	public ShopManager() {	
		instance = this;
		
		itemFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Shop/items.yml");
		itemConfig = YamlConfiguration.loadConfiguration(itemFile);	
		
		if(!itemFile.exists()) {
			try {
				itemFile.getParentFile().mkdirs();
				itemFile.createNewFile();
				writeDefaults();
				itemConfig = YamlConfiguration.loadConfiguration(itemFile);	
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
		if(isOutdated()) {
			try {	
				ZvP.log.info("[" + ZvP.getInstance().getName() + "] Found outdated item file! Renaming it!");
				itemFile.renameTo(new File(itemFile.getParentFile().getPath() + "/items-" + itemConfig.getString("version") + ".yml"));
				itemFile.delete();
				itemFile.createNewFile();
				writeDefaults();
				itemConfig = YamlConfiguration.loadConfiguration(itemFile);	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		items = loadItems();
	}
	
	private boolean isOutdated() {	
		return !ZvP.getInstance().getDescription().getVersion().equals(itemConfig.getString("version"));
	}
	
	
	private void writeDefaults() {
		
		try {
			itemConfig.set("version", ZvP.getInstance().getDescription().getVersion());
			itemConfig.save(itemFile);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		ShopItem[] defaultItems = new ShopItem[49];
		
		ItemStack item;
		ItemCategory cat;
		
		//Food
		cat = ItemCategory.FOOD;		
		item = new ItemStack(Material.APPLE);
		defaultItems[0] = new ShopItem(item, cat, 2.0);
		item = new ItemStack(Material.BAKED_POTATO);
		defaultItems[1] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.BREAD);
		defaultItems[2] = new ShopItem(item, cat, 3.5);
		item = new ItemStack(Material.CARROT_ITEM);
		defaultItems[3] = new ShopItem(item, cat, 2.0);
		item = new ItemStack(Material.COOKED_BEEF);
		defaultItems[4] = new ShopItem(item, cat, 5.0);
		item = new ItemStack(Material.COOKED_CHICKEN);
		defaultItems[5] = new ShopItem(item, cat, 5.0);
		item = new ItemStack(Material.COOKED_FISH);
		defaultItems[6] = new ShopItem(item, cat, 6.0);
		item = new ItemStack(Material.COOKIE);
		defaultItems[7] = new ShopItem(item, cat, 1.5);
		item = new ItemStack(Material.GRILLED_PORK);
		defaultItems[8] = new ShopItem(item, cat, 5.0);
		
		
		//Misc
		cat = ItemCategory.MISC;
		item = new ItemStack(Material.STICK);
		defaultItems[9] = new ShopItem(item, cat, 1.0);
		item = new ItemStack(Material.EXP_BOTTLE);
		defaultItems[10] = new ShopItem(item, cat, 7.5);
		item = new ItemStack(Material.LEATHER);
		defaultItems[11] = new ShopItem(item, cat, 2.0);
		item = new ItemStack(Material.FEATHER);
		defaultItems[12] = new ShopItem(item, cat, 1.5);
		item = new ItemStack(Material.ROTTEN_FLESH);
		defaultItems[13] = new ShopItem(item, cat, 0.15);
		item = new ItemStack(Material.POTATO_ITEM);
		defaultItems[14] = new ShopItem(item, cat, 4.5);
		item = new ItemStack(Material.GOLD_INGOT);
		defaultItems[15] = new ShopItem(item, cat, 3.0);
		item = new ItemStack(Material.IRON_INGOT);
		defaultItems[16] = new ShopItem(item, cat, 3.5);
		
		
		//Armor
		cat = ItemCategory.ARMOR;
		item = new ItemStack(Material.LEATHER_HELMET);
		defaultItems[17] = new ShopItem(item, cat, 3.0);
		item = new ItemStack(Material.LEATHER_CHESTPLATE);
		defaultItems[18] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.LEATHER_LEGGINGS);
		defaultItems[19] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.LEATHER_BOOTS);
		defaultItems[20] = new ShopItem(item, cat, 3.0);
		
		item = new ItemStack(Material.CHAINMAIL_HELMET);
		defaultItems[21] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		defaultItems[22] = new ShopItem(item, cat, 4.5);
		item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		defaultItems[23] = new ShopItem(item, cat, 4.5);
		item = new ItemStack(Material.CHAINMAIL_BOOTS);
		defaultItems[24] = new ShopItem(item, cat, 3.5);
		
		item = new ItemStack(Material.IRON_HELMET);
		defaultItems[25] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.IRON_CHESTPLATE);
		defaultItems[26] = new ShopItem(item, cat, 5.5);
		item = new ItemStack(Material.IRON_LEGGINGS);
		defaultItems[27] = new ShopItem(item, cat, 5.0);
		item = new ItemStack(Material.IRON_BOOTS);
		defaultItems[28] = new ShopItem(item, cat, 3.0);
		
		item = new ItemStack(Material.GOLD_HELMET);
		defaultItems[29] = new ShopItem(item, cat, 3.5);
		item = new ItemStack(Material.GOLD_CHESTPLATE);
		defaultItems[30] = new ShopItem(item, cat, 5.0);
		item = new ItemStack(Material.GOLD_LEGGINGS);
		defaultItems[31] = new ShopItem(item, cat, 4.5);
		item = new ItemStack(Material.GOLD_BOOTS);
		defaultItems[32] = new ShopItem(item, cat, 3.0);
		
		
		//weapons
		cat = ItemCategory.WEAPON;
		item = new ItemStack(Material.DIAMOND_AXE);
		defaultItems[33] = new ShopItem(item, cat, 9.0);
		item = new ItemStack(Material.DIAMOND_SWORD);
		defaultItems[34] = new ShopItem(item, cat, 10.0);
		item = new ItemStack(Material.IRON_AXE);
		defaultItems[35] = new ShopItem(item, cat, 5.0);
		item = new ItemStack(Material.IRON_SWORD);
		defaultItems[36] = new ShopItem(item, cat, 6.0);
		item = new ItemStack(Material.GOLD_SWORD);
		defaultItems[37] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.STONE_SWORD);
		defaultItems[38] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.STONE_AXE);
		defaultItems[39] = new ShopItem(item, cat, 4.0);
		item = new ItemStack(Material.WOOD_SWORD);
		defaultItems[40] = new ShopItem(item, cat, 2.0);		
		
		item = new ItemStack(Material.BOW);
		defaultItems[41] = new ShopItem(item, cat, 6.0);
		item = new ItemStack(Material.ARROW);
		defaultItems[42] = new ShopItem(item, cat, 0.08);		
		
		//Enchanted
		item = new ItemStack(Material.DIAMOND_SWORD);
		item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
		item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 3);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
		defaultItems[43] = new ShopItem(item, cat, 25.0);
		
		//Potions
		cat = ItemCategory.POTION;
		item = new Potion(PotionType.FIRE_RESISTANCE, 2).splash().toItemStack(1);
		defaultItems[44] = new ShopItem(item, cat, 3.5);		
		item = new Potion(PotionType.REGEN, 2).splash().toItemStack(1);
		defaultItems[45] = new ShopItem(item, cat, 5.0);
		item = new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1);
		defaultItems[46] = new ShopItem(item, cat, 3.5);
		item = new Potion(PotionType.SPEED, 2).splash().toItemStack(1);
		defaultItems[47] = new ShopItem(item, cat, 4.0);
		item = new Potion(PotionType.STRENGTH, 2).splash().toItemStack(1);
		defaultItems[48] = new ShopItem(item, cat, 5.0);		
		
		
		ItemStorage.saveItemsToFile(itemFile, "items", defaultItems);
	}
	
	
	private ShopItem[] loadItems(){
		try {
			return ItemStorage.getShopItemsFromFile(itemConfig.getList("items"));
		} catch (Exception e) {
			ZvP.log.log(Level.WARNING, "Error while loading Item from shop configuration!\nError: " + e.getMessage() + " in File " + itemFile.getPath(), e);
		}
		return null;
	}
	
	
	public static ShopManager getManager() {
		return instance;
	}
	
	public ShopItem[] getItems() {
		return items;
	}
	
	public List<ShopItem> getItems(ItemCategory cat) {
		List<ShopItem> items = new ArrayList<ShopItem>();
		for(ShopItem s : getItems()) {
			if(s.getCategory()==cat) {
				items.add(s);
			}
		}
		return items;
	}
	
	public ShopItem getItem(ItemCategory cat, ItemStack stack) {
		for(ShopItem item : getItems(cat)) {
			if(item.getItem().equals(stack)) {
				return item;
			}
		}
		return null;
	}
	
	public double getPrice(ItemStack item) {
		return 0;
	}
}
