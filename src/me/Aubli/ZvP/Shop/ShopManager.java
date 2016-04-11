package me.Aubli.ZvP.Shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Translation.MessageKeys.category;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.util.File.ItemStorageUtil.ItemStorage;

import com.google.common.io.Files;


public class ShopManager {

    public enum ItemCategory {
	FOOD(new ItemStack(Material.APPLE), MessageManager.getMessage(category.food)),
	ARMOR(new ItemStack(Material.IRON_HELMET), MessageManager.getMessage(category.armor)),
	WEAPON(new ItemStack(Material.STONE_SWORD), MessageManager.getMessage(category.weapon)),
	POTION(new Potion(PotionType.INSTANT_HEAL, 1).toItemStack(1), MessageManager.getMessage(category.potion)),
	MISC(new ItemStack(Material.BUCKET), MessageManager.getMessage(category.misc)),
	NULL(null, ""), ;

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

	    for (ItemCategory cat : ItemCategory.values()) {
		if (cat.toString().equals(string)) {
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

    private ShopManager() {
	this.itemFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Shop/items.yml");
	this.itemConfig = YamlConfiguration.loadConfiguration(this.itemFile);

	if (!this.itemFile.exists()) {
	    try {
		this.itemFile.getParentFile().mkdirs();
		this.itemFile.createNewFile();
		writeDefaults();
		this.itemConfig = YamlConfiguration.loadConfiguration(this.itemFile);
	    } catch (IOException e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving item config: " + e.getMessage(), true, false, e);
	    }
	}

	if (isOutdated()) {
	    try {
		ZvP.getPluginLogger().log(this.getClass(), "Found outdated item file! Updating it!", true);
		Files.copy(this.itemFile, new File(this.itemFile.getParentFile().getPath() + "/items-" + this.itemConfig.getString("version") + ".yml"));
		writeDefaults();
		this.itemConfig = YamlConfiguration.loadConfiguration(this.itemFile);
	    } catch (IOException e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving item config: " + e.getMessage(), true, false, e);
	    }
	}

	this.items = loadItems();
    }

    public static synchronized ShopManager init() {
	if (instance == null) {
	    instance = new ShopManager();
	}
	return instance;
    }

    public static ShopManager getManager() {
	return init();
    }

    public static void reload() {
	instance = new ShopManager();
    }

    private boolean isOutdated() {
	return !ZvP.getInstance().getDescription().getVersion().equals(this.itemConfig.getString("version"));
    }

    private void writeDefaults() {

	try {
	    this.itemConfig.options().header("This is the config file used in ZvP to store all items for the ingame Shop.\n\n" + "'id:' The id describes the item material. A list of all items can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html\n" + "'amount:' The amount of the item (Should be 1!)\n" + "'data:' Used by potions\n" + "'category:' The shop category can be FOOD, MISC, ARMOR, WEAPON, POTION\n" + "'price:' The price of the item\n" + "'ench: {}' A list of enchantings (ench: {ENCHANTMENT:LEVEL}). A list of enchantments can be found here:\n https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html\n");
	    this.itemConfig.options().copyHeader(true);

	    this.itemConfig.set("version", ZvP.getInstance().getDescription().getVersion());
	    this.itemConfig.save(this.itemFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving item config: " + e.getMessage(), true, false, e);
	}

	List<ShopItem> defaultItems = new ArrayList<ShopItem>();

	ItemStack item;
	ItemCategory cat;

	// Food
	cat = ItemCategory.FOOD;
	item = new ItemStack(Material.APPLE);
	defaultItems.add(new ShopItem(item, cat, 2.0, 1.0));
	item = new ItemStack(Material.BAKED_POTATO);
	defaultItems.add(new ShopItem(item, cat, 4.0, 2.0));
	item = new ItemStack(Material.BREAD);
	defaultItems.add(new ShopItem(item, cat, 3.5, 1.5));
	item = new ItemStack(Material.CARROT_ITEM);
	defaultItems.add(new ShopItem(item, cat, 2.0, 1.0));
	item = new ItemStack(Material.COOKED_BEEF);
	defaultItems.add(new ShopItem(item, cat, 5.0, 3.5));
	item = new ItemStack(Material.COOKED_CHICKEN);
	defaultItems.add(new ShopItem(item, cat, 5.0, 3.5));
	item = new ItemStack(Material.COOKED_FISH);
	defaultItems.add(new ShopItem(item, cat, 6.0, 4.0));
	item = new ItemStack(Material.COOKIE);
	defaultItems.add(new ShopItem(item, cat, 1.5, 0.5));
	item = new ItemStack(Material.GRILLED_PORK);
	defaultItems.add(new ShopItem(item, cat, 5.0, 3.5));

	// Misc
	cat = ItemCategory.MISC;
	item = new ItemStack(Material.STICK);
	defaultItems.add(new ShopItem(item, cat, 1.0, 0.25));
	item = new ItemStack(Material.EXP_BOTTLE);
	defaultItems.add(new ShopItem(item, cat, 7.5, 7.0));

	Dye lapis = new Dye();
	lapis.setColor(DyeColor.BLUE);
	item = lapis.toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 2.0, 1.0));

	item = new ItemStack(Material.LEATHER);
	defaultItems.add(new ShopItem(item, cat, 2.0, 1.0));
	item = new ItemStack(Material.FEATHER);
	defaultItems.add(new ShopItem(item, cat, 1.5, 0.5));
	item = new ItemStack(Material.ROTTEN_FLESH);
	defaultItems.add(new ShopItem(item, cat, 0.15, 0.15));
	item = new ItemStack(Material.POTATO_ITEM);
	defaultItems.add(new ShopItem(item, cat, 4.5, 3.0));
	item = new ItemStack(Material.GOLD_INGOT);
	defaultItems.add(new ShopItem(item, cat, 5.0, 4.5));
	item = new ItemStack(Material.IRON_INGOT);
	defaultItems.add(new ShopItem(item, cat, 3.5, 3.25));

	// Armor
	cat = ItemCategory.ARMOR;
	item = new ItemStack(Material.LEATHER_HELMET);
	defaultItems.add(new ShopItem(item, cat, 3.0, 2.0));
	item = new ItemStack(Material.LEATHER_CHESTPLATE);
	defaultItems.add(new ShopItem(item, cat, 4.0, 2.0));
	item = new ItemStack(Material.LEATHER_LEGGINGS);
	defaultItems.add(new ShopItem(item, cat, 4.0, 2.0));
	item = new ItemStack(Material.LEATHER_BOOTS);
	defaultItems.add(new ShopItem(item, cat, 3.0, 1.5));

	item = new ItemStack(Material.CHAINMAIL_HELMET);
	defaultItems.add(new ShopItem(item, cat, 4.0, 3.0));
	item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
	defaultItems.add(new ShopItem(item, cat, 4.5, 4.0));
	item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
	defaultItems.add(new ShopItem(item, cat, 4.5, 3.5));
	item = new ItemStack(Material.CHAINMAIL_BOOTS);
	defaultItems.add(new ShopItem(item, cat, 3.5, 2.5));

	item = new ItemStack(Material.IRON_HELMET);
	defaultItems.add(new ShopItem(item, cat, 4.0, 3.0));
	item = new ItemStack(Material.IRON_CHESTPLATE);
	defaultItems.add(new ShopItem(item, cat, 5.5, 4.5));
	item = new ItemStack(Material.IRON_LEGGINGS);
	defaultItems.add(new ShopItem(item, cat, 5.0, 4.5));
	item = new ItemStack(Material.IRON_BOOTS);
	defaultItems.add(new ShopItem(item, cat, 3.0, 2.0));

	item = new ItemStack(Material.GOLD_HELMET);
	defaultItems.add(new ShopItem(item, cat, 3.5, 2.75));
	item = new ItemStack(Material.GOLD_CHESTPLATE);
	defaultItems.add(new ShopItem(item, cat, 5.0, 4.0));
	item = new ItemStack(Material.GOLD_LEGGINGS);
	defaultItems.add(new ShopItem(item, cat, 4.5, 4.0));
	item = new ItemStack(Material.GOLD_BOOTS);
	defaultItems.add(new ShopItem(item, cat, 3.0, 2.25));

	item = new ItemStack(Material.DIAMOND_HELMET);
	defaultItems.add(new ShopItem(item, cat, 7.0, 6.0));
	item = new ItemStack(Material.DIAMOND_CHESTPLATE);
	defaultItems.add(new ShopItem(item, cat, 10.0, 8.5));
	item = new ItemStack(Material.DIAMOND_LEGGINGS);
	defaultItems.add(new ShopItem(item, cat, 9.0, 8.0));
	item = new ItemStack(Material.DIAMOND_BOOTS);
	defaultItems.add(new ShopItem(item, cat, 6.0, 5.0));

	// weapons
	cat = ItemCategory.WEAPON;
	item = new ItemStack(Material.DIAMOND_AXE);
	defaultItems.add(new ShopItem(item, cat, 9.0, 7.0));
	item = new ItemStack(Material.DIAMOND_SWORD);
	defaultItems.add(new ShopItem(item, cat, 10.0, 8.0));
	item = new ItemStack(Material.IRON_AXE);
	defaultItems.add(new ShopItem(item, cat, 5.0, 3.0));
	item = new ItemStack(Material.IRON_SWORD);
	defaultItems.add(new ShopItem(item, cat, 6.0, 4.0));
	item = new ItemStack(Material.GOLD_SWORD);
	defaultItems.add(new ShopItem(item, cat, 4.0, 3.0));
	item = new ItemStack(Material.STONE_SWORD);
	defaultItems.add(new ShopItem(item, cat, 4.0, 2.0));
	item = new ItemStack(Material.STONE_AXE);
	defaultItems.add(new ShopItem(item, cat, 4.0, 2.0));
	item = new ItemStack(Material.WOOD_SWORD);
	defaultItems.add(new ShopItem(item, cat, 2.0, 0.5));

	item = new ItemStack(Material.BOW);
	defaultItems.add(new ShopItem(item, cat, 6.0, 4.0));
	item = new ItemStack(Material.ARROW);
	defaultItems.add(new ShopItem(item, cat, 0.08, 0.01));

	// Enchanted
	item = new ItemStack(Material.DIAMOND_SWORD);
	item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
	item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
	item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 3);
	item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
	defaultItems.add(new ShopItem(item, cat, 35.0, 30.0));

	// Potions
	cat = ItemCategory.POTION;
	item = new Potion(PotionType.FIRE_RESISTANCE, 1).splash().toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 3.5, 3.0));
	item = new Potion(PotionType.REGEN, 2).splash().toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 5.0, 4.5));
	item = new Potion(PotionType.INSTANT_HEAL, 1).splash().toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 3.5, 3.0));
	item = new Potion(PotionType.SPEED, 2).splash().toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 4.0, 3.5));
	item = new Potion(PotionType.STRENGTH, 2).splash().toItemStack(1);
	defaultItems.add(new ShopItem(item, cat, 5.0, 4.5));

	ItemStorage.saveItemsToFile(this.itemFile, "items", defaultItems);
    }

    private ShopItem[] loadItems() {
	try {
	    return ItemStorage.getShopItemsFromFile(this.itemConfig.getList("items"));
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while loading Item from shop configuration!\nError: " + e.getMessage() + " in File " + this.itemFile.getPath(), true, false, e);
	}
	return null;
    }

    public ShopItem[] getItems() {
	return this.items;
    }

    public List<ShopItem> getItems(ItemCategory cat) {
	List<ShopItem> items = new ArrayList<ShopItem>();
	for (ShopItem s : getItems()) {
	    if (s.getCategory() == cat) {
		items.add(s);
	    }
	}
	return items;
    }

    public ShopItem getItem(ItemCategory cat, ItemStack stack) {
	for (ShopItem item : getItems(cat)) {
	    if (item.getItem().equals(stack)) {
		return item;
	    }
	}
	return null;
    }
}
