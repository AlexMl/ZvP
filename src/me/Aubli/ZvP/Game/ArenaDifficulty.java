package me.Aubli.ZvP.Game;

import java.util.Random;

import me.Aubli.ZvP.Game.GameManager.ArenaDifficultyLevel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


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
	z.setRemoveWhenFarAway(false);
	z.setTarget(getArena().getRandomPlayer().getPlayer());
	
	boolean setBaby = false;
	boolean setVillager = false;
	boolean setCanPickupItems = false;
	double maxHealth = 20D;
	double velocity = 1D;
	PotionEffect potionEffect = null;
	String name = "";
	
	switch (getDifficulty().getLevel()) {
	    case 1:
		setBaby = false;
		setVillager = (this.rand.nextBoolean() && this.rand.nextBoolean());
		setCanPickupItems = false;
		maxHealth = 18D;
		velocity = 0.8D;
		name = "EASY";
		break;
	    
	    case 2:
		switch (this.rand.nextInt(7)) {
		    case 0:
			setBaby = true;
			setCanPickupItems = true;
			maxHealth = 40D;
			velocity = 1.2D;
			break;
		    case 1:
			setBaby = true;
			setCanPickupItems = false;
			velocity = 0.75D;
			break;
		    case 2:
			setBaby = false;
			setCanPickupItems = true;
			setVillager = false;
			maxHealth = 15D;
			break;
		    case 3:
			setBaby = false;
			setCanPickupItems = false;
			setVillager = true;
			maxHealth = 30D;
			break;
		    default:
			break;
		}
		name = "NORMAL";
		break;
	    
	    case 3:
		switch (this.rand.nextInt(7)) {
		    case 0:
			setBaby = true;
			setCanPickupItems = true;
			maxHealth = 40D;
			velocity = 1.2D;
			potionEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 2);
			break;
		    case 1:
			setBaby = true;
			setCanPickupItems = true;
			maxHealth = 30D;
			velocity = 1D;
			potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100 * 20, 2);
			break;
		    case 2:
			setBaby = false;
			setCanPickupItems = true;
			setVillager = false;
			maxHealth = 20D;
			velocity = 1.2D;
			potionEffect = new PotionEffect(PotionEffectType.HEAL, 20 * 20, 2);
			break;
		    case 3:
			setBaby = false;
			setCanPickupItems = true;
			setVillager = true;
			maxHealth = 35D;
			velocity = 1.1D;
			potionEffect = new PotionEffect(PotionEffectType.SPEED, 20 * 20, 2);
			break;
		    default:
			maxHealth = 30D;
			setCanPickupItems = true;
			break;
		}
		name = "HARD";
		break;
	}
	
	z.setCustomName(name);
	z.setMaxHealth(maxHealth);
	z.setVelocity(z.getVelocity().multiply(velocity));
	z.setBaby(setBaby);
	z.setVillager(setVillager);
	z.setCanPickupItems(setCanPickupItems);
	z.addPotionEffect(potionEffect, true);
    }
}
