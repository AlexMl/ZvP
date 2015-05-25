package org.util.Polygon;

import java.awt.Polygon;
import java.util.ArrayList;

import org.bukkit.Location;


public class ArenaPolygon extends Polygon {
    
    private static final long serialVersionUID = 4768782249208578251L;
    
    public ArenaPolygon(ArrayList<Location> points) throws Exception {
	super();
	
	int[] xPoints = new int[points.size()];
	int[] yPoints = new int[points.size()];
	
	for (int i = 0; i < points.size(); i++) {
	    Location arenaCorner = points.get(i);
	    xPoints[i] = arenaCorner.getBlockX();
	    yPoints[i] = arenaCorner.getBlockZ();
	}
	
	this.npoints = xPoints.length;
	this.xpoints = xPoints;
	this.ypoints = yPoints;
    }
    
    public ArenaPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	super(xPoints, yPoints, nPoints);
    }
    
    @Override
    public void addPoint(int x, int y) {
	super.addPoint(x, y);
	this.invalidate();
    }
}
