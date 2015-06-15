package me.Aubli.ZvP.Game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.util.Polygon.ArenaPolygon;


public class ArenaArea {
    
    private Arena arena;
    
    private Random rand;
    
    private World world;
    
    private ArenaPolygon polygon;
    
    private final List<Location> spawnPositions;
    
    private final List<Location> cornerPoints;
    
    public ArenaArea(World world, Arena arena, List<Location> cornerPoints, List<Location> spawnPositions, Random arenaRandom) throws Exception {
	
	if (cornerPoints.size() < 2) {
	    throw new IllegalArgumentException("Arena needs at least 2 positions!");
	}
	if (arena == null) {
	    throw new NullPointerException("Arena can not be null!");
	}
	
	if (world == null) {
	    throw new NullPointerException("World can not be null!");
	}
	
	this.arena = arena;
	this.world = world;
	this.rand = arenaRandom;
	
	this.polygon = new ArenaPolygon(getWorld(), cornerPoints);
	this.cornerPoints = cornerPoints;
	this.spawnPositions = new ArrayList<Location>();
	
	if (spawnPositions != null && !spawnPositions.isEmpty()) {
	    for (Location loc : spawnPositions) {
		if (contains(loc)) {
		    this.spawnPositions.add(loc.clone());
		} else {
		    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Arena " + arena.getID() + " does not contain custom spawn location X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + " in world " + getWorld().getName() + "!", true, true);
		}
	    }
	}
	// System.out.println("ArenaPositions:" + this.polygon.npoints + ", SpawnLocations:" + this.spawnPositions.size());
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public World getWorld() {
	return this.world;
    }
    
    public double getDiagonal() {
	return this.polygon.getRectangularMaximum().distance(this.polygon.getRectangularMinimum());
    }
    
    public double getDiagonalSquared() {
	return this.polygon.getRectangularMaximum().distanceSquared(this.polygon.getRectangularMinimum());
    }
    
    public List<Location> getCornerLocations() {
	return this.cornerPoints;
    }
    
    public List<Location> getSpawnLocations() {
	return this.spawnPositions;
    }
    
    public boolean contains(double X, double Z) {
	return this.polygon.contains(X, Z);
    }
    
    public boolean contains(Location location) {
	return this.polygon.contains(location);
    }
    
    // INFO Debug
    @Deprecated
    public void paintBounds() {
	
	Rectangle boundBox = this.polygon.getBounds();
	
	Location min = this.polygon.getRectangularMinimum();
	
	for (int x = 0; x < boundBox.getWidth(); x++) {
	    min.clone().add(x, 0, 0).getBlock().setType(Material.SPONGE);
	    min.clone().add(x, 0, boundBox.getHeight()).getBlock().setType(Material.SAND);
	}
	
	for (int z = 0; z < boundBox.getHeight(); z++) {
	    min.clone().add(0, 0, z).getBlock().setType(Material.SPONGE);
	    min.clone().add(boundBox.getWidth(), 0, z).getBlock().setType(Material.SAND);
	}
	
	System.out.println(boundBox.toString());
	
    }
    
    public Entity[] getEntities() {
	List<Entity> eList = new ArrayList<Entity>();
	
	Chunk minC = this.polygon.getRectangularMinimum().getChunk();
	Chunk maxC = this.polygon.getRectangularMaximum().getChunk();
	
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
	
	Entity[] entities = new Entity[eList.size()];
	for (int i = 0; i < eList.size(); i++) {
	    entities[i] = eList.get(i);
	}
	return entities;
    }
    
    public Location getNewRandomLocation(boolean player) {
	
	final Location max = this.polygon.getRectangularMaximum();
	final Location min = this.polygon.getRectangularMinimum();
	
	if (player && !this.spawnPositions.isEmpty()) {
	    Location location = this.spawnPositions.get(this.rand.nextInt(this.spawnPositions.size()));
	    if (contains(location)) {
		return location.clone();
	    }
	    return getNewRandomLocation(player);
	} else {
	    
	    int x;
	    double y = 0;
	    int z;
	    
	    x = this.rand.nextInt((max.getBlockX() - min.getBlockX() - 1)) + min.getBlockX() + 1;
	    z = this.rand.nextInt((max.getBlockZ() - min.getBlockZ() - 1)) + min.getBlockZ() + 1;
	    
	    if (contains(x, z)) {
		int highestY = getWorld().getHighestBlockYAt(x, z) - 1;
		// System.out.println("max: " + max.getBlockY() + " min: " + min.getBlockY() + " hby@: " + highestY);
		if (highestY >= min.getBlockY() && highestY <= max.getBlockY()) {
		    // If highest y is between min and max, go for it
		    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Highest point is between min and max", true, true);
		    
		    y = highestY + 1.5;
		} else if (max.getBlockY() == min.getBlockY() && highestY == min.getBlockY()) {
		    // min y == max y == highest y at random location
		    // arena is flat
		    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Arena is flat", true, true);
		    
		    y = highestY + 1.5;
		} else if (max.getBlockY() == min.getBlockY()) {
		    // arena is flat but has a ceiling
		    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Arena is flat but has a ceiling", true, true);
		    
		    y = min.getBlockY() + 1;
		} else {
		    // iterate over y from min to max to find a perfect y
		    // not very performant but needed in worst case (above doesnt match)
		    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Iterate from 0 - " + (max.getBlockY() - min.getBlockY()), true, true);
		    
		    ArrayList<Integer> yList = new ArrayList<Integer>();
		    for (int iy = 0; iy < (max.getBlockY() - min.getBlockY()); iy++) {
			if (isValidLocation(min.clone().add(0, iy, 0))) {
			    yList.add(min.getBlockY() + iy);
			}
		    }
		    if (yList.isEmpty()) {
			return getNewRandomLocation(player);
		    } else {
			y = yList.get(this.rand.nextInt(yList.size()));
		    }
		}
		Location startLoc = new Location(getWorld(), x, y, z);
		
		// ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "valid? " + isValidLocation(startLoc) + " Y:" + startLoc.getBlockY(), true, true);
		if (isValidLocation(startLoc)) {
		    return startLoc.clone();
		} else {
		    return getNewRandomLocation(player);
		}
	    } else {
		return getNewRandomLocation(player);
	    }
	}
    }
    
    private boolean isValidLocation(Location location) {
	
	if (contains(location)) {
	    if (isValidBlock(location) && isValidBlock(location.clone().add(0, 1, 0))) { // Make sure the location is not a Block
		if (!isValidBlock(location.clone().subtract(0, 1, 0))) {
		    // Make sure the location under the location is a Block
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
	    
	    if (this.spawnPositions.isEmpty()) {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
	    } else {
		if (p.getLocation().distanceSquared(spawnLoc) <= (distance * distance)) {
		    return getNewSaveLocation();
		}
		for (Location loc : this.spawnPositions) {
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
    
    public boolean addSpawnPosition(Location location) {
	if (contains(location)) {
	    if (!this.spawnPositions.contains(location)) {
		this.spawnPositions.add(location);
		getArena().save();
		ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Added spawnpoint (X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ() + ") to Arena " + getArena().getID(), true, true);
		return true;
	    }
	}
	return false;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof ArenaArea) {
	    ArenaArea other = (ArenaArea) obj;
	    return (other.getDiagonalSquared() == getDiagonalSquared() && other.getWorld().equals(getWorld()));
	}
	return false;
    }
}
