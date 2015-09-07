package me.Aubli.ZvP.Game.ArenaParts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.util.File.InsertComment.CommentUtil;


public class ArenaConfig {
    
    private Arena arena;
    private int arenaID;
    
    private File arenaFile;
    private FileConfiguration arenaConfig;
    
    // Config values
    private boolean spawnProtection;
    private boolean voteSystem;
    private boolean autoWaves;
    private boolean keepXP;
    private boolean keepInventory;
    private boolean separatedScores;
    private boolean playerVsPlayer;
    private boolean increaseDifficulty;
    
    private int maxPlayers;
    private int minPlayers;
    private int maxRounds;
    private int maxWaves;
    
    private int joinTime;
    private int breakTime;
    
    private double zombieFund;
    private double deathFee;
    
    private int spawnRate;
    private int protectionDuration;
    private double saveRadius;
    
    public ArenaConfig(Arena arena, File saveFile) throws Exception {
	
	this.arena = arena;
	
	this.arenaFile = saveFile;
	this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaFile);
	
	if (!this.arenaFile.exists()) {
	    this.arenaFile.getParentFile().mkdirs();
	    this.arenaFile.createNewFile();
	    loadDefaults();
	    saveConfig();
	} else {
	    loadConfig();
	}
    }
    
    private void loadDefaults() {
	this.arenaID = getArena().getID();
	
	this.spawnProtection = true;
	this.voteSystem = true;
	this.autoWaves = true;
	this.keepXP = false;
	this.keepInventory = false;
	this.separatedScores = false;
	this.playerVsPlayer = false;
	this.increaseDifficulty = true;
	
	int maxP = ((int) ((Math.ceil(getArena().getArea().getDiagonal() + 2)) / 4));
	this.maxPlayers = maxP < 3 ? 3 : (maxP > ZvPConfig.getMaxPlayers() ? ZvPConfig.getMaxPlayers() : maxP);
	this.minPlayers = ((int) Math.ceil(this.maxPlayers / 4)) + 1;
	
	this.maxRounds = ZvPConfig.getDefaultRounds();
	this.maxWaves = ZvPConfig.getDefaultWaves();
	
	this.joinTime = 15;
	this.breakTime = 90;
	
	this.zombieFund = 0.37;
	this.deathFee = 3;
	
	this.spawnRate = ZvPConfig.getDefaultZombieSpawnRate();
	this.protectionDuration = 5;
	this.saveRadius = ((Math.ceil(getMaxPlayers() / 8))) + 2.5;
    }
    
    public void saveConfig() {
	try {
	    this.arenaFile.createNewFile();
	    
	    this.arenaConfig.set("arena.ID", getArena().getID());
	    this.arenaConfig.set("arena.Online", !(getArena().getStatus() == ArenaStatus.STOPED));
	    this.arenaConfig.set("arena.Difficulty", getArena().getDifficulty().name());
	    this.arenaConfig.set("arena.increaseDifficulty", isIncreaseDifficulty());
	    
	    this.arenaConfig.set("arena.minPlayers", getMinPlayers());
	    this.arenaConfig.set("arena.maxPlayers", getMaxPlayers());
	    this.arenaConfig.set("arena.rounds", getMaxRounds());
	    this.arenaConfig.set("arena.waves", getMaxWaves());
	    this.arenaConfig.set("arena.spawnRate", getSpawnRate());
	    
	    this.arenaConfig.set("arena.keepXP", isKeepXP());
	    this.arenaConfig.set("arena.keepInventory", isKeepInventory());
	    this.arenaConfig.set("arena.useVoteSystem", isVoteSystem());
	    this.arenaConfig.set("arena.autoWaves", isAutoWaves());
	    this.arenaConfig.set("arena.separatePlayerScores", isSeparatedScores());
	    this.arenaConfig.set("arena.joinTime", getJoinTime());
	    this.arenaConfig.set("arena.timeBetweenWaves", getBreakTime());
	    this.arenaConfig.set("arena.zombieFund", getZombieFund());
	    this.arenaConfig.set("arena.deathFee", getDeathFee());
	    this.arenaConfig.set("arena.enablePvP", isPlayerVsPlayer());
	    this.arenaConfig.set("arena.enableSpawnProtection", isSpawnProtection());
	    this.arenaConfig.set("arena.spawnProtectionDuration", getProtectionDuration());
	    this.arenaConfig.set("arena.saveRadius", getSaveRadius());
	    
	    List<String> cornerPoints = new ArrayList<String>();
	    for (Location loc : getArena().getArea().getCornerLocations()) {
		cornerPoints.add(loc.getX() + "," + loc.getY() + "," + loc.getZ());
	    }
	    
	    List<String> locationList = new ArrayList<String>();
	    for (Location loc : getArena().getArea().getSpawnLocations()) {
		locationList.add(loc.getX() + "," + loc.getY() + "," + loc.getZ());
	    }
	    
	    this.arenaConfig.set("arena.Location.world", getArena().getWorld().getUID().toString());
	    this.arenaConfig.set("arena.Location.cornerPoints", cornerPoints);
	    this.arenaConfig.set("arena.Location.staticPositions", locationList);
	    
	    this.arenaConfig.addDefault("version", ZvP.getInstance().getDescription().getVersion());
	    this.arenaConfig.options().header("\nThis is the config file for arena " + getArenaID() + "!\n");
	    this.arenaConfig.options().copyDefaults(true);
	    this.arenaConfig.options().copyHeader(false);
	    
	    this.arenaConfig.save(this.arenaFile);
	    
	    insertComments();
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Arena " + getArenaID() + ": " + e.getMessage(), true, false, e);
	}
    }
    
    private void insertComments() {
	CommentUtil insertComment = new CommentUtil(this.arenaFile);
	insertComment.addComment("ID", "The internal identifier of the arena!");
	insertComment.addComment("Online", "'true': The arena is online and can be used.#'false': The arena is offline and can not be used.");
	insertComment.addComment("Difficulty", "The Difficulty of the arena. There are three modes: EASY, NORMAL, HARD#Each mode will increase amount and health of zombies.");
	insertComment.addComment("increaseDifficulty", "If enabled, the health and strength of zombies increase as the game progresses.");
	insertComment.addComment("minPlayers", "Minimum amount of players. Set at least to 1.");
	insertComment.addComment("maxPlayers", "Maximal amount of players.");
	insertComment.addComment("rounds", "The amount of rounds you will play.#Note that one round has several waves. To get the full number of waves multiple rounds and waves.");
	insertComment.addComment("waves", "The amount of waves you will play. To get the full number of waves multiple rounds and waves.");
	insertComment.addComment("spawnRate", "SpawnRate defines the amount of spawning zombies. Default is 8.");
	insertComment.addComment("keepXP", "If set to false, the game will not reset/change your current XP level.#Note that the countdown system uses the xp level which overrides experience!#The plugin will not show countdowns if keepXP is enabled!");
	insertComment.addComment("keepInventory", "If set to true, the inventory will not get cleared after the game.#Important: Does not work with kits enabled!#Look into the main config file to disable kits!");
	insertComment.addComment("useVoteSystem", "Use votes to get to the next round.#If false the game will wait timeBetweenWaves in seconds.");
	insertComment.addComment("autoWaves", "If enabled the next round will start automatically even if not all zombies are defeated!");
	insertComment.addComment("separatePlayerScores", "True: Each player will have his own score.#False: All players have the same score. They pay and earn together.");
	insertComment.addComment("joinTime", "Time in seconds the game will wait before it starts.#Note that the arena specific minimum has to be reached.");
	insertComment.addComment("timeBetweenWaves", "Time in seconds the game will wait until a new wave starts.#Only applies if useVoteSystem is false!");
	insertComment.addComment("zombieFund", "Amount of money you will get from killing a zombie.");
	insertComment.addComment("deathFee", "Amount of money you have to pay when you die.");
	insertComment.addComment("enablePvP", "Allow Player vs Player damage. Includes projectile damage!");
	insertComment.addComment("enableSpawnProtection", "SpawnProtection will protect you when you respawn.#Note that you can not hit zombies during the protection!");
	insertComment.addComment("spawnProtectionDuration", "The duration of the spawn protection in seconds.");
	insertComment.addComment("saveRadius", "The save radius is the radius in blocks around you in which no zombies will spawn.");
	insertComment.addComment("Location", "This is the location section of the arena. It contains min, max and custom spawn location.#If you are not sure what you are doing do not touch this!");
	insertComment.writeComments();
    }
    
    public void loadConfig() {
	this.arenaID = this.arenaConfig.getInt("arena.ID");
	this.maxPlayers = this.arenaConfig.getInt("arena.maxPlayers", ZvPConfig.getMaxPlayers());
	this.minPlayers = this.arenaConfig.getInt("arena.minPlayers", 3);
	
	this.maxRounds = this.arenaConfig.getInt("arena.rounds", ZvPConfig.getDefaultRounds());
	this.maxWaves = this.arenaConfig.getInt("arena.waves", ZvPConfig.getDefaultWaves());
	
	this.spawnProtection = this.arenaConfig.getBoolean("arena.enableSpawnProtection", true);
	this.protectionDuration = this.arenaConfig.getInt("arena.spawnProtectionDuration");
	
	this.keepXP = this.arenaConfig.getBoolean("arena.keepXP", false);
	this.keepInventory = this.arenaConfig.getBoolean("arena.keepInventory", false);
	this.voteSystem = this.arenaConfig.getBoolean("arena.useVoteSystem", true);
	this.autoWaves = this.arenaConfig.getBoolean("arena.autoWaves", true);
	this.separatedScores = this.arenaConfig.getBoolean("arena.separatePlayerScores", false);
	this.playerVsPlayer = this.arenaConfig.getBoolean("arena.enablePvP", false);
	this.increaseDifficulty = this.arenaConfig.getBoolean("arena.increaseDifficulty", true);
	
	if (isKeepInventory() && ZvPConfig.getEnableKits()) {
	    this.keepInventory = false;
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "keepInventory is set to true in Arena " + getArenaID() + ", but Kits are enabled! Disable keepInventory ...", true, false);
	}
	
	this.joinTime = this.arenaConfig.getInt("arena.joinTime", 15);
	this.breakTime = this.arenaConfig.getInt("arena.timeBetweenWaves", 90);
	this.zombieFund = this.arenaConfig.getDouble("arena.zombieFund", 0.37);
	this.deathFee = this.arenaConfig.getDouble("arena.deathFee", 3.0);
	
	this.spawnRate = this.arenaConfig.getInt("arena.spawnRate", ZvPConfig.getDefaultZombieSpawnRate());
	this.saveRadius = this.arenaConfig.getDouble("arena.saveRadius", 4.0);
    }
    
    public boolean deleteFile() {
	return this.arenaFile.delete();
    }
    
    public boolean isSpawnProtection() {
	return this.spawnProtection;
    }
    
    public boolean isVoteSystem() {
	return this.voteSystem;
    }
    
    public boolean isAutoWaves() {
	return this.autoWaves;
    }
    
    public boolean isKeepXP() {
	return this.keepXP;
    }
    
    public boolean isKeepInventory() {
	return this.keepInventory;
    }
    
    public boolean isSeparatedScores() {
	return this.separatedScores;
    }
    
    public boolean isPlayerVsPlayer() {
	return this.playerVsPlayer;
    }
    
    public boolean isIncreaseDifficulty() {
	return this.increaseDifficulty;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public int getArenaID() {
	return this.arenaID;
    }
    
    public int getMaxPlayers() {
	return this.maxPlayers;
    }
    
    public int getMinPlayers() {
	return this.minPlayers;
    }
    
    public int getMaxRounds() {
	return this.maxRounds;
    }
    
    public int getMaxWaves() {
	return this.maxWaves;
    }
    
    public int getJoinTime() {
	return this.joinTime;
    }
    
    public int getBreakTime() {
	return this.breakTime;
    }
    
    public double getZombieFund() {
	return this.zombieFund;
    }
    
    public double getDeathFee() {
	return this.deathFee;
    }
    
    public int getSpawnRate() {
	return this.spawnRate;
    }
    
    public int getProtectionDuration() {
	return this.protectionDuration;
    }
    
    public double getSaveRadius() {
	return this.saveRadius;
    }
    
    public Object getConfigValue(String path) {
	return this.arenaConfig.get(path);
    }
    
    public List<String> getStringList(String path) {
	return this.arenaConfig.getStringList(path);
    }
    
    public void saveArenaLobby(ArenaLobby lobby) throws IOException {
	this.arenaConfig.set("arena.Location.PreLobby.X", lobby.getCenterLoc().getX());
	this.arenaConfig.set("arena.Location.PreLobby.Y", lobby.getCenterLoc().getY());
	this.arenaConfig.set("arena.Location.PreLobby.Z", lobby.getCenterLoc().getZ());
	
	List<String> locationList = new ArrayList<String>();
	
	for (Location loc : lobby.getLocationList()) {
	    locationList.add(loc.getX() + "," + loc.getY() + "," + loc.getZ());
	}
	
	this.arenaConfig.set("arena.Location.PreLobby.extraPositions", locationList);
	this.arenaConfig.save(this.arenaFile);
	
	insertComments();
    }
    
    public void removeArenaLobby() throws IOException {
	this.arenaConfig.set("arena.Location.PreLobby", null);
	this.arenaConfig.save(this.arenaFile);
    }
}
