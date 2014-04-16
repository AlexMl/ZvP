package me.Aubli.ZvP;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
	
public class ZvPPlayer {	

	private Player player;
	private UUID playerUUID;
		
	private Lobby lobby;
	private Arena arena;
	
	private int zombieKills;
	private int deaths;
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;
	
	private int totalXP;
	private GameMode mode;	
	
	private ZvPKit kit;
	
	public ZvPPlayer(Player player, Arena arena, Lobby lobby){
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
		
		arena.addPlayer(this);
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
	
	
	public void setArena(Arena arena){
		this.arena = arena;
	}
	
	public void setLobby(Lobby lobby){
		this.lobby = lobby;
	}
	
	public void setKills(int kills){
		this.zombieKills = kills;
	}
	
	public void setDeaths(int deaths){
		this.deaths = deaths;
	}
	
	public void setKit(ZvPKit kit){
		this.kit = kit;
	}
	
	
	
	public void reset(){
		
		player.getInventory().clear();
		
		player.teleport(lobby.getLocation());
		player.setVelocity(new Vector(0, 0, 0));
		
		player.setHealth(20D);
		player.setFoodLevel(20);
		
		player.setGameMode(mode);
		player.setTotalExperience(totalXP);
		
		player.getInventory().setArmorContents(armorContents);		
		player.getInventory().setContents(contents);
		
	}
	
	
	
	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public boolean equals(Object zvpPlayer){		
		if(zvpPlayer instanceof ZvPPlayer){
			ZvPPlayer zp = (ZvPPlayer)zvpPlayer;			
			if(zp.getUuid().equals(zp.getUuid())){
				return true;
			}
		}
		return false;		
	}
	
	
}
