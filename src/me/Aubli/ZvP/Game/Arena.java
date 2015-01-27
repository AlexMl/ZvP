package me.Aubli.ZvP.Game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
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


public class Arena implements Comparable<Arena> {
    
    private File arenaFile;
    private FileConfiguration arenaConfig;
    
    private int arenaID;
    
    private ArenaStatus status;
    
    private int maxPlayers;
    private int minPlayers;
    private int maxRounds;
    private int maxWaves;
    private int round;
    private int wave;
    
    private int spawnRate;
    private double saveRadius;
    
    private int TaskId;
    
    private World arenaWorld;
    private Location minLoc;
    private Location maxLoc;
    
    private Random rand;
    
    private double teamBalance;
    
    private ArrayList<ZvPPlayer> players;
    
    public Arena(int ID, int maxPlayers, String arenaPath, Location min, Location max, int rounds, int waves, int spawnRate, double saveRadius) {
	
	this.arenaID = ID;
	
	this.maxPlayers = maxPlayers;
	this.minPlayers = ((int) Math.ceil(maxPlayers / 4)) + 1;
	
	this.maxRounds = rounds;
	this.maxWaves = waves;
	
	this.arenaWorld = min.getWorld();
	this.minLoc = min.clone();
	this.maxLoc = max.clone();
	
	this.status = ArenaStatus.STANDBY;
	
	this.round = 0;
	this.wave = 0;
	
	this.teamBalance = 0.0;
	
	this.saveRadius = saveRadius;
	this.spawnRate = spawnRate;
	
	this.arenaFile = new File(arenaPath + "/" + ID + ".yml");
	this.arenaConfig = YamlConfiguration.loadConfiguration(this.arenaFile);
	
	this.players = new ArrayList<ZvPPlayer>();
	
	this.rand = new Random();
	
	try {
	    this.arenaFile.createNewFile();
	    save();
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while saving Arena " + getID() + ": " + e.getMessage(), true, false, e);
	}
    }
    
    public Arena(File arenaFile) {
	this.arenaFile = arenaFile;
	this.arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
	
	this.arenaID = this.arenaConfig.getInt("arena.ID");
	this.maxPlayers = this.arenaConfig.getInt("arena.maxPlayers");
	this.minPlayers = this.arenaConfig.getInt("arena.minPlayers");
	
	this.maxRounds = this.arenaConfig.getInt("arena.rounds");
	this.maxWaves = this.arenaConfig.getInt("arena.waves");
	
	if (this.arenaConfig.getBoolean("arena.Online")) {
	    this.status = ArenaStatus.STANDBY;
	} else {
	    this.status = ArenaStatus.STOPED;
	}
	
	this.round = 0;
	this.wave = 0;
	this.teamBalance = 0.0;
	
	this.spawnRate = this.arenaConfig.getInt("arena.spawnRate");
	this.saveRadius = this.arenaConfig.getDouble("arena.saveRadius");
	
	this.arenaWorld = Bukkit.getWorld(UUID.fromString(this.arenaConfig.getString("arena.Location.world")));
	this.minLoc = new Location(this.arenaWorld, this.arenaConfig.getInt("arena.Location.min.X"), this.arenaConfig.getInt("arena.Location.min.Y"), this.arenaConfig.getInt("arena.Location.min.Z"));
	this.maxLoc = new Location(this.arenaWorld, this.arenaConfig.getInt("arena.Location.max.X"), this.arenaConfig.getInt("arena.Location.max.Y"), this.arenaConfig.getInt("arena.Location.max.Z"));
	
	this.players = new ArrayList<ZvPPlayer>();
	this.rand = new Random();
    }
    
    void save() throws IOException {
	this.arenaConfig.set("arena.ID", this.arenaID);
	this.arenaConfig.set("arena.Online", !(getStatus() == ArenaStatus.STOPED));
	
	this.arenaConfig.set("arena.minPlayers", this.minPlayers);
	this.arenaConfig.set("arena.maxPlayers", this.maxPlayers);
	this.arenaConfig.set("arena.rounds", this.maxRounds);
	this.arenaConfig.set("arena.waves", this.maxWaves);
	this.arenaConfig.set("arena.spawnRate", this.spawnRate);
	this.arenaConfig.set("arena.saveRadius", this.saveRadius);
	
	this.arenaConfig.set("arena.Location.world", this.arenaWorld.getUID().toString());
	this.arenaConfig.set("arena.Location.min.X", this.minLoc.getBlockX());
	this.arenaConfig.set("arena.Location.min.Y", this.minLoc.getBlockY());
	this.arenaConfig.set("arena.Location.min.Z", this.minLoc.getBlockZ());
	
	this.arenaConfig.set("arena.Location.max.X", this.maxLoc.getBlockX());
	this.arenaConfig.set("arena.Location.max.Y", this.maxLoc.getBlockY());
	this.arenaConfig.set("arena.Location.max.Z", this.maxLoc.getBlockZ());
	
	this.arenaConfig.save(this.arenaFile);
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
    
    public double getBalance() {
	return this.teamBalance;
    }
    
    public double getSaveRadius() {
	return this.saveRadius;
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
    
    public Location getNewRandomLocation() {
	
	int x;
	int y;
	int z;
	
	x = this.rand.nextInt((getMax().getBlockX() - getMin().getBlockX() - 1)) + getMin().getBlockX() + 1;
	y = getWorld().getHighestBlockYAt(this.minLoc) + 1;
	z = this.rand.nextInt((getMax().getBlockZ() - getMin().getBlockZ() - 1)) + getMin().getBlockZ() + 1;
	
	Location startLoc = new Location(getWorld(), x, y, z);
	
	if (containsLocation(startLoc)) {
	    return startLoc.clone();
	} else {
	    return getNewRandomLocation();
	}
	
    }
    
    public Location getNewSaveLocation() {
	
	final double distance = getSaveRadius();
	
	final Location spawnLoc = getNewRandomLocation();
	
	for (ZvPPlayer p : getPlayers()) {
	    
	    if (p.getLocation().distance(spawnLoc) <= distance) {
		return getNewSaveLocation();
	    }
	}
	
	if (containsLocation(spawnLoc)) {
	    return spawnLoc.clone();
	} else {
	    return getNewSaveLocation();
	}
	
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
    
    public int getLivingZombies() {
	int zombies = 0;
	for (Entity e : getEntities()) {
	    if (e instanceof Zombie) {
		zombies++;
	    }
	}
	return zombies;
    }
    
    public int getKilledZombies() {
	int kills = 0;
	for (ZvPPlayer p : getPlayers()) {
	    kills += p.getKills();
	}
	return kills;
    }
    
    public boolean isOnline() {
	return !(getStatus() == ArenaStatus.STOPED || getStatus() == ArenaStatus.SUSPEND);
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
    
    public void addBalance(double sum) {
	this.teamBalance += sum;
	updatePlayerBoards();
    }
    
    public void subtractBalance(double sum) {
	if (sum > getBalance()) {
	    this.teamBalance = 0.0;
	} else {
	    this.teamBalance -= sum;
	}
	updatePlayerBoards();
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
		player.setStartPosition(getNewRandomLocation());
		player.getReady();
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(Level.INFO, e.getMessage(), true, false, e);
		addPlayer(player);
		return false;
	    }
	    
	    sendMessage(String.format(MessageManager.getMessage("game:player_joined"), player.getName()));
	    player.sendMessage(String.format(MessageManager.getMessage("game:joined"), getID()));
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
		    GameManager.getManager().startGame(this, player.getLobby(), getMaxRounds(), getMaxWaves());
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
	    
	    if (this.players.size() == 0) {
		this.stop();
	    }
	    
	    return true;
	}
	return false;
    }
    
    private void customizeEntity(Entity zombie) {
	Zombie z = (Zombie) zombie;
	
	z.setRemoveWhenFarAway(false);
	z.setTarget(getRandomPlayer().getPlayer());
	
	switch (this.rand.nextInt(7)) {
	    case 0:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setMaxHealth(40D);
		z.setVelocity(z.getVelocity().multiply(1.5D));
		// z.setCustomName("0");
		break;
	    case 1:
		z.setBaby(true);
		z.setCanPickupItems(false);
		z.setVillager(false);
		z.setHealth(20D);
		z.setVelocity(z.getVelocity().multiply(0.75D));
		// z.setCustomName("1");
		break;
	    case 2:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(true);
		z.setHealth(10D);
		// z.setCustomName("2");
		break;
	    case 3:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(true);
		z.setHealth(15D);
		// z.setCustomName("3");
		break;
	    case 4:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(false);
		z.setMaxHealth(30D);
		// z.setCustomName("4");
		break;
	    default:
		z.setBaby(false);
		z.setCanPickupItems(false);
		z.setVillager(false);
		z.setHealth(20D);
		// z.setCustomName("default");
		break;
	}
    }
    
    public void spawnZombies(int amount) {
	for (int i = 0; i < amount; i++) {
	    customizeEntity(getWorld().spawnEntity(getNewSaveLocation(), EntityType.ZOMBIE));
	}
	updatePlayerBoards();
    }
    
    public void start(int rounds, int waves) {
	this.maxRounds = rounds;
	this.maxWaves = waves;
	
	this.round = 0;
	this.wave = 0;
	this.teamBalance = 0.0;
	
	getWorld().setDifficulty(Difficulty.NORMAL);
	getWorld().setTime(15000L);
	getWorld().setMonsterSpawnLimit(0);
	clearArena();
	
	this.TaskId = new GameRunnable(this, ZvP.getStartDelay(), getSpawnRate()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId();
	ZvP.getPluginLogger().log(Level.INFO, "Arena " + getID() + " started a new Task!", true);
    }
    
    public void stop() {
	for (ZvPPlayer zp : getPlayers()) {
	    zp.reset();
	    removePlayer(zp);
	}
	
	this.round = 0;
	this.wave = 0;
	this.teamBalance = 0.0;
	
	getWorld().setMonsterSpawnLimit(-1);
	getWorld().setTime(5000L);
	
	clearArena();
	setStatus(ArenaStatus.STANDBY);
	Bukkit.getScheduler().cancelTask(getTaskId());
	ZvP.getPluginLogger().log(Level.INFO, "Arena " + getID() + " stoped!", true);
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
