package me.Aubli.ZvP.Statistic;

import java.sql.Timestamp;
import java.util.UUID;


public class DataRecord {
    
    private UUID playerUUID;
    private int kills;
    private int deaths;
    private double leftMoney;
    private Timestamp timestamp;
    
    public DataRecord(UUID playerUUID, int kills, int deaths, double leftMoney) {
	this(playerUUID, kills, deaths, leftMoney, System.currentTimeMillis());
    }
    
    public DataRecord(UUID playerUUID, int kills, int deaths, double leftMoney, long timestamp) {
	this.playerUUID = playerUUID;
	this.kills = kills;
	this.deaths = deaths;
	this.leftMoney = leftMoney;
	this.timestamp = new Timestamp(timestamp);
    }
    
    public UUID getPlayerUUID() {
	return this.playerUUID;
    }
    
    public int getKills() {
	return this.kills;
    }
    
    public int getDeaths() {
	return this.deaths;
    }
    
    public double getLeftMoney() {
	return this.leftMoney;
    }
    
    public Timestamp getTimestamp() {
	return this.timestamp;
    }
}
