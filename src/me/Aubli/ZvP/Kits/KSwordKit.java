package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;


public class KSwordKit implements IZvPKit, Comparable<IZvPKit> {

    private final String name;

    private final ItemStack icon;

    private final ItemStack[] items;

    public KSwordKit() {

	this.name = "Sword-Kit";
	this.icon = new ItemStack(Material.WOOD_SWORD);
	this.items = new ItemStack[7];

	this.items[0] = new ItemStack(Material.IRON_SWORD);
	this.items[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
	this.items[0].addEnchantment(Enchantment.KNOCKBACK, 2);

	this.items[1] = new ItemStack(Material.LEATHER_HELMET);
	this.items[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	this.items[2] = new ItemStack(Material.IRON_CHESTPLATE);
	this.items[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

	this.items[3] = new ItemStack(Material.IRON_LEGGINGS);
	this.items[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

	this.items[4] = new ItemStack(Material.LEATHER_BOOTS);
	this.items[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	this.items[5] = new ItemStack(Material.COOKED_CHICKEN, 10);

	this.items[6] = new Potion(PotionType.INSTANT_HEAL, 1).splash().toItemStack(2);

	new KCustomKit(KitManager.getManager().getKitPath().getAbsolutePath(), this.name, this.icon, 9, this.items);
    }

    @Override
    public void delete() {
	// Does nothing in this case
    }

    @Override
    public boolean isEnabled() {
	return false;
    }

    @Override
    public String getName() {
	return this.name;
    }

    @Override
    public String getPermissionNode() {
	return ""; // Return "" because class is not used
    }

    @Override
    public double getPrice() {
	return 0;
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
