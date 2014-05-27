package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener{
	public InventoryCloseListener(ZvP plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		
		if(event.getInventory().getTitle().contains("ZvP-Kit")&&event.getInventory().getSize()==9){	
			
			Inventory eventInv = event.getInventory();
			Player eventPlayer = (Player)event.getPlayer();
			String[] kitName = eventInv.getTitle().split("ZvP-Kit: ");
			String sk = "";
			int lastStack = 0;
			
			for(int y=0;y<eventInv.getSize();y++){
				if(eventInv.getContents()[(eventInv.getSize()-1)-y]!=null){
					lastStack = (eventInv.getSize()-1)-y;
					break;
				}
			}
			
			for(int i=0;i<eventInv.getSize();i++){
				if(eventInv.getContents()[i]!=null && i<lastStack){					
					sk = sk + eventInv.getContents()[i].getAmount() + "x" + eventInv.getContents()[i].getType() + ", ";					
				}else if(eventInv.getContents()[i]!=null && i==lastStack){
					sk = sk + eventInv.getContents()[i].getAmount() + "x" + eventInv.getContents()[i].getType();
				//	eventPlayer.sendMessage(sk);
				}
			}
			
			if(sk.equalsIgnoreCase("")==false){
				plugin.getConfig().set("config.starterkit.whichkit", kitName[1]);
				plugin.getConfig().set("config.starterkit." + kitName[1], sk);
				plugin.saveConfig();
				
//				eventPlayer.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.kit_created"));
			}
			
		}else{
			return;
		}		
	}
	
	private ZvP plugin;
}
