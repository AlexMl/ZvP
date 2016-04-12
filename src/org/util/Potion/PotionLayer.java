package org.util.Potion;

import java.lang.reflect.Method;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;


/**
 * This class is made for Spigot 1.9 and Bukkit 1.9 builds. This class lets you get the potion type from a potion item stack. Bukkit has class org.bukkit.potion.Potion to do this, but that class fails
 * to work properly in minecraft 1.9 due to potions using NBT tags instead of durability values.
 * 
 * @author Michael Forseth
 */
public class PotionLayer {

    private static Method asNMSCopy;
    private static Method stackGetTag;
    private static Method compoundSetString;
    private static Method compoundGetString;
    private static Method stackSetTag;
    private static Method asBukkitCopy;

    static {
	try {
	    asNMSCopy = getCraftClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
	    stackGetTag = getNMSClass("ItemStack").getMethod("getTag");
	    compoundSetString = getNMSClass("NBTTagCompound").getMethod("setString", String.class, String.class);
	    compoundGetString = getNMSClass("NBTTagCompound").getMethod("getString", String.class);
	    stackSetTag = getNMSClass("ItemStack").getMethod("setTag", getNMSClass("NBTTagCompound"));
	    asBukkitCopy = getCraftClass("inventory.CraftItemStack").getMethod("asBukkitCopy", getNMSClass("ItemStack"));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public enum PotionType {
	FIRE_RESISTANCE("fire_resistance"),
	INSTANT_DAMAGE("harming"),
	INSTANT_HEAL("healing"),
	INVISIBILITY("invisibility"),
	JUMP("leaping"),
	LUCK("luck"),
	NIGHT_VISION("night_vision"),
	POISON("poison"),
	REGEN("regeneration"),
	SLOWNESS("slowness"),
	SPEED("swiftness"),
	STRENGTH("strength"),
	WATER("water"),
	WATER_BREATHING("water_breathing"),
	WEAKNESS("weakness"),
	EMPTY("empty"),
	MUNDANE("mundane"),
	THICK("thick"),
	AWKWARD("awkward");

	private String tag;

	private PotionType(String tag) {
	    this.tag = tag;
	}

	String getTag() {
	    return this.tag;
	}

	org.bukkit.potion.PotionType getBukkitType() {
	    for (org.bukkit.potion.PotionType type : org.bukkit.potion.PotionType.values()) {
		if (type.name().equals(name())) {
		    return type;
		}
	    }
	    return null;
	}

	static PotionType match(org.bukkit.potion.PotionType bukkitType) {
	    for (PotionType type : values()) {
		if (type.name().equals(bukkitType.name())) {
		    return type;
		}
	    }
	    return null;
	}
    }

    private static boolean is19 = Bukkit.getServer().getBukkitVersion().contains("1.9");

    private PotionType type;
    private boolean strong, extended, linger, splash;

    /**
     * Construct a new potion of the given type.
     *
     * @param potionType
     *        The potion type
     */
    public static PotionLayer createPotion(PotionType potionType) {
	return new PotionLayer(potionType);
    }

    private PotionLayer(PotionType potionType) {
	this.type = potionType;
	this.strong = false;
	this.extended = false;
	this.linger = false;
	this.splash = false;
    }

    /**
     * Create a new potion of the given type and level.
     *
     * @param type
     *        The type of potion.
     * @param level
     *        The potion's level.
     * @deprecated In favour of {@link #createPotion(PotionType, boolean)}
     */
    @Deprecated
    public static PotionLayer createPotion(PotionType potionType, int level) {
	return new PotionLayer(potionType, level);
    }

    @Deprecated
    private PotionLayer(PotionType type, int level) {
	this(type);
	if (type == null)
	    throw new IllegalArgumentException("Type cannot be null");
	if (type != PotionType.WATER)
	    throw new IllegalArgumentException("Water bottles don't have a level!");
	if (level > 0 && level < 3)
	    throw new IllegalArgumentException("Level must be 1 or 2");
	if (level == 2) {
	    this.strong = true;
	} else {
	    this.strong = false;
	}
    }

    /**
     * Create a new potion of the given type and strength.
     *
     * @param type
     *        The type of potion.
     * @param strong
     *        True if the potion is a strong potion
     */
    public static PotionLayer createPotion(PotionType potionType, boolean strong) {
	return new PotionLayer(potionType, strong);
    }

    private PotionLayer(PotionType type, boolean strong) {
	this(type);
	if (type == null)
	    throw new IllegalArgumentException("Type cannot be null");
	if (type != PotionType.WATER)
	    throw new IllegalArgumentException("Water bottles cannot be strong!");
	this.strong = strong;
    }

    /**
     * This constructs an instance of PotionLayer.
     * 
     * @param type
     * @param strong
     * @param extended
     * @param linger
     * @param splash
     */
    public static PotionLayer createPotion(PotionType type, boolean strong, boolean extended, boolean linger, boolean splash) {
	return new PotionLayer(type, strong, extended, linger, splash);
    }

    private PotionLayer(PotionType type, boolean strong, boolean extended, boolean linger, boolean splash) {
	this.type = type;
	this.strong = strong;
	this.extended = extended;
	this.linger = linger;
	this.splash = splash;
    }

    /**
     * Chain this to the constructor to make the potion a splash potion.
     *
     * @return The potion.
     */
    public PotionLayer splash() {
	setSplash(true);
	return this;
    }

    /**
     * Chain this to the constructor to extend the potion's duration.
     *
     * @return The potion.
     */
    public PotionLayer extend() {
	setHasExtendedDuration(true);
	return this;
    }

    /**
     * Chain this to the constructor to make potion a linger potion.
     *
     * @return The potion.
     */
    public PotionLayer linger() {
	setLinger(true);
	return this;
    }

    /**
     * Chain this to the constructor to make potion a strong potion.
     *
     * @return The potion.
     */
    public PotionLayer strong() {
	setStrong(true);
	return this;
    }

    /**
     * Get the level of the Potion.<br>
     * <br>
     * 1 = normal<br>
     * 2 = strong
     * 
     * @return
     */
    public int getLevel() {
	return isStrong() ? 2 : 1;
    }

    /**
     * This converts PotionLayer to an ItemStack NOTICE:<br>
     * This does not allow a way to change the level of the potion. This will work for only default minecraft potions.
     * 
     * @param amount
     * @return ItemStack of a potion. NULL if it fails.
     * @throws Exception
     */
    public ItemStack toItemStack(int amount) throws Exception {
	ItemStack item = new ItemStack(Material.POTION, amount);
	// System.out.println("1.9? " + is19 + " " + Bukkit.getServer().getVersion());
	if (!is19) {
	    org.bukkit.potion.PotionType bukkitPotionType = getType().getBukkitType();

	    if (bukkitPotionType == null) {
		throw new Exception("no bukkit potiontype enum for " + getType().name());
	    }

	    Potion potion = new Potion(bukkitPotionType);
	    if (isSplash()) {
		potion.splash();
	    }
	    if (isExtendedDuration()) {
		potion.extend();
	    }
	    if (isStrong()) {
		potion.setLevel(2);
	    } else {
		potion.setLevel(1);
	    }
	    return potion.toItemStack(amount);
	}

	try {
	    if (this.splash) {
		item = new ItemStack(Material.valueOf("SPLASH_POTION"), amount);
	    } else if (this.linger) {
		item = new ItemStack(Material.valueOf("LINGERING_POTION"), amount);
	    }
	} catch (IllegalArgumentException e) {
	    return null;
	}

	try {
	    Object nmsStack = asNMSCopy.invoke(null, item);
	    Object nbtTagCompound = stackGetTag.invoke(nmsStack);

	    if (nbtTagCompound == null) {
		nbtTagCompound = getNMSClass("NBTTagCompound").newInstance();
	    }
	    String tag = "";
	    if (this.extended) {
		tag = "long_";
	    } else if (this.strong) {
		tag = "strong_";
	    }
	    tag += this.type.getTag();

	    compoundSetString.invoke(nbtTagCompound, "Potion", "minecraft:" + tag);
	    stackSetTag.invoke(nmsStack, nbtTagCompound);
	    ItemStack stack = (ItemStack) asBukkitCopy.invoke(null, nmsStack);
	    return stack;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * This parses an Potion into an instance of PotionLayer. This lets you get the potion type, if the potion is strong, if the potion is long, if the potion is lingering, and if the potion is a
     * splash potion.
     * 
     * @param item
     * @return {@link PotionLayer}. If it fails to parse, or the item argument is not a valid potion this will return null.
     * @throws Exception
     */
    @Deprecated
    private static PotionLayer fromPotion(Potion potion) throws Exception {
	PotionType type = PotionType.match(potion.getType());
	if (type == null) {
	    throw new Exception("no enum for " + potion.getType().name());
	}
	return new PotionLayer(type, potion.getLevel() > 1, potion.hasExtendedDuration(), false, potion.isSplash());
    }

    /**
     * This parses an ItemStack into an instance of PotionLayer. This lets you get the potion type, if the potion is strong, if the potion is long, if the potion is lingering, and if the potion is a
     * splash potion.
     * 
     * @param item
     * @return {@link PotionLayer}. If it fails to parse, or the item argument is not a valid potion this will return null.
     */
    public static PotionLayer fromItemStack(ItemStack item) {
	Validate.notNull(item, "Item cannot be null!");
	Validate.isTrue(item.getType().name().equals("POTION") || item.getType().name().equals("SPLASH_POTION") || item.getType().name().equals("LINGERING_POTION"), "Item is not a potion!");

	if (!is19) {
	    try {
		return fromPotion(Potion.fromItemStack(item));
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(PotionLayer.class, Level.WARNING, "Can't process potion: " + item.toString() + " " + e.getMessage(), true, false, e);
	    }
	    return null;
	}

	try {
	    Object nmsStack = asNMSCopy.invoke(null, item);
	    Object nbtTagCompound = stackGetTag.invoke(nmsStack);

	    if (nbtTagCompound != null && compoundGetString.invoke(nbtTagCompound, "Potion") != null && !((String) compoundGetString.invoke(nbtTagCompound, "Potion")).isEmpty()) {

		String tag = ((String) compoundGetString.invoke(nbtTagCompound, "Potion")).replace("minecraft:", "");
		PotionType type = null;
		boolean strong = tag.contains("strong");
		boolean _long = tag.contains("long");
		if (tag.equals("fire_resistance") || tag.equals("long_fire_resistance")) {
		    type = PotionType.FIRE_RESISTANCE;
		} else if (tag.equals("harming") || tag.equals("strong_harming")) {
		    type = PotionType.INSTANT_DAMAGE;
		} else if (tag.equals("healing") || tag.equals("strong_healing")) {
		    type = PotionType.INSTANT_HEAL;
		} else if (tag.equals("invisibility") || tag.equals("long_invisibility")) {
		    type = PotionType.INVISIBILITY;
		} else if (tag.equals("leaping") || tag.equals("long_leaping") || tag.equals("strong_leaping")) {
		    type = PotionType.JUMP;
		} else if (tag.equals("luck")) {
		    type = PotionType.LUCK;
		} else if (tag.equals("night_vision") || tag.equals("long_night_vision")) {
		    type = PotionType.NIGHT_VISION;
		} else if (tag.equals("poison") || tag.equals("long_poison") || tag.equals("strong_poison")) {
		    type = PotionType.POISON;
		} else if (tag.equals("regeneration") || tag.equals("long_regeneration") || tag.equals("strong_regeneration")) {
		    type = PotionType.REGEN;
		} else if (tag.equals("slowness") || tag.equals("long_slowness")) {
		    type = PotionType.SLOWNESS;
		} else if (tag.equals("swiftness") || tag.equals("long_swiftness") || tag.equals("strong_swiftness")) {
		    type = PotionType.SPEED;
		} else if (tag.equals("strength") || tag.equals("long_strength") || tag.equals("strong_strength")) {
		    type = PotionType.STRENGTH;
		} else if (tag.equals("water_breathing") || tag.equals("long_water_breathing")) {
		    type = PotionType.WATER_BREATHING;
		} else if (tag.equals("water")) {
		    type = PotionType.WATER;
		} else if (tag.equals("weakness") || tag.equals("long_weakness")) {
		    type = PotionType.WEAKNESS;
		} else if (tag.equals("empty")) {
		    type = PotionType.EMPTY;
		} else if (tag.equals("mundane")) {
		    type = PotionType.MUNDANE;
		} else if (tag.equals("thick")) {
		    type = PotionType.THICK;
		} else if (tag.equals("awkward")) {
		    type = PotionType.AWKWARD;
		} else {
		    return null;
		}

		return new PotionLayer(type, strong, _long, item.getType().name().equals("LINGERING_POTION"), item.getType().name().equals("SPLASH_POTION"));
	    }
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(PotionLayer.class, Level.SEVERE, "Can't process potion: " + item.toString() + " " + e.getMessage(), true, false, e);
	}
	return null;
    }

    /**
     * This gets the potion type
     * 
     * @return PotionType
     */
    public PotionType getType() {
	return this.type;
    }

    /**
     * Sets the PotionType for this Potion1_9
     * 
     * @param type
     */
    public void setType(PotionType type) {
	this.type = type;
    }

    /**
     * A strong potion is a potion which is level II.
     * 
     * @return boolean. True if the potion is strong.
     */
    public boolean isStrong() {
	return this.strong;
    }

    /**
     * This sets if the Potion1_9 is strong.
     * 
     * @param strong
     */
    public void setStrong(boolean strong) {
	this.strong = strong;
    }

    /**
     * A long potion is an extended duration potion.
     * 
     * @return boolen. True if the potion is the extended type.
     */
    public boolean isExtendedDuration() {
	return this.extended;
    }

    /**
     * This changes the _long value for Potion1_9.
     * 
     * @param extended
     */
    public void setHasExtendedDuration(boolean extended) {
	this.extended = extended;
    }

    /**
     * This lets you know if Potion1_9 is a lingering potion.
     * 
     * @return boolean. True if the potion is a lingering potion.
     */
    public boolean isLinger() {
	return this.linger;
    }

    /**
     * Set linger to true or false.
     * 
     * @param linger
     */
    public void setLinger(boolean linger) {
	this.linger = linger;
    }

    /**
     * Checks if a potion is a splash potion.
     * 
     * @return boolean. True if the potion is a splash potion.
     */
    public boolean isSplash() {
	return this.splash;
    }

    /**
     * This sets this Potion1_9 to a splash potion.
     * 
     * @param splash
     */
    public void setSplash(boolean splash) {
	this.splash = splash;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
	if (object instanceof PotionLayer) {
	    PotionLayer test = (PotionLayer) object;
	    if (test.type.equals(this.type) && test.extended == this.extended && test.linger == this.linger && test.splash == this.splash) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Get a nms class from name.
     *
     * @param name
     *        the name of the class
     * @return the Class or null
     */
    private static Class<?> getNMSClass(String name) {
	String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	String className = "net.minecraft.server." + version + "." + name;
	Class<?> nmsClazz = null;
	try {
	    nmsClazz = Class.forName(className);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	return nmsClazz;
    }

    /**
     * Get a craftbukkit class from name.
     *
     * @param name
     *        the name of the class
     * @return the Class or null
     */
    private static Class<?> getCraftClass(String name) {
	String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	String className = "org.bukkit.craftbukkit." + version + "." + name;
	Class<?> nmsClazz = null;
	try {
	    nmsClazz = Class.forName(className);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	return nmsClazz;
    }

}
