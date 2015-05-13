package me.Aubli.ZvP.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;


public class ArenaLobby {
    
    private Location centerLoc;
    
    private List<Location> locations;
    
    private Arena arena;
    
    private Random rand;
    
    public ArenaLobby(Arena arena, Location center, List<Location> locations, Random arenaRandom) throws Exception {
	
	if (arena == null) {
	    throw new NullPointerException("Arena can not be null!");
	}
	if (center == null) {
	    throw new NullPointerException("Center Location can not be null!");
	}
	if (center.getWorld() == null) {
	    throw new IllegalArgumentException("Center Location is not available! The World is not loaded!");
	}
	
	this.arena = arena;
	this.centerLoc = center.clone();
	this.locations = locations != null ? locations : new ArrayList<Location>();
	
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
	return getLocationList() != null ? getLocationList().get(this.rand.nextInt(getLocationList().size())).clone() : getCenterLoc();
    }
}
