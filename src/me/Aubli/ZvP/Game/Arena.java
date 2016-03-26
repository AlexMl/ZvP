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
import me.Aubli.ZvP.Game.GameEnums.ArenaDifficultyLevel;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.ArenaParts.ArenaArea;
import me.Aubli.ZvP.Game.ArenaParts.ArenaConfig;
import me.Aubli.ZvP.Game.ArenaParts.ArenaDifficulty;
import me.Aubli.ZvP.Game.ArenaParts.ArenaLobby;
import me.Aubli.ZvP.Game.ArenaParts.ArenaScore;
import me.Aubli.ZvP.Game.Mode.ZvPMode;
import me.Aubli.ZvP.Game.Mode.ZvPMode.ModeType;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Statistic.DataRecordManager;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.util.SortMap.SortMap;


public class Arena implements Comparable<Arena> {

    private int arenaID;

    private int currentRound;
    private int currentWave;

    private ZvPMode arenaMode;

    private ArenaStatus status;
    private ArenaScore score;
    private ArenaDifficulty difficultyTool;
    private ArenaArea arenaArea;
    private ArenaLobby preLobby;
    private ArenaConfig config;

    private DataRecordManager recordManager;

    private Random rand;

    private ArrayList<ZvPPlayer> players;

    public Arena(int ID, String arenaPath, World world, List<Location> arenaCorners) throws Exception {

	this.arenaID = ID;

	this.rand = new Random();
	this.arenaArea = new ArenaArea(world, this, arenaCorners, null, this.rand);

	this.status = ArenaStatus.STANDBY;

	this.currentRound = 0;
	this.currentWave = 0;

	this.players = new ArrayList<ZvPPlayer>();
	this.difficultyTool = new ArenaDifficulty(this, ArenaDifficultyLevel.NORMAL);
	this.arenaMode = ModeType.STANDARD.getInstance(this);
	this.config = new ArenaConfig(this, new File(arenaPath, getID() + ".yml"));

	this.recordManager = new DataRecordManager();
    }

    public Arena(File arenaFile) throws Exception {
	this.config = new ArenaConfig(this, arenaFile);
	this.rand = new Random();

	this.arenaID = getConfig().getArenaID();

	this.currentRound = 0;
	this.currentWave = 0;

	if (Boolean.parseBoolean(getConfig().getConfigValue("arena.Online").toString())) {
	    this.status = ArenaStatus.STANDBY;
	} else {
	    this.status = ArenaStatus.STOPED;
	}

	World arenaWorld = Bukkit.getWorld(UUID.fromString(getConfig().getConfigValue("arena.Location.world").toString()));

	List<Location> cornerPoints = new ArrayList<Location>();
	for (String locationString : getConfig().getStringList("arena.Location.cornerPoints")) {
	    String[] cords = locationString.split(",");
	    Location loc = new Location(arenaWorld, Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
	    cornerPoints.add(loc);
	}

	List<Location> spawnPositions = new ArrayList<Location>();
	for (String locationString : getConfig().getStringList("arena.Location.staticPositions")) {
	    String[] cords = locationString.split(",");
	    Location loc = new Location(arenaWorld, Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
	    spawnPositions.add(loc);
	}

	this.arenaArea = new ArenaArea(arenaWorld, this, cornerPoints, spawnPositions, this.rand);

	this.difficultyTool = new ArenaDifficulty(this, ArenaDifficultyLevel.valueOf(getConfig().getConfigValue("arena.Difficulty").toString()));
	this.arenaMode = ModeType.valueOf(getConfig().getConfigValue("arena.Mode").toString()).getInstance(this);
	this.players = new ArrayList<ZvPPlayer>();
	this.preLobby = loadArenaLobby();
	this.recordManager = new DataRecordManager();
    }

    public boolean saveArenaLobby(ArenaLobby preLobby) {
	try {
	    getConfig().saveArenaLobby(preLobby);
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "PreLobby for Arena " + getID() + " was successfully saved!", true, true);
	    return true;
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving PreLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
	    return false;
	}
    }

    private ArenaLobby loadArenaLobby() {

	if (getConfig().getConfigValue("arena.Location.PreLobby.X") != null) {
	    Location centerLoc = new Location(getWorld(), Double.parseDouble(getConfig().getConfigValue("arena.Location.PreLobby.X").toString()), Double.parseDouble(getConfig().getConfigValue("arena.Location.PreLobby.Y").toString()), Double.parseDouble(getConfig().getConfigValue("arena.Location.PreLobby.Z").toString()));

	    ArrayList<Location> locations = new ArrayList<Location>();
	    for (String locationString : getConfig().getStringList("arena.Location.PreLobby.extraPositions")) {
		String[] cords = locationString.split(",");
		Location loc = new Location(getWorld(), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
		locations.add(loc);
	    }

	    try {
		return new ArenaLobby(this, centerLoc, locations, this.rand);
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while loading PreLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
		return null;
	    }
	}
	return null;
    }

    public boolean deleteArenaLobby() {
	try {
	    this.preLobby = null;
	    getConfig().removeArenaLobby();
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Deleted PreLobby from Arena " + getID() + " successfully!", true, true);
	    return true;
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while deleting ArenaLobby for Arena " + getID() + ": " + e.getMessage(), true, false, e);
	    return false;
	}
    }

    boolean delete() {
	return getConfig().deleteFile();
    }

    public void setStatus(ArenaStatus status) {
	this.status = status;
	SignManager.getManager().updateSigns(this);
    }

    public void setRound(int round) {
	this.currentRound = round;
	SignManager.getManager().updateSigns(this);
	updatePlayerBoards();
    }

    public void setWave(int wave) {
	this.currentWave = wave;
	SignManager.getManager().updateSigns(this);
	updatePlayerBoards();
    }

    public int getID() {
	return this.arenaID;
    }

    public ArenaStatus getStatus() {
	return this.status;
    }

    public ArenaDifficultyLevel getDifficulty() {
	return getDifficultyTool().getDifficulty();
    }

    public int getCurrentRound() {
	return this.currentRound;
    }

    public int getCurrentWave() {
	return this.currentWave;
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

    public ArenaArea getArea() {
	return this.arenaArea;
    }

    public ArenaConfig getConfig() {
	return this.config;
    }

    public ZvPMode getArenaMode() {
	return this.arenaMode;
    }

    public ModeType getArenaModeType() {
	return getArenaMode().getType();
    }

    public DataRecordManager getRecordManager() {
	return this.recordManager;
    }

    public World getWorld() {
	return getArea().getWorld();
    }

    public ZvPPlayer getRandomPlayer() {
	ZvPPlayer[] players = getArenaMode().getLivingPlayers();
	return players.length > 0 ? players[this.rand.nextInt(players.length)] : null;
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
	return getSpawningZombies(getCurrentWave(), getCurrentRound(), getPlayers().length, getDifficulty().getLevel());
    }

    public int getSpawningZombies(int w, int r, int p, int d) {
	return ((int) Math.sqrt(r * w * getConfig().getSpawnRate() * getArea().getDiagonal() * p * ((d + 1.0) / 2.0)));
    }

    public ZvPPlayer getBestPlayer() {
	Map<UUID, Double> scoreMap = new HashMap<UUID, Double>();

	for (ZvPPlayer player : getPlayers()) {
	    double score = (player.getKills() + getScore().getScore(player)) - player.getDeaths();
	    scoreMap.put(player.getUuid(), score);
	}

	scoreMap = SortMap.sortByValueDescending(scoreMap);
	return GameManager.getManager().getPlayer(scoreMap.keySet().toArray(new UUID[0])[0]);
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
	return getPlayers().length == getConfig().getMaxPlayers();
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

    public boolean initArenaScore(boolean force) {
	if (this.score == null) {
	    this.score = new ArenaScore(this, getConfig().isSeparatedScores(), ZvPConfig.getEnableEcon(), ZvPConfig.getIntegrateGame());
	    return true;
	} else {
	    if (force) {
		this.score = new ArenaScore(this, getConfig().isSeparatedScores(), ZvPConfig.getEnableEcon(), ZvPConfig.getIntegrateGame());
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
		if (getStatus() == ArenaStatus.WAITING) {
		    sendMessage(MessageManager.getMessage(game.waiting_for_players));
		}
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
		return addPlayer(player);
	    }

	    if (!hasPreLobby()) {
		sendMessage(MessageManager.getFormatedMessage(game.player_joined, player.getName()));
		player.sendMessage(MessageManager.getFormatedMessage(game.joined, getID()));
	    }
	    this.players.add(player);

	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " has joined Arena " + getID() + "! ASTATUS: " + getStatus().name(), true);

	    if (this.players.size() >= getConfig().getMinPlayers() && !isRunning()) {
		if (getStatus() == ArenaStatus.STANDBY && !isWaiting()) {
		    if (hasPreLobby()) {
			start(0, 0, 5); // INFO: magic number.
		    } else {
			GameManager.getManager().startGame(this, player.getLobby());
		    }
		}
	    } else if (this.players.size() >= getConfig().getMinPlayers() && isRunning()) {
		// Seems like a player who joined during game.
		// Needs scoreboard updates and a new score entry
		// INFO: maybe inform players on increased zombies
		getScore().reInitPlayer(player);
		setPlayerBoards();
		removePlayerBoards();
		updatePlayerBoards();
	    }
	    this.arenaMode.onJoin(player, this);
	    return true;
	}
	return false;
    }

    // INFO: I guess not the best way
    @Deprecated
    public void addPreLobbyPlayer(ZvPPlayer player) {
	this.players.add(player);
    }

    @Deprecated
    public void removePreLobbyPlayer(ZvPPlayer player) {
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

	    this.arenaMode.onLeave(player);
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
	start(0, 0, getConfig().getJoinTime());
    }

    public void start(int startRound, int startWave, int startDelay) {
	this.currentRound = startRound;
	this.currentWave = startWave;
	this.score = null;

	getWorld().setDifficulty(Difficulty.NORMAL);
	getWorld().setTime(15000L);
	getWorld().setMonsterSpawnLimit(0);
	clearArena();

	// this.TaskId = new GameRunnable(this, startDelay).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId();
	this.arenaMode.start(startDelay);
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + getID() + " started a new Task in mode " + this.arenaMode.getName() + "!", true);
    }

    public void reStart(int startDelay) {
	getWorld().setDifficulty(Difficulty.NORMAL);
	getWorld().setTime(15000L);
	getWorld().setMonsterSpawnLimit(0);
	clearArena();

	this.arenaMode = this.arenaMode.reInitialize();
	this.arenaMode.start(startDelay);
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + getID() + " started a new Task in mode " + this.arenaMode.getName() + "!", true);
    }

    public void stop() {

	setStatus(ArenaStatus.STANDBY);
	this.arenaMode.stop();
	this.arenaMode = this.arenaMode.reInitialize();
	getRecordManager().transmitRecords();

	if (hasPreLobby()) {
	    getPreLobby().stopPreLobbyTask();
	}

	for (ZvPPlayer zp : getPlayers()) {
	    zp.reset();
	    removePlayer(zp);
	}

	this.currentRound = 0;
	this.currentWave = 0;
	this.score = null;
	this.players.clear();

	getWorld().setMonsterSpawnLimit(-1);
	getWorld().setTime(5000L);

	clearArena();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + getID() + " stoped!", false, true);
    }

    public boolean hasNext() {
	return (getCurrentWave() * getCurrentRound()) < (getConfig().getMaxWaves() * getConfig().getMaxRounds());
    }

    public boolean next() {

	if (getCurrentWave() == getConfig().getMaxWaves()) {
	    if (getCurrentRound() == getConfig().getMaxRounds()) {
		return true;
	    }
	    setRound(getCurrentRound() + 1);
	    setWave(1);
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Arena " + getID() + " from R:" + (getCurrentRound() - 1) + "W:" + getConfig().getMaxWaves() + " to R:" + getCurrentRound() + "W:1", true);
	    return false;
	}
	setWave(getCurrentWave() + 1);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Arena " + getID() + " from R:" + getCurrentRound() + "W:" + (getCurrentWave() - 1) + " to R:" + getCurrentRound() + "W:" + getCurrentWave(), true);
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
