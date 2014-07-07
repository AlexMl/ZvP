package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener{
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getWhoClicked().hasPermission("zvp.play")) {
			if(event.getCurrentItem()!=null && event.getCurrentItem().getType()!=Material.AIR) {
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
				if(event.getInventory().getTitle().contains("Select Category")) {					
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					
					int signID = Integer.parseInt(event.getInventory().getTitle().split("Category ")[1]);
					ItemCategory cat = ItemCategory.valueOf(event.getCurrentItem().getItemMeta().getDisplayName());
					
					if(cat!=null && SignManager.getManager().getSign(signID)!=null) {
						ShopSign sign = (ShopSign)SignManager.getManager().getSign(signID);
						
						Arena a = sign.getArena();
						Lobby l = sign.getLobby();
						Location lo = sign.getLocation();
						
						SignManager.getManager().removeSign(signID);						
						SignManager.getManager().createSign(SignType.SHOP_SIGN, lo, a, l, cat);
					}
				}
				if(event.getInventory().getTitle().contains("Items: ")) {
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					
					ItemCategory cat = ItemCategory.valueOf(event.getInventory().getTitle().split("s: ")[1]);
					
					ShopItem item = ShopManager.getManager().getItem(cat, event.getCurrentItem());
					
					Bukkit.broadcastMessage(item.getType() + ": " + item.getCategory() + " - " + item.getPrice());
					event.getWhoClicked().getInventory().addItem(item.getItem());
					return;
				}
			}			
		}
	}
	
	
	private ItemStack[] content = null;
	private String name;
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		final Player eventPlayer = (Player)event.getPlayer();
		
		if(ChatColor.stripColor(event.getInventory().getTitle()).startsWith("ZvP-Kit: ") && event.getInventory().getSize()==9){	
			
			Inventory eventInv = event.getInventory();
			name = ChatColor.stripColor(eventInv.getTitle().split("ZvP-Kit: ")[1]);
			content = eventInv.getContents();
			
			Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
				@Override
				public void run() {
					KitManager.getManager().openAddKitIconGUI(eventPlayer);			
				}
			}, 1*10L);
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase("Select your Kit!")) {
			ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
			
			if(player!=null) {
				if(!player.hasKit()) {
					player.setKit(KitManager.getManager().getKit("No Kit"));
					return;
				}
			}
		}
		
		if(event.getInventory().getTitle().equals("Place Kit icon here")){
			
			for(ItemStack item : event.getInventory().getContents()) {
				if(item!=null && item.getType()!=Material.AIR) {
					KitManager.getManager().addKit(name, item, content);
					this.content = null;
					return;
				}
			}
			this.content = null;
			this.name = null;
		}else {
			return;
		}
	}
}
