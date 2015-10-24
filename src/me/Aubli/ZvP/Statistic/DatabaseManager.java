package me.Aubli.ZvP.Statistic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;

import org.bukkit.Bukkit;


interface DatabaseCallback {
    
    void handleException(Exception e);
    
    void onRecordTransmission(DataRecord record);
}


public class DatabaseManager implements DatabaseCallback {
    
    private static DatabaseManager instance;
    
    private Map<UUID, DataRecord> dataMap = new TreeMap<UUID, DataRecord>();
    
    private Connection conn;
    private DatabaseInfo db_info;
    
    private File dbFolder = new File(ZvP.getInstance().getDataFolder(), "Statistics/");
    public static final String tableName = "zvp_player_stats";
    
    public DatabaseManager(DatabaseInfo info) throws SQLException, ClassNotFoundException {
	
	instance = this;
	
	this.db_info = info;
	this.conn = initializeConnection();
	createTable();
	updateMap();
	// String url = "jdbc:" + sqlType + "://" + sqlUrl + ":" + port + "/" + database;
	// mysql://localhost:3306/mc
	System.out.println(this.conn.getMetaData().getDatabaseProductName());
    }
    
    private Connection initializeConnection() throws SQLException, ClassNotFoundException {
	getFlatFileFolder().mkdirs();
	return DriverManager.getConnection(this.db_info.getConnectionURL(), this.db_info.getUser(), this.db_info.getPass());
    }
    
    private void createTable() throws SQLException {
	String createTableSTM = "CREATE TABLE IF NOT EXISTS " + tableName + "(puuid VARCHAR(36) NOT NULL, pkills INTEGER, pdeaths INTEGER, plmoney DECIMAL(8,3), changed TIMESTAMP, added TIMESTAMP, PRIMARY KEY(puuid));";
	this.conn.createStatement().executeUpdate(createTableSTM);
    }
    
    public static DatabaseManager getManager() {
	return instance;
    }
    
    public File getFlatFileFolder() {
	return this.dbFolder;
    }
    
    public void handleRecord(DataRecord record) {
	
	DataRecord playerRecord = getRecord(record.getPlayerUUID());
	
	if (playerRecord == null) {
	    insertRecord(record);
	} else {
	    try {
		DataRecord mergedRecord = DataRecord.merge(playerRecord, record);
		updateRecord(mergedRecord);
		updateMap();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
    }
    
    private void insertRecord(DataRecord record) {
	String stm = "INSERT INTO " + tableName + "(puuid,pkills,pdeaths,plmoney,added) VALUES('" + record.getPlayerUUID().toString() + "', " + record.getKills() + ", " + record.getDeaths() + ", " + record.getLeftMoney() + ", '" + record.getTimestamp() + "');";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stm));
    }
    
    private void updateRecord(DataRecord record) {
	String stm = "UPDATE " + tableName + " SET pkills=" + record.getKills() + ",pdeaths=" + record.getDeaths() + ",plmoney=" + record.getLeftMoney() + " WHERE puuid='" + record.getPlayerUUID().toString() + "';";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stm));
    }
    
    private DataRecord getRecord(UUID playerUUID) {
	
	for (Entry<UUID, DataRecord> entry : this.dataMap.entrySet()) {
	    if (entry.getKey().equals(playerUUID)) {
		return entry.getValue();
	    }
	}
	
	updateMap();
	return null;
    }
    
    private void updateMap() {
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseReader(this, this.conn));
    }
    
    @Override
    public void handleException(Exception e) {
	ZvP.getPluginLogger().log(getClass(), Level.SEVERE, "Error executing SQL statement: " + e.getMessage(), true, false, e);
    }
    
    @Override
    public void onRecordTransmission(DataRecord record) {
	this.dataMap.put(record.getPlayerUUID(), record);
    }
    
    private class DatabaseWriter implements Runnable {
	
	private DatabaseCallback callback;
	private Connection conn;
	private String sqlStatement;
	
	public DatabaseWriter(DatabaseCallback callback, Connection conn, String sqlstm) {
	    this.callback = callback;
	    this.conn = conn;
	    this.sqlStatement = sqlstm;
	}
	
	@Override
	public void run() {
	    try {
		this.conn.createStatement().executeUpdate(this.sqlStatement);
	    } catch (SQLException e) {
		this.callback.handleException(e);
	    }
	}
    }
    
    private class DatabaseReader implements Runnable {
	
	private DatabaseCallback callback;
	private Connection conn;
	
	public DatabaseReader(DatabaseCallback callback, Connection conn) {
	    this.callback = callback;
	    this.conn = conn;
	}
	
	@Override
	public void run() {
	    try {
		ResultSet result = this.conn.createStatement().executeQuery("SELECT * FROM " + tableName + ";");
		System.out.println("Abfrage");
		while (result.next()) {
		    UUID playerUUID = UUID.fromString(result.getString(1));
		    int kills = result.getInt(2);
		    int deaths = result.getInt(3);
		    double leftMoney = result.getDouble(4);
		    long timestamp = result.getTimestamp(6).getTime();
		    
		    DataRecord record = new DataRecord(playerUUID, kills, deaths, leftMoney, timestamp);
		    this.callback.onRecordTransmission(record);
		}
		
	    } catch (SQLException e) {
		this.callback.handleException(e);
	    }
	    
	}
    }
}
