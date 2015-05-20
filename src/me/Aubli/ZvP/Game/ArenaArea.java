package me.Aubli.ZvP.Game;

import java.util.ArrayList;

import org.bukkit.Location;


public class ArenaArea {
    
    private Arena arena;
    
    private ArrayList<Location> corners;
    
    public ArenaArea(Arena arena, ArrayList<Location> cornerPoints) throws Exception {
	
	if (cornerPoints.size() < 2) {
	    throw new IllegalArgumentException("Arena needs at least 2 positions!");
	}
	if (arena == null) {
	    throw new NullPointerException("Arena can not be null!");
	}
	
	this.arena = arena;
	this.corners = cornerPoints;
    }
    
    public boolean isRectangular() {
	return this.corners.size() == 2;
    }
    
    public boolean isPolygonal() {
	return this.corners.size() > 2;
    }
}
