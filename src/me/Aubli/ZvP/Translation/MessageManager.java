package me.Aubli.ZvP.Translation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	
	LanguageBundle bundle = LanguageBundle.getLanguageBundle("me.Aubli.ZvP.Translation.Resources.DefaultTranslation");
	
	getConfig().set("version", ZvP.getInstance().getDescription().getVersion());
	getConfig().set("author", bundle.getAuthor());
	getConfig().set("locale", bundle.getLocale().toString());
	save();
	
	SortedMap<String, String> sortedBundle = new TreeMap<String, String>();
	
	for (String key : bundle.keySet()) {
	    sortedBundle.put(getEnumName(key) + ":" + key, bundle.getString(key));
	}
	
	for (String sortedKey : sortedBundle.keySet()) {
	    getConfig().addDefault("messages." + sortedKey, sortedBundle.get(sortedKey));
	}
	getConfig().options().copyDefaults(true);
	save();
    }
    
    private void copyTranslations() {
	
	for (LanguageBundle bundle : LanguageBundle.getLanguageBundles()) {
	    
	    try {
		this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Messages/" + bundle.getLocale().toString() + ".yml");
		this.conf = YamlConfiguration.loadConfiguration(this.languageFile);
		
		if (!this.languageFile.exists() || isOutdated()) {
		    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Copying new translation for " + bundle.getLocale().toString() + "!", false);
		    
		    if (this.languageFile.exists() && isOutdated()) {
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
		    getConfig().set("author", bundle.getAuthor());
		    getConfig().set("locale", bundle.getLocale().toString());
		    save();
		    
		    SortedMap<String, String> sortedBundle = new TreeMap<String, String>();
		    
		    for (String key : bundle.keySet()) {
			sortedBundle.put(getEnumName(key) + ":" + key, bundle.getString(key));
		    }
		    
		    for (String sortedKey : sortedBundle.keySet()) {
			getConfig().set("messages." + sortedKey, sortedBundle.get(sortedKey));
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
	
	for (String enumKey : defaultBundle.keySet()) {
	    String key = getEnumName(enumKey) + ":" + enumKey;
	    translation.put(key, getConfig().getString("messages." + key));
	}
	
	return translation;
    }
    
    private FileConfiguration getConfig() {
	return this.conf;
    }
    
    @SuppressWarnings("rawtypes")
    private static String getEnumName(Object key) throws IllegalArgumentException {
	ArrayList<Class<?>> enums = MessageKeys.getEnums();
	
	for (Class enumClass : enums) {
	    if (enumClass.isEnum()) {
		for (Object enumKey : enumClass.getEnumConstants()) {
		    if (key.toString().equals(enumKey.toString())) {
			return enumClass.getSimpleName();
		    }
		}
	    }
	}
	throw new IllegalArgumentException("There is no enum for key: " + key.toString());
    }
    
    private void save() {
	try {
	    getConfig().save(this.languageFile);
	} catch (IOException e) {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while saving Message file: " + e.getMessage(), true, false, e);
	}
    }
    
    private static String getMessage(String messageKey) {
	for (String Key : messages.keySet()) {
	    
	    if (Key.equals(messageKey)) {
		return messages.get(Key);
	    }
	}
	return "";
    }
    
    public static String getMessage(Object key) {
	String messageKey = getEnumName(key) + ":" + key.toString();
	return getMessage(messageKey);
    }
    
    public static String getFormatedMessage(Object key, Object... args) {
	String message = getMessage(key);
	
	if (!message.isEmpty()) {
	    return String.format(message, args);
	}
	return "";
    }
    
    public static Locale getLocale() {
	return loc;
    }
    
}
