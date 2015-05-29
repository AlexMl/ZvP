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
    
    private final static String[] bundleBaseNames = new String[] {"me.Aubli.ZvP.Translation.DefaultTranslation", "me.Aubli.ZvP.Translation.Resources.GermanTranslation", "me.Aubli.ZvP.Translation.Resources.HungarianTranslation"};
    
    private static Map<String, String> messages;
    
    public MessageManager(Locale locale) {
	
	copyTranslations();
	
	this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Messages/" + locale.toString() + ".yml");
	this.conf = YamlConfiguration.loadConfiguration(this.languageFile);
	
	loc = locale;
	
	if (!this.languageFile.exists() || isOutdated()) {
	    try {
		ZvP.getPluginLogger().log(this.getClass(), "Updating message file for locale " + getLocale().toString() + "!", true);
		this.languageFile.getParentFile().mkdirs();
		this.languageFile.createNewFile();
		writeDefaults();
	    } catch (IOException e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Message file: " + e.getMessage(), true, false, e);
	    }
	}
	
	messages = getTranslation();
    }
    
    private boolean isOutdated() {
	return !ZvP.getInstance().getDescription().getVersion().equals(getConfig().getString("version"));
    }
    
    private void writeDefaults() {
	getConfig().options().header("This file contains all Text messages used in ZvP.\n" + "A guide for translation can be found here: http://dev.bukkit.org/bukkit-plugins/zombievsplayer/pages/language-setup/\n");
	getConfig().options().copyHeader(true);
	
	getConfig().set("version", ZvP.getInstance().getDescription().getVersion());
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
    
    private void copyTranslations() {
	
	for (String baseName : bundleBaseNames) {
	    ResourceBundle bundle = ResourceBundle.getBundle(baseName);
	    
	    try {
		this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Messages/" + bundle.getLocale().toString() + ".yml");
		this.conf = YamlConfiguration.loadConfiguration(this.languageFile);
		
		if (!this.languageFile.exists() || isOutdated()) {
		    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Copying new translation for " + bundle.getLocale().toString() + "!", false);
		    
		    if (isOutdated() && this.languageFile.exists()) {
			this.languageFile.renameTo(new File(this.languageFile.getParentFile(), this.languageFile.getName() + "." + getConfig().getString("version")));
			this.languageFile.delete();
			this.languageFile.createNewFile();
			this.conf = YamlConfiguration.loadConfiguration(this.languageFile);
		    }
		    
		    this.languageFile.getParentFile().mkdirs();
		    this.languageFile.createNewFile();
		    
		    getConfig().options().header("This file contains all Text messages used in ZvP.\n" + "A guide for translation can be found here: http://dev.bukkit.org/bukkit-plugins/zombievsplayer/pages/language-setup/\n");
		    getConfig().options().copyHeader(true);
		    
		    getConfig().set("version", ZvP.getInstance().getDescription().getVersion());
		    save();
		    
		    SortedMap<String, String> sortedBundle = new TreeMap<String, String>();
		    
		    for (String key : bundle.keySet()) {
			sortedBundle.put(key, bundle.getString(key));
		    }
		    
		    for (String key : sortedBundle.keySet()) {
			getConfig().set("messages." + key, sortedBundle.get(key));
		    }
		    save();
		}
	    } catch (IOException e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Message file for Locale " + bundle.getLocale().toString() + ": " + e.getMessage(), true, false, e);
	    }
	}
	this.languageFile = null;
	this.conf = null;
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
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Message file: " + e.getMessage(), true, false, e);
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
    
    public static String getFormatedMessage(String messageKey, Object... args) {
	String message = getMessage(messageKey);
	
	if (!message.isEmpty()) {
	    return String.format(message, args);
	}
	return "";
    }
    
    public static Locale getLocale() {
	return loc;
    }
    
}
