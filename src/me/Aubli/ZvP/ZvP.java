package me.Aubli.ZvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Listeners.AsyncChatListener;
import me.Aubli.ZvP.Listeners.BlockListener;
import me.Aubli.ZvP.Listeners.DeathListener;
import me.Aubli.ZvP.Listeners.EntityDamageListener;
import me.Aubli.ZvP.Listeners.GUIListener;
import me.Aubli.ZvP.Listeners.PlayerInteractListener;
import me.Aubli.ZvP.Listeners.PlayerQuitListener;
import me.Aubli.ZvP.Listeners.PlayerRespawnListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.util.Logger.PluginOutput;
import org.util.Metrics.Metrics;


/* ZvP TODO:
 *
 * 	- Some Translations in GameRunnable		+
 *  - Status commands						+
 *  - plugin.yml commands					+
 *  - Add some colors						+
 *  - add kit management
 */

public class ZvP extends JavaPlugin {
    
    private static PluginOutput logger;
    
    private static ZvP instance;
    
    public static ItemStack tool;
    
    private static String pluginPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_RED + "Z" + ChatColor.DARK_GRAY + "v" + ChatColor.DARK_RED + "P" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + " ";
    
    private static Locale locale;
    
    private int logLevel;
    
    private static int maxPlayers;
    private static int DEFAULT_ROUNDS;
    private static int DEFAULT_WAVES;
    private static int START_DELAY;
    private static int DEFAULT_ZOMBIE_SPAWN_RATE;
    private static double DEFAULT_SAVE_RADIUS;
    private static double ZOMBIE_FUND;
    private static double DEATH_FEE;
    
    private boolean useMetrics = false;
    private boolean debugMode = false;
    
    @Override
    public void onDisable() {
	
	for (Player p : Bukkit.getOnlinePlayers()) {
	    removeTool(p);
	}
	GameManager.getManager().shutdown();
	
	logger.log(String.format("[%s] Plugin is disabled!", getDescription().getName()));
    }
    
    @Override
    public void onEnable() {
	initialize();
	
	logger.log(String.format("[%s] Plugin is enabled!", getDescription().getName()));
    }
    
    private void initialize() {
	instance = this;
	
	loadConfig();
	setTool();
	
	logger = new PluginOutput(this, this.debugMode, this.logLevel);
	
	new MessageManager(locale);
	new GameManager();
	new SignManager();
	new ShopManager();
	new KitManager();
	
	registerListeners();
	getCommand("zvp").setExecutor(new ZvPCommands());
	getCommand("test").setExecutor(new ZvPCommands());
	
	if (this.useMetrics == true) {
	    try {
		Metrics metrics = new Metrics(this);
		metrics.start();
	    } catch (IOException e) {
		logger.log(String.format("[%s] Can't start Metrics! Skip!", getDescription().getName()));
	    }
	}
    }
    
    private void registerListeners() {
	PluginManager pm = Bukkit.getPluginManager();
	
	pm.registerEvents(new BlockListener(), this);
	pm.registerEvents(new DeathListener(), this);
	pm.registerEvents(new PlayerInteractListener(), this);
	pm.registerEvents(new PlayerQuitListener(), this);
	pm.registerEvents(new PlayerRespawnListener(), this);
	pm.registerEvents(new SignChangelistener(), this);
	pm.registerEvents(new GUIListener(), this);
	pm.registerEvents(new EntityDamageListener(), this);
	pm.registerEvents(new AsyncChatListener(), this);
    }
    
    private void setTool() {
	tool = new ItemStack(Material.STICK);
	
	List<String> lore = new ArrayList<String>();
	
	ItemMeta toolMeta = tool.getItemMeta();
	toolMeta.setDisplayName(pluginPrefix + ChatColor.BOLD + "Tool");
	toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
	lore.add("Use this tool to add an Arena!");
	toolMeta.setLore(lore);
	
	tool.setItemMeta(toolMeta);
    }
    
    public boolean removeTool(Player player) {
	if (player.getInventory().contains(tool)) {
	    player.getInventory().removeItem(tool);
	    return true;
	}
	return false;
    }
    
    public static ZvP getInstance() {
	return instance;
    }
    
    public static PluginOutput getPluginLogger() {
	return logger;
    }
    
    public static Locale getLocale() {
	return locale;
    }
    
    public static String getPrefix() {
	return pluginPrefix;
    }
    
    public static int getMaxPlayers() {
	return maxPlayers;
    }
    
    public static int getDefaultRounds() {
	return DEFAULT_ROUNDS;
    }
    
    public static int getDefaultWaves() {
	return DEFAULT_WAVES;
    }
    
    public static int getStartDelay() {
	return START_DELAY;
    }
    
    public static int getDefaultSpawnRate() {
	return DEFAULT_ZOMBIE_SPAWN_RATE;
    }
    
    public static double getDefaultDistance() {
	return DEFAULT_SAVE_RADIUS;
    }
    
    public static double getZombieFund() {
	return ZOMBIE_FUND;
    }
    
    public static double getDeathFee() {
	return DEATH_FEE;
    }
    
    public void loadConfig() {
	
	getConfig().options().header("\n" + "This is the main config file for ZombieVsPlayer.\n" + "For more informations about this file visit the website:\n" + "http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
	
	getConfig().addDefault("plugin.enableMetrics", true);
	getConfig().addDefault("plugin.Locale", "en");
	getConfig().addDefault("plugin.debugMode", false);
	getConfig().addDefault("plugin.loglevel", Level.FINE.intValue());
	
	this.useMetrics = getConfig().getBoolean("plugin.enableMetrics");
	this.debugMode = getConfig().getBoolean("plugin.debugMode");
	locale = new Locale(getConfig().getString("plugin.Locale"));
	this.logLevel = getConfig().getInt("plugin.loglevel");
	
	getConfig().addDefault("game.maximal_Players", 25);
	getConfig().addDefault("game.default_rounds", 3);
	getConfig().addDefault("game.default_waves", 5);
	
	maxPlayers = getConfig().getInt("game.maximal_Players");
	DEFAULT_ROUNDS = getConfig().getInt("game.default_rounds");
	DEFAULT_WAVES = getConfig().getInt("game.default_waves");
	
	getConfig().addDefault("times.joinTime", 15);
	
	START_DELAY = getConfig().getInt("times.joinTime");
	
	getConfig().addDefault("zombies.default_spawnRate", 20);
	getConfig().addDefault("zombies.default_saveRadius", 3.0);
	
	DEFAULT_ZOMBIE_SPAWN_RATE = getConfig().getInt("zombies.default_spawnRate");
	DEFAULT_SAVE_RADIUS = getConfig().getDouble("zombies.default_saveRadius");
	
	getConfig().addDefault("money.ZombieFund", 0.37);
	getConfig().addDefault("money.DeathFee", 3);
	
	ZOMBIE_FUND = getConfig().getDouble("money.ZombieFund");
	DEATH_FEE = getConfig().getDouble("money.DeathFee");
	
	// this.getConfig().addDefault("config.misc.portOnJoinGame", true);
	// this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
	
	getConfig().options().copyDefaults(true);
	saveConfig();
    }
    
}
