package me.Aubli.ZvP.Sign;

import java.util.Map;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;


public interface ISign {
    
    void delete();
    
    public int getID();
    
    public World getWorld();
    
    public Location getLocation();
    
    public SignType getType();
    
    public Arena getArena();
    
    public Lobby getLobby();
    
    public void update(Map<String, ChatColor> colorMap);
    
    public int compareTo(ISign o);
}
