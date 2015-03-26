package me.Aubli.ZvP.Game;

import java.util.HashMap;


public class ArenaScore {
    
    private Arena arena;
    
    private double score;
    
    private HashMap<ZvPPlayer, Double> playerScore;
    
    private final boolean seperated;
    
    public ArenaScore(Arena arena, boolean seperated) {
	this.arena = arena;
	this.seperated = seperated;
	
	if (seperated) {
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
    
    public double getScore() {
	return this.score;
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public boolean isSeperated() {
	return this.seperated;
    }
    
    public void addScore(ZvPPlayer player, double score) {
	if (isSeperated()) {
	    this.playerScore.put(player, this.playerScore.get(player) + score);
	    player.updateScoreboard();
	} else {
	    this.score += score;
	    this.arena.updatePlayerBoards();
	}
    }
    
    public void subtractScore(ZvPPlayer player, double score) {
	if (isSeperated()) {
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
