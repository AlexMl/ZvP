package me.Aubli.ZvP;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.util.File.InsertComment.CommentUtil;


public class ZvPConfig {
    
    private static FileConfiguration config;
    
    private static boolean useMetrics = true;
    private static boolean debugMode = false;
    private static Locale locale = null;
    private static int logLevel = Level.INFO.intValue();
    
    private static boolean enableUpdater = true;
    private static boolean autoUpdate = true;
    private static boolean logUpdate = false;
    
    private static boolean enableEcon = false;
    private static boolean integrateGame = true;
    private static boolean integrateKits = true;
    
    private static boolean keepXP = false;
    private static boolean keepInventory = false;
    
    private static boolean allowDuringGameJoin = true;
    
    private static boolean enableKits = true;
    private static boolean enableFirework = true;
    private static boolean modifyChat = true;
    private static boolean useVoteSystem = true;
    private static boolean separatePlayerScores = false;
    private static int maxPlayers;
    private static int defaultRounds;
    private static int defaultWaves;
    
    private static int startDelay;
    private static int breakTime;
    
    private static int defaultZombieSpawnRate;
    
    private static double zombieFund;
    private static double deathFee;
    
    public ZvPConfig(FileConfiguration configuration) throws Exception {
	config = configuration;
	init();
	load();
    }
    
    private static void init() {
	File configFile = new File(ZvP.getInstance().getDataFolder(), "config.yml");
	getConfig().options().header("\n" + "This is the main config file for ZombieVsPlayer.\n" + "For more information read the comments or visit the Bukkit page:\n" + "http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
	
	getConfig().addDefault("plugin.enableMetrics", true);
	getConfig().addDefault("plugin.Locale", "en");
	getConfig().addDefault("plugin.debugMode", false);
	getConfig().addDefault("plugin.loglevel", Level.FINE.intValue());
	
	getConfig().addDefault("plugin.update.enable", true);
	getConfig().addDefault("plugin.update.autoUpdate", true);
	getConfig().addDefault("plugin.update.showUpdateInConsole", true);
	
	getConfig().addDefault("economy.enableEcon", false);
	getConfig().addDefault("economy.integrateKits", true);
	getConfig().addDefault("economy.integrateGame", true);
	
	getConfig().addDefault("game.enableKits", true);
	getConfig().addDefault("game.enableFirework", true);
	getConfig().addDefault("game.allowDuringGameJoin", true);
	getConfig().addDefault("game.modifyChat", true);
	getConfig().addDefault("game.useVoteSystem", true);
	getConfig().addDefault("game.keepInventory", false);
	getConfig().addDefault("game.keepXP", false);
	getConfig().addDefault("game.separatePlayerScores", false);
	getConfig().addDefault("game.maximal_Players", 25);
	getConfig().addDefault("game.default_rounds", 2);
	getConfig().addDefault("game.default_waves", 4);
	
	getConfig().addDefault("times.joinTime", 15);
	getConfig().addDefault("times.timeBetweenWaves", 90);
	
	getConfig().addDefault("zombies.default_spawnRate", 8);
	
	getConfig().addDefault("money.ZombieFund", 0.37);
	getConfig().addDefault("money.DeathFee", 3);
	saveConfig();
	
	CommentUtil.insertComment(configFile, "enableEcon", "Enable or disable economy support.#If enabled your bank account will be used for the game!#Note that you need Vault for working economics on your server!");
	CommentUtil.insertComment(configFile, "integrateKits", "If enabled kits costs money too.#Note that the price of the kit is set in their kit-file.");
	CommentUtil.insertComment(configFile, "integrateGame", "If enabled your bank account will be used for purchasing/selling and Kill/death bonuses.#Note that this game could ruin your bank balance!");
	CommentUtil.insertComment(configFile, "enableKits", "Enable kits for the game.#If disabled the player will start and end the game with their current items.#The inventory will be restored after the game.#Note that this has to be false if you use keepInventory!");
	CommentUtil.insertComment(configFile, "enableFirework", "Fireworks will shoot when the game ends.#Note that Fireworks take extra time!");
	CommentUtil.insertComment(configFile, "allowDuringGameJoin", "If set to true the game will allow players to join a running game.#Note that a change of players affects the number of Zombies!");
	CommentUtil.insertComment(configFile, "modifyChat", "If enabled the chat will be modified to match ZvP colors.#If disabled the chat will not be changed at all!");
	CommentUtil.insertComment(configFile, "useVoteSystem", "Use votes to get to the next round.#If false the game will wait timeBetweenWaves in seconds.");
	CommentUtil.insertComment(configFile, "keepInventory", "If set to true, the inventory will not get cleared after the game.#Important: Does not work with kits enabled!");
	CommentUtil.insertComment(configFile, "keepXP", "If set to false, the game will not reset/change your current XP level.");
	CommentUtil.insertComment(configFile, "separatePlayerScores", "True: Each player will have his own score.#False: All players have the same score. They pay and earn together.");
	CommentUtil.insertComment(configFile, "maximal_Players", "Maximal amount of players in an arena.");
	CommentUtil.insertComment(configFile, "default_rounds", "Amount of rounds a newly created arena will have by default.");
	CommentUtil.insertComment(configFile, "default_waves", "Amount of waves a newly created arena will have by default.");
	CommentUtil.insertComment(configFile, "joinTime", "Time in seconds the game will wait before it starts.#Note that the arena specific minimum has to be reached.");
	CommentUtil.insertComment(configFile, "timeBetweenWaves", "Time in seconds the game will wait until a new wave starts.#Only applies if useVoteSystem is false!");
	CommentUtil.insertComment(configFile, "default_spawnRate", "Default zombie spawnrate for newly created arenas.#The spawnrate defines how many zombies will spawn.#The calculation uses arena size, amount of player, spawnrate and difficulty setting.");
	CommentUtil.insertComment(configFile, "ZombieFund", "Amount of money you will get from killing a zombie.");
	CommentUtil.insertComment(configFile, "DeathFee", "Amount of money you have to pay when you die.");
    }
    
    private static void load() throws Exception {
	useMetrics = getConfig().getBoolean("plugin.enableMetrics", true);
	debugMode = getConfig().getBoolean("plugin.debugMode", false);
	locale = new Locale(getConfig().getString("plugin.Locale", "en"));
	logLevel = getConfig().getInt("plugin.loglevel", 500);
	
	enableUpdater = getConfig().getBoolean("plugin.update.enable", true);
	autoUpdate = getConfig().getBoolean("plugin.update.autoUpdate", true);
	logUpdate = getConfig().getBoolean("plugin.update.showUpdateInConsole", false);
	
	enableEcon = getConfig().getBoolean("economy.enableEcon", false);
	integrateKits = getConfig().getBoolean("economy.integrateKits", true);
	integrateGame = getConfig().getBoolean("economy.integrateGame", true);
	
	allowDuringGameJoin = getConfig().getBoolean("game.allowDuringGameJoin", true);
	
	enableKits = getConfig().getBoolean("game.enableKits", true);
	enableFirework = getConfig().getBoolean("game.enableFirework", true);
	modifyChat = getConfig().getBoolean("game.modifyChat", true);
	useVoteSystem = getConfig().getBoolean("game.useVoteSystem", true);
	keepInventory = getConfig().getBoolean("game.keepInventory", false);
	keepXP = getConfig().getBoolean("game.keepXP", false);
	separatePlayerScores = getConfig().getBoolean("game.separatePlayerScores", false);
	maxPlayers = getConfig().getInt("game.maximal_Players");
	defaultRounds = getConfig().getInt("game.default_rounds");
	defaultWaves = getConfig().getInt("game.default_waves");
	
	startDelay = getConfig().getInt("times.joinTime");
	breakTime = getConfig().getInt("times.timeBetweenWaves");
	
	defaultZombieSpawnRate = getConfig().getInt("zombies.default_spawnRate");
	
	zombieFund = getConfig().getDouble("money.ZombieFund");
	deathFee = getConfig().getDouble("money.DeathFee");
	
	// this.getConfig().addDefault("config.misc.portOnJoinGame", true);
	// this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
	
	if (keepInventory == true && enableKits == true) {
	    setValue("game.keepInventory", false);
	    throw new IllegalArgumentException("keepInventory is set to true but Kits are enabled! Disable keepInventory ...");
	}
    }
    
    public static void reloadConfig() {
	ZvP.getInstance().reloadConfig();
	config = ZvP.getInstance().getConfig();
	init();
	
	try {
	    load();
	} catch (Exception e) {
	    // Do nothing here!
	    // Wrong configurations are catched on init
	}
    }
    
    public static boolean getUseMetrics() {
	return useMetrics;
    }
    
    public static boolean getDebugMode() {
	return debugMode;
    }
    
    public static boolean getEnableUpdater() {
	return enableUpdater;
    }
    
    public static boolean getAutoUpdate() {
	return autoUpdate;
    }
    
    public static boolean getlogUpdate() {
	return logUpdate;
    }
    
    public static boolean getEnableKits() {
	return enableKits;
    }
    
    public static boolean getEnableFirework() {
	return enableFirework;
    }
    
    public static boolean getEnableEcon() {
	return enableEcon;
    }
    
    public static boolean getIntegrateKits() {
	return integrateKits;
    }
    
    public static boolean getIntegrateGame() {
	return integrateGame;
    }
    
    public static boolean getSeparatePlayerScores() {
	return separatePlayerScores;
    }
    
    public static boolean getAllowDuringGameJoin() {
	return allowDuringGameJoin;
    }
    
    public static boolean getUseVoteSystem() {
	return useVoteSystem;
    }
    
    public static boolean getModifyChat() {
	return modifyChat;
    }
    
    public static boolean getKeepInventory() {
	return keepInventory;
    }
    
    public static boolean getKeepXP() {
	return keepXP;
    }
    
    public static Locale getLocale() {
	return locale;
    }
    
    public static int getLogLevel() {
	return logLevel;
    }
    
    public static int getMaxPlayers() {
	return maxPlayers;
    }
    
    public static int getDefaultRounds() {
	return defaultRounds;
    }
    
    public static int getDefaultWaves() {
	return defaultWaves;
    }
    
    public static int getStartDelay() {
	return startDelay;
    }
    
    public static int getBreakTime() {
	return breakTime;
    }
    
    public static int getDefaultZombieSpawnRate() {
	return defaultZombieSpawnRate;
    }
    
    public static double getZombieFund() {
	return zombieFund;
    }
    
    public static double getDeathFee() {
	return deathFee;
    }
    
    public static void setEconEnabled(boolean enabled) {
	setValue("economy.enableEcon", enabled);
    }
    
    public static void setValue(String path, Object value) {
	getConfig().set(path, value);
	saveConfig();
	reloadConfig();
    }
    
    private static FileConfiguration getConfig() {
	return config;
    }
    
    private static void saveConfig() {
	getConfig().options().copyDefaults(true);
	ZvP.getInstance().saveConfig();
    }
}
