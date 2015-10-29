package me.Aubli.ZvP;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import me.Aubli.ZvP.Statistic.DatabaseInfo;

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
    
    // INFO: Could be moved in Arena config
    private static boolean enableEcon = false;
    private static boolean integrateGame = true;
    private static boolean integrateKits = true;
    
    private static boolean useEssentialsKits = false;
    
    private static boolean handleWorldGuard = false;
    
    private static boolean allowDuringGameJoin = true;
    
    private static boolean enableKits = true;
    private static boolean enableFirework = true;
    private static int maxPlayers;
    private static int defaultRounds;
    private static int defaultWaves;
    
    private static int defaultZombieSpawnRate;
    
    private static boolean enableStatistics;
    private static boolean useDatabase;
    private static String db_host;
    private static int db_port;
    private static String db_protocol;
    private static String db_name;
    private static String db_user;
    private static String db_pass;
    
    private static boolean modifyChat = true;
    private static List<String> commandWhiteList;
    
    public ZvPConfig(FileConfiguration configuration) {
	config = configuration;
	init();
	load();
    }
    
    private static void init() {
	File configFile = new File(ZvP.getInstance().getDataFolder(), "config.yml");
	getConfig().options().header("\nThis is the main config file for ZombieVsPlayer.A lot of arena specific options can be found in the arena file.\nFor more information read the comments or visit the Bukkit page:\nhttp://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
	
	getConfig().addDefault("plugin.enableMetrics", true);
	getConfig().addDefault("plugin.Locale", "en");
	getConfig().addDefault("plugin.debugMode", false);
	getConfig().addDefault("plugin.loglevel", Level.FINE.intValue());
	
	getConfig().addDefault("plugin.update.enable", true);
	getConfig().addDefault("plugin.update.autoUpdate", true);
	getConfig().addDefault("plugin.update.showUpdateInConsole", true);
	
	getConfig().addDefault("plugin.useEssentialsKits", false);
	getConfig().addDefault("plugin.manageWorldGuard", false);
	
	getConfig().addDefault("economy.enableEcon", false);
	getConfig().addDefault("economy.integrateKits", true);
	getConfig().addDefault("economy.integrateGame", true);
	
	getConfig().addDefault("game.enableKits", true);
	getConfig().addDefault("game.enableFirework", true);
	getConfig().addDefault("game.allowDuringGameJoin", true);
	getConfig().addDefault("game.maximal_Players", 25);
	getConfig().addDefault("game.default_rounds", 2);
	getConfig().addDefault("game.default_waves", 4);
	
	getConfig().addDefault("zombies.default_spawnRate", 8);
	
	getConfig().addDefault("stats.enable_game_statistics", false);
	getConfig().addDefault("stats.useDatabase", true);
	getConfig().addDefault("stats.db_host", "localhost");
	getConfig().addDefault("stats.db_port", 3306);
	getConfig().addDefault("stats.db_protocol", "mysql");
	getConfig().addDefault("stats.db_name", "minecraft");
	getConfig().addDefault("stats.db_user", "user");
	getConfig().addDefault("stats.db_pass", "password");
	
	getConfig().addDefault("chat.modifyChat", true);
	getConfig().addDefault("chat.commandWhitelist", commandWhiteList);
	
	saveConfig();
	
	CommentUtil insertComments = new CommentUtil(configFile);
	insertComments.addComment("Locale", "Language setting for the plugin. Currently supported are: en - English, de - German, hu - Hungarian#For language configuration look into the messages folder.");
	insertComments.addComment("debugMode", "This option enables debugMode.#Only for development or testing purposes. This option can harm your game!");
	insertComments.addComment("loglevel", "The loglevel is only used if debugMode is true.#It defines the amount of log messages on the console.");
	
	insertComments.addComment("useEssentialsKits", "ZvP can use essentials kits from the essentials config file.#This method is read-only and will only read from the configuration file.#Some features from essentials kits may not be supported!#Using essentials kits disables features provided by zvp kits!");
	insertComments.addComment("manageWorldGuard", "If enabled ZvP handles WorldGuard region flags by itself. It will create an arena region and set if necessary his parent region. Some Flags are applied too.#Note that this feature is experimental and you should rather do the region settings on your own!");
	insertComments.addComment("enableEcon", "Enable or disable economy support.#If enabled your bank account will be used for the game!#Note that you need Vault for working economics on your server!");
	insertComments.addComment("integrateKits", "If enabled kits costs money too.#Note that the price of the kit is set in their kit-file.");
	insertComments.addComment("integrateGame", "If enabled your bank account will be used for purchasing/selling and Kill/death bonuses.#Note that this game could ruin your bank balance!");
	insertComments.addComment("enableKits", "Enable kits for the game.#If disabled the player will start and end the game with their current items.#The inventory will be restored after the game.#Note that this has to be false if you use keepInventory!");
	insertComments.addComment("enableFirework", "Fireworks will shoot when the game ends.#Note that Fireworks take extra time!");
	insertComments.addComment("allowDuringGameJoin", "If set to true the game will allow players to join a running game.#Note that a change of players will affect the number of Zombies!");
	insertComments.addComment("maximal_Players", "Maximal amount of players in an arena.");
	insertComments.addComment("default_rounds", "Amount of rounds a newly created arena will have by default.");
	insertComments.addComment("default_waves", "Amount of waves a newly created arena will have by default.");
	insertComments.addComment("default_spawnRate", "Default zombie spawnrate for newly created arenas.#The spawnrate defines how many zombies will spawn.#The calculation uses arena size, amount of player, spawnrate and difficulty setting.");
	
	insertComments.addComment("enable_game_statistics", "Enables general game statistics such as kills, deaths, etc..#Statistics should be written in a database. If no database is available, a file will be used!");
	insertComments.addComment("useDatabase", "If true, the following information will be used to connect to the database.#Otherwise ZvP uses a file for statistics!");
	
	insertComments.addComment("modifyChat", "If enabled the chat will be modified to match ZvP colors and commands will be disabled.#If disabled the chat will not be changed at all!");
	insertComments.addComment("commandWhitelist", "A list of commands that can be executed during a ZvP game.#Note that zvp commands are automatically included!");
	insertComments.writeComments();
    }
    
    private static void load() {
	useMetrics = getConfig().getBoolean("plugin.enableMetrics", true);
	debugMode = getConfig().getBoolean("plugin.debugMode", false);
	locale = new Locale(getConfig().getString("plugin.Locale", "en"));
	logLevel = getConfig().getInt("plugin.loglevel", 500);
	
	enableUpdater = getConfig().getBoolean("plugin.update.enable", true);
	autoUpdate = getConfig().getBoolean("plugin.update.autoUpdate", true);
	logUpdate = getConfig().getBoolean("plugin.update.showUpdateInConsole", false);
	
	useEssentialsKits = getConfig().getBoolean("plugin.useEssentialsKits", false);
	handleWorldGuard = getConfig().getBoolean("plugin.manageWorldGuard", false);
	
	enableEcon = getConfig().getBoolean("economy.enableEcon", false);
	integrateKits = getConfig().getBoolean("economy.integrateKits", true);
	integrateGame = getConfig().getBoolean("economy.integrateGame", true);
	
	allowDuringGameJoin = getConfig().getBoolean("game.allowDuringGameJoin", true);
	
	enableKits = getConfig().getBoolean("game.enableKits", true);
	enableFirework = getConfig().getBoolean("game.enableFirework", true);
	maxPlayers = getConfig().getInt("game.maximal_Players");
	defaultRounds = getConfig().getInt("game.default_rounds");
	defaultWaves = getConfig().getInt("game.default_waves");
	
	enableStatistics = getConfig().getBoolean("stats.enable_game_statistics");
	useDatabase = getConfig().getBoolean("stats.useDatabase");
	db_host = getConfig().getString("stats.db_host");
	db_port = getConfig().getInt("stats.db_port");
	db_protocol = getConfig().getString("stats.db_protocol");
	db_name = getConfig().getString("stats.db_name");
	db_user = getConfig().getString("stats.db_user");
	db_pass = getConfig().getString("stats.db_pass");
	
	defaultZombieSpawnRate = getConfig().getInt("zombies.default_spawnRate");
	
	modifyChat = getConfig().getBoolean("chat.modifyChat", true);
	commandWhiteList = getConfig().getStringList("chat.commandWhitelist");
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
    
    public static boolean getEnableEcon() {
	return enableEcon;
    }
    
    public static boolean getIntegrateKits() {
	return integrateKits;
    }
    
    public static boolean getIntegrateGame() {
	return integrateGame;
    }
    
    public static boolean getUseEssentialsKits() {
	return useEssentialsKits;
    }
    
    public static boolean getHandleWorldGuard() {
	return handleWorldGuard;
    }
    
    public static boolean getAllowDuringGameJoin() {
	return allowDuringGameJoin;
    }
    
    public static boolean getEnabledStatistics() {
	return enableStatistics;
    }
    
    public static boolean getUseDatabase() {
	return useDatabase;
    }
    
    public static boolean getModifyChat() {
	return modifyChat;
    }
    
    public static Locale getLocale() {
	return locale;
    }
    
    public static DatabaseInfo getDBInfo() {
	return new DatabaseInfo(db_host, db_port, db_protocol, db_name, db_user, db_pass, getUseDatabase());
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
    
    public static int getDefaultZombieSpawnRate() {
	return defaultZombieSpawnRate;
    }
    
    public static List<String> getCommandWhitelist() {
	return commandWhiteList;
    }
    
    public static void setEconEnabled(boolean enabled) {
	setValue("economy.enableEcon", enabled);
    }
    
    public static void setEssentialsSupport(boolean enabled) {
	setValue("plugin.useEssentialsKits", enabled);
    }
    
    public static void setWorlGuardSupport(boolean enabled) {
	setValue("plugin.manageWorldGuard", enabled);
    }
    
    private static void setValue(String path, Object value) {
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
