package me.Aubli.ZvP.Game.ArenaParts;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.util.Polygon.ArenaPolygon;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


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

	if (ZvPConfig.getHandleWorldGuard() && ZvP.getWorldGuardPlugin() != null) {
	    try {
		new RegionHelper(this).initializeRegion();
	    } catch (NoClassDefFoundError e) {
		// only happens if softdependens is not available
	    }
	}
	// System.out.println("Arena: " + arena.getID() + ", ArenaPositions:" + this.polygon.npoints + ", SpawnLocations:" + this.spawnPositions.size());
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
		// ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "max: " + max.getBlockY() + " min: " + min.getBlockY() + " hby@: " + highestY, true, true);
		if (highestY >= min.getBlockY() && highestY < max.getBlockY()) {
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
		    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Iterate from 0 - " + (max.getBlockY() - min.getBlockY()) + " @" + x + "," + z, true, true);

		    ArrayList<Integer> yList = new ArrayList<Integer>();

		    int range = max.getBlockY() - min.getBlockY();
		    Location tempMin = new Location(getWorld(), x, min.getBlockY(), z);

		    for (int iy = 0; iy < range; iy++) {
			if (isValidLocation(tempMin.clone().add(0, iy, 0))) {
			    yList.add(tempMin.getBlockY() + iy);
			    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "added " + yList.get(yList.size() - 1) + " to y-list" , true, true);
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
	    // ZvP.getPluginLogger().log(this.getClass(), Level.ALL, location.clone().add(0, -1, 0).getBlock().getType() + " " + location.getBlock().getType() + " " + location.clone().add(0, 1,
	    // 0).getBlock().getType(), true, true);
	    if (isValidBlock(location) && isValidBlock(location.clone().add(0, 1, 0))) { // Make sure the location is not a Block
		// ZvP.getPluginLogger().log(this.getClass(), Level.ALL, "Location free to stand; down_air?: " + isValidBlock(location.clone().subtract(0, 1, 0)), true, true);
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
	    case FLOWER_POT:
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

	final double distance = getArena().getConfig().getSaveRadius();

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

	if (maxDistance < getArena().getConfig().getSaveRadius()) {
	    maxDistance += getArena().getConfig().getSaveRadius();
	}

	for (ZvPPlayer player : getArena().getPlayers()) {
	    if (saveLoc.distanceSquared(player.getLocation()) <= Math.pow(getArena().getConfig().getSaveRadius() + maxDistance, 2)) {
		return saveLoc.clone();
	    }
	}
	return getNewUnsaveLocation(maxDistance);
    }

    public boolean addSpawnPosition(Location location) {
	if (contains(location)) {
	    if (!this.spawnPositions.contains(location)) {
		this.spawnPositions.add(location);
		getArena().getConfig().saveConfig();
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

    private class RegionHelper {

	private ArenaArea area;

	public RegionHelper(ArenaArea area) {
	    this.area = area;
	}

	public void initializeRegion() {

	    RegionManager regionManager = ZvP.getWorldGuardPlugin().getRegionManager(getWorld());

	    if (regionManager.getRegion("zvpregion" + getArena().getID()) == null) { // region does not exist, will create one

		ProtectedRegion zvpRegion;

		if (getArea().polygon.isPolygonal()) {
		    List<BlockVector2D> points = new ArrayList<BlockVector2D>();

		    for (Point polygonPoint : getArea().polygon.getPoints()) {
			points.add(new BlockVector2D(polygonPoint.getX(), polygonPoint.getY()));
		    }
		    zvpRegion = new ProtectedPolygonalRegion("zvpregion" + getArena().getID(), points, 0, getWorld().getMaxHeight());
		} else {
		    BlockVector min = new BlockVector(getArea().polygon.getRectangularMinimum().getBlockX(), 0, getArea().polygon.getRectangularMinimum().getBlockZ());
		    BlockVector max = new BlockVector(getArea().polygon.getRectangularMaximum().getBlockX(), getWorld().getMaxHeight(), getArea().polygon.getRectangularMaximum().getBlockZ());

		    zvpRegion = new ProtectedCuboidRegion("zvpregion" + getArena().getID(), min, max);
		}

		try {
		    zvpRegion.setFlag(DefaultFlag.INTERACT, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.USE, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.ITEM_DROP, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.ITEM_PICKUP, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.DAMAGE_ANIMALS, StateFlag.State.ALLOW);
		    zvpRegion.setFlag(DefaultFlag.POTION_SPLASH, StateFlag.State.ALLOW);
		} catch (Exception e) {
		    // in case of missing Flags
		}

		ApplicableRegionSet regionSet = regionManager.getApplicableRegions(getNewRandomLocation(false));
		if (regionSet.getRegions().size() == 1) {
		    // There is already a region set at the arena. Try to set it as parent
		    try {
			ProtectedRegion parentRegion = (ProtectedRegion) regionSet.getRegions().toArray()[0];
			zvpRegion.setParent(parentRegion);
			zvpRegion.setPriority(50);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		regionManager.addRegion(zvpRegion);
	    }
	}

	public ArenaArea getArea() {
	    return this.area;
	}

    }
}
