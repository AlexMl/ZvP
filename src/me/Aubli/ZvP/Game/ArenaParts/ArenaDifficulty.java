package me.Aubli.ZvP.Game.ArenaParts;

import java.util.Random;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaDifficultyLevel;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
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

    public double getExpFactor() {
	return (getDifficulty().getLevel() + 3.0) / 2.0;
    }

    public double getMoneyFactor() {
	return (getDifficulty().getLevel() + 1.0) / 2.0;
    }

    public double getZombieStrengthFactor() {
	return ((((getArena().getCurrentRound() * 5.0 + getArena().getCurrentWave()) - 6.0) + 5.0) / 100.0 + 1.0) * ((getDifficulty().getLevel() + 1.0) / 2.0);
    }

    public double getZombieHealthFactor() {
	return ((((getArena().getCurrentRound() * 5.0 + getArena().getCurrentWave()) - 6.0) + 0.0) / 100.0 + 1.0) * ((getDifficulty().getLevel() + 1.0) / 2.0);
    }

    public void customizeEntity(Entity zombie) {
	Zombie z = (Zombie) zombie;
	z.setRemoveWhenFarAway(false);
	z.setTarget(getArena().getRandomPlayer().getPlayer());

	boolean setBaby = false;
	boolean setVillager = false;
	boolean setCanPickupItems = false;
	double maxHealth = 20D;
	ItemStack[] armorContent = new ItemStack[4];
	Float dropchance = 0.25F;
	PotionEffect potionEffect = null;

	switch (getDifficulty().getLevel()) {
	    case 1:
		setBaby = false;
		setVillager = (this.rand.nextBoolean() && this.rand.nextBoolean());
		setCanPickupItems = false;
		maxHealth = 15D;
		dropchance = 0F;
		break;

	    case 2:
		switch (this.rand.nextInt(7)) {
		    case 0:
			setBaby = true;
			setCanPickupItems = true;
			maxHealth = 40D;
			dropchance = 0.35F;
			break;
		    case 1:
			setBaby = true;
			setCanPickupItems = false;
			maxHealth = 20D;
			dropchance = 0.35F;
			break;
		    case 2:
			setBaby = false;
			setCanPickupItems = true;
			setVillager = false;
			maxHealth = 15D;
			dropchance = 0.20F;
			armorContent = getRandomArmor();
			break;
		    case 3:
			setBaby = false;
			setCanPickupItems = false;
			setVillager = true;
			maxHealth = 30D;
			dropchance = 0.18F;
			armorContent = getRandomArmor();
			break;
		    default:
			break;
		}
		break;

	    case 3:
		switch (this.rand.nextInt(7)) {
		    case 0:
			setBaby = true;
			setVillager = true;
			maxHealth = 40D;
			potionEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 2);
			dropchance = 0.1F;
			break;
		    case 1:
			setBaby = true;
			setVillager = false;
			maxHealth = 30D;
			potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100 * 20, 2);
			dropchance = 0.1F;
			break;
		    case 2:
			setBaby = false;
			setVillager = false;
			maxHealth = 20D;
			potionEffect = new PotionEffect(PotionEffectType.HEAL, 20 * 20, 2);
			dropchance = 0F;
			break;
		    case 3:
			setBaby = false;
			setVillager = true;
			maxHealth = 35D;
			potionEffect = new PotionEffect(PotionEffectType.SPEED, 20 * 20, 2);
			dropchance = 0F;
			break;
		    default:
			maxHealth = 30D;
			dropchance = 0F;
			break;
		}
		armorContent = getRandomArmor();
		setCanPickupItems = true;
		break;
	}

	if (getArena().getConfig().isIncreaseDifficulty()) {
	    maxHealth *= getZombieHealthFactor();
	}
	for (int i = 0; i < armorContent.length; i++) {
	    if (armorContent[i] == null) {
		armorContent[i] = new ItemStack(Material.AIR);
	    }
	}

	z.setMaxHealth(Math.ceil(maxHealth));
	z.setHealth(maxHealth);
	z.getEquipment().setArmorContents(armorContent);
	z.getEquipment().setBootsDropChance(dropchance);
	z.getEquipment().setChestplateDropChance(dropchance);
	z.getEquipment().setHelmetDropChance(dropchance);
	z.getEquipment().setLeggingsDropChance(dropchance);
	z.getEquipment().setItemInHandDropChance(dropchance * 2);
	z.setBaby(setBaby);
	z.setVillager(setVillager);
	z.setCanPickupItems(setCanPickupItems);

	if (potionEffect != null) {
	    z.addPotionEffect(potionEffect, true);
	}
    }

    private ItemStack[] getRandomArmor() {
	ItemStack[] content = new ItemStack[4];

	int i = this.rand.nextInt(10) + getDifficulty().getLevel();
	switch (i) {
	    case 1:
		content[0] = new ItemStack(Material.LEATHER_HELMET);
		content[2] = new ItemStack(Material.IRON_LEGGINGS);
		break;

	    case 2:
		content[0] = new ItemStack(Material.IRON_HELMET);
		content[1] = new ItemStack(Material.LEATHER_CHESTPLATE);
		content[3] = new ItemStack(Material.LEATHER_BOOTS);
		break;

	    case 4:
		content[1] = new ItemStack(Material.IRON_CHESTPLATE);
		content[2] = new ItemStack(Material.IRON_LEGGINGS);
		break;

	    case 6:
		content[0] = new ItemStack(Material.GOLD_HELMET);
		content[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
		break;

	    case 10:
		content[0] = new ItemStack(Material.CHAINMAIL_HELMET);
		content[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
		content[3] = new ItemStack(Material.DIAMOND_BOOTS);
		break;

	    case 11:
		content[0] = new ItemStack(Material.CHAINMAIL_HELMET);
		content[1] = new ItemStack(Material.IRON_CHESTPLATE);
		content[2] = new ItemStack(Material.IRON_LEGGINGS);
		content[3] = new ItemStack(Material.CHAINMAIL_BOOTS);
		break;

	    case 12:
		content[0] = new ItemStack(Material.DIAMOND_HELMET);
		content[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
		content[2] = new ItemStack(Material.DIAMOND_LEGGINGS);
		content[3] = new ItemStack(Material.DIAMOND_BOOTS);
		break;

	    default:
		content = new ItemStack[4];
		break;
	}

	return content;
    }
}
