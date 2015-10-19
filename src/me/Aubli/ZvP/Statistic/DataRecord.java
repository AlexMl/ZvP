package me.Aubli.ZvP.Statistic;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;


public class DataRecord {
    
    private UUID playerUUID;
    private int kills;
    private int deaths;
    private double leftMoney;
    private Date timestamp;
    
    public DataRecord(Player player, int kills, int deaths, double leftMoney) {
	this.playerUUID = player.getUniqueId();
	this.kills = kills;
	this.deaths = deaths;
	this.leftMoney = leftMoney;
	this.timestamp = new Date();
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
    
    public Date getTimestamp() {
	return this.timestamp;
    }
}
