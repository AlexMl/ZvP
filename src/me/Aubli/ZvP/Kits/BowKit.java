package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BowKit implements IZvPKit{

	private final String name;
	
	private final ItemStack[] items;
	
	
	public BowKit(String path) {
		
		this.name = "Bow-Kit";
		this.items = new ItemStack[8];

		items[0] = new ItemStack(Material.BOW);
		items[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);

		items[1] = new ItemStack(Material.ARROW, 2);
		
		items[2] = new ItemStack(Material.LEATHER_HELMET);
		items[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		 
		items[3] = new ItemStack(Material.LEATHER_CHESTPLATE);
		items[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

		items[4] = new ItemStack(Material.LEATHER_LEGGINGS);
		items[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		items[5] = new ItemStack(Material.LEATHER_BOOTS);
		items[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		items[6] = new ItemStack(Material.GOLDEN_APPLE, 5);
		
		items[7] = new ItemStack(Material.COOKED_BEEF, 5);
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
	public ItemStack[] getContents() {
		return items.clone();
	}

}
