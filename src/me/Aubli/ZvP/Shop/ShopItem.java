package me.Aubli.ZvP.Shop;

import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

	private final ItemStack item;

	private double price;
	
	private final ItemCategory category;
	
	public ShopItem(ItemStack item, ItemCategory cat, double price) {
		this.item = item.clone();
		this.category = cat;
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
	
	public ItemCategory getCategory() {
		return this.category;
	}

	public Material getType() {
		return getItem().getType();
	}
	
}
