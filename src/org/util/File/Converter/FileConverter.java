package org.util.File.Converter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class FileConverter {
    
    public enum FileType {
	ARENAFILE,
	KITFILE,
	LANGUAGEFILE,
	LOBBYFILE,
	SHOPFILE,
	SIGNFILE;
    }
    
    private String currentVersion;
    
    public FileConverter(JavaPlugin plugin) {
	this.currentVersion = plugin.getDescription().getVersion();
    }
    
    public boolean convert(FileType type, File file, double uptadeRequired) {
	
	FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
	
	try {
	    String fileVersion = conf.getString("version");
	    
	    if (fileVersion == null || parseVersion(fileVersion) < uptadeRequired) { // Version that needs upgrade
	    
		switch (type) {
		    case ARENAFILE:
			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Found outdated arena file(" + file.getName() + " v" + fileVersion + ")! Converting to " + uptadeRequired + " ...", true, false);
			int arenaID = conf.getInt("arena.ID");
			String status = conf.getString("arena.Online");
			
			int minPlayers = conf.getInt("arena.minPlayers");
			int maxPlayers = conf.getInt("arena.maxPlayers");
			int rounds = conf.getInt("arena.rounds");
			int waves = conf.getInt("arena.waves");
			int spawnRate = conf.getInt("arena.spawnRate");
			
			String world = conf.getString("arena.Location.world");
			int minX = conf.getInt("arena.Location.min.X");
			int minY = conf.getInt("arena.Location.min.Y");
			int minZ = conf.getInt("arena.Location.min.Z");
			
			int maxX = conf.getInt("arena.Location.max.X");
			int maxY = conf.getInt("arena.Location.max.Y");
			int maxZ = conf.getInt("arena.Location.max.Z");
			
			List<String> staticPositions = conf.getStringList("arena.Location.staticPositions");
			
			boolean keepXP = conf.getBoolean("arena.keepXP", false);
			boolean keepInventory = conf.getBoolean("arena.keepInventory", false);
			boolean useVoteSystem = conf.getBoolean("arena.useVoteSystem", true);
			boolean separateScores = conf.getBoolean("arena.separatePlayerScores", false);
			
			int joinTime = conf.getInt("arena.joinTime", 15);
			int breakTime = conf.getInt("arena.timeBetweenWaves", 90);
			double zombieFund = conf.getDouble("arena.zombieFund", 0.37);
			double deathFee = conf.getDouble("arena.deathFee", 3);
			
			double saveRadius;
			boolean spawnProtection;
			int duration;
			String difficulty;
			if (fileVersion == null || parseVersion(fileVersion) < 240.0) {
			    saveRadius = conf.getDouble("arena.saveRadius", 4.0);
			    spawnProtection = true;
			    duration = 5;
			    difficulty = "NORMAL";
			} else if (parseVersion(fileVersion) > 240.0 && parseVersion(fileVersion) < 260.0) {
			    saveRadius = conf.getDouble("arena.safety.saveRadius", 4.0);
			    spawnProtection = conf.getBoolean("arena.safety.SpawnProtection.enabled", true);
			    duration = conf.getInt("arena.safety.SpawnProtection.duration", 5);
			    difficulty = conf.getString("arena.Difficulty");
			} else {
			    saveRadius = 4.0;
			    spawnProtection = true;
			    duration = 5;
			    difficulty = "NORMAL";
			}
			
			boolean success = file.renameTo(new File(file.getParentFile(), file.getName() + ".old"));
			
			if (!success) {
			    boolean deleteSuccess = new File(file.getParentFile(), file.getName() + ".old").delete();
			    if (!deleteSuccess) {
				if (!file.renameTo(new File(file.getParentFile(), file.getName() + ".old" + System.currentTimeMillis()))) {
				    return false;
				}
			    }
			}
			
			file.delete();
			file.createNewFile();
			
			conf = YamlConfiguration.loadConfiguration(file);
			
			conf.set("arena.ID", arenaID);
			conf.set("arena.Online", status);
			conf.set("arena.Difficulty", difficulty);
			
			conf.set("arena.minPlayers", minPlayers);
			conf.set("arena.maxPlayers", maxPlayers);
			conf.set("arena.rounds", rounds);
			conf.set("arena.waves", waves);
			conf.set("arena.spawnRate", spawnRate);
			
			conf.set("arena.keepXP", keepXP);
			conf.set("arena.keepInventory", keepInventory);
			conf.set("arena.useVoteSystem", useVoteSystem);
			conf.set("arena.separatePlayerScores", separateScores);
			conf.set("arena.joinTime", joinTime);
			conf.set("arena.timeBetweenWaves", breakTime);
			conf.set("arena.zombieFund", zombieFund);
			conf.set("arena.deathFee", deathFee);
			
			conf.set("arena.enableSpawnProtection", spawnProtection);
			conf.set("arena.spawnProtectionDuration", duration);
			conf.set("arena.saveRadius", saveRadius);
			
			conf.set("arena.Location.world", world);
			conf.set("arena.Location.min.X", minX);
			conf.set("arena.Location.min.Y", minY);
			conf.set("arena.Location.min.Z", minZ);
			
			conf.set("arena.Location.max.X", maxX);
			conf.set("arena.Location.max.Y", maxY);
			conf.set("arena.Location.max.Z", maxZ);
			
			conf.set("arena.Location.staticPositions", staticPositions);
			
			conf.set("version", this.currentVersion);
			
			conf.save(file);
			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Updated " + file.getName() + " to " + uptadeRequired + " successfully!", true, false);
			return true;
			
		    case KITFILE:
			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Found outdated kit file(" + file.getName() + " v" + fileVersion + ")! Converting to " + uptadeRequired + " ...", true, false);
			String name = conf.getString("name");
			boolean enabled = conf.getBoolean("enabled", true);
			String icon = conf.getString("icon");
			List<String> itemList = conf.getStringList("items");
			
			file.renameTo(new File(file.getParentFile(), file.getName() + ".old"));
			file.delete();
			file.createNewFile();
			
			conf = YamlConfiguration.loadConfiguration(file);
			conf.options().header("This is the config file used in ZvP to store a customm kit.\n\n'name:' The name of the kit\n'enabled:' State of the kit\n'price:' The price of the kit if economy is used\n'icon:' An item used as an icon\n\n" + "'id:' The id describes the item material. A list of all items can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html\n" + "'amount:' The amount of the item (Should be 1!)\n" + "'data:' Used by potions\n" + "'ench: {}' A list of enchantings (ench: {ENCHANTMENT:LEVEL}). A list of enchantments can be found here:\n https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html\n");
			conf.options().copyHeader(true);
			conf.set("name", name);
			conf.set("enabled", enabled);
			conf.set("price", 0.0);
			conf.set("icon", icon);
			conf.set("items", itemList);
			conf.set("version", this.currentVersion);
			
			conf.save(file);
			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Updated " + file.getName() + " to " + uptadeRequired + " successfully!", true, false);
			return true;
			
		    default:
			return false;
		}
	    }
	    
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Failed saving converted file for " + file.getAbsolutePath() + "!", true, false, e);
	    return false;
	}
	return false;
    }
    
    public static double parseVersion(String version) {
	// Possibilities: ZvP v2.3.7-5_LP
	if (version.contains("v")) {
	    return parseVersion(version.split("v")[1]);
	} else if (version.contains("_")) {
	    return parseVersion(version.split("_")[0]);
	} else {
	    return Double.parseDouble(version.replace(".", "").replace("-", "."));
	}
	
    }
}
