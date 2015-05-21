package me.Aubli.ZvP.Game;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.EconomyResponse;


public class ArenaScore {
    
    public enum ScoreType {
	ZOMBIE_SCORE,
	SHOP_SCORE;
    }
    
    private Arena arena;
    
    private double score;
    
    private HashMap<ZvPPlayer, Double> playerScore;
    
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
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Finished init of " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + " for arena " + arena.getID(), true);
    }
    
    private void initMap() {
	this.playerScore = new HashMap<ZvPPlayer, Double>();
	
	for (ZvPPlayer player : getArena().getPlayers()) {
	    initPlayer(player);
	}
    }
    
    private void initPlayer(ZvPPlayer player) {
	if (useVaultEconomy()) {
	    this.playerScore.put(player, ZvP.getEconProvider().getBalance(player.getPlayer()));
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "Finished init for " + player.getName() + ": " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")), true, true);
	} else {
	    this.playerScore.put(player, 0.0);
	    ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "Finished init for " + player.getName() + ": " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")), true, true);
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
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while returning score for Arena:" + this.arena.getID() + "; separated:" + isSeparated() + " player==null:" + (player == null), true, true);
	    return 0.0;
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
		player.sendMessage(MessageManager.getMessage("error:transaction_failed"));
		ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! " + response.errorMessage + "; Task:" + type.name(), false);
	    }
	}
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
	    EconomyResponse response = ZvP.getEconProvider().withdrawPlayer(player.getPlayer(), score);
	    printResponse(response);
	    
	    if (!response.transactionSuccess()) {
		player.sendMessage(MessageManager.getMessage("error:transaction_failed"));
		ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! " + response.errorMessage + "; Task:" + type.name(), false);
	    }
	}
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "A" + getArena().getID() + ": " + player.getName() + " -- " + score + " --> " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + "; Task:" + type, true);
    }
    
    private void printResponse(EconomyResponse res) {
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "EconomyResponse: " + res.type + " Amount:" + res.amount + " ---> " + res.balance, true);
    }
}
