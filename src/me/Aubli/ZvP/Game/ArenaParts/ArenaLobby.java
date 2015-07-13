package me.Aubli.ZvP.Game.ArenaParts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;


public class ArenaLobby {
    
    private Location centerLoc;
    
    private List<Location> locations;
    
    private Arena arena;
    
    private Random rand;
    
    private BukkitRunnable task;
    
    private boolean joinProcessRunning;
    
    private ArrayList<ZvPPlayer> playerList;
    
    public ArenaLobby(Arena arena, Location center, List<Location> locations, Random arenaRandom) throws Exception {
	
	if (arena == null) {
	    throw new NullPointerException("Arena can not be null!");
	}
	if (center == null || (center.getBlockX() == 0 && center.getBlockY() == 0 && center.getBlockZ() == 0)) {
	    throw new IllegalArgumentException("Center Location is not valid!");
	}
	if (center.getWorld() == null) {
	    throw new IllegalArgumentException("Center Location is not available! The World is not loaded!");
	}
	
	this.arena = arena;
	this.centerLoc = center.clone();
	this.locations = locations != null ? locations : new ArrayList<Location>();
	
	this.playerList = new ArrayList<ZvPPlayer>();
	this.rand = arenaRandom;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public World getWorld() {
	return getCenterLoc().getWorld();
    }
    
    public Location getCenterLoc() {
	return this.centerLoc.clone();
    }
    
    public List<Location> getLocationList() {
	return this.locations;
    }
    
    public Location getRandomLocation() {
	return (getLocationList() != null && !getLocationList().isEmpty()) ? getLocationList().get(this.rand.nextInt(getLocationList().size())).clone() : getCenterLoc();
    }
    
    public ZvPPlayer[] getPlayers() {
	ZvPPlayer[] parray = new ZvPPlayer[this.playerList.size()];
	
	for (int i = 0; i < this.playerList.size(); i++) {
	    parray[i] = this.playerList.get(i);
	}
	return parray;
    }
    
    public boolean containsPlayer(Player player) {
	for (ZvPPlayer zp : this.playerList) {
	    if (zp.getUuid() == player.getUniqueId()) {
		return true;
	    }
	}
	return false;
    }
    
    public void setPlayerLevel(int level) {
	for (ZvPPlayer p : this.playerList) {
	    p.setXPLevel(level);
	}
    }
    
    public void sendMessage(Object message) {
	for (ZvPPlayer p : this.playerList) {
	    p.sendMessage(message.toString());
	}
    }
    
    public void addSpawnLocation(Location location) {
	this.locations.add(location.clone());
	getArena().saveArenaLobby(this);
    }
    
    public void removePlayer(ZvPPlayer player) {
	this.playerList.remove(player);
    }
    
    public void addPlayerToLobby(final ZvPPlayer player) {
	
	Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
	    
	    @Override
	    public void run() {
		player.openKitSelectGUI();
		addPlayer(player);
	    }
	}, (int) Math.ceil(this.arena.getConfig().getJoinTime() / 4) * 20L);
	
	player.getPlayer().teleport(getRandomLocation(), TeleportCause.PLUGIN);
	player.getPlayer().setGameMode(GameMode.SURVIVAL);
	player.getArena().addPreLobbyPlayer(player);
	player.setSpawnProtected(true); // INFO Messy workaround need cleaner solution
	this.joinProcessRunning = true;
	
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " joined PreLobby " + this.arena.getID(), true, true);
    }
    
    private void addPlayer(final ZvPPlayer player) {
	ZvP.getPluginLogger().log(this.getClass(), Level.FINER, "Player " + player.getName() + " inGame: " + GameManager.getManager().isInGame(player.getPlayer()) + ", hasCanceled: " + player.hasCanceled() + " , Kit: " + player.hasKit(), true);
	this.joinProcessRunning = true;
	
	if (player.hasCanceled()) {
	    this.joinProcessRunning = false;
	    removePlayer(player);
	    return;
	}
	
	if (!player.hasKit() && !player.hasCanceled()) {
	    
	    if (!this.playerList.contains(player)) {
		this.playerList.add(player);
	    }
	    
	    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		
		@Override
		public void run() {
		    addPlayer(player);
		}
	    }, 20L);
	    return;
	    
	} else if (player.hasKit() && this.playerList.contains(player)) {
	    removePlayer(player);
	}
	
	if (!this.playerList.contains(player) && !player.hasCanceled()) {
	    player.getArena().removePreLobbyPlayer(player);
	    
	    player.sendMessage(MessageManager.getFormatedMessage(game.joined, this.arena.getID()));
	    sendMessage(MessageManager.getFormatedMessage(game.player_joined, player.getName()));
	    getArena().sendMessage(MessageManager.getFormatedMessage(game.player_joined, player.getName()));
	    
	    this.joinProcessRunning = false;
	    this.playerList.add(player);
	    startPreLobbyTask(player);
	}
	return;
    }
    
    private void startPreLobbyTask(ZvPPlayer player) {
	
	if (this.task == null) {
	    this.task = new BukkitRunnable() {
		
		private int seconds = 0;
		private boolean countDownOver = false;
		
		@Override
		public void run() {
		    
		    if (getPlayers().length >= getArena().getConfig().getMinPlayers() || this.countDownOver) { // Dont count down if the minimum is not reached
			if (this.seconds < getArena().getConfig().getJoinTime() * 20) {
			    setPlayerLevel((ArenaLobby.this.arena.getConfig().getJoinTime() * 20 - this.seconds) / 20);
			} else if (this.seconds > getArena().getConfig().getJoinTime() && getPlayers().length > 0) {
			    ZvP.getPluginLogger().log(ArenaLobby.class, Level.FINE, "PreLobby Task is over! Adding players to Arena " + getArena().getID() + ".", true);
			    this.countDownOver = true;
			    
			    for (int i = 0; i < getPlayers().length;) {
				ZvPPlayer player = getPlayers()[i];
				boolean success = getArena().addPlayer(player);
				removePlayer(player);
				ZvP.getPluginLogger().log(ArenaLobby.class, Level.FINE, "Added player " + player.getName() + "! Arena returned: " + (success ? "success" : "failure").toUpperCase() + "!", true);
				return;
			    }
			    
			} else if (this.seconds > getArena().getConfig().getJoinTime() && ArenaLobby.this.playerList.size() == 0) {
			    ArenaLobby.this.playerList.clear();
			    this.cancel();
			}
			if (!ArenaLobby.this.joinProcessRunning) {
			    this.seconds++;
			}
		    }
		}
	    };
	    this.task.runTaskTimer(ZvP.getInstance(), 0L, 1L);
	} else if (getArena().getPlayers().length >= getArena().getConfig().getMinPlayers() && getArena().isRunning()) { // Player joined PreLobby and Arena is already running
	
	    boolean success = getArena().addPlayer(player);
	    removePlayer(player);
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Added player " + player.getName() + " to running game! Arena returned: " + (success ? "success" : "failure").toUpperCase() + "!", true);
	}
    }
    
    public void stopPreLobbyTask() {
	
	if (this.task != null) {
	    this.task.cancel();
	    this.task = null;
	}
	
	if (!this.playerList.isEmpty()) {
	    for (ZvPPlayer player : getPlayers()) {
		player.reset();
	    }
	}
	
	this.playerList.clear();
	this.joinProcessRunning = false;
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "PreLobby (Arena:" + getArena().getID() + ") stopt tasks", false, true);
    }
}
