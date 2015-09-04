package me.Aubli.ZvP.Kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;


public class KEssentialsKit implements IZvPKit, Comparable<IZvPKit> {
    
    private enum essEnchants {
	ARROW_FIRE("firearrow", "flame"),
	ARROW_DAMAGE("arrowdamage", "power"),
	ARROW_KNOCKBACK("arrowknockback", "arrowkb", "punch"),
	ARROW_INFINITE("infinitearrows", "infarrows", "infinity"),
	DAMAGE_ALL("alldamage", "alldmg", "sharpness"),
	DAMAGE_ARTHROPODS("arthropodsdamage", "ardmg", "baneofarthropods"),
	DAMAGE_UNDEAD("undeaddamage", "smite"),
	DIG_SPEED("digspeed", "efficiency"),
	DURABILITY("durability", "dura", "unbreaking"),
	FIRE_ASPECT("fireaspect", "fire"),
	KNOCKBACK("knockback"),
	LOOT_BONUS_BLOCKS("blockslootbonus", "fortune"),
	LOOT_BONUS_MOBS("mobslootbonus", "mobloot", "looting"),
	OXYGEN("oxygen", "respiration"),
	PROTECTION_ENVIRONMENTAL("protection", "prot"),
	PROTECTION_EXPLOSIONS("explosionsprotection", "expprot", "blastprotection"),
	PROTECTION_FALL("fallprotection", "fallprot", "featherfall", "featherfalling"),
	PROTECTION_FIRE("fireprotection", "fireprot"),
	PROTECTION_PROJECTILE("projectileprotection", "projprot"),
	SILK_TOUCH("silktouch"),
	WATER_WORKER("waterworker", "aquaaffinity");
	
	private String[] enchantStrings;
	
	private essEnchants(String... essEnchantNames) {
	    this.enchantStrings = essEnchantNames;
	}
	
	public String[] getESSEnchantmentStrings() {
	    return this.enchantStrings;
	}
	
	public Enchantment getBukkitEnchantment() {
	    return Enchantment.getByName(this.name());
	}
	
	public boolean matches(String essEnchantment) {
	    for (String ench : getESSEnchantmentStrings()) {
		if (ench.equalsIgnoreCase(essEnchantment)) {
		    return true;
		}
	    }
	    return false;
	}
	
	public static Enchantment getBukkitEnchantment(String essEnchantString) {
	    for (essEnchants esse : values()) {
		if (esse.matches(essEnchantString)) {
		    return esse.getBukkitEnchantment();
		}
	    }
	    return null;
	}
	
	public static boolean contains(String essEnchantString) {
	    return getBukkitEnchantment(essEnchantString) != null;
	}
    }
    
    private enum essPotionTyps {
	SPEED("speed"),
	SLOWNESS("slowness"),
	STRENGTH("strength"),
	INSTANT_HEAL("heal"),
	INSTANT_DAMAGE("harm"),
	JUMP("jump"),
	REGEN("regeneration"),
	FIRE_RESISTANCE("fireresist"),
	WATER_BREATHING("waterbreath"),
	INVISIBILITY("invisibility"),
	NIGHT_VISION("nightvision"),
	WEAKNESS("weakness"),
	POISON("poison"), ;
	
	/*
	 * Not Supported:
		haste
		fatigue	
		nausea	
		resistance	
		blindness	
		hunger	
		wither
		healthboost
		absorption
		saturation

	 */
	
	private String[] typeStrings;
	
	private essPotionTyps(String... essEffectNames) {
	    this.typeStrings = essEffectNames;
	}
	
	public String[] getESSTypeStrings() {
	    return this.typeStrings;
	}
	
	public PotionType getBukkitPotionType() {
	    return PotionType.valueOf(this.name());
	}
	
	public boolean matches(String essPotionType) {
	    for (String type : getESSTypeStrings()) {
		if (type.equalsIgnoreCase(essPotionType)) {
		    return true;
		}
	    }
	    return false;
	}
	
	public static PotionType getBukkitPotionType(String essPotionTypeString) {
	    for (essPotionTyps essp : values()) {
		if (essp.matches(essPotionTypeString)) {
		    return essp.getBukkitPotionType();
		}
	    }
	    return null;
	}
    }
    
    private final String name;
    
    private final ItemStack[] items;
    
    @SuppressWarnings("deprecation")
    public KEssentialsKit(String kitName, ConfigurationSection kitConfig) {
	this.name = kitName;
	
	List<String> itemList = kitConfig.getStringList("items");
	List<ItemStack> itemStackList = new ArrayList<ItemStack>(itemList.size());
	
	// itemID[:DataValue/Durability] Amount [Enchantment:Level].. [itemmeta:value]...
	try {
	    for (String listEntry : itemList) {
		String[] itemArgs = listEntry.split(" ");
		
		int materialID;
		short durabillity;
		int amount;
		
		if (itemArgs[0].contains(":")) {
		    materialID = Integer.parseInt(itemArgs[0].split(":")[0]);
		    durabillity = Short.parseShort(itemArgs[0].split(":")[1]);
		} else {
		    materialID = Integer.parseInt(itemArgs[0]);
		    durabillity = 0;
		}
		
		amount = Integer.parseInt(itemArgs[1]);
		Map<Enchantment, Integer> enchMap = new HashMap<Enchantment, Integer>();
		Potion potion = null;
		
		String name = null;
		List<String> lore = new ArrayList<String>();
		
		if (itemArgs.length > 2) {
		    String[] metaArgs = Arrays.copyOfRange(itemArgs, 2, itemArgs.length);
		    
		    // System.out.println(Arrays.toString(metaArgs));
		    
		    boolean enchanting = true;
		    
		    for (int i = 0; i < metaArgs.length; i++) {
			String metaArg = metaArgs[i];
			String metaArgKey = metaArg.split(":")[0];
			
			if (enchanting && essEnchants.contains(metaArgKey)) {
			    enchMap.put(essEnchants.getBukkitEnchantment(metaArgKey), Integer.parseInt(metaArg.split(":")[1]));
			} else if (!essEnchants.contains(metaArgKey) && i == 1) {
			    enchanting = false;
			}
			
			if (materialID == Material.POTION.getId()) {
			    if (metaArgKey.equalsIgnoreCase("effect")) {
				PotionType type = essPotionTyps.getBukkitPotionType(metaArg.split(":")[1]);
				int power = Integer.parseInt(metaArgs[i + 1].split(":")[1]);
				
				// int duration = Integer.parseInt(metaArgs[i+2].split(":")[1]);
				// INFO: Duration currently not supported
				
				if (type != null) {
				    potion = new Potion(type, power);
				}
			    }
			}
			
			if (metaArgKey.equalsIgnoreCase("name")) {
			    name = ChatColor.translateAlternateColorCodes('&', metaArg.split(":")[1]);
			}
			if (metaArgKey.equalsIgnoreCase("lore")) {
			    lore.clear();
			    for (String loreString : metaArg.split(":")[1].replace("_", " ").replace("|", "linebreak").split("linebreak")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', loreString));
			    }
			}
			
		    }
		}
		
		ItemStack kitItem;
		
		if (potion != null) {
		    kitItem = potion.toItemStack(amount);
		} else {
		    kitItem = new ItemStack(materialID, amount, durabillity);
		    kitItem.addUnsafeEnchantments(enchMap);
		}
		
		ItemMeta meta = kitItem.getItemMeta();
		if (name != null) {
		    meta.setDisplayName(name);
		}
		meta.setLore(lore);
		kitItem.setItemMeta(meta);
		
		itemStackList.add(kitItem);
	    }
	    
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(getClass(), Level.WARNING, "Error while loading Essentials kit " + getName() + ": " + e.getMessage(), true, false, e);
	}
	
	this.items = new ItemStack[itemStackList.size()];
	for (int i = 0; i < itemStackList.size(); i++) {
	    this.items[i] = itemStackList.get(i);
	}
    }
    
    @Override
    public void delete() {
	// Essentials kits are read-only
	// No deletion allowed!
    }
    
    @Override
    public boolean isEnabled() {
	return true;
    }
    
    @Override
    public String getName() {
	return this.name;
    }
    
    @Override
    public ItemStack getIcon() {
	return new ItemStack(Material.IRON_PICKAXE);
    }
    
    @Override
    public String getPermissionNode() {
	return "zvp.play";
    }
    
    @Override
    public double getPrice() {
	return 0;// TODO parse price from config file
    }
    
    @Override
    public ItemStack[] getContents() {
	return this.items;
    }
    
    @Override
    public int compareTo(IZvPKit o) {
	return +10;
    }
    
}
