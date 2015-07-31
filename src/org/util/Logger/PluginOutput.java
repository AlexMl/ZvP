package org.util.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;


public class PluginOutput {
    
    private final Logger log;
    
    private File logFile;
    
    private boolean debugMode;
    private int loglevel;
    
    private String pluginVersion;
    
    public PluginOutput(JavaPlugin plugin, boolean enableDebug, int minLevel) {
	
	this.log = plugin.getLogger();
	
	this.pluginVersion = plugin.getDescription().getVersion();
	
	this.debugMode = enableDebug;
	
	this.loglevel = minLevel;
	
	this.logFile = new File(plugin.getDataFolder().getAbsolutePath() + "/logs/pluginlog-" + this.pluginVersion + ".info");
	
	try {
	    this.logFile.getParentFile().mkdirs();
	    this.logFile.createNewFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
    }
    
    public int getLogLevel() {
	return this.loglevel;
    }
    
    public boolean isDebugMode() {
	return this.debugMode;
    }
    
    public void log(Class<?> senderClass, String message, boolean logMessage) {
	log(senderClass, Level.INFO, message, logMessage, false);
    }
    
    public void log(Class<?> senderClass, Level level, String message, boolean debugMessage) {
	log(senderClass, level, message, true, debugMessage, null);
    }
    
    public void log(Class<?> senderClass, Level level, String message, boolean logMessage, boolean debugMessage) {
	log(senderClass, level, message, logMessage, debugMessage, null);
    }
    
    public void log(Class<?> senderClass, Level level, String message, boolean logMessage, boolean debugMessage, Exception e) {
	
	if (logMessage || e != null || level.intValue() >= Level.WARNING.intValue()) {
	    logData(senderClass, level, message, e);
	}
	
	if (e != null) {
	    this.log.log(Level.WARNING, message);
	    this.log.log(Level.WARNING, e.toString());
	    return;
	}
	
	if (!debugMessage) {
	    if (level.intValue() >= Level.INFO.intValue()) {
		this.log.log(level, message);
		return;
	    }
	    this.log.info("[" + level.getName() + "] " + message);
	    return;
	}
	
	if (debugMessage) {
	    if (this.debugMode && level.intValue() >= getLogLevel()) {
		if (level.intValue() >= Level.INFO.intValue()) {
		    this.log.log(level, message);
		    return;
		}
		this.log.info("[" + level.getName() + "] " + message);
		return;
	    }
	}
    }
    
    private void logData(Class<?> senderClass, Level level, String message, Exception exception) {
	SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ('UTC' Z)");
	Date currentTime = new Date();
	
	try {
	    FileWriter writer = new FileWriter(this.logFile, true);
	    
	    if (exception != null) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		
		message += "\n" + sw.toString();
	    }
	    
	    writer.write("[" + formatter.format(currentTime) + "] [" + senderClass.getSimpleName() + "] [" + level.getName() + "] " + message);
	    writer.write(System.getProperty("line.separator"));
	    writer.flush();
	    writer.close();
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
}
