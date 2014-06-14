package me.Aubli.ZvP.Translation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import me.Aubli.ZvP.ZvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {

	private Locale loc;
	
	private File languageFile;
	private FileConfiguration langConfig;
	
	private Map<String, String> messages;	
	
	public MessageManager(Locale loc){
		
		this.languageFile = new File(ZvP.getInstance().getDataFolder().getPath() + "/messages/" + loc.toString() + ".yml");	
		this.langConfig = YamlConfiguration.loadConfiguration(languageFile);
		
		this.loc = loc;
		
		if(!languageFile.exists()) {	
			try {
				new File(languageFile.getParent()).mkdirs();
				languageFile.createNewFile();	
				writeDefaults();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		this.messages = getTranslation();		
	}
	
	private void writeDefaults(){
		ResourceBundle bundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
		
		for(String key : bundle.keySet()) {
			getConfig().set(key, bundle.getObject(key));
		}
		save();
	}	
	
	private Map<String, String> getTranslation(){
		
		Map<String, String> translation = new HashMap<String, String>();
		ResourceBundle defaultBundle = ResourceBundle.getBundle("me.Aubli.ZvP.Translation.DefaultTranslation");
		
		for(String key : defaultBundle.keySet()) {
			translation.put(key, getConfig().getString(key));
		}
		
		return translation;
	}
	
	private FileConfiguration getConfig() {
		return langConfig;
	}
	
	
	private void save() {
		try {
			getConfig().save(languageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getMessage(String key) {
		for(String Key : messages.keySet()) {
			
			if(key.equals(key)) {
				return messages.get(Key);
			}			
		}
		return "";
	}
	
	
	public Locale getLocale() {
		return loc;
	}
	
	
}
