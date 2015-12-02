package me.Aubli.ZvP.Statistic;

import java.sql.Timestamp;
import java.util.UUID;


public class DataRecord {
    
    private UUID playerUUID;
    private int kills;
    private int maxKills;
    private int deaths;
    private double leftMoney;
    private Timestamp timestamp;
    
    public DataRecord(UUID playerUUID, int kills, int maxKills, int deaths, double leftMoney) {
	this(playerUUID, kills, maxKills, deaths, leftMoney, System.currentTimeMillis());
    }
    
    public DataRecord(UUID playerUUID, int kills, int maxKills, int deaths, double leftMoney, long timestamp) {
	this.playerUUID = playerUUID;
	this.kills = kills;
	this.maxKills = maxKills;
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
    
    public int getMaxKills() {
	return this.maxKills;
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
    
    public Object getValue(DataRecordType type) {
	switch (type) {
	    case KILLS:
		return getKills();
		
	    case KILLRECORD:
		return getMaxKills();
		
	    case DEATHS:
		return getDeaths();
		
	    case LEFTMONEY:
		return getLeftMoney();
		
	    default:
		throw new IllegalArgumentException(type.name() + " is not a supported Type!");
	}
    }
    
    @Override
    public String toString() {
	return getClass().getSimpleName() + "[P:" + getPlayerUUID() + ", K:" + getKills() + ", hK:" + getMaxKills() + ", D:" + getDeaths() + ", lM:" + getLeftMoney() + ", " + getTimestamp().toString() + "]";
    }
    
    public static DataRecord merge(DataRecord oldRecord, DataRecord newRecord) throws Exception {
	
	if (!oldRecord.getPlayerUUID().equals(newRecord.getPlayerUUID())) {
	    throw new Exception("Datarecords can not be merged! Different playerUUIDs!");
	}
	
	if (oldRecord.getTimestamp().after(newRecord.getTimestamp())) {
	    DataRecord temp = oldRecord;
	    oldRecord = newRecord;
	    newRecord = temp;
	}
	return new DataRecord(oldRecord.getPlayerUUID(), oldRecord.getKills() + newRecord.getKills(), (newRecord.getMaxKills() > oldRecord.getMaxKills() ? newRecord.getMaxKills() : oldRecord.getMaxKills()), oldRecord.getDeaths() + newRecord.getDeaths(), newRecord.getLeftMoney(), newRecord.getTimestamp().getTime());
    }
}
