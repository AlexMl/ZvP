package me.Aubli.ZvP.Game;

public class ArenaScore {
    
    private Arena arena;
    
    private double score;
    
    public ArenaScore(Arena arena) {
	this.arena = arena;
	this.score = 0.0;
    }
    
    public double getScore() {
	return this.score;
    }
    
    public Arena getArena() {
	return this.arena;
    }
}
