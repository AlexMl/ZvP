package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.zombie;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangelistener implements Listener{

	public SignChangelistener(zombie plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){

		String materialLine = "";
		String[] materialLineArray;
		
		if(event.getLine(0).equalsIgnoreCase("[zvp]")){
			event.setLine(0, ChatColor.GOLD + "[" + ChatColor.AQUA + "Z" + ChatColor.DARK_RED + "v" + ChatColor.AQUA + "P" + ChatColor.GOLD + "]");
			if(event.getLine(3)!=""){
			
				materialLine = event.getLine(3);
				materialLineArray = materialLine.split(":");
				
				if(materialLineArray.length == 1){
					String ml = materialLineArray[0];
					if(ml.equalsIgnoreCase("bow")){
						event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.bow")) + ChatColor.DARK_GREEN + "$");
					}
					if(ml.equalsIgnoreCase("arrow64")){
						event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.arrow64")) + ChatColor.DARK_GREEN + "$");
					}
					if(ml.equalsIgnoreCase("arrow32")){
						event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.arrow32")) + ChatColor.DARK_GREEN + "$");
					}
					if(ml.equalsIgnoreCase("brewingstand")){
						event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.brewingStand")) + ChatColor.DARK_GREEN + "$");
					}
				}else{
					if(materialLineArray[0].equalsIgnoreCase("w")){
						if(materialLineArray[1].equalsIgnoreCase("sword")){
							//Holzschwert
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.woodenSword")) + ChatColor.DARK_GREEN + "$");
						}							
					}
					if(materialLineArray[0].equalsIgnoreCase("s")){
						if(materialLineArray[1].equalsIgnoreCase("sword")){
							//Steinschwert
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.stoneSword")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("axe")){
							//SteinAxt
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.stoneAxe")) + ChatColor.DARK_GREEN + "$");
						}
					}
					if(materialLineArray[0].equalsIgnoreCase("i")){
						if(materialLineArray[1].equalsIgnoreCase("sword")){
							//eisenschwert
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironSword")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("helmet")){
							//eisenhelm
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironHelmet")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("chestplate")){
							//eisenbrustpanzer
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironChestplate")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("leggings")){
							//eisenleggings
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironLeggings")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("boots")){
							//eisenstiefel
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironBoots")) + ChatColor.DARK_GREEN + "$");
						}
					}
					if(materialLineArray[0].equalsIgnoreCase("l")){
						if(materialLineArray[1].equalsIgnoreCase("helmet")){
							//lederhelm
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherHelmet")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("chestplate")){
							//lederbrustpanzer
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherChestplate")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("leggings")){
							//lederleggings
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherLeggings")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("boots")){
							//lederstiefel
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherBoots")) + ChatColor.DARK_GREEN + "$");
						}
					}
					if(materialLineArray[0].equalsIgnoreCase("p")){
						if(materialLineArray[1].equalsIgnoreCase("regen")){
							//Regeneration
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionRegeneration")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("heal")){
							//Heilung
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionHealing")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("speed")){
							//Geschwindigkeit
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionSpeed")) + ChatColor.DARK_GREEN + "$");
						}
						if(materialLineArray[1].equalsIgnoreCase("strenght")){
							//Stärke
							event.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionStrenght")) + ChatColor.DARK_GREEN + "$");
						}
					}
				}
			}
		}
	}	
	private zombie plugin;
}
