package me.Aubli.ZvP.Statistic;

public class DatabaseInfo {
    
    private String host;
    private int port;
    private String protocol;
    private String name;
    private String user;
    private String pass;
    
    private boolean flatFile;
    
    public DatabaseInfo(String host, int port, String protocol, String name, String user, String pass, boolean useDatabase) {
	this.host = host;
	this.port = port;
	this.protocol = protocol;
	this.name = name;
	this.user = user;
	this.pass = pass;
	
	this.flatFile = !useDatabase;
    }
    
    public String getHost() {
	return this.host;
    }
    
    public int getPort() {
	return this.port;
    }
    
    public String getProtocol() {
	return this.protocol;
    }
    
    public String getName() {
	return this.name;
    }
    
    public String getUser() {
	return this.user;
    }
    
    public String getPass() {
	return this.pass;
    }
    
    public void setHost(String host) {
	this.host = host;
    }
    
    public void setPort(int port) {
	this.port = port;
    }
    
    public void setProtocol(String protocol) {
	this.protocol = protocol;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public void setUser(String user) {
	this.user = user;
    }
    
    public void setPass(String pass) {
	this.pass = pass;
    }
    
    public boolean usingFlatFile() {
	return this.flatFile;
    }
    
    public String getConnectionURL() throws ClassNotFoundException {
	
	if (usingFlatFile()) {
	    Class.forName("org.sqlite.JDBC");
	    return "jdbc:sqlite:" + DatabaseManager.getManager().getFlatFileFolder().getAbsolutePath() + "/" + getName() + ".db";
	} else {
	    return "jdbc:" + getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getName();
	}
    }
}
