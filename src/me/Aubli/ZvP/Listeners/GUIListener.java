package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.KitManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener{
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getWhoClicked().hasPermission("zvp.play")) {
			if(event.getInventory().getTitle().equalsIgnoreCase("Select your Kit!")) {
				event.setCancelled(true);
				event.getWhoClicked().closeInventory();
				
				String kitName = event.getCurrentItem().getItemMeta().getDisplayName();				
				ZvPPlayer player = GameManager.getManager().getPlayer((Player)event.getWhoClicked());
				
				if(KitManager.getManager().getKit(kitName)!=null && player!=null) {
					player.setKit(KitManager.getManager().getKit(kitName));
					System.out.println(player.getName() + " took the " + player.getKit().getName() + " Kit");
					return;
				}
			}
		}
	}
	
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		
		if(ChatColor.stripColor(event.getInventory().getTitle()).startsWith("ZvP-Kit: ") && event.getInventory().getSize()==9){	
			
			Inventory eventInv = event.getInventory();
			String kitName = eventInv.getTitle().split("ZvP-Kit: ")[1];
			
			KitManager.getManager().addKit(kitName,new ItemStack(Material.ITEM_FRAME), eventInv.getContents());
			
		}else{
			return;
		}		
	}
}
