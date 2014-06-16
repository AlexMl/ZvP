package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class KSwordKit implements IZvPKit{

	private final String name;
	
	private final ItemStack[] items;
	
	
	public KSwordKit(String path) {
		
		this.name = "Sword-Kit";
		this.items = new ItemStack[7];

		items[0] = new ItemStack(Material.IRON_SWORD);
		items[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
		items[0].addEnchantment(Enchantment.KNOCKBACK, 2);
		
		items[1] = new ItemStack(Material.LEATHER_HELMET);
		items[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		 
		items[2] = new ItemStack(Material.IRON_CHESTPLATE);
		items[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

		items[3] = new ItemStack(Material.IRON_LEGGINGS);
		items[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		items[4] = new ItemStack(Material.LEATHER_BOOTS);
		items[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		
		items[5] = new ItemStack(Material.COOKED_CHICKEN, 10);
		
		Potion p = new Potion(PotionType.INSTANT_HEAL, 2);
		items[6] = p.toItemStack(2);
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
