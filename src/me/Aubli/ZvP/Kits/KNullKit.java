package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class KNullKit implements IZvPKit {
    
    private final String name;
    
    private final ItemStack icon;
    
    private final ItemStack[] content;
    
    public KNullKit() {
	this.name = "No Kit";
	this.icon = new ItemStack(Material.ITEM_FRAME);
	this.content = new ItemStack[1];
	
	this.content[0] = new ItemStack(Material.AIR);
    }
    
    @Override
    public void delete() {
	// Does nothing
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
    public ItemStack getIcon() {
	return this.icon;
    }
    
    @Override
    public ItemStack[] getContents() {
	return this.content;
    }
    
}
