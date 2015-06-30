package me.Aubli.ZvP.Game;

import me.Aubli.ZvP.Translation.MessageKeys.status;
import me.Aubli.ZvP.Translation.MessageManager;


public class GameEnums {
    
    public enum ArenaStatus {
	RUNNING(MessageManager.getMessage(status.running)),
	VOTING(MessageManager.getMessage(status.running)),
	BREAKWAITING(MessageManager.getMessage(status.running)),
	WAITING(MessageManager.getMessage(status.waiting)),
	STANDBY(MessageManager.getMessage(status.waiting)),
	STOPED(MessageManager.getMessage(status.stoped));
	
	private String name;
	
	private ArenaStatus(String name) {
	    this.name = name;
	}
	
	public String getName() {
	    return this.name;
	}
    }
    
    public enum ArenaDifficultyLevel {
	EASY(1),
	NORMAL(2),
	HARD(3);
	
	private int level;
	
	private ArenaDifficultyLevel(int level) {
	    this.level = level;
	}
	
	public int getLevel() {
	    return this.level;
	}
    }
    
    public enum ScoreType {
	DEATH_SCORE,
	KILL_SCORE,
	SHOP_SCORE;
    }
}
