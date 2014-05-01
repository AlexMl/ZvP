package me.Aubli.ZvP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.Aubli.ZvP.GameManager.ArenaStatus;
import me.Aubli.ZvP.Sign.SignManager;

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
	
	private ArenaStatus status;
	
	private int maxPlayers;
	private int minPlayers;
	private int maxRounds;
	private int maxWaves;
	private int round;
	private int wave;	
	
	private World arenaWorld;
	private Location minLoc;
	private Location maxLoc;
	
	private boolean online;
	private boolean running;
	private boolean full;
	
	private ArrayList<ZvPPlayer> players;
	
	
	public Arena(int ID, int maxPlayers, String arenaPath, Location min, Location max, int rounds, int waves){
		
		this.arenaID = ID;
		
		this.maxPlayers = maxPlayers;
		this.minPlayers = ((int)Math.ceil(maxPlayers/4))+1;
		
		this.maxRounds = rounds;
		this.maxWaves = waves;
		
		this.arenaWorld = min.getWorld();
		this.minLoc = min.clone();
		this.maxLoc = max.clone();
		
		this.status = ArenaStatus.WAITING;
		
		this.online = true;
		this.running = false;
		this.full = false;
		
		this.round = 0;
		this.wave = 0;
		
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
		this.minPlayers = arenaConfig.getInt("arena.minPlayers");
		
		this.maxRounds = arenaConfig.getInt("arena.rounds");
		this.maxWaves = arenaConfig.getInt("arena.waves");
		
		this.online = arenaConfig.getBoolean("arena.Online");
		this.running = false;
		this.full = false;
		
		if(online){
			this.status = ArenaStatus.WAITING;
		}else{
			this.status = ArenaStatus.STOPED;
		}
		
		this.arenaWorld = Bukkit.getWorld(arenaConfig.getString("arena.Location.world"));		
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
		arenaConfig.set("arena.Online", online);
		
		arenaConfig.set("arena.minPlayers", minPlayers);
		arenaConfig.set("arena.maxPlayers", maxPlayers);
		arenaConfig.set("arena.rounds", maxRounds);
		arenaConfig.set("arena.waves", maxWaves);
		
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
	
	
	public void setStatus(ArenaStatus status){
		this.status = status;
	}	
	
	
	public int getID(){
		return arenaID;
	}
	
	public ArenaStatus getStatus(){
		return status;
	}
	
	public int getMaxPlayers(){
		return maxPlayers;
	}
	
	public int getMinPlayers(){
		return minPlayers;
	}
	
	public int getMaxRounds(){
		return maxRounds;
	}
	
	public int getMaxWaves(){
		return maxWaves;
	}
	
	public int getRound(){
		return round;
	}
	
	public int getWave(){
		return wave;
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
		return online;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public boolean isFull(){
		return full;
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
			
			player.setStartPosition(getMax());
			
			try{
				player.getReady();
			}catch(Exception e){
				e.printStackTrace();
				player.setStartPosition(getMin());
				addPlayer(player);
				return false;
			}
			
			players.add(player);
			
			if(players.size()>=minPlayers){
				start();
			}
			
			if(players.size()==maxPlayers){
				this.full = true;
				if(!isRunning()){
					start();
				}
			}
			
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(ZvPPlayer player){
		if(players.contains(player)){
			this.full = false;
			players.remove(player);
			SignManager.getManager().updateSigns(this);
			return true;
		}
		return false;
	}
	
	public void start(int rounds, int waves){
		this.maxRounds = rounds;
		this.maxWaves = waves;
		start();
	}
	
	public void start(){
		this.running = true;		
		this.round = 0;
		this.wave = 0;
		
		new GameRunnable(this, ZvP.getStartDelay()).runTaskTimer(ZvP.getInstance(), 20L, 1*20L);
	}
	
	public void stop(){		
		for(ZvPPlayer zp : getPlayers()){
			zp.reset();
			removePlayer(zp);
		}
		
		this.running = false;
		this.full = false;
		this.round = 0;
		this.wave = 0;
		
		clearArena();	
		
	}
	
	public void clearArena(){		
		for(Entity e : getEntities()){
			if(e instanceof Zombie || e instanceof Item || e instanceof ExperienceOrb){
				e.remove();
			}			
		}
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Arena){
			Arena a = (Arena)obj;
			if(a.getID() == this.getID()){
				if(a.getMin().equals(this.getMin()) && a.getMax().equals(this.getMax())){
					return true;
				}
			}
		}
		return false;
	}
}
