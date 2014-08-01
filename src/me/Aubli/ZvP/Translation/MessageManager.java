package me.Aubli.ZvP.Translation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import me.Aubli.ZvP.ZvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {

	private Locale loc;
	
	private File languageFile;
	private FileConfiguration conf;
	
	private static Map<String, String> messages;	
	
	public MessageManager(Locale loc){
		
		this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/Messages/" + loc.toString() + ".yml");	
		this.conf = YamlConfiguration.loadConfiguration(languageFile);
		
		this.loc = loc;
		
		if(!languageFile.exists() || isOutdated()) {
			try {
				ZvP.log.info("[" + ZvP.getInstance().getName() + "] Creating new message File!");
				languageFile.getParentFile().mkdirs();
				languageFile.createNewFile();
				writeDefaults();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		messages = getTranslation();		
	}
	
	private boolean isOutdated() {
		return !ZvP.getInstance().getDescription().getVersion().equals(getConfig().getString("Version"));
	}	
	
	private void writeDefaults(){
		getConfig().set("Version", ZvP.getInstance().getDescription().getVersion());
		
		ResourceBundle bundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
		
		SortedMap<String, String> sortedBundle = new TreeMap<String, String>();
		
		for(String key : bundle.keySet()) {
			sortedBundle.put(key, bundle.getString(key));
		}
		
		for(String key : sortedBundle.keySet()) {
			getConfig().addDefault("messages." + key, sortedBundle.get(key));
		}
		getConfig().options().copyDefaults(true);
		save();
	}	
	
	private Map<String, String> getTranslation(){
		
		Map<String, String> translation = new HashMap<String, String>();
		ResourceBundle defaultBundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
		
		for(String key : defaultBundle.keySet()) {
			translation.put(key, getConfig().getString("messages." + key));
		}
		
		return translation;
	}
	
	private FileConfiguration getConfig() {		
		return this.conf;
	}
	
	
	private void save() {
		try {
			System.out.println(getConfig().getCurrentPath());
			getConfig().save(languageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getMessage(String messageKey) {
		for(String Key : messages.keySet()) {
			
			if(Key.equals(messageKey)) {
				return messages.get(Key);
			}			
		}
		return "";
	}
	
	
	public Locale getLocale() {
		return loc;
	}
	
	
}
