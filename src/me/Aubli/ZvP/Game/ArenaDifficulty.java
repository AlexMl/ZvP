package me.Aubli.ZvP.Game;

import java.util.Random;

import me.Aubli.ZvP.Game.GameManager.ArenaDifficultyLevel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;


public class ArenaDifficulty {
    
    private Arena arena;
    
    private ArenaDifficultyLevel arenaLevel;
    
    private Random rand;
    
    public ArenaDifficulty(Arena arena, ArenaDifficultyLevel level) {
	this.arena = arena;
	this.arenaLevel = level;
	
	this.rand = new Random();
    }
    
    public Arena getArena() {
	return this.arena;
    }
    
    public ArenaDifficultyLevel getDifficulty() {
	return this.arenaLevel;
    }
    
    public void customizeEntity(Entity zombie) {
	Zombie z = (Zombie) zombie;
	// TODO apply difficulty level
	z.setRemoveWhenFarAway(false);
	z.setTarget(getArena().getRandomPlayer().getPlayer());
	
	switch (this.rand.nextInt(7)) {
	    case 0:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setMaxHealth(40D);
		z.setVelocity(z.getVelocity().multiply(1.5D));
		// z.setCustomName("0");
		break;
	    case 1:
		z.setBaby(true);
		z.setCanPickupItems(false);
		z.setVillager(false);
		z.setHealth(20D);
		z.setVelocity(z.getVelocity().multiply(0.75D));
		// z.setCustomName("1");
		break;
	    case 2:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(true);
		z.setHealth(10D);
		// z.setCustomName("2");
		break;
	    case 3:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(true);
		z.setHealth(15D);
		// z.setCustomName("3");
		break;
	    case 4:
		z.setBaby(false);
		z.setCanPickupItems(true);
		z.setVillager(false);
		z.setMaxHealth(30D);
		// z.setCustomName("4");
		break;
	    default:
		z.setBaby(false);
		z.setCanPickupItems(false);
		z.setVillager(false);
		z.setHealth(20D);
		// z.setCustomName("default");
		break;
	}
    }
    
}
