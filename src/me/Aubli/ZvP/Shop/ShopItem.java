package me.Aubli.ZvP.Shop;

import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;


public class ShopItem {

    private final ItemStack item;

    private double buyPrice;
    private double sellPrice;

    private final ItemCategory category;

    public ShopItem(ItemStack item, ItemCategory cat, double buyPrice, double sellPrice) {
	this.item = item.clone();
	this.category = cat;
	this.buyPrice = buyPrice;
	this.sellPrice = sellPrice;
    }

    public void setBuyPrice(double price) {
	this.buyPrice = price;
    }

    public double getBuyPrice() {
	return this.buyPrice;
    }

    public void setSellPrice(double price) {
	this.sellPrice = price;
    }

    public double getSellPrice() {
	return this.sellPrice;
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

    public boolean isPotion() {
	return (getItem().getItemMeta() instanceof PotionMeta);
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "[" + getItem().getType().toString() + " ," + getCategory().toString() + " ,sell:" + getSellPrice() + " ,buy:" + getBuyPrice() + "]";
    }
}
