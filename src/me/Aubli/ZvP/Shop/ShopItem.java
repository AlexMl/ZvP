package me.Aubli.ZvP.Shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

	private final ItemStack item;

	private double price;
	
	
	public ShopItem(ItemStack item, double price) {
		this.item = item.clone();
		this.price = price;
	}
	
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	
	public double getPrice() {
		return this.price;
	}
	
	public ItemStack getItem() {
		return this.item;
	}


	public Material getType() {
		return getItem().getType();
	}
	
}
