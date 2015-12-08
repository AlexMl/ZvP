package me.Aubli.ZvP.Statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DataRecordManager {
    
    private Map<UUID, DataRecord> recordQueue = new HashMap<UUID, DataRecord>();
    
    public DataRecord addKills(UUID playerUUID, int kills) {
	return mergeRecord(playerUUID, kills, kills, 0, 0);
    }
    
    public DataRecord addDeaths(UUID playerUUID, int deaths) {
	return mergeRecord(playerUUID, 0, 0, deaths, 0);
    }
    
    public DataRecord subtractMoney(UUID playerUUID, double money) {
	return addMoney(playerUUID, -money);
    }
    
    public DataRecord addMoney(UUID playerUUID, double money) {
	return mergeRecord(playerUUID, 0, 0, 0, money);
    }
    
    private DataRecord mergeRecord(UUID playerUUID, int kills, int maxKills, int deaths, double money) {
	try {
	    DataRecord record = this.recordQueue.get(playerUUID);
	    DataRecord newRecord = new DataRecord(playerUUID, kills, maxKills, deaths, money);
	    
	    if (record != null) {
		this.recordQueue.put(playerUUID, DataRecord.merge(record, newRecord, true));
	    } else {
		System.out.println("Create new record\n" + newRecord);
		this.recordQueue.put(playerUUID, newRecord);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return this.recordQueue.get(playerUUID);
    }
    
    public void transmitRecords() {
	if (this.recordQueue.size() > 0) {
	    DataRecord[] records = this.recordQueue.values().toArray(new DataRecord[0]);
	    DatabaseManager.getManager().handleRecord(records);
	    this.recordQueue.clear();
	}
    }
}
