package me.Aubli.ZvP.Statistic;

public class DatabaseInfo {
    
    private String host;
    private int port;
    private String protocol;
    private String name;
    private String user;
    private String pass;
    
    private boolean flatFile;
    
    public DatabaseInfo(String host, int port, String protocol, String databaseName, String user, String pass, boolean useDatabase) {
	this.host = host;
	this.port = port;
	this.protocol = protocol;
	this.name = databaseName;
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
    
    public String getDatabaseName() {
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
    
    public void setDatabaseName(String name) {
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
	    return "jdbc:sqlite:" + DatabaseManager.getFlatFileFolder().getAbsolutePath() + "/" + getDatabaseName() + ".db";
	} else {
	    // INFO jdbc:mysql://localhost:3306/mc
	    return "jdbc:" + getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getDatabaseName();
	}
    }
}
