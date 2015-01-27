package me.Aubli.ZvP.Translation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class MessageManager {
    
    private static Locale loc;
    
    private File languageFile;
    private FileConfiguration conf;
    
    private static Map<String, String> messages;
    
    public MessageManager(Locale locale) {
	
	this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Messages/" + locale.toString() + ".yml");
	this.conf = YamlConfiguration.loadConfiguration(this.languageFile);
	
	loc = locale;
	
	if (!this.languageFile.exists() || isOutdated()) {
	    try {
		ZvP.getPluginLogger().log("Creating new message File for Locale " + getLocale().toString() + "!", true);
		this.languageFile.getParentFile().mkdirs();
		this.languageFile.createNewFile();
		writeDefaults();
	    } catch (IOException e) {
		ZvP.getPluginLogger().log(Level.WARNING, "Error while saving Message file: " + e.getMessage(), true, false, e);
	    }
	}
	
	messages = getTranslation();
    }
    
    private boolean isOutdated() {
	return !ZvP.getInstance().getDescription().getVersion().equals(getConfig().getString("Version"));
    }
    
    private void writeDefaults() {
	getConfig().options().header("This file contains all Text messages used in ZvP.\n" + "A guide for translation can be found here: http://dev.bukkit.org/bukkit-plugins/zombievsplayer/pages/language-setup/\n");
	getConfig().options().copyHeader(true);
	
	getConfig().set("Version", ZvP.getInstance().getDescription().getVersion());
	save();
	
	ResourceBundle bundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
	
	SortedMap<String, String> sortedBundle = new TreeMap<String, String>();
	
	for (String key : bundle.keySet()) {
	    sortedBundle.put(key, bundle.getString(key));
	}
	
	for (String key : sortedBundle.keySet()) {
	    getConfig().addDefault("messages." + key, sortedBundle.get(key));
	}
	getConfig().options().copyDefaults(true);
	save();
    }
    
    private Map<String, String> getTranslation() {
	
	Map<String, String> translation = new HashMap<String, String>();
	ResourceBundle defaultBundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
	
	for (String key : defaultBundle.keySet()) {
	    translation.put(key, getConfig().getString("messages." + key));
	}
	
	return translation;
    }
    
    private FileConfiguration getConfig() {
	return this.conf;
    }
    
    private void save() {
	try {
	    getConfig().save(this.languageFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while saving Message file: " + e.getMessage(), true, false, e);
	}
    }
    
    public static String getMessage(String messageKey) {
	for (String Key : messages.keySet()) {
	    
	    if (Key.equals(messageKey)) {
		return messages.get(Key);
	    }
	}
	return "";
    }
    
    public static Locale getLocale() {
	return loc;
    }
    
}
