package me.Aubli.ZvP.Game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.GameManager.ArenaDifficultyLevel;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.util.File.InsertComment.CommentUtil;
import org.util.SortMap.SortMap;


public class Arena implements Comparable<Arena> {
    
    private File arenaFile;
    private FileConfiguration arenaConfig;
    
    private int arenaID;
    private int TaskId;
    
    private ArenaStatus status;
    private ArenaDifficultyLevel difficulty;
    
    private ArenaScore score;
    private ArenaDifficulty difficultyTool;
    
    private ArenaArea arenaArea;
    
    private ArenaLobby preLobby;
    
    private Random rand;
    
    private ArrayList<ZvPPlayer> players;
    
    private int round;
    private int wave;
    
    /* ---- Config values ---- */
    private final boolean enableSpawnProtection;
    private final boolean useVoteSystem;
    private final boolean keepXP;
    private boolean keepInventory;
    private final boolean separatePlayerScores;
    
    private final int maxPlayers;
    private final int minPlayers;
    private final int maxRounds;
    private final int maxWaves;
    
    private final int joinTime;
    private final int breakTime;
    
    private final double zombieFund;
    private final double deathFee;
    
    private final int spawnRate;
    private final int protectionDuration;
    private final double saveRadius;
    
    public Arena(int ID, String arenaPath, World world, List<Location> arenaCorners, int rounds, int waves, int spawnRate, ArenaDifficultyLevel difficulty, boolean spawnProtection) throws Exception {
	
	this.arenaID = ID;
	
	this.rand = new Random();
	this.arenaArea = new ArenaArea(world, this, arenaCorners, null, this.rand);
	
	int maxP = ((int) ((Math.ceil(getArea().getDiagonal() + 2)) / 4));
	
	this.maxPlayers = maxP < 3 ? 3 : (maxP > ZvPConfig.getMaxPlayers() ? ZvPConfig.getMaxPlayers() : maxP);
	this.minPlayers = ((int) Math.ceil(this.maxPlayers / 4)) + 1;
	
	this.maxRounds = rounds;
	this.maxWaves = waves;
	
	this.status = ArenaStatus.STANDBY;
	this.difficulty = difficulty;
	
	this.round = 0;
	this.wave = 0;
	
	/* ---- INFO: final standard config values ---- */
	this.useVoteSystem = true;
	this.keepXP = false;
	this.keepInventory = false;
	this.separatePlayerScores = false;
	
	this.joinTime = 15;
	this.breakTime = 90;
	this.zombieFund = 0.37;
	this.deathFee = 3;
	// END
	
	this.saveRadius = ((Math.ceil(getMaxPlayers() / 8))) + 2.5;
	this.spawnRate = spawnRate;
	
	this.enableSpawnProtection = spawnProtection;
	this.protectionDuration = 5;
	
	this.arenaFile = new File(arenaPath + "/" + ID + ".yml");
	this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaFile);
	
	this.players = new ArrayList<ZvPPlayer>();
	this.difficultyTool = new ArenaDifficulty(this, getDifficulty());
	save();
    }
    
    public Arena(File arenaFile) {
	this.arenaFile = arenaFile;
	this.arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
	
	this.rand = new Random();
	
	this.arenaID = this.arenaConfig.getInt("arena.ID");
	this.maxPlayers = this.arenaConfig.getInt("arena.maxPlayers", ZvPConfig.getMaxPlayers());
	this.minPlayers = this.arenaConfig.getInt("arena.minPlayers", 3);
	
	this.maxRounds = this.arenaConfig.getInt("arena.rounds", ZvPConfig.getDefaultRounds());
	this.maxWaves = this.arenaConfig.getInt("arena.waves", ZvPConfig.getDefaultWaves());
	
	if (this.arenaConfig.getBoolean("arena.Online", true)) {
	    this.status = ArenaStatus.STANDBY;
	} else {
	    this.status = ArenaStatus.STOPED;
	}
	
	this.enableSpawnProtection = this.arenaConfig.getBoolean("arena.enableSpawnProtection", true);
	this.protectionDuration = this.arenaConfig.getInt("arena.spawnProtectionDuration");
	this.difficulty = ArenaDifficultyLevel.valueOf(this.arenaConfig.getString("arena.Difficulty", "NORMAL"));
	
	this.round = 0;
	this.wave = 0;
	
	this.keepXP = this.arenaConfig.getBoolean("arena.keepXP", false);
	this.keepInventory = this.arenaConfig.getBoolean("arena.keepInventory", false);
	this.useVoteSystem = this.arenaConfig.getBoolean("arena.useVoteSystem", true);
	this.separatePlayerScores = this.arenaConfig.getBoolean("arena.separatePlayerScores", false);
	
	if (keepInventory() && ZvPConfig.getEnableKits()) {
	    this.keepInventory = false;
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "keepInventory is set to true in Arena " + getID() + ", but Kits are enabled! Disable keepInventory ...", true, false);
	}
	
	this.joinTime = this.arenaConfig.getInt("arena.joinTime", 15);
	this.breakTime = this.arenaConfig.getInt("arena.timeBetweenWaves", 90);
	this.zombieFund = this.arenaConfig.getDouble("arena.zombieFund", 0.37);
	this.deathFee = this.arenaConfig.getDouble("arena.deathFee", 3.0);
	
	this.spawnRate = this.arenaConfig.getInt("arena.spawnRate", ZvPConfig.getDefaultZombieSpawnRate());
	this.saveRadius = this.arenaConfig.getDouble("arena.saveRadius", 4.0);
	
	World arenaWorld = Bukkit.getWorld(UUID.fromString(this.arenaConfig.getString("arena.Location.world")));
	
	List<Location> cornerPoints = new ArrayList<Location>();
	for (String locationString : this.arenaConfig.getStringList("arena.Location.cornerPoints")) {
	    String[] cords = locationString.split(",");
	    Location loc = new Location(arenaWorld, Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), Integer.parseInt(cords[2]));
	    cornerPoints.add(loc);
	}
	
	List<Location> spawnPositions = new ArrayList<Location>();
	for (String locationString : this.arenaConfig.getStringList("arena.Location.staticPositions")) {
	    String[] cords = locationString.split(",");
	    Location loc = new Location(arenaWorld, Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), Integer.parseInt(cords[2]));
	    spawnPositions.add(loc);
	}
	
	try {
	    this.arenaArea = new ArenaArea(arenaWorld, this, cornerPoints, spawnPositions, this.rand);
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(ArenaArea.class, Level.SEVERE, "Error while loading Arena: " + e.getMessage(), true, false, e);
	}
	
	this.difficultyTool = new ArenaDifficulty(this, getDifficulty());
	this.players = new ArrayList<ZvPPlayer>();
	this.preLobby = loadArenaLobby();
    }
    
    public void save() {
	try {
	    this.arenaFile.createNewFile();
	    
	    this.arenaConfig.set("arena.ID", this.arenaID);
	    this.arenaConfig.set("arena.Online", !(getStatus() == ArenaStatus.STOPED));
	    this.arenaConfig.set("arena.Difficulty", getDifficulty().name());
	    
	    this.arenaConfig.set("arena.minPlayers", this.minPlayers);
	    this.arenaConfig.set("arena.maxPlayers", this.maxPlayers);
	    this.arenaConfig.set("arena.rounds", this.maxRounds);
	    this.arenaConfig.set("arena.waves", this.maxWaves);
	    this.arenaConfig.set("arena.spawnRate", this.spawnRate);
	    
	    this.arenaConfig.set("arena.keepXP", this.keepXP);
	    this.arenaConfig.set("arena.keepInventory", this.keepInventory);
	    this.arenaConfig.set("arena.useVoteSystem", this.useVoteSystem);
	    this.arenaConfig.set("arena.separatePlayerScores", this.separatePlayerScores);
	    this.arenaConfig.set("arena.joinTime", this.joinTime);
	    this.arenaConfig.set("arena.timeBetweenWaves", this.breakTime);
	    this.arenaConfig.set("arena.zombieFund", this.zombieFund);
	    this.arenaConfig.set("arena.deathFee", this.deathFee);
	    
	    this.arenaConfig.set("arena.enableSpawnProtection", this.enableSpawnProtection);
	    this.arenaConfig.set("arena.spawnProtectionDuration", this.protectionDuration);
	    this.arenaConfig.set("arena.saveRadius", this.saveRadius);
	    
	    List<String> cornerPoints = new ArrayList<String>();
	    for (Location loc : getArea().getCornerLocations()) {
		cornerPoints.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
	    }
	    
	    List<String> locationList = new ArrayList<String>();
	    for (Location loc : getArea().getSpawnLocations()) {
		locationList.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
	    }
	    
	    this.arenaConfig.set("arena.Location.world", getWorld().getUID().toString());
	    this.arenaConfig.set("arena.Location.cornerPoints", cornerPoints);
	    this.arenaConfig.set("arena.Location.staticPositions", locationList);
	    
	    this.arenaConfig.addDefault("version", ZvP.getInstance().getDescription().getVersion());
	    this.arenaConfig.options().header("\nThis is the config file for arena " + getID() + "!\n");
	    this.arenaConfig.options().copyDefaults(true);
	    this.arenaConfig.options().copyHeader(false);
	    
	    this.arenaConfig.save(this.arenaFile);
	    
	    insertComments();
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Arena " + getID() + ": " + e.getMessage(), true, false, e);
	}
    }
    
    boolean saveArenaLobby(ArenaLobby preLobby) {
	
	try {
	    this.arenaConfig.set("arena.Location.PreLobby.X", preLobby.getCenterLoc().getBlockX());
	    this.arenaConfig.set("arena.Location.PreLobby.Y", preLobby.getCenterLoc().getBlockY());
	    this.arenaConfig.set("arena.Location.PreLobby.Z", preLobby.getCenterLoc().getBlockZ());
	    
	    List<String> locationList = new ArrayList<String>();
	    
	    for (Location loc : preLobby.getLocationList()) {
		locationList.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
	    }
	    
	    this.arenaConfig.set("arena.Location.PreLobby.extraPositions", locationList);
	    
	    this.arenaConfig.save(this.arenaFile);
	    
	    insertComments();
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "PreLobby for Arena " + getID() + " was successfully saved!", true, true);
	    return true;
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving PreLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
	    return false;
	}
    }
    
    private ArenaLobby loadArenaLobby() {
	
	if (this.arenaConfig.get("arena.Location.PreLobby.X") != null) {
	    Location centerLoc = new Location(getWorld(), this.arenaConfig.getInt("arena.Location.PreLobby.X"), this.arenaConfig.getInt("arena.Location.PreLobby.Y"), this.arenaConfig.getInt("arena.Location.PreLobby.Z"));
	    
	    ArrayList<Location> locations = new ArrayList<Location>();
	    for (String locationString : this.arenaConfig.getStringList("arena.Location.PreLobby.extraPositions")) {
		String[] cords = locationString.split(",");
		Location loc = new Location(getWorld(), Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), Integer.parseInt(cords[2]));
		locations.add(loc);
	    }
	    
	    try {
		return new ArenaLobby(this, centerLoc, locations, this.rand);
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while loading ArenaLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
		return null;
	    }
	}
	return null;
    }
    
    public boolean deleteArenaLobby() {
	try {
	    this.preLobby = null;
	    this.arenaConfig.set("arena.Location.PreLobby", null);
	    this.arenaConfig.save(this.arenaFile);
	    save();
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Deleted PreLobby from Arena " + getID() + " successfully!", true, true);
	    return true;
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while deleting ArenaLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
	    return false;
	}
    }
    
    boolean delete() {
	return this.arenaFile.delete();
    }
    
    private void insertComments() {
	CommentUtil.insertComment(this.arenaFile, "ID", "The internal identifier of the arena!");
	CommentUtil.insertComment(this.arenaFile, "Online", "'true': The arena is online and can be used.#'false': The arena is offline and can not be used.");
	CommentUtil.insertComment(this.arenaFile, "Difficulty", "The Difficulty of the arena. There are three modes: EASY, NORMAL, HARD#Each mode will increase amount and health of zombies.");
	CommentUtil.insertComment(this.arenaFile, "minPlayers", "Minimum amount of players. Set at least to 1.");
	CommentUtil.insertComment(this.arenaFile, "maxPlayers", "Maximal amount of players.");
	CommentUtil.insertComment(this.arenaFile, "rounds", "The amount of rounds you will play.#Note that one round has several waves. To get the full number of waves multiple rounds and waves.");
	CommentUtil.insertComment(this.arenaFile, "waves", "The amount of waves you will play. To get the full number of waves multiple rounds and waves.");
	CommentUtil.insertComment(this.arenaFile, "spawnRate", "SpawnRate defines the amount of spawning zombies. Default is 8.");
	CommentUtil.insertComment(this.arenaFile, "keepXP", "If set to false, the game will not reset/change your current XP level.#Note that the countdown system uses the xp level which overrides experience!#The plugin will not show countdowns if keepXP is enabled!");
	CommentUtil.insertComment(this.arenaFile, "keepInventory", "If set to true, the inventory will not get cleared after the game.#Important: Does not work with kits enabled!#Look into the main config file to disable kits!");
	CommentUtil.insertComment(this.arenaFile, "useVoteSystem", "Use votes to get to the next round.#If false the game will wait timeBetweenWaves in seconds.");
	CommentUtil.insertComment(this.arenaFile, "separatePlayerScores", "True: Each player will have his own score.#False: All players have the same score. They pay and earn together.");
	CommentUtil.insertComment(this.arenaFile, "joinTime", "Time in seconds the game will wait before it starts.#Note that the arena specific minimum has to be reached.");
	CommentUtil.insertComment(this.arenaFile, "timeBetweenWaves", "Time in seconds the game will wait until a new wave starts.#Only applies if useVoteSystem is false!");
	CommentUtil.insertComment(this.arenaFile, "zombieFund", "Amount of money you will get from killing a zombie.");
	CommentUtil.insertComment(this.arenaFile, "deathFee", "Amount of money you have to pay when you die.");
	CommentUtil.insertComment(this.arenaFile, "enableSpawnProtection", "SpawnProtection will protect you when you respawn.#Note that you can not hit zombies during the protection!");
	CommentUtil.insertComment(this.arenaFile, "spawnProtectionDuration", "The duration of the spawn protection in seconds.");
	CommentUtil.insertComment(this.arenaFile, "saveRadius", "The save radius is the radius in blocks around you in which no zombies will spawn.");
	CommentUtil.insertComment(this.arenaFile, "Location", "This is the location section of the arena. It contains min, max and custom spawn location.#If you are not sure what you are doing do not touch this!");
    }
    
    public void setStatus(ArenaStatus status) {
	this.status = status;
	SignManager.getManager().updateSigns(this);
    }
    
    public void setRound(int round) {
	this.round = round;
	SignManager.getManager().updateSigns(this);
	updatePlayerBoards();
    }
    
    public void setWave(int wave) {
	this.wave = wave;
	SignManager.getManager().updateSigns(this);
	updatePlayerBoards();
    }
    
    public void setTaskID(int ID) {
	this.TaskId = ID;
    }
    
    public int getID() {
	return this.arenaID;
    }
    
    public ArenaStatus getStatus() {
	return this.status;
    }
    
    public ArenaDifficultyLevel getDifficulty() {
	return this.difficulty;
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
    
    public int getRound() {
	return this.round;
    }
    
    public int getWave() {
	return this.wave;
    }
    
    public int getSpawnRate() {
	return this.spawnRate;
    }
    
    public int getTaskId() {
	return this.TaskId;
    }
    
    public int getArenaProtectionDuration() {
	return this.protectionDuration;
    }
    
    public int getArenaJoinTime() {
	return this.joinTime;
    }
    
    public int getArenaBreakTime() {
	return this.breakTime;
    }
    
    public double getSaveRadius() {
	return this.saveRadius;
    }
    
    public double getArenaZombieFund() {
	return this.zombieFund;
    }
    
    public double getArenaDeathFee() {
	return this.deathFee;
    }
    
    public ArenaScore getScore() {
	return this.score;
    }
    
    public ArenaDifficulty getDifficultyTool() {
	return this.difficultyTool;
    }
    
    public ArenaLobby getPreLobby() {
	return this.preLobby;
    }
    
    public boolean getSpawnProtection() {
	return this.enableSpawnProtection;
    }
    
    public ArenaArea getArea() {
	return this.arenaArea;
    }
    
    public World getWorld() {
	return getArea().getWorld();
    }
    
    public ZvPPlayer getRandomPlayer() {
	return getPlayers()[this.rand.nextInt(getPlayers().length)];
    }
    
    public ZvPPlayer[] getPlayers() {
	ZvPPlayer[] parray = new ZvPPlayer[this.players.size()];
	
	for (int i = 0; i < this.players.size(); i++) {
	    parray[i] = this.players.get(i);
	}
	return parray;
    }
    
    public Zombie[] getLivingZombies() {
	List<Zombie> zombieList = new ArrayList<Zombie>();
	for (Entity e : getArea().getEntities()) {
	    if (e instanceof Zombie) {
		zombieList.add((Zombie) e);
	    }
	}
	
	Zombie[] zombies = new Zombie[zombieList.size()];
	for (Zombie z : zombieList) {
	    zombies[zombieList.indexOf(z)] = z;
	}
	
	return zombies;
    }
    
    public int getLivingZombieAmount() {
	return getLivingZombies().length;
    }
    
    public int getKilledZombies() {
	int kills = 0;
	for (ZvPPlayer p : getPlayers()) {
	    kills += p.getKills();
	}
	return kills;
    }
    
    public int getSpawningZombies() {
	return getSpawningZombies(getWave(), getRound(), getPlayers().length, getDifficulty().getLevel());
    }
    
    public int getSpawningZombies(int w, int r, int p, int d) {
	return ((int) Math.sqrt(r * w * getSpawnRate() * getArea().getDiagonal() * p * ((d + 1.0) / 2.0)));
    }
    
    public ZvPPlayer getBestPlayer() {
	Map<UUID, Double> scoreMap = new HashMap<UUID, Double>();
	
	for (ZvPPlayer player : getPlayers()) {
	    double score = (player.getKills() + getScore().getScore(player)) - player.getDeaths();
	    scoreMap.put(player.getUuid(), score);
	}
	
	scoreMap = SortMap.sortByValue(scoreMap);
	return GameManager.getManager().getPlayer(UUID.fromString(scoreMap.entrySet().toArray()[scoreMap.size() - 1].toString().split("=")[0]));
    }
    
    public boolean isOnline() {
	return !(getStatus() == ArenaStatus.STOPED);
    }
    
    public boolean isRunning() {
	return getStatus() == ArenaStatus.RUNNING;
    }
    
    public boolean isWaiting() {
	return getStatus() == ArenaStatus.WAITING;
    }
    
    public boolean isFull() {
	return getPlayers().length == getMaxPlayers();
    }
    
    public boolean hasKit() {
	for (ZvPPlayer p : this.players) {
	    if (!p.hasKit()) {
		return false;
	    }
	}
	return true;
    }
    
    public boolean hasVoted() {
	for (ZvPPlayer p : this.players) {
	    if (!p.hasVoted()) {
		return false;
	    }
	}
	return true;
    }
    
    public boolean hasPreLobby() {
	return getPreLobby() != null;
    }
    
    public boolean containsPlayer(Player player) {
	for (ZvPPlayer zp : getPlayers()) {
	    if (zp.getUuid() == player.getUniqueId()) {
		return true;
	    }
	}
	return false;
    }
    
    public boolean containsLocation(Location location) {
	return getArea().contains(location);
    }
    
    public boolean useVoteSystem() {
	return this.useVoteSystem;
    }
    
    public boolean keepExp() {
	return this.keepXP;
    }
    
    public boolean keepInventory() {
	return this.keepInventory;
    }
    
    public boolean separatePlayerScores() {
	return this.separatePlayerScores;
    }
    
    public boolean initArenaScore(boolean force) {
	if (this.score == null) {
	    this.score = new ArenaScore(this, separatePlayerScores(), ZvPConfig.getEnableEcon(), ZvPConfig.getIntegrateGame());
	    return true;
	} else {
	    if (force) {
		this.score = new ArenaScore(this, separatePlayerScores(), ZvPConfig.getEnableEcon(), ZvPConfig.getIntegrateGame());
	    }
	}
	return false;
    }
    
    public void setPlayerBoards() {
	for (ZvPPlayer p : getPlayers()) {
	    p.setScoreboard(GameManager.getManager().getNewBoard());
	}
    }
    
    public void setPlayerLevel(int level) {
	for (ZvPPlayer p : getPlayers()) {
	    p.setXPLevel(level);
	}
    }
    
    public void updatePlayerBoards() {
	for (ZvPPlayer p : getPlayers()) {
	    p.updateScoreboard();
	}
    }
    
    public void removePlayerBoards() {
	for (ZvPPlayer p : getPlayers()) {
	    p.removeScoreboard();
	}
    }
    
    public void sendMessage(String message) {
	for (ZvPPlayer p : getPlayers()) {
	    p.sendMessage(message);
	}
	ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "[Message] " + ChatColor.stripColor(message), true);
    }
    
    public boolean addArenaLobby(Location center) {// INFO: return class would make sense here
    
	if (!center.getWorld().getUID().equals(getWorld().getUID())) {
	    return false;
	}
	
	try {
	    this.preLobby = new ArenaLobby(this, center, null, this.rand);
	    return saveArenaLobby(this.preLobby);
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while creating ArenaLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
	    return false;
	}
    }
    
    public boolean addPlayer(final ZvPPlayer player) {
	
	ZvP.getPluginLogger().log(this.getClass(), Level.FINER, "Player " + player.getName() + " inGame: " + GameManager.getManager().isInGame(player.getPlayer()) + ", hasCanceled: " + player.hasCanceled() + " , Kit: " + player.hasKit(), true);
	
	if (!player.hasKit() && !player.hasCanceled()) {
	    
	    if (!containsPlayer(player.getPlayer())) {
		this.players.add(player);
	    }
	    
	    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		
		@Override
		public void run() {
		    addPlayer(player);
		}
	    }, 20L);
	    return true;
	    
	} else if (player.hasKit() && containsPlayer(player.getPlayer())) {
	    this.players.remove(player);
	}
	
	if (!this.players.contains(player) && !player.hasCanceled()) {
	    try {
		player.setStartPosition(getArea().getNewRandomLocation(true));
		player.getReady();
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.INFO, e.getMessage(), true, false, e);
		addPlayer(player);
		return false;
	    }
	    
	    if (!hasPreLobby()) {
		sendMessage(MessageManager.getFormatedMessage("game:player_joined", player.getName()));
		player.sendMessage(MessageManager.getFormatedMessage("game:joined", getID()));
	    }
	    this.players.add(player);
	    
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " has joined Arena " + getID() + "! ASTATUS: " + getStatus().name(), true);
	    
	    if (this.players.size() >= this.minPlayers && !isRunning()) {
		for (ZvPPlayer p : this.players) {
		    if (!p.hasKit()) {
			for (ZvPPlayer p2 : this.players) {
			    if (p2.hasKit()) {
				p2.sendMessage(MessageManager.getMessage("game:waiting"));
			    }
			}
			return false;
		    }
		}
		
		if (getStatus() == ArenaStatus.STANDBY && !isWaiting()) {
		    if (hasPreLobby()) {
			start(0, 0, 5); // INFO: magic number.
		    } else {
			GameManager.getManager().startGame(this, player.getLobby());
		    }
		}
	    } else if (this.players.size() >= this.minPlayers && isRunning()) {
		// Seems like a player who joined during game.
		// Needs scoreboard updates and a new score entry
		// INFO: maybe inform players on increased zombies
		getScore().reInitPlayer(player);
		setPlayerBoards();
		removePlayerBoards();
		updatePlayerBoards();
	    }
	    return true;
	}
	return false;
    }
    
    // INFO: I guess not the best way
    @Deprecated
    void addPreLobbyPlayer(ZvPPlayer player) {
	this.players.add(player);
    }
    
    @Deprecated
    void removePreLobbyPlayer(ZvPPlayer player) {
	this.players.remove(player);
    }
    
    public boolean removePlayer(ZvPPlayer player) {
	if (this.players.contains(player) || (hasPreLobby() && getPreLobby().containsPlayer(player.getPlayer()))) {
	    this.players.remove(player);
	    
	    if (hasPreLobby()) {
		if (getPreLobby().containsPlayer(player.getPlayer())) {
		    getPreLobby().removePlayer(player);
		}
	    }
	    
	    updatePlayerBoards();
	    SignManager.getManager().updateSigns(this);
	    
	    if (hasPreLobby()) {
		if (getPreLobby().getPlayers().length == 0 && this.players.size() == 0 && getStatus() != ArenaStatus.STANDBY) {
		    this.stop();
		} else if (getPreLobby().getPlayers().length == 0 && this.players.size() == 0) {
		    getPreLobby().stopPreLobbyTask();
		}
	    } else {
		if (this.players.size() == 0 && getStatus() != ArenaStatus.STANDBY) {
		    this.stop();
		}
	    }
	    
	    return true;
	}
	return false;
    }
    
    public int spawnZombies(int amount) {
	int successfullySpawned = 0;
	
	for (int i = 0; i < amount; i++) {
	    Entity zombie = getWorld().spawnEntity(getArea().getNewSaveLocation(), EntityType.ZOMBIE);
	    if (zombie != null) {
		getDifficultyTool().customizeEntity(zombie);
		successfullySpawned++;
	    }
	}
	
	updatePlayerBoards();
	return successfullySpawned;
    }
    
    public void start() {
	start(0, 0, getArenaJoinTime());
    }
    
    public void start(int startRound, int startWave, int startDelay) {
	this.round = startRound;
	this.wave = startWave;
	this.score = null;
	
	getWorld().setDifficulty(Difficulty.NORMAL);
	getWorld().setTime(15000L);
	getWorld().setMonsterSpawnLimit(0);
	clearArena();
	
	this.TaskId = new GameRunnable(this, startDelay).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + getID() + " started a new Task!", true);
    }
    
    public void stop() {
	
	setStatus(ArenaStatus.STANDBY);
	Bukkit.getScheduler().cancelTask(getTaskId());
	
	if (hasPreLobby()) {
	    getPreLobby().stopPreLobbyTask();
	}
	
	for (ZvPPlayer zp : getPlayers()) {
	    zp.reset();
	    removePlayer(zp);
	}
	
	this.round = 0;
	this.wave = 0;
	this.score = null;
	
	getWorld().setMonsterSpawnLimit(-1);
	getWorld().setTime(5000L);
	
	clearArena();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + getID() + " stoped!", false, true);
    }
    
    public boolean next() {
	
	if (getWave() == getMaxWaves()) {
	    if (getRound() == getMaxRounds()) {
		return true;
	    }
	    setRound(getRound() + 1);
	    setWave(1);
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Arena " + getID() + " from R:" + (getRound() - 1) + "W:" + getMaxWaves() + " to R:" + getRound() + "W:1", true);
	    return false;
	}
	setWave(getWave() + 1);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Arena " + getID() + " from R:" + getRound() + "W:" + (getWave() - 1) + " to R:" + getRound() + "W:" + getWave(), true);
	return false;
    }
    
    public void clearArena() {
	for (Entity e : getArea().getEntities()) {
	    if (e instanceof Monster || e instanceof Item || e instanceof ExperienceOrb) {
		e.remove();
	    }
	}
    }
    
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Arena) {
	    Arena a = (Arena) obj;
	    if (a.getID() == this.getID()) {
		if (a.getArea().equals(this.getArea())) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    @Override
    public int compareTo(Arena other) {
	return getID() == other.getID() ? 0 : (getID() < other.getID() ? -1 : 1);
    }
}
