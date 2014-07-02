package me.Aubli.ZvP.Game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class ShopManager {

	private HashMap<ItemStack, Double> items;
	
	
	public ShopManager() {
		items = new HashMap<ItemStack, Double>();
	}
	
	
	private Map<ItemStack, Double> loadItems(){
		return null;
	}
	
	
	//Shop control
	public ItemStack[] getItems() {
		return null;
	}
	
	
	
	
	public double getPrice(ItemStack item) {
		return 0;
	}
	
	
}
