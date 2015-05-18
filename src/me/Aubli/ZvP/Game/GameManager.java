package me.Aubli.ZvP.Game;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.util.File.Converter.FileConverter.FileType;


public class GameManager {
    
    public enum ArenaStatus {
	RUNNING(MessageManager.getMessage("status:running")),
	VOTING(MessageManager.getMessage("status:running")),
	BREAKWAITING(MessageManager.getMessage("status:running")),
	WAITING(MessageManager.getMessage("status:waiting")),
	STANDBY(MessageManager.getMessage("status:waiting")),
	STOPED(MessageManager.getMessage("status:stoped"));
	
	private String name;
	
	private ArenaStatus(String name) {
	    this.name = name;
	}
	
	public String getName() {
	    return this.name;
	}
    }
    
    public enum ArenaDifficultyLevel {
	EASY(1),
	NORMAL(2),
	HARD(3);
	
	private int level;
	
	private ArenaDifficultyLevel(int level) {
	    this.level = level;
	}
	
	public int getLevel() {
	    return this.level;
	}
    }
    
    private static GameManager manager;
    private ZvP plugin;
    
    private String arenaPath;
    private String lobbyPath;
    
    private FilenameFilter fileFilter;
    
    private ArrayList<Lobby> lobbys;
    private ArrayList<Arena> arenas;
    
    private ScoreboardManager boardman;
    
    public GameManager() {
	manager = this;
	this.plugin = ZvP.getInstance();
	
	this.arenaPath = this.plugin.getDataFolder().getPath() + "/Arenas";
	this.lobbyPath = this.plugin.getDataFolder().getPath() + "/Lobbys";
	
	this.boardman = Bukkit.getScoreboardManager();
	
	this.fileFilter = new FilenameFilter() {
	    
	    @Override
	    public boolean accept(File dir, String name) {
		if (name.contains(".old")) {
		    return false;
		}
		return true;
	    }
	};
	
	loadConfig(true);
    }
    
    public static GameManager getManager() {
	return manager;
    }
    
    public void shutdown() {
	stopGames();
    }
    
    public void reloadConfig() {
	stopGames();
	loadConfig(false);
    }
    
    // Config
    private void loadConfig(boolean delay) {
	
	if (delay) {
	    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		
		@Override
		public void run() {
		    reloadConfig();
		}
	    }, 3 * 20L);
	}
	
	if (!new File(this.arenaPath).exists() || !new File(this.lobbyPath).exists()) {
	    new File(this.arenaPath).mkdirs();
	    new File(this.lobbyPath).mkdirs();
	}
	
	this.lobbys = new ArrayList<Lobby>();
	this.arenas = new ArrayList<Arena>();
	
	ZvPConfig.reloadConfig();
	
	for (Player player : Bukkit.getOnlinePlayers()) {
	    ZvP.removeTool(player);
	}
	
	loadArenas();
	loadLobbys();
	
	if (SignManager.getManager() != null) {
	    SignManager.getManager().reloadConfig();
	}
	
	new MessageManager(ZvPConfig.getLocale());
	new ShopManager();
	
	if (KitManager.getManager() != null) {
	    KitManager.getManager().loadKits();
	}
    }
    
    // Load and save
    private void loadArenas() {
	for (File arenaFile : new File(this.arenaPath).listFiles(this.fileFilter)) {
	    // Version 2.4.0 needs converted arena files
	    // Version 2.6.0 needs converted arena files too
	    ZvP.getConverter().convert(FileType.ARENAFILE, arenaFile, 260.0);
	    
	    Arena arena = new Arena(arenaFile);
	    if (arena.getWorld() != null) {
		this.arenas.add(arena);
		arena.save();
	    }
	    
	}
    }
    
    private void loadLobbys() {
	for (File lobbyFile : new File(this.lobbyPath).listFiles()) {
	    Lobby lobby = new Lobby(lobbyFile);
	    
	    if (lobby.getWorld() != null) {
		this.lobbys.add(lobby);
	    }
	}
    }
    
    // Method to get new UIDs
    public int getNewID(String path) {
	
	File folder = new File(path);
	File[] files = folder.listFiles(this.fileFilter);
	if (files.length == 0) {
	    return 1;
	} else {
	    
	    int[] fileIds = new int[files.length];
	    
	    for (int i = 0; i < fileIds.length; i++) {
		fileIds[i] = Integer.parseInt(files[i].getName().split(".ym")[0]);
	    }
	    
	    Arrays.sort(fileIds);
	    
	    for (int k = 0; k < fileIds.length; k++) {
		if (fileIds[k] != (k + 1)) {
		    return (k + 1);
		}
	    }
	    return fileIds.length + 1;
	}
	
    }
    
    // arena, lobby getter methods
    public Arena[] getArenas() {
	Arena[] array = new Arena[this.arenas.size()];
	
	for (int i = 0; i < this.arenas.size(); i++) {
	    array[i] = this.arenas.get(i);
	}
	Arrays.sort(array);
	return array;
    }
    
    public Lobby[] getLobbys() {
	Lobby[] array = new Lobby[this.lobbys.size()];
	
	for (int i = 0; i < this.lobbys.size(); i++) {
	    array[i] = this.lobbys.get(i);
	}
	Arrays.sort(array);
	return array;
    }
    
    public Arena getArena(int ID) {
	for (Arena a : getArenas()) {
	    if (a.getID() == ID) {
		return a;
	    }
	}
	return null;
    }
    
    public Lobby getLobby(int ID) {
	for (Lobby l : getLobbys()) {
	    if (l.getID() == ID) {
		return l;
	    }
	}
	return null;
    }
    
    // get ZvPPlayer from Player
    public ZvPPlayer getPlayer(Player player) {
	for (Arena a : getArenas()) {
	    for (ZvPPlayer zp : a.getPlayers()) {
		if (zp.getUuid().equals(player.getUniqueId())) {
		    return zp;
		}
	    }
	    if (a.hasPreLobby()) {
		for (ZvPPlayer zp : a.getPreLobby().getPlayers()) {
		    if (zp.getUuid().equals(player.getUniqueId())) {
			return zp;
		    }
		}
	    }
	}
	return null;
    }
    
    // get ZvPPlayer from UUID
    public ZvPPlayer getPlayer(UUID uuid) {
	return getPlayer(Bukkit.getPlayer(uuid));
    }
    
    // Scoreboard manager
    public ScoreboardManager getBoardManager() {
	return this.boardman;
    }
    
    public Scoreboard getNewBoard() {
	return getBoardManager().getNewScoreboard();
    }
    
    // Manage Arenas and Lobbys
    public boolean addArena(Location min, Location max) {
	
	if (min.getWorld().equals(max.getWorld())) {
	    
	    double tempX;
	    double tempY;
	    double tempZ;
	    
	    if (min.getX() > max.getX()) {
		tempX = min.getX();
		min.setX(max.getX());
		max.setX(tempX);
	    }
	    
	    if (min.getY() > max.getY()) {
		tempY = min.getY();
		min.setY(max.getY());
		max.setY(tempY);
	    }
	    
	    if (min.getZ() > max.getZ()) {
		tempZ = min.getZ();
		min.setZ(max.getZ());
		max.setZ(tempZ);
	    }
	    
	    Location tempMax = max.clone();
	    tempMax.setY(min.getY());
	    
	    double dist = min.clone().distance(tempMax);
	    
	    int maxP = ((int) ((Math.ceil(dist + 2)) / 4)) + 1;
	    
	    if (maxP < 3) {
		maxP = 3;
	    }
	    
	    if (maxP > ZvPConfig.getMaxPlayers()) {
		maxP = ZvPConfig.getMaxPlayers();
	    }
	    
	    Arena a = new Arena(getNewID(this.arenaPath), maxP, this.arenaPath, min.clone(), max.clone(), ZvPConfig.getDefaultRounds(), ZvPConfig.getDefaultWaves(), ZvPConfig.getDefaultZombieSpawnRate(), ArenaDifficultyLevel.NORMAL, true);
	    this.arenas.add(a);
	    
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "New Arena added!", true);
	    return true;
	}
	return false;
    }
    
    public void addLobby(Location loc) {
	Lobby l = new Lobby(getNewID(this.lobbyPath), this.lobbyPath, loc.clone());
	this.lobbys.add(l);
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "New Lobby added!", true);
    }
    
    public boolean removeArena(Arena arena) {
	if (arena != null && this.arenas.contains(arena)) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + arena.getID() + " removed!", true);
	    this.arenas.remove(arena);
	    arena.delete();
	    return true;
	} else {
	    return false;
	}
    }
    
    public boolean removeLobby(Lobby lobby) {
	if (lobby != null && this.lobbys.contains(lobby)) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Lobby " + lobby.getID() + " removed!", true);
	    this.lobbys.remove(lobby);
	    lobby.delete();
	    return true;
	} else {
	    return false;
	}
    }
    
    // Manage Players
    public boolean createPlayer(Player player, Arena arena, Lobby lobby) {
	
	if (!arena.isFull() && arena.isOnline()) {
	    if (ZvPConfig.getAllowDuringGameJoin() || !arena.isRunning()) {
		try {
		    new ZvPPlayer(player, arena, lobby);
		    SignManager.getManager().updateSigns(lobby);
		    SignManager.getManager().updateSigns(arena);
		    return true;
		} catch (Exception e) {
		    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while creating Player: " + e.getMessage(), true, false, e);
		    return false;
		}
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }
    
    public boolean removePlayer(ZvPPlayer player) {
	boolean success = player.getArena().removePlayer(player);
	player.reset();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " removed from Game!", true);
	return success;
    }
    
    // Game control
    public void startGame(Arena a, Lobby l) {
	a.start();
	SignManager.getManager().updateSigns(l);
	SignManager.getManager().updateSigns(a);
    }
    
    public void stopGame(Arena a) throws Exception {
	if (a.isRunning()) {
	    a.stop();
	} else {
	    throw new Exception("Arena is not running!");
	}
    }
    
    public void stopGames() {
	for (Arena a : getArenas()) {
	    a.stop();
	}
	Bukkit.getScheduler().cancelTasks(ZvP.getInstance());;
    }
    
    public boolean isInGame(Player player) {
	for (Arena a : getArenas()) {
	    if (a.containsPlayer(player)) {
		return true;
	    }
	    if (a.hasPreLobby()) {
		if (a.getPreLobby().containsPlayer(player)) {
		    return true;
		}
	    }
	}
	return false;
    }
    
}
