package me.Aubli.ZvP;

import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;


public class ZvPConfig {
    
    private static FileConfiguration config;
    
    private static boolean useMetrics = true;
    private static boolean debugMode = false;
    private static Locale locale = null;
    private static int logLevel = Level.INFO.intValue();
    
    private static boolean enableUpdater = true;
    private static boolean autoUpdate = true;
    private static boolean logUpdate = false;
    
    private static boolean enableKits = true;
    private static boolean enableFirework = true;
    private static boolean useVoteSystem = true;
    private static boolean separatePlayerScores = false;
    private static int maxPlayers;
    private static int defaultRounds;
    private static int defaultWaves;
    
    private static int startDelay;
    private static int breakTime;
    
    private static int defaultZombieSpawnRate;
    private static double defaultSaveRadius;
    
    private static double zombieFund;
    private static double deathFee;
    
    public ZvPConfig(FileConfiguration configuration) {
	config = configuration;
	init();
	load();
    }
    
    private static void init() {
	getConfig().options().header("\n" + "This is the main config file for ZombieVsPlayer.\n" + "For more informations about this file visit the website:\n" + "http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
	
	getConfig().addDefault("plugin.enableMetrics", true);
	getConfig().addDefault("plugin.Locale", "en");
	getConfig().addDefault("plugin.debugMode", false);
	getConfig().addDefault("plugin.loglevel", Level.FINE.intValue());
	
	getConfig().addDefault("plugin.update.enable", true);
	getConfig().addDefault("plugin.update.autoUpdate", true);
	getConfig().addDefault("plugin.update.showUpdateInConsole", true);
	
	getConfig().addDefault("game.enableKits", true);
	getConfig().addDefault("game.enableFirework", true);
	getConfig().addDefault("game.useVoteSystem", true);
	getConfig().addDefault("game.separatePlayerScores", false);
	getConfig().addDefault("game.maximal_Players", 25);
	getConfig().addDefault("game.default_rounds", 3);
	getConfig().addDefault("game.default_waves", 5);
	
	getConfig().addDefault("times.joinTime", 15);
	getConfig().addDefault("times.timeBetweenWaves", 90);
	
	getConfig().addDefault("zombies.default_spawnRate", 20);
	getConfig().addDefault("zombies.default_saveRadius", 3.0);
	
	getConfig().addDefault("money.ZombieFund", 0.37);
	getConfig().addDefault("money.DeathFee", 3);
	saveConfig();
    }
    
    private static void load() {
	useMetrics = getConfig().getBoolean("plugin.enableMetrics", true);
	debugMode = getConfig().getBoolean("plugin.debugMode", false);
	locale = new Locale(getConfig().getString("plugin.Locale", "en"));
	logLevel = getConfig().getInt("plugin.loglevel", 500);
	
	enableUpdater = getConfig().getBoolean("plugin.update.enable", true);
	autoUpdate = getConfig().getBoolean("plugin.update.autoUpdate", true);
	logUpdate = getConfig().getBoolean("plugin.update.showUpdateInConsole", false);
	
	enableKits = getConfig().getBoolean("game.enableKits", true);
	enableFirework = getConfig().getBoolean("game.enableFirework", true);
	useVoteSystem = getConfig().getBoolean("game.useVoteSystem", true);
	separatePlayerScores = getConfig().getBoolean("game.separatePlayerScores", false);
	maxPlayers = getConfig().getInt("game.maximal_Players");
	defaultRounds = getConfig().getInt("game.default_rounds");
	defaultWaves = getConfig().getInt("game.default_waves");
	
	startDelay = getConfig().getInt("times.joinTime");
	breakTime = getConfig().getInt("times.timeBetweenWaves");
	
	defaultZombieSpawnRate = getConfig().getInt("zombies.default_spawnRate");
	defaultSaveRadius = getConfig().getDouble("zombies.default_saveRadius");
	
	zombieFund = getConfig().getDouble("money.ZombieFund");
	deathFee = getConfig().getDouble("money.DeathFee");
	
	// this.getConfig().addDefault("config.misc.portOnJoinGame", true);
	// this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
    }
    
    public static void reloadConfig() {
	ZvP.getInstance().reloadConfig();
	config = ZvP.getInstance().getConfig();
	init();
	load();
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
    
    public static boolean getSeparatePlayerScores() {
	return separatePlayerScores;
    }
    
    public static boolean getUseVoteSystem() {
	return useVoteSystem;
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
    
    public static double getDefaultSaveRadius() {
	return defaultSaveRadius;
    }
    
    public static double getZombieFund() {
	return zombieFund;
    }
    
    public static double getDeathFee() {
	return deathFee;
    }
    
    private static FileConfiguration getConfig() {
	return config;
    }
    
    private static void saveConfig() {
	getConfig().options().copyDefaults(true);
	ZvP.getInstance().saveConfig();
    }
}
