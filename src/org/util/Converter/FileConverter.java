package org.util.Converter;

import java.io.File;
import java.io.IOException;

import me.Aubli.ZvP.Game.GameManager.ArenaDifficultyLevel;

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
	    String localVersion = conf.getString("version");
	    
	    if (type == FileType.ARENAFILE) {
		if (localVersion == null || parseVersion(localVersion) < uptadeRequired) { // Version that needs upgrade
		    int arenaID = conf.getInt("arena.ID");
		    String status = conf.getString("arena.Online");
		    
		    int minPlayers = conf.getInt("arena.minPlayers");
		    int maxPlayers = conf.getInt("arena.maxPlayers");
		    int rounds = conf.getInt("arena.rounds");
		    int waves = conf.getInt("arena.waves");
		    int spawnRate = conf.getInt("arena.spawnRate");
		    int saveRadius = conf.getInt("arena.saveRadius");
		    
		    String world = conf.getString("arena.Location.world");
		    int minX = conf.getInt("arena.Location.min.X");
		    int minY = conf.getInt("arena.Location.min.Y");
		    int minZ = conf.getInt("arena.Location.min.Z");
		    
		    int maxX = conf.getInt("arena.Location.max.X");
		    int maxY = conf.getInt("arena.Location.max.Y");
		    int maxZ = conf.getInt("arena.Location.max.Z");
		    
		    file.renameTo(new File(file.getParentFile(), file.getName() + ".old"));
		    file.delete();
		    file.createNewFile();
		    
		    conf = YamlConfiguration.loadConfiguration(file);
		    
		    conf.set("arena.ID", arenaID);
		    conf.set("arena.Online", status);
		    conf.set("arena.Difficulty", ArenaDifficultyLevel.NORMAL.name());
		    
		    conf.set("arena.minPlayers", minPlayers);
		    conf.set("arena.maxPlayers", maxPlayers);
		    conf.set("arena.rounds", rounds);
		    conf.set("arena.waves", waves);
		    conf.set("arena.spawnRate", spawnRate);
		    
		    conf.set("arena.safety.SpawnProtection.enabled", true);
		    conf.set("arena.safety.SpawnProtection.duration", 5);
		    conf.set("arena.safety.saveRadius", saveRadius);
		    
		    conf.set("arena.Location.world", world);
		    conf.set("arena.Location.min.X", minX);
		    conf.set("arena.Location.min.Y", minY);
		    conf.set("arena.Location.min.Z", minZ);
		    
		    conf.set("arena.Location.max.X", maxX);
		    conf.set("arena.Location.max.Y", maxY);
		    conf.set("arena.Location.max.Z", maxZ);
		    
		    conf.set("version", this.currentVersion);
		    
		    conf.save(file);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
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
