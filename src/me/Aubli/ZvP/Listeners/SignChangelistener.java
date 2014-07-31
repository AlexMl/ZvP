package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SignChangelistener implements Listener{

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		
		Player eventPlayer = event.getPlayer();
		
		if(event.getLine(0).equalsIgnoreCase("[zvp]")){
			if(eventPlayer.hasPermission("zvp.manage.sign")){
				if(!event.getLine(1).isEmpty()){
					if(!event.getLine(2).isEmpty()){
						if(event.getLine(3).equalsIgnoreCase("interact") || event.getLine(3).equalsIgnoreCase("info") || event.getLine(3).equalsIgnoreCase("shop")){
							int arenaID = Integer.parseInt(event.getLine(1));
							int lobbyID = Integer.parseInt(event.getLine(2));
							
							Arena a = GameManager.getManager().getArena(arenaID);
							Lobby l = GameManager.getManager().getLobby(lobbyID);
							
							SignType type;
							
							if(event.getLine(3).equalsIgnoreCase("interact")) {
								type = SignType.INTERACT_SIGN;
							}else if(event.getLine(3).equalsIgnoreCase("info")) {
								type = SignType.INFO_SIGN;
							}else if(event.getLine(3).equalsIgnoreCase("shop")) {
								type = SignType.SHOP_SIGN;
							}else {
								return;
							}							
							
							if(a!=null){
								if(l!=null){
									boolean success = SignManager.getManager().createSign(type, event.getBlock().getLocation().clone(), a, l, null);
									
									if(success){
										event.setLine(0, ZvP.getPrefix());
										event.setLine(1, "Arena: " + arenaID);
										
										if(type==SignType.INFO_SIGN) {
											event.setLine(2, ChatColor.AQUA + "" + a.getPlayers().length + ChatColor.RESET + " / " + ChatColor.DARK_RED + a.getMaxPlayers());
											event.setLine(3, ChatColor.BLUE + "" + a.getRound() + ":" + a.getWave() + ChatColor.RESET + " / " + ChatColor.DARK_RED + a.getMaxRounds() + ":" + a.getMaxWaves());
										}else if(type==SignType.INTERACT_SIGN) {
											event.setLine(2, ChatColor.YELLOW + "Waiting");
											event.setLine(3, ChatColor.GREEN + "[JOIN]");	
										}else if(type==SignType.SHOP_SIGN) {
										
											Inventory catSelect = Bukkit.createInventory(eventPlayer, ((int)Math.ceil((ItemCategory.values().length/9.0)))*9, "Select Category " + SignManager.getManager().getSign(event.getBlock().getLocation()).getID());
																						
											for(ItemCategory cat : ItemCategory.values()) {
												if(cat.getIcon()!=null) {
													ItemStack icon = cat.getIcon().clone();
													ItemMeta meta = icon.getItemMeta();
													meta.setDisplayName(cat.toString());
													icon.setItemMeta(meta);
													catSelect.addItem(icon);
												}
											}
											
											eventPlayer.closeInventory();
											eventPlayer.openInventory(catSelect);
											return;
										}
										//TODO Message
										return;
									}else{
										eventPlayer.sendMessage("error");
										//TODO MESSAGE
										return;
									}								
								}else{
									//TODO message
									event.setCancelled(true);
									return;
								}
							}else{
								//TODO message
								event.setCancelled(true);
								return;
							}	
						}else{
							//TODO message				
							event.setCancelled(true);
							return;
						}
					}else{
						//TODO message				
						event.setCancelled(true);
						return;
					}
				}else{
					//TODO message				
					event.setCancelled(true);
					return;
				}
			}else{
				//TODO permission
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				return;
			}
		}
		/*
		
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
		}*/
	}	
}
