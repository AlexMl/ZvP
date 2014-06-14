package me.Aubli.ZvP.Sign;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

public interface ISign {
	
	void delete();	
	
	public int getID();
	
	public World getWorld();
	
	public Location getLocation();
	
	public Sign getSign();
	
	public SignType getType();
	
	public Arena getArena();
	
	public Lobby getLobby();
	
	public void update();
}
