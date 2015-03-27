package me.Aubli.ZvP.Game;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;


public class ArenaScore {
    
    private Arena arena;
    
    private double score;
    
    private HashMap<ZvPPlayer, Double> playerScore;
    
    private final boolean separated;
    
    public ArenaScore(Arena arena, boolean separated) {
	this.arena = arena;
	this.separated = separated;
	
	if (separated) {
	    this.playerScore = new HashMap<ZvPPlayer, Double>();
	    initMap();
	} else {
	    this.score = 0.0;
	}
    }
    
    private void initMap() {
	for (ZvPPlayer player : getArena().getPlayers()) {
	    this.playerScore.put(player, 0.0);
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
	    ZvP.getPluginLogger().log(Level.WARNING, "Error while returning score for Arena:" + this.arena.getID() + "; separated:" + isSeparated() + " player==null:" + (player == null), true, true);
	    return 0.0;
	}
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public boolean isSeparated() {
	return this.separated;
    }
    
    public void addScore(ZvPPlayer player, double score) {
	if (isSeparated()) {
	    this.playerScore.put(player, this.playerScore.get(player) + score);
	    player.updateScoreboard();
	} else {
	    this.score += score;
	    this.arena.updatePlayerBoards();
	}
    }
    
    public void subtractScore(ZvPPlayer player, double score) {
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
    }
}
