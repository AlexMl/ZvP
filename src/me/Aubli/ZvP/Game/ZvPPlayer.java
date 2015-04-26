package me.Aubli.ZvP.Game;

import java.text.DecimalFormat;
import java.util.UUID;

import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.ArenaScore.ScoreType;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KitManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;


public class ZvPPlayer {
    
    private Player player;
    private UUID playerUUID;
    
    private Lobby lobby;
    private Arena arena;
    
    private Location startPosition;
    
    private boolean voted;
    private boolean canceled;
    private boolean spawnProtected;
    
    private int zombieKills;
    private int deaths;
    
    private ItemStack[] contents;
    private ItemStack[] armorContents;
    
    private int totalXP;
    private GameMode mode;
    
    private Scoreboard board;
    
    private IZvPKit kit;
    
    public ZvPPlayer(Player player, Arena arena, Lobby lobby) throws Exception {
	this.player = player;
	this.playerUUID = player.getUniqueId();
	
	this.arena = arena;
	this.lobby = lobby;
	
	this.voted = false;
	this.canceled = false;
	this.spawnProtected = false;
	
	this.zombieKills = 0;
	this.deaths = 0;
	
	this.contents = player.getInventory().getContents();
	this.armorContents = player.getInventory().getArmorContents();
	
	this.totalXP = player.getTotalExperience();
	this.mode = player.getGameMode();
	
	this.startPosition = null;
	this.kit = null;
	
	if (KitManager.getManager().isEnabled()) {
	    KitManager.getManager().openSelectKitGUI(this);
	} else {
	    this.kit = KitManager.getManager().getKit("No Kit");
	}
	
	arena.addPlayer(this);
    }
    
    public UUID getUuid() {
	return this.playerUUID;
    }
    
    public Player getPlayer() {
	return this.player;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public Lobby getLobby() {
	return this.lobby;
    }
    
    public Location getLocation() {
	return this.player.getLocation();
    }
    
    public Location getStartLocation() {
	return this.startPosition;
    }
    
    public String getName() {
	return this.player.getName();
    }
    
    public ItemStack[] getPlayerContents() {
	return this.contents;
    }
    
    public ItemStack[] getArmorContents() {
	return this.armorContents;
    }
    
    public int getKills() {
	return this.zombieKills;
    }
    
    public int getDeaths() {
	return this.deaths;
    }
    
    public IZvPKit getKit() {
	return this.kit;
    }
    
    public Scoreboard getBoard() {
	return this.board;
    }
    
    public void setArena(Arena arena) {
	this.arena = arena;
    }
    
    public void setLobby(Lobby lobby) {
	this.lobby = lobby;
    }
    
    private void setKills(int kills) {
	this.zombieKills = kills;
	getArena().updatePlayerBoards();
    }
    
    private void setDeaths(int deaths) {
	this.deaths = deaths;
	getArena().updatePlayerBoards();
    }
    
    public void setKit(IZvPKit kit) {
	this.kit = kit;
    }
    
    public void setXPLevel(int level) {
	getPlayer().setLevel(level);
    }
    
    public void setStartPosition(Location position) throws Exception {
	if (position != null) {
	    this.startPosition = position.clone();
	} else {
	    throw new Exception("start location is null!");
	}
    }
    
    public void setScoreboard(Scoreboard board) {
	this.board = board;
    }
    
    private void setPlayerBoard() {
	getPlayer().setScoreboard(getBoard());
    }
    
    public void setVoted(boolean vote) {
	this.voted = vote;
    }
    
    public void setCanceled(boolean cancel) {
	this.canceled = cancel;
    }
    
    public void setSpawnProtected(boolean protection) {
	this.spawnProtected = protection;
    }
    
    public boolean hasKit() {
	return getKit() != null;
    }
    
    public boolean hasVoted() {
	return this.voted;
    }
    
    public boolean hasCanceled() {
	return this.canceled;
    }
    
    public boolean hasProtection() {
	return this.spawnProtected;
    }
    
    public void sendMessage(String message) {
	getPlayer().sendMessage(message);
    }
    
    public void openInventory(Inventory inv) {
	getPlayer().closeInventory();
	getPlayer().openInventory(inv);
    }
    
    public void addKill() {
	setKills(getKills() + 1);
	getArena().getScore().addScore(this, ZvPConfig.getZombieFund(), ScoreType.ZOMBIE_SCORE);
    }
    
    public void die() {
	setDeaths(getDeaths() + 1);
	getArena().getScore().subtractScore(this, ZvPConfig.getDeathFee(), ScoreType.ZOMBIE_SCORE);
    }
    
    public void removeScoreboard() {
	getBoard().clearSlot(DisplaySlot.SIDEBAR);
	getBoard().clearSlot(DisplaySlot.BELOW_NAME);
	getBoard().clearSlot(DisplaySlot.PLAYER_LIST);
	setPlayerBoard();
    }
    
    @SuppressWarnings("deprecation")
    public void updateScoreboard() {
	
	if (getBoard() != null) {
	    
	    for (String e : getBoard().getEntries()) {
		getBoard().resetScores(e);
	    }
	    
	    Objective obj = getBoard().getObjective("zvp-main");
	    if (obj == null) {
		obj = getBoard().registerNewObjective("zvp-main", "custom");
	    }
	    obj.setDisplayName(ChatColor.GREEN + "Arena: " + ChatColor.GOLD + getArena().getID());
	    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	    
	    obj.getScore(ChatColor.RESET + " ").setScore(15);
	    obj.getScore(ChatColor.BLUE + "Players: " + ChatColor.RED + getArena().getPlayers().length).setScore(14);
	    obj.getScore(ChatColor.GRAY + "R: " + ChatColor.AQUA + getArena().getRound() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + getArena().getMaxRounds()).setScore(13);
	    obj.getScore(ChatColor.GRAY + "W: " + ChatColor.AQUA + getArena().getWave() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + getArena().getMaxWaves()).setScore(12);
	    
	    obj.getScore(ChatColor.WHITE + "-------------").setScore(11);
	    obj.getScore(ChatColor.GREEN + "Money: " + new DecimalFormat("#0.00").format(getArena().getScore().getScore(this))).setScore(10);
	    
	    obj.getScore("-------------").setScore(9);
	    obj.getScore(ChatColor.RED + "Left:  " + ChatColor.GREEN + getArena().getLivingZombieAmount()).setScore(8);
	    obj.getScore(ChatColor.RED + "Killed: " + ChatColor.GREEN + getArena().getKilledZombies()).setScore(7);
	    
	    obj.getScore(ChatColor.RESET + "-------------").setScore(6);
	    obj.getScore(ChatColor.GOLD + "Kills: " + getKills()).setScore(5);
	    obj.getScore(ChatColor.DARK_PURPLE + "Deaths: " + getDeaths()).setScore(4);
	    
	    Objective voteObj = getBoard().getObjective("zvp-vote");
	    if (voteObj == null) {
		voteObj = getBoard().registerNewObjective("zvp-vote", "custom");
	    }
	    voteObj.setDisplayName(ChatColor.GOLD + "Voted");
	    voteObj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	    
	    Objective belowObj = getBoard().getObjective("zvp-kills");
	    if (belowObj == null) {
		belowObj = getBoard().registerNewObjective("zvp-kills", "custom");
	    }
	    belowObj.setDisplayName(ChatColor.GOLD + "Kills");
	    belowObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
	    
	    for (ZvPPlayer p : this.arena.getPlayers()) {
		belowObj.getScore(Bukkit.getOfflinePlayer(p.getUuid())).setScore(p.getKills());
		voteObj.getScore(Bukkit.getOfflinePlayer(p.getUuid())).setScore(p.hasVoted() ? 1 : 0);
	    }
	    setPlayerBoard();
	}
    }
    
    @SuppressWarnings("deprecation")
    public void getReady() {
	
	if (KitManager.getManager().isEnabled()) {
	    this.player.getInventory().clear();
	    this.player.getInventory().setHelmet(null);
	    this.player.getInventory().setChestplate(null);
	    this.player.getInventory().setLeggings(null);
	    this.player.getInventory().setBoots(null);
	    
	    this.player.getInventory().setContents(getKit().getContents());
	    this.player.updateInventory();
	}
	
	this.player.setTotalExperience(0);
	this.player.setExp(0F);
	this.player.setLevel(0);
	this.player.setGameMode(GameMode.SURVIVAL);
	this.player.resetPlayerTime();
	this.player.resetPlayerWeather();
	
	this.player.setHealth(20D);
	this.player.setFoodLevel(20);
	this.player.resetMaxHealth();
	
	this.player.setFlying(false);
	this.player.setWalkSpeed((float) 0.2);
	this.player.setFlySpeed((float) 0.2);
	
	this.player.teleport(getStartLocation(), TeleportCause.PLUGIN);
    }
    
    @SuppressWarnings("deprecation")
    public void reset() {
	
	if (getBoard() != null) {
	    removeScoreboard();
	}
	
	this.player.getInventory().clear();
	
	this.player.teleport(this.lobby.getLocation());
	this.player.setVelocity(new Vector(0, 0, 0));
	
	this.player.setHealth(20D);
	this.player.setFoodLevel(20);
	
	this.player.setGameMode(this.mode);
	this.player.setTotalExperience(this.totalXP);
	
	this.player.getInventory().setArmorContents(this.armorContents);
	this.player.getInventory().setContents(this.contents);
	
	for (PotionEffectType effect : PotionEffectType.values()) {
	    if (effect != null) {
		this.player.removePotionEffect(effect);
	    }
	}
	
	this.player.updateInventory();
    }
    
    @Override
    public String toString() {
	return getName();
    }
    
    @Override
    public boolean equals(Object zvpPlayer) {
	if (zvpPlayer instanceof ZvPPlayer) {
	    ZvPPlayer zp = (ZvPPlayer) zvpPlayer;
	    if (this.getUuid().equals(zp.getUuid())) {
		return true;
	    }
	}
	return false;
    }
}
