package me.Aubli.ZvP.Kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KNullKit implements IZvPKit{

	private final String name;
	
	private final ItemStack icon;
	
	private final ItemStack[] content;
	
	
	public KNullKit() {
		this.name = "No Kit";
		this.icon = new ItemStack(Material.ITEM_FRAME);
		this.content = new ItemStack[1];
		
		content[0] = new ItemStack(Material.AIR);
	}
	
	
	@Override
	public void delete() {
		// Does nothing
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
		return content;
	}

}
