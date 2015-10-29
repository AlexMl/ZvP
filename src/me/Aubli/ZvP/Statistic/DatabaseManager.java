package me.Aubli.ZvP.Statistic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;

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
	
	// TODO remove debug, replace with logger
	System.out.println(this.conn.getMetaData().getDatabaseProductName());
	System.out.println(this.conn.getMetaData().getDatabaseProductVersion());
	System.out.println(this.conn.getMetaData().getDriverVersion());
    }
    
    private Connection initializeConnection() throws SQLException, ClassNotFoundException {
	getFlatFileFolder().mkdirs();
	return DriverManager.getConnection(this.db_info.getConnectionURL(), this.db_info.getUser(), this.db_info.getPass());
    }
    
    private void createTable() throws SQLException {
	String createTableSTM = "CREATE TABLE IF NOT EXISTS " + tableName + "(puuid VARCHAR(36) NOT NULL, pkills INTEGER, phkills INTEGER, pdeaths INTEGER, plmoney DECIMAL(8,3), changed TIMESTAMP, added TIMESTAMP, PRIMARY KEY(puuid));";
	this.conn.createStatement().executeUpdate(createTableSTM);
    }
    
    public void reload() throws ClassNotFoundException, SQLException {
	instance = new DatabaseManager(ZvPConfig.getDBInfo());
    }
    
    public static DatabaseManager getManager() {
	return instance;
    }
    
    public File getFlatFileFolder() {
	return this.dbFolder;
    }
    
    public void handleRecord(DataRecord... records) {
	List<DataRecord> insertRecords = new LinkedList<DataRecord>();
	
	try {
	    for (DataRecord record : records) {
		DataRecord playerRecord = getRecord(record.getPlayerUUID());
		
		if (playerRecord == null) {
		    insertRecords.add(record);
		} else {
		    DataRecord mergedRecord = DataRecord.merge(playerRecord, record);
		    updateRecord(mergedRecord);
		}
	    }
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(getClass(), Level.WARNING, "Error while queuing data record: " + e.getMessage(), true, false, e);
	}
	
	if (insertRecords.size() > 0) {
	    insertRecord(insertRecords.toArray(new DataRecord[0]));
	}
	updateMap();
    }
    
    private void insertRecord(DataRecord... records) {
	
	StringBuilder stmBuilder = new StringBuilder();
	stmBuilder.append("INSERT INTO " + tableName + "(puuid,pkills,phkills,pdeaths,plmoney,added)");
	
	for (DataRecord record : records) {
	    stmBuilder.append(" SELECT '" + record.getPlayerUUID() + "', '" + record.getKills() + "', '" + record.getKills() + "', '" + record.getDeaths() + "', '" + record.getLeftMoney() + "', '" + record.getTimestamp() + "'");
	    stmBuilder.append(" UNION ALL");
	}
	
	stmBuilder.delete(stmBuilder.length() - 10, stmBuilder.length());
	stmBuilder.append(";");
	
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stmBuilder.toString()));// stmBuilder.toString()));
    }
    
    private void updateRecord(DataRecord record) {
	String stm = "UPDATE " + tableName + " SET pkills=" + record.getKills() + ",phkills=" + record.getMaxKills() + ",pdeaths=" + record.getDeaths() + ",plmoney=" + record.getLeftMoney() + " WHERE puuid='" + record.getPlayerUUID().toString() + "';";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stm));
    }
    
    private DataRecord getRecord(UUID playerUUID) {
	if (this.dataMap.containsKey(playerUUID)) {
	    return this.dataMap.get(playerUUID);
	}
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
		Statement s = this.conn.createStatement();
		s.executeUpdate(this.sqlStatement);
		s.close();
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
		Statement statement = this.conn.createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM " + tableName + ";");
		
		while (result.next()) {
		    UUID playerUUID = UUID.fromString(result.getString(1));
		    int kills = result.getInt(2);
		    int maxKills = result.getInt(3);
		    int deaths = result.getInt(4);
		    double leftMoney = result.getDouble(5);
		    long timestamp = result.getTimestamp(7).getTime();
		    
		    this.callback.onRecordTransmission(new DataRecord(playerUUID, kills, maxKills, deaths, leftMoney, timestamp));
		}
		
		statement.close();
		result.close();
	    } catch (SQLException e) {
		this.callback.handleException(e);
	    }
	}
    }
}
