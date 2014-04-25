package me.Aubli.ZvP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import me.Aubli.ZvP.Sign.SignManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameManager {
	
	public enum ArenaStatus{
		RUNNING,
		WAITING,
		STOPED,
		;
	}
	
	private static GameManager manager;
	private ZvP plugin;
	
	private String arenaPath;
	private String lobbyPath;
	
	private ArrayList<Lobby> lobbys;
	private ArrayList<Arena> arenas;
	
	
	public GameManager(){
		manager = this;
		plugin = ZvP.getInstance();
		
		arenaPath = plugin.getDataFolder().getPath() + "/Arenas";
		lobbyPath = plugin.getDataFolder().getPath() + "/Lobbys";
		
		loadConfig();
	}
	
	public static GameManager getManager(){
		return manager;
	}
	
	public void loadConfig(){
		
		if(!new File(arenaPath).exists() || !new File(lobbyPath).exists()){
			new File(arenaPath).mkdirs();
			new File(lobbyPath).mkdirs();
		}
		
		lobbys = new ArrayList<Lobby>();
		arenas = new ArrayList<Arena>();
		
		loadArenas();
		loadLobbys();
		
		if(SignManager.getManager()!=null){
			SignManager.getManager().reloadConfig();
		}
	}
	
	public void saveConfig(){
		try{
			saveArenas();
			saveLobbys();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	private void loadArenas(){		
		for(int i=0;i<new File(arenaPath).listFiles().length;i++){			
			Arena arena = new Arena(new File(arenaPath).listFiles()[i]);
			
			if(arena.getWorld()!=null){
				arenas.add(arena);
			}			
		}
	}
	
	private void loadLobbys(){
		for(int i=0;i<new File(lobbyPath).listFiles().length;i++){
			Lobby lobby = new Lobby(new File(lobbyPath).listFiles()[i]);
			
			if(lobby.getWorld()!=null){
				lobbys.add(lobby);
			}
		}
	}
	
	private void saveArenas() throws IOException{
		for(int i=0;i<getArenas().length;i++){
			getArenas()[i].save();
		}
	}
	
	private void saveLobbys() throws IOException{
		for(int i=0;i<getLobbys().length;i++){
			getLobbys()[i].save();
		}
	}
	
	
	public int getNewID(String path){
		
		File folder = new File(path);
		if(folder.listFiles().length==0){
			return 1;
		}else{
			
			int[] fileIds = new int[folder.listFiles().length];
			
			for(int i=0;i<fileIds.length;i++){
				fileIds[i] = Integer.parseInt(folder.listFiles()[i].getName().split(".ym")[0]);
			}
			
			Arrays.sort(fileIds);
			
			for(int k=0;k<fileIds.length;k++){
				if(fileIds[k]!=(k+1)){
					return (k+1);
				}
			}
			return fileIds.length+1;
		}
		
	}
	
	
	public Arena[] getArenas(){
		Arena[] array = new Arena[arenas.size()];
		
		for(int i=0;i<arenas.size();i++){
			array[i] = arenas.get(i);
		}
		return array;
	}
	
	public Lobby[] getLobbys(){
		Lobby[] array = new Lobby[lobbys.size()];
		
		for(int i=0;i<lobbys.size();i++){
			array[i] = lobbys.get(i);
		}
		return array;
	}

	public Arena getArena(int ID){
		for(Arena a : getArenas()){
			if(a.getID()==ID){
				return a;
			}
		}
		return null;
	}
	
	public Lobby getLobby(int ID){
		for(Lobby l : getLobbys()){
			if(l.getID()==ID){
				return l;
			}
		}
		return null;
	}
	
	public ZvPPlayer getPlayer(Player player){
		for(Arena a : getArenas()){
			for(ZvPPlayer zp : a.getPlayers()){
				if(zp.getUuid().equals(player.getUniqueId())){
					return zp;
				}
			}
		}
		return null;
	}
	
	
	public boolean addArena(Location min, Location max){		
		
		if(min.getWorld().equals(max.getWorld())){
			
			double tempX;
			double tempY;		
			double tempZ;
			
			if(min.getX()>max.getX()){
				tempX = min.getX();
				min.setX(max.getX());
				max.setX(tempX);
			}
			
			if(min.getY()>max.getY()){
				tempY = min.getY();
				min.setY(max.getY());
				max.setY(tempY);
			}
			
			if(min.getZ()>max.getZ()){
				tempZ = min.getZ();
				min.setZ(max.getZ());
				max.setZ(tempZ);
			}			
			
			Location tempMax = max.clone();
			tempMax.setY(min.getY());
			
			double dist = min.clone().distance(tempMax);
			Bukkit.broadcastMessage("Distance: " + dist);
			
			int mP = ((int)((Math.ceil(dist+2))/4))+1;
			Bukkit.broadcastMessage("maxP: " + mP);
			
			if(mP<3){
				mP = 3;
			}
			
			if(mP>ZvP.getMaxPlayers()){
				mP = ZvP.getMaxPlayers();
			}
			
			Arena a = new Arena(getNewID(arenaPath), mP, arenaPath, min.clone(), max.clone(), ZvP.getDefaultRounds(), ZvP.getDefaultWaves());
			arenas.add(a);
			return true;
		}
		return false;
	}
	
	public void addLobby(Location loc){
		Lobby l = new Lobby(getNewID(lobbyPath), lobbyPath, loc.clone());
		lobbys.add(l);
	}
	
	
	public void removeArena(Arena arena){
		arenas.remove(arena);
		arena.delete();
	}
	
	public void removeLobby(Lobby lobby){
		lobbys.remove(lobby);
		lobby.delete();
	}
	
	
	public boolean createPlayer(Player player, Arena arena, Lobby lobby){
		
		if(!arena.isFull() && (arena.getStatus()==ArenaStatus.WAITING)){
			new ZvPPlayer(player, arena, lobby);
			lobby.updateSigns();
			return true;
		}else{
			return false;
		}		
	}
	
	public boolean removePlayer(ZvPPlayer player){
		boolean success = player.getArena().removePlayer(player);
		player.reset();
		return success;
	}
	
	
	public void startGame(Arena a, Lobby l, int rounds, int waves){
		
		a.start(rounds, waves);
		l.updateSigns();
		
	}
	
	public void stopGame(Arena a){
		a.stop();
	}
	
	public void stopGames(){
		for(Arena a : getArenas()){
			a.stop();
		}
	}
	
	public boolean isInGame(Player player){		
		for(Arena a : getArenas()){
			return a.containsPlayer(player);
		}
		return false;
	}
	
	
}