package me.Aubli.ZvP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class Arena {

	private File arenaFile;
	private FileConfiguration arenaConfig;
	
	private int arenaID;
	
	private int maxPlayers;
	
	private World arenaWorld;
	private Location minLoc;
	private Location maxLoc;
	
	private boolean isOnline;
	private boolean running;
	
	private ArrayList<ZvPPlayer> players;
	
	
	public Arena(int ID, int maxPlayers, String arenaPath, Location min, Location max){
		
		this.arenaID = ID;
		
		this.maxPlayers = maxPlayers;
		
		this.arenaWorld = min.getWorld();
		this.minLoc = min.clone();
		this.maxLoc = max.clone();
		
		isOnline = true;
		running = false;
		
		arenaFile = new File(arenaPath + "/" + ID + ".yml");
		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
		
		players = new ArrayList<ZvPPlayer>();
		
		try {
			arenaFile.createNewFile();
			save();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public Arena(File arenaFile){
		this.arenaFile = arenaFile;
		this.arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
		
		this.arenaID = arenaConfig.getInt("arena.ID");
		this.maxPlayers = arenaConfig.getInt("arena.maxPlayers");
		this.arenaWorld = Bukkit.getWorld(arenaConfig.getString("arena.Location.world"));
		
		this.isOnline = arenaConfig.getBoolean("arena.Online");
		this.running = false;
		
		this.minLoc = new Location(arenaWorld, 
				arenaConfig.getInt("arena.Location.min.X"), 
				arenaConfig.getInt("arena.Location.min.Y"),
				arenaConfig.getInt("arena.Location.min.Z"));
		this.maxLoc = new Location(arenaWorld, 
				arenaConfig.getInt("arena.Location.max.X"), 
				arenaConfig.getInt("arena.Location.max.Y"),
				arenaConfig.getInt("arena.Location.max.Z"));
		
		this.players = new ArrayList<ZvPPlayer>();
	}
	
	
	void save() throws IOException{	
		arenaConfig.set("arena.ID", arenaID);
		arenaConfig.set("arena.maxPlayers", maxPlayers);
		arenaConfig.set("arena.Online", isOnline);
		
		arenaConfig.set("arena.Location.world", arenaWorld.getName());
		arenaConfig.set("arena.Location.min.X", minLoc.getBlockX());
		arenaConfig.set("arena.Location.min.Y", minLoc.getBlockY());
		arenaConfig.set("arena.Location.min.Z", minLoc.getBlockZ());
		
		arenaConfig.set("arena.Location.max.X", maxLoc.getBlockX());
		arenaConfig.set("arena.Location.max.Y", maxLoc.getBlockY());
		arenaConfig.set("arena.Location.max.Z", maxLoc.getBlockZ());
			
		arenaConfig.save(arenaFile);			
	}
	
	void delete(){
		this.arenaFile.delete();
	}
	
	
	public int getID(){
		return arenaID;
	}
	
	public int getMaxPlayers(){
		return maxPlayers;
	}
	
	public World getWorld(){
		return arenaWorld;
	}
	
	public Location getMin(){
		return minLoc;
	}
	
	public Location getMax(){
		return maxLoc;
	}	
	
	public ZvPPlayer[] getPlayers(){
		
		ZvPPlayer[] parray = new ZvPPlayer[players.size()];
		
		for(int i=0;i<players.size();i++){
			parray[i] = players.get(i);
		}
		return parray;
	}
	
	private Entity[] getEntities(){
		List<Entity> eList = new ArrayList<Entity>();
		Entity[] entities;
		
		Chunk minC = minLoc.getChunk();
		Chunk maxC = maxLoc.getChunk();
		
		int minX = minC.getX();
		int minZ = minC.getZ();
		int maxX = maxC.getX();
		int maxZ = maxC.getZ();		
	
		for(int x = minX;x<=maxX;x++){
			for(int z = minZ;z<=+maxZ;z++){
				Chunk entityChunk = getWorld().getChunkAt(x, z);				
				for(Entity e : entityChunk.getEntities()){
					eList.add(e);
				}
			}
		}
		
		entities = new Entity[eList.size()];
		for(int i=0;i<eList.size();i++){
			entities[i] = eList.get(i);
		}
		return entities;
	}
	
	public int getZombies(){
		int zombies = 0;
		for(Entity e : getEntities()){
			if(e instanceof Zombie){
				zombies++;
			}
		}
		return zombies;
	}
	
	
	public boolean isOnline(){
		return isOnline;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	
	public boolean containsPlayer(Player player){		
		for(ZvPPlayer zp : getPlayers()){
			if(zp.getUuid() == player.getUniqueId()){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean addPlayer(ZvPPlayer player){
		if(!players.contains(player)){
			players.add(player);
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(ZvPPlayer player){
		if(players.contains(player)){
			players.remove(player);
			return true;
		}
		return false;
	}
	
	public void start(){
		this.running = true;
		
	}
	
	public void stop(){		
		for(ZvPPlayer zp : getPlayers()){
			zp.reset();
			removePlayer(zp);
		}
		clearArena();
		this.running = false;
	}
	
	public void clearArena(){		
		for(Entity e : getEntities()){
			if(e instanceof Zombie || e instanceof Item || e instanceof ExperienceOrb){
				e.remove();
			}			
		}
	}
}
