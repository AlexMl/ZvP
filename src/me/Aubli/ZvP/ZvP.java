package me.Aubli.ZvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Listeners.BlockListener;
import me.Aubli.ZvP.Listeners.ChatListener;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Listeners.GUIListener;
import me.Aubli.ZvP.Listeners.InteractListener;
import me.Aubli.ZvP.Listeners.PlayerListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.util.File.Converter.FileConverter;
import org.util.Logger.PluginOutput;
import org.util.Metrics.Metrics;
import org.util.Metrics.Metrics.Graph;
import org.util.Updater.Updater;
import org.util.Updater.Updater.UpdateResult;
import org.util.Updater.Updater.UpdateType;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class ZvP extends JavaPlugin {
    
    private static PluginOutput logger;
    
    private static FileConverter converter;
    
    private static ZvP instance;
    
    private static Economy economy;
    private static WorldGuardPlugin worldGuard;
    
    public static final String ADDARENA_SINGLE = "Use this tool to create arenas with two positions!";
    public static final String ADDARENA_POLYGON = "Use this tool to create polygon sized arenas";
    public static final String ADDPOSITION = "Use this tool to add a spawn position";
    
    private static String pluginPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_RED + "Z" + ChatColor.DARK_GRAY + "v" + ChatColor.DARK_RED + "P" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + " ";
    
    private final int pluginID = 59021;
    
    public static boolean updateAvailable = false;
    public static String newVersion = "";
    
    @Override
    public void onDisable() {
	
	for (Player p : Bukkit.getOnlinePlayers()) {
	    removeTool(p);
	}
	GameManager.getManager().shutdown();
	
	logger.log(this.getClass(), "Plugin is disabled!", false);
    }
    
    @Override
    public void onEnable() {
	initialize();
	
	logger.log(this.getClass(), "Plugin is enabled!", false);
    }
    
    private void initialize() {
	instance = this;
	
	new ZvPConfig(getConfig());
	
	logger = new PluginOutput(this, ZvPConfig.getDebugMode(), ZvPConfig.getLogLevel());
	
	converter = new FileConverter(this);
	
	new MessageManager(ZvPConfig.getLocale());
	new GameManager();
	new SignManager();
	new ShopManager();
	new KitManager(ZvPConfig.getEnableKits());
	
	registerListeners();
	getCommand("zvp").setExecutor(new ZvPCommands());
	getCommand("zvptest").setExecutor(new ZvPCommands());
	
	if (ZvPConfig.getEnableEcon()) {
	    if (getServer().getPluginManager().getPlugin("Vault") != null) {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		
		if (economyProvider != null) {
		    economy = economyProvider.getProvider();
		    getPluginLogger().log(this.getClass(), Level.INFO, "Successfully hooked into Vault economy!", false, false);
		} else {
		    getPluginLogger().log(this.getClass(), Level.WARNING, "Could not hook into Vault! Disabling economy ...", false);
		    ZvPConfig.setEconEnabled(false);
		}
	    } else {
		getPluginLogger().log(this.getClass(), Level.WARNING, "Economy is enabled but Vault is not installed! Disabling economy ...", false);
		ZvPConfig.setEconEnabled(false);
	    }
	}
	
	if (ZvPConfig.getHandleWorldGuard()) {
	    if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
		worldGuard = WGBukkit.getPlugin();
	    } else {
		getPluginLogger().log(this.getClass(), Level.WARNING, "WorldGuard should be used but is not installed! Disabling WorldGuard support ...", false);
		ZvPConfig.setWorlGuardSupport(false);
	    }
	}
	
	// TODO Check essentials
	
	if (ZvPConfig.getUseEssentialsKits()) {
	    if (getServer().getPluginManager().getPlugin("Essentials") == null) {
		getPluginLogger().log(this.getClass(), Level.WARNING, "Essentials kits are enabled but Essentials is not installed! Disabling Essentials support ...", false);
		ZvPConfig.setEssentialsSupport(false);
	    }
	}
	
	if (ZvPConfig.getEnableUpdater()) {
	    UpdateType updType = UpdateType.DEFAULT;
	    
	    if (ZvPConfig.getAutoUpdate() == false) {
		updType = UpdateType.NO_DOWNLOAD;
	    }
	    
	    Updater upd = new Updater(this, this.pluginID, this.getFile(), updType, ZvPConfig.getlogUpdate());
	    
	    if (ZvPConfig.getAutoUpdate() == false) {
		updateAvailable = (upd.getResult() == UpdateResult.UPDATE_AVAILABLE);
		newVersion = upd.getLatestName();
	    }
	}
	
	if (ZvPConfig.getUseMetrics() == true) {
	    try {
		Metrics metrics = new Metrics(this);
		
		Graph localeUsed = metrics.createGraph("Language Locale usage");
		
		localeUsed.addPlotter(new Metrics.Plotter(ZvPConfig.getLocale().getDisplayLanguage(Locale.ENGLISH)) {
		    
		    @Override
		    public int getValue() {
			return 1;
		    }
		    
		});
		
		metrics.start();
	    } catch (IOException e) {
		logger.log(this.getClass(), Level.WARNING, "Can't start Metrics! Skip!", true, false, e);
	    }
	}
    }
    
    private void registerListeners() {
	PluginManager pm = Bukkit.getPluginManager();
	
	pm.registerEvents(new BlockListener(), this);
	pm.registerEvents(new InteractListener(), this);
	pm.registerEvents(new PlayerListener(), this);
	pm.registerEvents(new SignChangelistener(), this);
	pm.registerEvents(new GUIListener(), this);
	pm.registerEvents(new EntityListener(), this);
	pm.registerEvents(new ChatListener(), this);
    }
    
    public static ZvP getInstance() {
	return instance;
    }
    
    public static PluginOutput getPluginLogger() {
	return logger;
    }
    
    public static FileConverter getConverter() {
	return converter;
    }
    
    public static Economy getEconProvider() {
	return economy;
    }
    
    public static WorldGuardPlugin getWorldGuardPlugin() {
	return worldGuard;
    }
    
    public static String getPrefix() {
	return pluginPrefix;
    }
    
    public void updatePlugin() {
	if (ZvPConfig.getEnableUpdater() && updateAvailable) {
	    new Updater(this, this.pluginID, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, ZvPConfig.getlogUpdate());
	}
    }
    
    public static ItemStack getTool(String loreString) {
	ItemStack tool = new ItemStack(Material.STICK);
	
	List<String> lore = new ArrayList<String>();
	
	ItemMeta toolMeta = tool.getItemMeta();
	toolMeta.setDisplayName(pluginPrefix + ChatColor.BOLD + "Tool");
	toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
	lore.add(ChatColor.GOLD + loreString);
	toolMeta.setLore(lore);
	toolMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
	
	tool.setItemMeta(toolMeta);
	return tool;
    }
    
    public static void removeTool(Player player) {
	player.getInventory().removeItem(getTool(ADDARENA_POLYGON));
	player.getInventory().removeItem(getTool(ADDARENA_SINGLE));
	player.getInventory().removeItem(getTool(ADDPOSITION));
    }
}
