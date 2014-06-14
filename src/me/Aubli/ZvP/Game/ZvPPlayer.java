package me.Aubli.ZvP.Game;

import java.util.UUID;

import me.Aubli.ZvP.Kits.ZvPKit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
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
	
	private int zombieKills;
	private int deaths;
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;
	
	private int totalXP;
	private GameMode mode;	
	
	private Scoreboard board;
	
	private ZvPKit kit;
	
	public ZvPPlayer(Player player, Arena arena, Lobby lobby) throws Exception{
		this.player = player;
		this.playerUUID = player.getUniqueId();
		
		this.arena = arena;
		this.lobby = lobby;
		
		this.zombieKills = 0;
		this.deaths = 0;
		
		this.contents = player.getInventory().getContents();
		this.armorContents = player.getInventory().getArmorContents();
		
		this.totalXP = player.getTotalExperience();
		this.mode = player.getGameMode();
		
		this.startPosition = null;
		
		if(arena.addPlayer(this)==false){
			throw new Exception("Player already joined!");
		}
	}
		
	
	public UUID getUuid(){
		return playerUUID;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Arena getArena(){
		return arena;
	}
	
	public Lobby getLobby(){
		return lobby;
	}
	
	public Location getLocation(){
		return player.getLocation();
	}
	
	public Location getStartLocation(){
		return startPosition;
	}
	
	public String getName(){
		return player.getName();
	}
	
	public ItemStack[] getPlayerContents(){
		return contents;
	}
	
	public ItemStack[] getArmorContents(){
		return armorContents;
	}	
	
	public int getKills(){
		return zombieKills;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public ZvPKit getKit(){
		return kit;
	}
	
	public Scoreboard getBoard() {
		return board;
	}
	
	
	public void setArena(Arena arena){
		this.arena = arena;
	}
	
	public void setLobby(Lobby lobby){
		this.lobby = lobby;
	}
	
	private void setKills(int kills){
		this.zombieKills = kills;
		getArena().updatePlayerBoards();
	}
	
	private void setDeaths(int deaths){
		this.deaths = deaths;
		getArena().updatePlayerBoards();
	}
	
	public void setKit(ZvPKit kit){
		this.kit = kit;
	}
	
	public void setXPLevel(int level) {
		getPlayer().setLevel(level);
	}
	
	public void setStartPosition(Location position){
		this.startPosition = position.clone();
	}
	
	public void setScoreboard(Scoreboard board) {
		this.board = board;
	}
	
	private void setPlayerBoard() {
		getPlayer().setScoreboard(getBoard());
	}
	
	public void sendMessage(String message) {
		getPlayer().sendMessage(message);
	}
	
	
	public void addKill() {
		setKills(getKills() + 1);
	}
	
	public void die() {
		setDeaths(getDeaths() + 1);
	}
	
	
	public void removeScoreboard() {
		getBoard().clearSlot(DisplaySlot.SIDEBAR);
		getBoard().clearSlot(DisplaySlot.BELOW_NAME);
		getBoard().clearSlot(DisplaySlot.PLAYER_LIST);
		setPlayerBoard();
	}
	
	@SuppressWarnings("deprecation")
	public void updateScoreboard() {
		
		for(String e : getBoard().getEntries()) {
			getBoard().resetScores(e);
		}
		
		Objective obj = getBoard().getObjective("zvp-main");
		if(obj==null) {
			obj = getBoard().registerNewObjective("zvp-main", "custom");				
		}
		obj.setDisplayName(ChatColor.GREEN + "Arena: " + ChatColor.GOLD + getArena().getID());
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);		
		
		obj.getScore(Bukkit.getOfflinePlayer("")).setScore(15);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Players: " + ChatColor.RED + getArena().getPlayers().length)).setScore(14);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "R: " + ChatColor.AQUA + getArena().getRound() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + getArena().getMaxRounds())).setScore(13);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "W: " + ChatColor.AQUA + getArena().getWave() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + getArena().getMaxWaves())).setScore(12);
		obj.getScore(Bukkit.getOfflinePlayer("")).setScore(11);		
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Kills: " + getKills())).setScore(10);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_PURPLE + "Deaths: " + getDeaths())).setScore(9);
				
		
		Objective belowObj = getBoard().getObjective("zvp-kills");		
		if(belowObj==null) {
			belowObj = getBoard().registerNewObjective("zvp-kills", "custom");
		}		
		belowObj.setDisplayName(ChatColor.GOLD + "Kills");
		belowObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		for(ZvPPlayer p : arena.getPlayers()) {
			belowObj.getScore(Bukkit.getOfflinePlayer(p.getUuid())).setScore(p.getKills());
		}
		setPlayerBoard();		
	}
	
	
	@SuppressWarnings("deprecation")
	public void getReady() throws Exception{
		
		if(getStartLocation()!=null){
			player.getInventory().clear();
			player.getInventory().setHelmet(null);
			player.getInventory().setChestplate(null);
			player.getInventory().setLeggings(null);
			player.getInventory().setBoots(null);
			
			player.setTotalExperience(0);
			player.setExp(0F);
			player.setLevel(0);
			player.setGameMode(GameMode.SURVIVAL);
			player.resetPlayerTime();
			player.resetPlayerWeather();
			
			player.setHealth(20D);
			player.setFoodLevel(20);
			player.resetMaxHealth();
			
			player.setFlying(false);
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.2);
			
			player.updateInventory();
			
			player.teleport(getStartLocation(), TeleportCause.PLUGIN);			
		}else{
			throw new Exception("Startlocation is null!");
		}		
	}
	
	@SuppressWarnings("deprecation")
	public void reset(){		
		
		if(getBoard()!=null) {
			removeScoreboard();
		}
		
		player.getInventory().clear();
		
		player.teleport(lobby.getLocation());
		player.setVelocity(new Vector(0, 0, 0));
		
		player.setHealth(20D);
		player.setFoodLevel(20);
		
		player.setGameMode(mode);
		player.setTotalExperience(totalXP);
		
		player.getInventory().setArmorContents(armorContents);		
		player.getInventory().setContents(contents);		
		
		player.removePotionEffect(PotionEffectType.HEAL);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.REGENERATION);
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		
		player.updateInventory();
	}
	

	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public boolean equals(Object zvpPlayer){		
		if(zvpPlayer instanceof ZvPPlayer){
			ZvPPlayer zp = (ZvPPlayer)zvpPlayer;
			if(this.getUuid().equals(zp.getUuid())){
				return true;
			}
		}
		return false;		
	}	
}
