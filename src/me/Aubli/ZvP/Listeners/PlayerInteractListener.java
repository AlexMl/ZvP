package me.Aubli.ZvP.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.InteractSign;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener{
	
	private HashMap<Action, Location> clickLoc = new HashMap<Action, Location>();
	
	private SignManager sm = SignManager.getManager();
		
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		Player eventPlayer = (Player) event.getPlayer();
		
		if(event.getItem()!=null){
			if(event.getItem().equals(ZvP.tool)){
				if(eventPlayer.hasPermission("zvp.tool") && eventPlayer.hasPermission("zvp.manage.arena")){
					event.setCancelled(true);
					
					if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
						clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
						eventPlayer.sendMessage("Right click saved!"); //TODO Message
					}
					
					if(event.getAction() == Action.LEFT_CLICK_BLOCK){
						clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
						eventPlayer.sendMessage("Left click saved!"); //TODO Message						
					}
					
					if(clickLoc.containsKey(Action.RIGHT_CLICK_BLOCK) && clickLoc.containsKey(Action.LEFT_CLICK_BLOCK)){
						GameManager.getManager().addArena(clickLoc.get(Action.LEFT_CLICK_BLOCK), clickLoc.get(Action.RIGHT_CLICK_BLOCK));
						eventPlayer.sendMessage("arena created!"); //TODO Message
						clickLoc.clear();
						return;
					}
				}else{
					//TODO Permissions
					ZvP.getInstance().removeTool(eventPlayer);
					return;
				}
			}
		}
		
		
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(sm.isZVPSign(event.getClickedBlock().getLocation())){
				if(sm.getType(event.getClickedBlock().getLocation())==SignType.INTERACT_SIGN) {
					if(!GameManager.getManager().isInGame(eventPlayer)){
						if(eventPlayer.hasPermission("zvp.play")){
							if(eventPlayer.getInventory().getItemInHand().getType()==Material.AIR) {
								InteractSign sign = (InteractSign)sm.getSign(event.getClickedBlock().getLocation());
								if(sign.getArena().isOnline()){
									boolean success = GameManager.getManager().createPlayer(eventPlayer, sign.getArena(), sign.getLobby());
									
									if(success){
										eventPlayer.sendMessage("You joined Arena " + sign.getArena().getID()); //TODO message
										return;
									}else{
										event.setCancelled(true);
										eventPlayer.sendMessage("arena full or running"); //TODO message
										return;
									}
								}else{
									event.setCancelled(true);
									eventPlayer.sendMessage("arena offline"); //TODO message
									return;
								}
							}else {
								event.setCancelled(true);
								eventPlayer.sendMessage("You have something in your hand"); //TODO message
								return;
							}
						}else{
							event.setCancelled(true);
							eventPlayer.sendMessage("No permissions"); //TODO Permission Message
							return;						
						}
					}else{
						event.setCancelled(true);
						eventPlayer.sendMessage("already in game"); //TODO message
						return;
					}
				}else if(sm.getType(event.getClickedBlock().getLocation())==SignType.SHOP_SIGN) {
					if(GameManager.getManager().isInGame(eventPlayer)){
						if(eventPlayer.hasPermission("zvp.play")){
							
							ShopSign shopSign = (ShopSign)sm.getSign(event.getClickedBlock().getLocation());
						
							ItemCategory cat = shopSign.getCategory();
							Inventory shopInv = Bukkit.createInventory(eventPlayer, ((int)Math.ceil((double)ShopManager.getManager().getItems(cat).size()/9.0))*9, "Items: " + cat.toString());
							
							for(ShopItem shopItem : ShopManager.getManager().getItems(cat)) {
								
								ItemStack item = shopItem.getItem();
								ItemMeta meta = item.getItemMeta();
								List<String> lore = new ArrayList<String>();
								
								lore.add("Category: " + shopItem.getCategory().toString());
								lore.add(ChatColor.RED + "Price: " + shopItem.getPrice());
								
								meta.setLore(lore);
								item.setItemMeta(meta);
								shopInv.addItem(item);
							}
							eventPlayer.closeInventory();
							eventPlayer.openInventory(shopInv);
							return;
						}else{
							event.setCancelled(true);
							eventPlayer.sendMessage("No permissions"); //TODO Permission Message
							return;						
						}
					}else{
						event.setCancelled(true);
						eventPlayer.sendMessage("not in game"); //TODO message
						return;
					}					
				}
			}		
		}else if(event.getAction()==Action.LEFT_CLICK_BLOCK) {
			if(GameManager.getManager().isInGame(eventPlayer)) {
				event.setCancelled(true);
				return;
			}
		}
	}		
}