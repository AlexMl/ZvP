package me.Aubli.ZvP.Statistic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
    
    void handleException(Exception e, String stmt);
    
    void onRecordTransmission(DataRecord record, String tableName);
    
}


public class DatabaseManager implements DatabaseCallback {
    
    private static DatabaseManager instance;
    
    private Map<UUID, DataRecord> dataMap = new TreeMap<UUID, DataRecord>();
    private Map<UUID, DataRecord> timedDataMap = new TreeMap<UUID, DataRecord>();
    
    private Connection conn;
    private DatabaseInfo db_info;
    
    private static final File dbFolder = new File(ZvP.getInstance().getDataFolder(), "Statistics/");
    private final String tableName = "zvp_player_stats";
    private String timedTableName;
    private Date statisticEnd;
    private final String infoTableName = "zvp_table_info";
    
    private DatabaseManager(DatabaseInfo info) throws SQLException, ClassNotFoundException {
	
	this.db_info = info;
	this.conn = initializeConnection();
	
	createTables(this.tableName);
	updateMap(this.tableName);
	loadTimedStatistics();
	// String url = "jdbc:" + sqlType + "://" + sqlUrl + ":" + port + "/" + database;
	// mysql://localhost:3306/mc
	
	// TODO remove debug, replace with logger
	System.out.println(this.conn.getMetaData().getDatabaseProductName());
	System.out.println(this.conn.getMetaData().getDatabaseProductVersion());
	System.out.println(this.conn.getMetaData().getDriverVersion());
    }
    
    public static DatabaseManager init(DatabaseInfo info) throws ClassNotFoundException, SQLException {
	if (instance == null) {
	    instance = new DatabaseManager(info);
	}
	return instance;
    }
    
    public static DatabaseManager getManager() {
	return instance;
    }
    
    private Connection initializeConnection() throws SQLException, ClassNotFoundException {
	getFlatFileFolder().mkdirs();
	return DriverManager.getConnection(this.db_info.getConnectionURL(), this.db_info.getUser(), this.db_info.getPass());
    }
    
    private void createTables(String tableName) throws SQLException {
	String createTableSTM = "CREATE TABLE IF NOT EXISTS " + tableName + "(puuid VARCHAR(36) NOT NULL, pkills INTEGER, phkills INTEGER, pdeaths INTEGER, plmoney DECIMAL(8,3), changed TIMESTAMP, added TIMESTAMP, PRIMARY KEY(puuid));";
	String createInfoTableSTM = "CREATE TABLE IF NOT EXISTS " + this.infoTableName + "(id INTEGER NOT NULL AUTO_INCREMENT, start TIMESTAMP NULL DEFAULT NULL, finish TIMESTAMP NULL DEFAULT NULL, PRIMARY KEY(id));";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, createTableSTM, createInfoTableSTM));
    }
    
    public void reload() throws ClassNotFoundException, SQLException {
	instance = new DatabaseManager(ZvPConfig.getDBInfo());
    }
    
    public static File getFlatFileFolder() {
	return dbFolder;
    }
    
    private boolean isTimedStatistics() {
	return this.timedTableName != null && !this.tableName.equals(this.timedTableName) && this.statisticEnd != null && this.statisticEnd.after(new Date());
    }
    
    public void startTimedStatistics(long duration) throws SQLException {
	Date now = new Date();
	
	this.timedTableName = this.tableName + "_" + new SimpleDateFormat("ddMy_Hm").format(now);
	createTables(this.timedTableName);
	this.dataMap.clear();
	
	Date until = new Date(now.getTime() + duration);
	this.statisticEnd = until;
	
	String stmt = "INSERT INTO " + this.infoTableName + "(start, finish) VALUES('" + new Timestamp(now.getTime()) + "', '" + new Timestamp(until.getTime()) + "');";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stmt));
	updateMap(this.timedTableName);
    }
    
    private void loadTimedStatistics() throws SQLException {
	
	Statement stmt = this.conn.createStatement();
	ResultSet result = stmt.executeQuery("SELECT * FROM " + this.infoTableName + ";");
	
	while (result.next()) {
	    Timestamp start = result.getTimestamp("start");
	    Timestamp finish = result.getTimestamp("finish");
	    
	    if (finish.after(new Date())) {
		this.timedTableName = this.tableName + "_" + new SimpleDateFormat("ddMy_Hm").format(start);
		this.statisticEnd = finish;
		updateMap(this.timedTableName);
		break;
	    }
	}
	result.close();
	stmt.close();
    }
    
    public void handleRecord(DataRecord... records) {
	List<DataRecord> insertRecords = new LinkedList<DataRecord>();
	
	System.out.println(isTimedStatistics());
	System.out.println(this.dataMap);
	
	try {
	    if (isTimedStatistics()) {
		for (DataRecord record : records) {
		    DataRecord playerRecord = getRecord(true, record.getPlayerUUID());
		    
		    if (playerRecord == null) {
			insertRecords.add(record);
		    } else {
			DataRecord mergedRecord = DataRecord.merge(playerRecord, record);
			updateRecord(this.timedTableName, mergedRecord);
		    }
		}
		
		if (insertRecords.size() > 0) {
		    insertRecord(this.timedTableName, insertRecords.toArray(new DataRecord[0]));
		}
		updateMap(this.timedTableName);
		insertRecords.clear();
	    }
	    
	    for (DataRecord record : records) {
		DataRecord playerRecord = getRecord(false, record.getPlayerUUID());
		
		if (playerRecord == null) {
		    insertRecords.add(record);
		} else {
		    DataRecord mergedRecord = DataRecord.merge(playerRecord, record);
		    updateRecord(this.tableName, mergedRecord);
		}
	    }
	    
	    if (insertRecords.size() > 0) {
		insertRecord(this.tableName, insertRecords.toArray(new DataRecord[0]));
	    }
	    updateMap(this.tableName);
	    
	} catch (Exception e) {
	    ZvP.getPluginLogger().log(getClass(), Level.WARNING, "Error while queuing data record: " + e.getMessage(), true, false, e);
	}
    }
    
    private void insertRecord(String tableName, DataRecord... records) {
	
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
    
    private void updateRecord(String tableName, DataRecord record) {
	String stm = "UPDATE " + tableName + " SET pkills=" + record.getKills() + ",phkills=" + record.getMaxKills() + ",pdeaths=" + record.getDeaths() + ",plmoney=" + record.getLeftMoney() + " WHERE puuid='" + record.getPlayerUUID().toString() + "';";
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseWriter(this, this.conn, stm));
    }
    
    private DataRecord getRecord(boolean timed, UUID playerUUID) {
	if (timed) {
	    return this.timedDataMap.get(playerUUID);
	} else {
	    return this.dataMap.get(playerUUID);
	}
    }
    
    private void updateMap(String tableName) {
	Bukkit.getScheduler().runTaskAsynchronously(ZvP.getInstance(), new DatabaseReader(this, this.conn, tableName));
    }
    
    @Override
    public void handleException(Exception e, String stmt) {
	ZvP.getPluginLogger().log(getClass(), Level.SEVERE, "Error executing SQL statement: " + e.getMessage() + " for Statement:\n" + stmt, true, false, e);
    }
    
    @Override
    public void onRecordTransmission(DataRecord record, String tableName) {
	if (tableName.equals(this.timedTableName)) {
	    this.timedDataMap.put(record.getPlayerUUID(), record);
	} else if (tableName.equals(this.tableName)) {
	    this.dataMap.put(record.getPlayerUUID(), record);
	} else {
	    try {
		throw new Exception("No table: " + tableName);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
    
    private class DatabaseWriter implements Runnable {
	
	private DatabaseCallback callback;
	private Connection conn;
	private String[] sqlStatement;
	
	public DatabaseWriter(DatabaseCallback callback, Connection conn, String... sqlstm) {
	    this.callback = callback;
	    this.conn = conn;
	    this.sqlStatement = sqlstm;
	}
	
	@Override
	public void run() {
	    try {
		System.out.println("exe write");
		Statement s = this.conn.createStatement();
		for (String stmt : this.sqlStatement) {
		    s.executeUpdate(stmt);
		}
		s.close();
	    } catch (SQLException e) {
		this.callback.handleException(e, Arrays.toString(this.sqlStatement));
	    }
	}
    }
    
    private class DatabaseReader implements Runnable {
	
	private DatabaseCallback callback;
	private Connection conn;
	private String table;
	
	public DatabaseReader(DatabaseCallback callback, Connection conn, String table) {
	    this.callback = callback;
	    this.conn = conn;
	    this.table = table;
	}
	
	@Override
	public void run() {
	    try {
		Statement statement = this.conn.createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM " + this.table + ";");
		System.out.println("exe read");
		while (result.next()) {
		    UUID playerUUID = UUID.fromString(result.getString(1));
		    int kills = result.getInt(2);
		    int maxKills = result.getInt(3);
		    int deaths = result.getInt(4);
		    double leftMoney = result.getDouble(5);
		    long timestamp = result.getTimestamp(7).getTime();
		    
		    this.callback.onRecordTransmission(new DataRecord(playerUUID, kills, maxKills, deaths, leftMoney, timestamp), this.table);
		}
		
		statement.close();
		result.close();
	    } catch (SQLException e) {
		this.callback.handleException(e, "SELECT * FROM " + this.table + ";");
	    }
	}
    }
}