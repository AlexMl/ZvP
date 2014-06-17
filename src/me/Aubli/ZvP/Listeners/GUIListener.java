package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Kits.KitManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener{
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		
		if(event.getInventory().getTitle().contains("ZvP-Kit")&&event.getInventory().getSize()==9){	
			
			Inventory eventInv = event.getInventory();
			String kitName = eventInv.getTitle().split("ZvP-Kit: ")[1];
			
			KitManager.getManager().addKit(kitName, eventInv.getContents());
			
		}else{
			return;
		}		
	}
}
