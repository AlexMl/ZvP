package org.util.Polygon;

import java.awt.Polygon;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;


public class ArenaPolygon extends Polygon {
    
    private static final long serialVersionUID = 4768782249208578251L;
    
    private final World world;
    
    private int minY;
    private int maxY;
    
    public ArenaPolygon(World world, List<Location> points) throws Exception {
	super();
	
	int[] xPoints = new int[points.size()];
	int[] yPoints = new int[points.size()];
	
	this.minY = 256;
	this.maxY = 0;
	
	for (int i = 0; i < points.size(); i++) {
	    Location arenaCorner = points.get(i);
	    
	    if (arenaCorner.getBlockY() > this.maxY) {
		this.maxY = arenaCorner.getBlockY();
	    }
	    if (arenaCorner.getBlockY() < this.minY) {
		this.minY = arenaCorner.getBlockY();
	    }
	    
	    xPoints[i] = arenaCorner.getBlockX();
	    yPoints[i] = arenaCorner.getBlockZ();
	}
	
	this.npoints = xPoints.length;
	this.xpoints = xPoints;
	this.ypoints = yPoints;
	
	this.world = world;
	
	// System.out.println("max " + this.maxY);
	// System.out.println("min " + this.minY);
	invalidate();
    }
    
    @Override
    public boolean contains(int x, int y) {
	return contains((double) x, (double) y);
    }
    
    @Override
    public boolean contains(double x, double y) {
	if (isRectangular()) {
	    return ((x <= getRectangularMaximum().getX() && x >= getRectangularMinimum().getX()) && (y <= getRectangularMaximum().getZ() && y >= getRectangularMinimum().getZ()));
	} else {
	    return super.contains(x, y);
	}
    }
    
    public boolean contains(Location location, boolean checkY) {
	// TODO: May cause errors on wrong y coordinate
	if (this.minY == this.maxY && isRectangular()) {
	    return contains(location.getX(), location.getZ());
	} else {
	    return (this.minY <= location.getBlockY() && this.maxY >= location.getBlockY()) && contains(location.getX(), location.getZ());
	}
    }
    
    public boolean isRectangular() {
	return this.npoints == 2;
    }
    
    public boolean isPolygonal() {
	return this.npoints > 2;
    }
    
    public Location getRectangularMinimum() {
	return new Location(this.world, getBounds().getX(), this.minY, getBounds().getY());
    }
    
    public Location getRectangularMaximum() {
	return new Location(this.world, getBounds().getX() + getBounds().getWidth(), this.maxY, getBounds().getY() + getBounds().getHeight());
    }
    
}
