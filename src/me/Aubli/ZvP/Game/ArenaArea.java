package me.Aubli.ZvP.Game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.util.Polygon.ArenaPolygon;


public class ArenaArea {
    
    private Arena arena;
    
    private World world;
    
    private ArenaPolygon polygon;
    
    private final int height;
    
    private ArrayList<Point> spawnPositions;
    
    public ArenaArea(Arena arena, ArrayList<Location> cornerPoints, ArrayList<Location> spawnPositions) throws Exception {
	
	if (cornerPoints.size() < 2) {
	    throw new IllegalArgumentException("Arena needs at least 2 positions!");
	}
	if (arena == null) {
	    throw new NullPointerException("Arena can not be null!");
	}
	
	this.arena = arena;
	this.world = arena.getWorld();
	this.height = initPositions(cornerPoints, spawnPositions);
    }
    
    private int initPositions(ArrayList<Location> arenaCorners, ArrayList<Location> spawnLocations) throws Exception {
	int y = arenaCorners.get(0).getBlockY();
	
	for (Location loc : arenaCorners) {
	    if (y != loc.getBlockY()) {
		throw new RuntimeException("Locations do not have same Y value!");
	    }
	}
	
	this.polygon = new ArenaPolygon(arenaCorners);
	this.spawnPositions = new ArrayList<Point>();
	
	for (Location loc : spawnLocations) {
	    this.spawnPositions.add(new Point(loc.getBlockX(), loc.getBlockZ()));
	}
	
	System.out.println("Npoints:" + this.polygon.npoints + ", Xpoints:" + this.polygon.xpoints.length + ", YPoints:" + this.polygon.ypoints.length);
	
	return y;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public World getWorld() {
	return this.world;
    }
    
    public double getSize() {
	return this.polygon.getBounds2D().getWidth() * this.polygon.getBounds2D().getHeight();
    }
    
    public boolean isRectangular() {
	return this.polygon.npoints == 2;
    }
    
    public boolean isPolygonal() {
	return this.polygon.npoints > 2;
    }
    
    public boolean contains(Location location) {
	if (location.getBlockY() == this.height) {
	    return this.polygon.contains(location.getX(), location.getZ());
	}
	return false;
    }
    
    // TODO
    public Entity[] getEntities() {
	List<Entity> eList = new ArrayList<Entity>();
	Entity[] entities;
	
	Chunk minC = this.minLoc.getChunk();
	Chunk maxC = this.maxLoc.getChunk();
	
	int minX = minC.getX();
	int minZ = minC.getZ();
	int maxX = maxC.getX();
	int maxZ = maxC.getZ();
	
	for (int x = minX; x <= maxX; x++) {
	    for (int z = minZ; z <= +maxZ; z++) {
		Chunk entityChunk = getWorld().getChunkAt(x, z);
		for (Entity e : entityChunk.getEntities()) {
		    eList.add(e);
		}
	    }
	}
	
	entities = new Entity[eList.size()];
	for (int i = 0; i < eList.size(); i++) {
	    entities[i] = eList.get(i);
	}
	return entities;
    }
    
    // TODO
    public Location getNewRandomLocation(boolean player) {
	
	if (player && !this.staticSpawnLocations.isEmpty()) {
	    Location location = this.staticSpawnLocations.get(getArena()..rand.nextInt(this.staticSpawnLocations.size()));
	    if (contains(location)) {
		return location.clone();
	    }
	    return getNewRandomLocation(player);
	} else {
	    
	    int x;
	    int y = 0;
	    int z;
	    
	    x = this.rand.nextInt((getMax().getBlockX() - getMin().getBlockX() - 1)) + getMin().getBlockX() + 1;
	    z = this.rand.nextInt((getMax().getBlockZ() - getMin().getBlockZ() - 1)) + getMin().getBlockZ() + 1;
	    
	    if (getWorld().getHighestBlockYAt(x, z) + 1 >= getMin().getBlockY() && getWorld().getHighestBlockYAt(x, z) + 1 <= getMax().getBlockY()) {
		// If highest y is between min and max, go for it
		y = getWorld().getHighestBlockYAt(x, z) + 1;
	    } else if (getMax().getBlockY() == getMin().getBlockY() && getWorld().getHighestBlockYAt(x, z) == getMin().getBlockY()) {
		// min y == max y == highest y at random location
		// arena is flat
		y = getWorld().getHighestBlockYAt(x, z) + 1;
	    } else if (getMax().getBlockY() == getMin().getBlockY()) {
		// arena is flat but has a ceiling
		y = getMin().getBlockY() + 1;
	    } else {
		// iterate over y from min to max to find a perfect y
		// not very performant but needed in worst case (above doesnt match)
		
		for (int iy = 0; iy < (getMax().getBlockY() - getMin().getBlockY()); iy++) {
		    if (isValidLocation(getMin().clone().add(0, iy, 0))) {
			y = getMin().getBlockY() + iy;
			// System.out.println("ny:" + y);
			break;
		    }
		}
		if (y == 0) {
		    return getNewRandomLocation(player);
		}
	    }
	    Location startLoc = new Location(getWorld(), x, y, z);
	    
	    // System.out.println("valid? " + isValidLocation(startLoc) + " Y:" + startLoc.getBlockY());
	    if (isValidLocation(startLoc)) {
		return startLoc.clone();
	    } else {
		return getNewRandomLocation(player);
	    }
	}
    }
    
    private boolean isValidLocation(Location location) {
	
	if (contains(location)) {
	    if (isValidBlock(location) && isValidBlock(location.clone().add(0, 1, 0))) { // Make sure the location is not a Block
		if (!isValidBlock(location.clone().subtract(0, 1, 0))) {
		    return true;
		}
	    }
	}
	
	return false;
    }
    
    private boolean isValidBlock(Location location) {
	
	if (location.getBlock().isEmpty() || location.getBlock().isLiquid()) {
	    return true;
	}
	
	switch (location.getBlock().getType()) {
	    case ACTIVATOR_RAIL:
	    case ARMOR_STAND:
	    case BREWING_STAND:
	    case DEAD_BUSH:
	    case DETECTOR_RAIL:
	    case DOUBLE_PLANT:
	    case ENDER_PORTAL:
	    case FLOWER_POT:
	    case GRASS:
	    case LONG_GRASS:
	    case PORTAL:
	    case POWERED_RAIL:
	    case PUMPKIN_STEM:
	    case RAILS:
	    case RED_MUSHROOM:
	    case RED_ROSE:
	    case REDSTONE_COMPARATOR:
	    case REDSTONE_COMPARATOR_OFF:
	    case REDSTONE_COMPARATOR_ON:
	    case REDSTONE_TORCH_OFF:
	    case REDSTONE_TORCH_ON:
	    case REDSTONE_WIRE:
	    case SAPLING:
	    case SIGN_POST:
	    case SUGAR_CANE_BLOCK:
	    case TORCH:
	    case TRIPWIRE:
	    case WEB:
	    case YELLOW_FLOWER:
		return true;
		
	    default:
		return false;
	}
    }
    
    public Location getNewSaveLocation() {
	// Save means Location with no players nearby
	// ---> Spawn zombies in a location save for players
	
	final double distance = getArena().getSaveRadius();
	
	final Location spawnLoc = getNewRandomLocation(false);
	
	for (ZvPPlayer p : getArena().getPlayers()) {
	    
	    if (this.staticSpawnLocations.isEmpty()) {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
	    } else {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
		for (Location loc : this.staticSpawnLocations) {
		    if (spawnLoc.distanceSquared(loc) <= ((distance * distance) * 0.5)) {
			return getNewSaveLocation();
		    }
		}
	    }
	}
	
	if (contains(spawnLoc)) {
	    return spawnLoc.clone();
	} else {
	    return getNewSaveLocation();
	}
    }
    
    public Location getNewUnsaveLocation(double maxDistance) {
	// Exact opposite of getNewSaveLocation
	// ---> Spawn zombies in a location nearby the player
	
	Location saveLoc = getNewSaveLocation(); // Do not break the save radius rule
	
	if (maxDistance < getArena().getSaveRadius()) {
	    maxDistance += getArena().getSaveRadius();
	}
	
	for (ZvPPlayer player : getArena().getPlayers()) {
	    if (saveLoc.distanceSquared(player.getLocation()) <= Math.pow(getArena().getSaveRadius() + maxDistance, 2)) {
		return saveLoc.clone();
	    }
	}
	return getNewUnsaveLocation(maxDistance);
    }
    
}
