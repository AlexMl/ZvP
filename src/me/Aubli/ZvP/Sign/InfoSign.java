package me.Aubli.ZvP.Sign;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import me.Aubli.ZvP.Arena;
import me.Aubli.ZvP.GameManager;
import me.Aubli.ZvP.Lobby;
import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class InfoSign {
	
	private int ID;
	
	private File signFile;
	private FileConfiguration signConfig;
	
	private Location signLoc;	
	private Sign sign;
	
	private SignType type;
	
	private Arena arena;
	private Lobby lobby;
	
	public InfoSign(Location signLoc, int ID, String path, Arena arena, Lobby lobby) throws Exception{
		this.ID = ID;
		this.signLoc = signLoc.clone();
		
		this.signFile = new File(path + "/" + ID + ".yml");
		this.signConfig = YamlConfiguration.loadConfiguration(signFile);
		
		this.type = SignType.INFO_SIGN;
		
		this.arena = arena;
		this.lobby = lobby;
		this.lobby.addSign(this);
		
		try{
			signFile.createNewFile();
			signConfig.set("sign.ID", ID);
			signConfig.set("sign.Arena", arena.getID());
			signConfig.set("sign.Lobby", lobby.getID());
			
			signConfig.set("sign.Location.world", signLoc.getWorld().getUID().toString());
			signConfig.set("sign.Location.X", signLoc.getBlockX());
			signConfig.set("sign.Location.Y", signLoc.getBlockY());
			signConfig.set("sign.Location.Z", signLoc.getBlockZ());
			
			signConfig.save(signFile);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		if(signLoc.getBlock().getState() instanceof Sign){
			sign = (Sign)signLoc.getBlock().getState();
		}else{
			delete();
			throw new Exception("Location is not a Sign!");
		}
		update();
	}
	
	public InfoSign(File signFile){
		this.signFile = signFile;
		this.signConfig = YamlConfiguration.loadConfiguration(signFile);
		
		this.type = SignType.INFO_SIGN;
		
		this.ID = signConfig.getInt("sign.ID");
		this.signLoc = new Location(
				Bukkit.getWorld(UUID.fromString(signConfig.getString("sign.Location.world"))),
				signConfig.getDouble("sign.Location.X"),
				signConfig.getDouble("sign.Location.Y"), 
				signConfig.getDouble("sign.Location.Z"));
		this.sign = (Sign) signLoc.getBlock().getState();
		
		this.arena = GameManager.getManager().getArena(signConfig.getInt("sign.Arena"));
		this.lobby = GameManager.getManager().getLobby(signConfig.getInt("sign.Lobby"));
		this.lobby.addSign(this);
		update();
	}
	
	void delete(){
		this.lobby.removeSign(this);
		this.signFile.delete();
	}
	
	
	public int getID(){
		return ID;
	}
	
	public World getWorld(){
		return signLoc.getWorld();
	}
	
	public Location getLocation(){
		return signLoc.clone();
	}
	
	public Sign getSign(){
		return sign;
	}
	
	public SignType getType(){
		return type;
	}
	
	public Arena getArena(){
		return arena;
	}
	
	public Lobby getLobby(){
		return lobby;
	}
	
	
	public void update(){
		if(arena!=null){
			sign.setLine(0, ZvP.getPrefix());
			sign.setLine(1, "Arena: " + arena.getID());
			sign.setLine(2, ChatColor.AQUA + "" + arena.getPlayers().length + ChatColor.DARK_GRAY + " / " + ChatColor.DARK_RED + arena.getMaxPlayers());
			sign.setLine(3, "R " + ChatColor.BLUE + "" + arena.getRound() + ":" + arena.getWave() + ChatColor.RESET + " / " + ChatColor.DARK_RED + arena.getMaxRounds());
			sign.update();
		}else{
			sign.setLine(0, ZvP.getPrefix());
			sign.setLine(1, "");
			sign.setLine(2, ChatColor.DARK_RED + "Arena is not");
			sign.setLine(3, ChatColor.DARK_RED + "available!");
			sign.update();
		}
	}
	
}
