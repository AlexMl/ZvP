package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KBowKit implements IZvPKit{

	private final String name;

	private final ItemStack icon;
	
	private final ItemStack[] items;
	
	
	public KBowKit() {
		
		this.name = "Bow-Kit";
		this.icon = new ItemStack(Material.BOW);
		this.items = new ItemStack[9];		
		
		items[0] = new ItemStack(Material.BOW);
		items[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);

		items[1] = new ItemStack(Material.ARROW, 2);
		
		items[2] = new ItemStack(Material.STONE_SWORD);
		items[2].addEnchantment(Enchantment.KNOCKBACK, 1);
		items[2].addEnchantment(Enchantment.DAMAGE_ALL, 1);
		
		items[3] = new ItemStack(Material.LEATHER_HELMET);
		items[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		 
		items[4] = new ItemStack(Material.IRON_CHESTPLATE);
		items[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

		items[5] = new ItemStack(Material.LEATHER_LEGGINGS);
		items[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		items[6] = new ItemStack(Material.CHAINMAIL_BOOTS);
		items[6].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		
		items[7] = new ItemStack(Material.GOLDEN_APPLE, 5);
		
		items[8] = new ItemStack(Material.COOKED_BEEF, 5);
	}
	
	
	
	@Override
	public void delete() {
		// Does nothing in this case
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public ItemStack[] getContents() {
		return items.clone();
	}	
}
