package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class KBowKit implements IZvPKit, Comparable<IZvPKit> {
    
    private final String name;
    
    private final ItemStack icon;
    
    private final ItemStack[] items;
    
    public KBowKit() {
	
	this.name = "Bow-Kit";
	this.icon = new ItemStack(Material.BOW);
	this.items = new ItemStack[9];
	
	this.items[0] = new ItemStack(Material.BOW);
	this.items[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);
	
	this.items[1] = new ItemStack(Material.ARROW, 2);
	
	this.items[2] = new ItemStack(Material.STONE_SWORD);
	this.items[2].addEnchantment(Enchantment.KNOCKBACK, 1);
	this.items[2].addEnchantment(Enchantment.DAMAGE_ALL, 1);
	
	this.items[3] = new ItemStack(Material.LEATHER_HELMET);
	this.items[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	
	this.items[4] = new ItemStack(Material.IRON_CHESTPLATE);
	this.items[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
	
	this.items[5] = new ItemStack(Material.LEATHER_LEGGINGS);
	this.items[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	
	this.items[6] = new ItemStack(Material.CHAINMAIL_BOOTS);
	this.items[6].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
	
	this.items[7] = new ItemStack(Material.GOLDEN_APPLE, 5);
	
	this.items[8] = new ItemStack(Material.COOKED_BEEF, 5);
    }
    
    @Override
    public void delete() {
	// Does nothing in this case
    }
    
    @Override
    public String getName() {
	return this.name;
    }
    
    @Override
    public ItemStack getIcon() {
	return this.icon;
    }
    
    @Override
    public ItemStack[] getContents() {
	return this.items.clone();
    }
    
    @Override
    public int compareTo(IZvPKit o) {
	return getName().compareTo(o.getName());
    }
}
