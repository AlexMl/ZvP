package me.Aubli.ZvP.Statistic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.Aubli.ZvP.ZvP;


public class DatabaseManager {
    
    private static DatabaseManager instance;
    
    private Connection conn;
    private DatabaseInfo db_info;
    
    private File dbFolder = new File(ZvP.getInstance().getDataFolder(), "Statistics/");
    public static final String tableName = "zvp_player_stats";
    
    public DatabaseManager(DatabaseInfo info) throws SQLException, ClassNotFoundException {
	
	instance = this;
	
	this.db_info = info;
	this.conn = initializeConnection();
	createTable();
	// String url = "jdbc:" + sqlType + "://" + sqlUrl + ":" + port + "/" + database;
	// mysql://localhost:3306/mc
	System.out.println(this.conn.getMetaData().getDatabaseProductName());
    }
    
    private Connection initializeConnection() throws SQLException, ClassNotFoundException {
	getFlatFileFolder().mkdirs();
	return DriverManager.getConnection(this.db_info.getConnectionURL(), this.db_info.getUser(), this.db_info.getPass());
    }
    
    private void createTable() throws SQLException {
	String createTableSTM = "CREATE TABLE IF NOT EXISTS " + tableName + "(puuid VARCHAR(36) NOT NULL, pkills INTEGER, pdeaths INTEGER, plmoney DECIMAL(10,5), changed TIMESTAMP, added TIMESTAMP, PRIMARY KEY(puuid));";
	this.conn.createStatement().execute(createTableSTM);
    }
    
    public static DatabaseManager getManager() {
	return instance;
    }
    
    public File getFlatFileFolder() {
	return this.dbFolder;
    }
}
