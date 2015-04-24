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
import org.bukkit.Chunk;
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
    
    private Random rand;
    
    private ArrayList<ZvPPlayer> players;
    
    private boolean enableSpawnProtection;
    
    private final int maxPlayers;
    private final int minPlayers;
    private final int maxRounds;
    private final int maxWaves;
    private int round;
    private int wave;
    
    private final int spawnRate;
    private final int protectionDuration;
    private final double saveRadius;
    
    private World arenaWorld;
    private Location minLoc;
    private Location maxLoc;
    private List<Location> staticSpawnLocations;
    
    public Arena(int ID, int maxPlayers, String arenaPath, Location min, Location max, int rounds, int waves, int spawnRate, double saveRadius, ArenaDifficultyLevel difficulty, boolean spawnProtection) {
	
	this.arenaID = ID;
	
	this.maxPlayers = maxPlayers;
	this.minPlayers = ((int) Math.ceil(maxPlayers / 4)) + 1;
	
	this.maxRounds = rounds;
	this.maxWaves = waves;
	
	this.arenaWorld = min.getWorld();
	this.minLoc = min.clone();
	this.maxLoc = max.clone();
	
	this.status = ArenaStatus.STANDBY;
	this.difficulty = difficulty;
	
	this.round = 0;
	this.wave = 0;
	
	this.saveRadius = saveRadius;
	this.spawnRate = spawnRate;
	
	this.enableSpawnProtection = spawnProtection;
	this.protectionDuration = 5;
	
	this.arenaFile = new File(arenaPath + "/" + ID + ".yml");
	this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaFile);
	
	this.players = new ArrayList<ZvPPlayer>();
	this.staticSpawnLocations = new ArrayList<Location>();
	this.difficultyTool = new ArenaDifficulty(this, getDifficulty());
	
	this.rand = new Random();
	save();
    }
    
    public Arena(File arenaFile) {
	this.arenaFile = arenaFile;
	this.arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
	
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
	
	this.enableSpawnProtection = this.arenaConfig.getBoolean("arena.safety.SpawnProtection.enabled", true);
	this.protectionDuration = this.arenaConfig.getInt("arena.safety.SpawnProtection.duration");
	this.difficulty = ArenaDifficultyLevel.valueOf(this.arenaConfig.getString("arena.Difficulty", "NORMAL"));
	
	this.round = 0;
	this.wave = 0;
	
	this.spawnRate = this.arenaConfig.getInt("arena.spawnRate", ZvPConfig.getDefaultZombieSpawnRate());
	this.saveRadius = this.arenaConfig.getDouble("arena.safety.saveRadius", ZvPConfig.getDefaultSaveRadius());
	
	this.arenaWorld = Bukkit.getWorld(UUID.fromString(this.arenaConfig.getString("arena.Location.world")));
	this.minLoc = new Location(this.arenaWorld, this.arenaConfig.getInt("arena.Location.min.X"), this.arenaConfig.getInt("arena.Location.min.Y"), this.arenaConfig.getInt("arena.Location.min.Z"));
	this.maxLoc = new Location(this.arenaWorld, this.arenaConfig.getInt("arena.Location.max.X"), this.arenaConfig.getInt("arena.Location.max.Y"), this.arenaConfig.getInt("arena.Location.max.Z"));
	
	this.staticSpawnLocations = new ArrayList<Location>();
	for (String locationString : this.arenaConfig.getStringList("arena.Location.staticPositions")) {
	    
	    String[] cords = locationString.split(",");
	    
	    Location loc = new Location(getWorld(), Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), Integer.parseInt(cords[2]));
	    this.staticSpawnLocations.add(loc);
	}
	
	this.difficultyTool = new ArenaDifficulty(this, getDifficulty());
	this.players = new ArrayList<ZvPPlayer>();
	this.rand = new Random();
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
	    
	    this.arenaConfig.set("arena.safety.SpawnProtection.enabled", getSpawnProtection());
	    this.arenaConfig.set("arena.safety.SpawnProtection.duration", getProtectionDuration());
	    this.arenaConfig.set("arena.safety.saveRadius", this.saveRadius);
	    
	    this.arenaConfig.set("arena.Location.world", this.arenaWorld.getUID().toString());
	    this.arenaConfig.set("arena.Location.min.X", this.minLoc.getBlockX());
	    this.arenaConfig.set("arena.Location.min.Y", this.minLoc.getBlockY());
	    this.arenaConfig.set("arena.Location.min.Z", this.minLoc.getBlockZ());
	    
	    this.arenaConfig.set("arena.Location.max.X", this.maxLoc.getBlockX());
	    this.arenaConfig.set("arena.Location.max.Y", this.maxLoc.getBlockY());
	    this.arenaConfig.set("arena.Location.max.Z", this.maxLoc.getBlockZ());
	    
	    List<String> locationList = new ArrayList<String>();
	    
	    for (Location loc : this.staticSpawnLocations) {
		locationList.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
	    }
	    
	    this.arenaConfig.set("arena.Location.staticPositions", locationList);
	    
	    this.arenaConfig.addDefault("version", ZvP.getInstance().getDescription().getVersion());
	    this.arenaConfig.options().copyDefaults(true);
	    
	    this.arenaConfig.save(this.arenaFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while saving Arena " + getID() + ": " + e.getMessage(), true, false, e);
	}
    }
    
    void delete() {
	this.arenaFile.delete();
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
    
    public double getSaveRadius() {
	return this.saveRadius;
    }
    
    public int getProtectionDuration() {
	return this.protectionDuration;
    }
    
    public ArenaScore getScore() {
	return this.score;
    }
    
    public ArenaDifficulty getDifficultyTool() {
	return this.difficultyTool;
    }
    
    public boolean getSpawnProtection() {
	return this.enableSpawnProtection;
    }
    
    public World getWorld() {
	return this.arenaWorld;
    }
    
    public Location getMin() {
	return this.minLoc;
    }
    
    public Location getMax() {
	return this.maxLoc;
    }
    
    public Location getNewRandomLocation(boolean player) {
	
	if (player && !this.staticSpawnLocations.isEmpty()) {
	    Location location = this.staticSpawnLocations.get(this.rand.nextInt(this.staticSpawnLocations.size()));
	    if (containsLocation(location)) {
		return location.clone();
	    }
	    return getNewRandomLocation(player);
	} else {
	    
	    int x;
	    int y = 0;
	    int z;
	    
	    x = this.rand.nextInt((getMax().getBlockX() - getMin().getBlockX() - 1)) + getMin().getBlockX() + 1;
	    z = this.rand.nextInt((getMax().getBlockZ() - getMin().getBlockZ() - 1)) + getMin().getBlockZ() + 1;
	    
	    if (getWorld().getHighestBlockYAt(x, z) + 1 >= getMin().getBlockY() && getWorld().getHighestBlockYAt(x, z) + 1 <= getMax().getBlockY()) {
		// If highest y is between min and max, go for it
		y = getWorld().getHighestBlockYAt(x, z) + 1;
	    } else if (getMax().getBlockY() == getMin().getBlockY() && getWorld().getHighestBlockYAt(x, z) == getMin().getBlockY()) {
		// min y == max y == highest y at random location
		// arena is flat
		y = getWorld().getHighestBlockYAt(x, z) + 1;
	    } else if (getMax().getBlockY() == getMin().getBlockY()) {
		// arena is flat but has a ceiling
		y = getMin().getBlockY() + 1;
	    } else {
		// iterate over y from min to max to find a perfect y
		// not very performant but needed in worst case (above doesnt match)
		
		for (int iy = 0; iy < (getMax().getBlockY() - getMin().getBlockY()); iy++) {
		    if (isValidLocation(getMin().clone().add(0, iy, 0))) {
			y = getMin().getBlockY() + iy;
			System.out.println("ny:" + y);
			break;
		    }
		}
		if (y == 0) {
		    return getNewRandomLocation(player);
		}
	    }
	    Location startLoc = new Location(getWorld(), x, y, z);
	    
	    // System.out.println("valid? " + isValidLocation(startLoc) + " Y:" + startLoc.getBlockY());
	    if (isValidLocation(startLoc)) {
		return startLoc.clone();
	    } else {
		return getNewRandomLocation(player);
	    }
	}
    }
    
    private boolean isValidLocation(Location location) {
	
	if (containsLocation(location)) {
	    if (isValidBlock(location) && isValidBlock(location.clone().add(0, 1, 0))) { // Make sure the location is not a Block
		if (!isValidBlock(location.clone().subtract(0, 1, 0))) {
		    return true;
		}
	    }
	}
	
	return false;
    }
    
    private boolean isValidBlock(Location location) {
	
	if (location.getBlock().isEmpty() || location.getBlock().isLiquid()) {
	    return true;
	}
	
	switch (location.getBlock().getType()) {
	    case ACTIVATOR_RAIL:
	    case ARMOR_STAND:
	    case BREWING_STAND:
	    case DEAD_BUSH:
	    case DETECTOR_RAIL:
	    case DOUBLE_PLANT:
	    case ENDER_PORTAL:
	    case FLOWER_POT:
	    case GRASS:
	    case LONG_GRASS:
	    case PORTAL:
	    case POWERED_RAIL:
	    case PUMPKIN_STEM:
	    case RAILS:
	    case RED_MUSHROOM:
	    case RED_ROSE:
	    case REDSTONE_COMPARATOR:
	    case REDSTONE_COMPARATOR_OFF:
	    case REDSTONE_COMPARATOR_ON:
	    case REDSTONE_TORCH_OFF:
	    case REDSTONE_TORCH_ON:
	    case REDSTONE_WIRE:
	    case SAPLING:
	    case SIGN_POST:
	    case SUGAR_CANE_BLOCK:
	    case TORCH:
	    case TRIPWIRE:
	    case WEB:
	    case YELLOW_FLOWER:
		return true;
		
	    default:
		return false;
	}
    }
    
    public Location getNewSaveLocation() {
	// Save means Location with no players nearby
	// ---> Spawn zombies in a location save for players
	
	final double distance = getSaveRadius();
	
	final Location spawnLoc = getNewRandomLocation(false);
	
	for (ZvPPlayer p : getPlayers()) {
	    
	    if (this.staticSpawnLocations.isEmpty()) {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
	    } else {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
		for (Location loc : this.staticSpawnLocations) {
		    if (spawnLoc.distanceSquared(loc) <= ((distance * distance) * 0.5)) {
			return getNewSaveLocation();
		    }
		}
	    }
	}
	
	if (containsLocation(spawnLoc)) {
	    return spawnLoc.clone();
	} else {
	    return getNewSaveLocation();
	}
    }
    
    public Location getNewUnsaveLocation(double maxDistance) {
	// Exact opposite of getNewSaveLocation
	// ---> Spawn zombies in a location nearby the player
	
	Location saveLoc = getNewSaveLocation(); // Do not break the save radius rule
	
	if (maxDistance < this.saveRadius) {
	    maxDistance += this.saveRadius;
	}
	
	for (ZvPPlayer player : getPlayers()) {
	    if (saveLoc.distanceSquared(player.getLocation()) <= Math.pow(this.saveRadius + maxDistance, 2)) {
		return saveLoc.clone();
	    }
	}
	return getNewUnsaveLocation(maxDistance);
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
    
    private Entity[] getEntities() {
	List<Entity> eList = new ArrayList<Entity>();
	Entity[] entities;
	
	Chunk minC = this.minLoc.getChunk();
	Chunk maxC = this.maxLoc.getChunk();
	
	int minX = minC.getX();
	int minZ = minC.getZ();
	int maxX = maxC.getX();
	int maxZ = maxC.getZ();
	
	for (int x = minX; x <= maxX; x++) {
	    for (int z = minZ; z <= +maxZ; z++) {
		Chunk entityChunk = getWorld().getChunkAt(x, z);
		for (Entity e : entityChunk.getEntities()) {
		    eList.add(e);
		}
	    }
	}
	
	entities = new Entity[eList.size()];
	for (int i = 0; i < eList.size(); i++) {
	    entities[i] = eList.get(i);
	}
	return entities;
    }
    
    public Zombie[] getLivingZombies() {
	List<Zombie> zombieList = new ArrayList<Zombie>();
	for (Entity e : getEntities()) {
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
	int spawn = (int) Math.sqrt(r * w * getSpawnRate() * getMin().distance(getMax()) * p * ((d + 1.0) / 2.0));
	return spawn;
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
    
    public boolean containsPlayer(Player player) {
	for (ZvPPlayer zp : getPlayers()) {
	    if (zp.getUuid() == player.getUniqueId()) {
		return true;
	    }
	}
	return false;
    }
    
    public boolean containsLocation(Location location) {
	return ((location.getX() <= getMax().getX() && location.getX() >= getMin().getX()) && (location.getZ() <= getMax().getZ() && location.getZ() >= getMin().getZ()));
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
	ZvP.getPluginLogger().log(Level.FINEST, "[Message] " + ChatColor.stripColor(message), true);
    }
    
    public boolean addSpawnLocation(Location loc) {
	if (containsLocation(loc)) {
	    if (!this.staticSpawnLocations.contains(loc)) {
		this.staticSpawnLocations.add(loc);
		save();
		return true;
	    }
	}
	return false;
    }
    
    public boolean addPlayer(final ZvPPlayer player) {
	
	ZvP.getPluginLogger().log(Level.FINER, "Player " + player.getName() + " inGame: " + GameManager.getManager().isInGame(player.getPlayer()) + ", hasCanceled: " + player.hasCanceled() + " , Kit: " + player.hasKit(), true);
	
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
		player.setStartPosition(getNewRandomLocation(true));
		player.getReady();
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(Level.INFO, e.getMessage(), true, false, e);
		addPlayer(player);
		return false;
	    }
	    
	    sendMessage(MessageManager.getFormatedMessage("game:player_joined", player.getName()));
	    player.sendMessage(MessageManager.getFormatedMessage("game:joined", getID()));
	    this.players.add(player);
	    
	    ZvP.getPluginLogger().log(Level.INFO, "Player " + player.getName() + " has joined Arena " + getID(), true);
	    
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
		
		if (!isWaiting()) {
		    GameManager.getManager().startGame(this, player.getLobby());
		}
	    }
	    return true;
	}
	return false;
    }
    
    public boolean removePlayer(ZvPPlayer player) {
	if (this.players.contains(player)) {
	    this.players.remove(player);
	    updatePlayerBoards();
	    SignManager.getManager().updateSigns(this);
	    
	    if (this.players.size() == 0 && getStatus() != ArenaStatus.STANDBY) {
		this.stop();
	    }
	    
	    return true;
	}
	return false;
    }
    
    public int spawnZombies(int amount) {
	int successfullySpawned = 0;
	
	for (int i = 0; i < amount; i++) {
	    Entity zombie = getWorld().spawnEntity(getNewSaveLocation(), EntityType.ZOMBIE);
	    if (zombie != null) {
		getDifficultyTool().customizeEntity(zombie);
		successfullySpawned++;
	    }
	}
	
	updatePlayerBoards();
	return successfullySpawned;
    }
    
    public void start() {
	this.round = 0;
	this.wave = 0;
	
	this.score = new ArenaScore(this, ZvPConfig.getSeparatePlayerScores());
	getWorld().setDifficulty(Difficulty.NORMAL);
	getWorld().setTime(15000L);
	getWorld().setMonsterSpawnLimit(0);
	clearArena();
	
	this.TaskId = new GameRunnable(this, ZvPConfig.getStartDelay()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId();
	ZvP.getPluginLogger().log(Level.INFO, "Arena " + getID() + " started a new Task!", true);
    }
    
    public void stop() {
	
	setStatus(ArenaStatus.STANDBY);
	Bukkit.getScheduler().cancelTask(getTaskId());
	
	for (ZvPPlayer zp : getPlayers()) {
	    zp.reset();
	    removePlayer(zp);
	}
	
	this.round = 0;
	this.wave = 0;
	
	getWorld().setMonsterSpawnLimit(-1);
	getWorld().setTime(5000L);
	
	clearArena();
	ZvP.getPluginLogger().log(Level.INFO, "Arena " + getID() + " stoped!", false, true);
    }
    
    public boolean next() {
	
	if (getWave() == getMaxWaves()) {
	    if (getRound() == getMaxRounds()) {
		return true;
	    }
	    setRound(getRound() + 1);
	    setWave(1);
	    ZvP.getPluginLogger().log(Level.FINE, "Arena " + getID() + " from R:" + (getRound() - 1) + "W:" + getMaxWaves() + " to R:" + getRound() + "W:1", true);
	    return false;
	}
	setWave(getWave() + 1);
	ZvP.getPluginLogger().log(Level.FINE, "Arena " + getID() + " from R:" + getRound() + "W:" + (getWave() - 1) + " to R:" + getRound() + "W:" + getWave(), true);
	return false;
    }
    
    public void clearArena() {
	for (Entity e : getEntities()) {
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
		if (a.getMin().equals(this.getMin()) && a.getMax().equals(this.getMax())) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    @Override
    public int compareTo(Arena o) {
	
	if (getID() == o.getID()) {
	    return 0;
	} else if (getID() > o.getID()) {
	    return 1;
	} else if (getID() < o.getID()) {
	    return -1;
	}
	
	return 0;
    }
}
