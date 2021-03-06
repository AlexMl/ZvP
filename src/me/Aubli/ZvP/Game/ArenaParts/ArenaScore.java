package me.Aubli.ZvP.Game.ArenaParts;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ScoreType;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.EconomyResponse;


public class ArenaScore {
    
    private Arena arena;
    
    private double score;
    
    private HashMap<ZvPPlayer, Double> playerScore;
    private HashMap<ZvPPlayer, Double> originScore;
    
    private final boolean separated;
    private final boolean vaultEcon;
    
    public ArenaScore(Arena arena, boolean separated, boolean econSupport, boolean econGameIntegration) {
	this.arena = arena;
	this.vaultEcon = (econSupport && econGameIntegration);
	this.separated = useVaultEconomy() ? true : separated;
	
	if (isSeparated()) {
	    initMap();
	} else {
	    this.score = 0.0;
	}
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Finished init of " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + " for arena " + arena.getID(), true);
    }
    
    private void initMap() {
	this.playerScore = new HashMap<ZvPPlayer, Double>();
	this.originScore = new HashMap<ZvPPlayer, Double>();
	
	for (ZvPPlayer player : getArena().getPlayers()) {
	    initPlayer(player);
	}
    }
    
    private void initPlayer(ZvPPlayer player) {
	if (useVaultEconomy()) {
	    this.playerScore.put(player, ZvP.getEconProvider().getBalance(player.getPlayer()));
	    this.originScore.put(player, ZvP.getEconProvider().getBalance(player.getPlayer()));
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Finished init for " + player.getName() + ": " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")), true, true);
	} else {
	    this.playerScore.put(player, 0.0);
	    this.originScore.put(player, 0.0);
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Finished init for " + player.getName() + ": " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")), true, true);
	}
    }
    
    public void reInitPlayer(ZvPPlayer player) {
	if (isSeparated()) {
	    initPlayer(player);
	}
    }
    
    public double getScore(ZvPPlayer player) {
	if (player == null && isSeparated()) {
	    double score = 0.0;
	    for (Entry<ZvPPlayer, Double> entry : this.playerScore.entrySet()) {
		score += entry.getValue();
	    }
	    return score;
	} else if (!isSeparated()) {
	    return this.score;
	} else if (isSeparated() && player != null) {
	    return this.playerScore.get(player);
	} else {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while returning score for Arena:" + this.arena.getID() + "; separated:" + isSeparated() + " player==null:" + (player == null), true, false);
	    return 0.0;
	}
    }
    
    public double getScoreDiffSum() {
	if (isSeparated()) {
	    double money = 0;
	    
	    for (Entry<ZvPPlayer, Double> entry : this.playerScore.entrySet()) {
		money += (entry.getValue() - this.originScore.get(entry.getKey()));
	    }
	    return money;
	} else {
	    return this.score;
	}
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public boolean isSeparated() {
	return this.separated;
    }
    
    public boolean useVaultEconomy() {
	return this.vaultEcon;
    }
    
    public void addScore(ZvPPlayer player, double score, ScoreType type) {
	if (isSeparated()) {
	    this.playerScore.put(player, this.playerScore.get(player) + score);
	    player.updateScoreboard();
	} else {
	    this.score += score;
	    this.arena.updatePlayerBoards();
	}
	
	if (useVaultEconomy()) {
	    EconomyResponse response = ZvP.getEconProvider().depositPlayer(player.getPlayer(), score);
	    printResponse(response);
	    
	    if (!response.transactionSuccess()) {
		player.sendMessage(MessageManager.getMessage(error.transaction_failed));
		ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! " + response.errorMessage + "; Task:" + type.name(), false);
	    }
	}
	this.arena.getRecordManager().addMoney(player.getUuid(), score);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "A" + getArena().getID() + ": " + player.getName() + " ++ " + score + " --> " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + "; Task:" + type, true);
    }
    
    public void subtractScore(ZvPPlayer player, double score, ScoreType type) {
	if (isSeparated()) {
	    double prevScore = this.playerScore.get(player);
	    if (prevScore <= score) {
		this.playerScore.put(player, 0.0);
	    } else {
		this.playerScore.put(player, this.playerScore.get(player) - score);
	    }
	    player.updateScoreboard();
	} else {
	    if (score >= this.score) {
		this.score = 0;
	    } else {
		this.score -= score;
	    }
	    this.arena.updatePlayerBoards();
	}
	
	if (useVaultEconomy()) {
	    if (ZvP.getEconProvider().has(player.getPlayer(), score)) {
		EconomyResponse response = ZvP.getEconProvider().withdrawPlayer(player.getPlayer(), score);
		printResponse(response);
		
		if (!response.transactionSuccess()) {
		    player.sendMessage(MessageManager.getMessage(error.transaction_failed));
		    ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! " + response.errorMessage + "; Task:" + type.name(), false);
		}
	    } else if (type == ScoreType.DEATH_SCORE) {
		// Player does not have enough money! Should only fire on death. Set balance to zero!
		subtractScore(player, ZvP.getEconProvider().getBalance(player.getPlayer()), type);
		return;
	    } else {
		// This case should never be activated!
		ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! Insufficent Balance!; Task:" + type.name(), false);
	    }
	}
	this.arena.getRecordManager().subtractMoney(player.getUuid(), score);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "A" + getArena().getID() + ": " + player.getName() + " -- " + score + " --> " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + "; Task:" + type, true);
    }
    
    private void printResponse(EconomyResponse res) {
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "EconomyResponse: " + res.type + " Amount:" + res.amount + " ---> " + res.balance, true);
    }
}
