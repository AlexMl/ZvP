package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPCommands;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ISign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Statistic.DataRecordType;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SignChangelistener implements Listener {
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
	
	Player eventPlayer = event.getPlayer();
	
	if (event.getLine(0).replace(" ", "").equalsIgnoreCase("[zvp]")) {
	    if (eventPlayer.hasPermission("zvp.manage.sign")) {
		if (!event.getLine(1).isEmpty() && !event.getLine(2).isEmpty()) {
		    try {
			int arenaID = Integer.parseInt(event.getLine(1));
			int lobbyID = Integer.parseInt(event.getLine(2));
			
			Arena a = GameManager.getManager().getArena(arenaID);
			Lobby l = GameManager.getManager().getLobby(lobbyID);
			
			SignType type = SignType.fromString(event.getLine(3));
			
			if (a != null) {
			    if (l != null) {
				if (type != null) {
				    ISign sign = SignManager.getManager().createSign(type, event.getBlock().getLocation().clone(), a, l);
				    
				    if (sign != null) {
					event.setLine(0, ZvP.getPrefix().trim());
					event.setLine(1, "Arena: " + arenaID);
					event.setLine(2, ChatColor.DARK_RED + "'/zvp reload'");
					event.setLine(3, ChatColor.DARK_RED + "required!");
					
					if (type == SignType.SHOP_SIGN) {
					    
					    Inventory catSelect = Bukkit.createInventory(eventPlayer, ((int) Math.ceil((ItemCategory.values().length / 9.0))) * 9, MessageManager.getMessage(inventory.select_category) + " (" + sign.getID() + ")");
					    for (ItemCategory cat : ItemCategory.values()) {
						if (cat.getIcon() != null) {
						    ItemStack icon = cat.getIcon().clone();
						    ItemMeta meta = icon.getItemMeta();
						    meta.setDisplayName(cat.toString());
						    meta.addItemFlags(ItemFlag.values());
						    icon.setItemMeta(meta);
						    catSelect.addItem(icon);
						}
					    }
					    
					    eventPlayer.closeInventory();
					    eventPlayer.openInventory(catSelect);
					}
					if (type == SignType.STATISTIC_SIGN) {
					    
					    Inventory statSelect = Bukkit.createInventory(eventPlayer, ((int) Math.ceil((DataRecordType.values().length / 9.0))) * 9, MessageManager.getMessage(inventory.select_recordType) + " (" + sign.getID() + ")");
					    for (DataRecordType dataType : DataRecordType.values()) {
						if (dataType.getIcon() != null) {
						    ItemStack icon = dataType.getIcon().clone();
						    ItemMeta meta = icon.getItemMeta();
						    meta.setDisplayName(dataType.getDisplayName());
						    meta.addItemFlags(ItemFlag.values());
						    icon.setItemMeta(meta);
						    statSelect.addItem(icon);
						}
					    }
					    
					    eventPlayer.closeInventory();
					    eventPlayer.openInventory(statSelect);
					}
					
					eventPlayer.sendMessage(MessageManager.getMessage(manage.sign_saved));
					return;
				    } else {
					eventPlayer.sendMessage(MessageManager.getMessage(error.sign_place));
					return;
				    }
				} else {
				    eventPlayer.sendMessage(MessageManager.getMessage(error.sign_layout));
				    event.setCancelled(true);
				    return;
				}
			    } else {
				eventPlayer.sendMessage(MessageManager.getMessage(error.lobby_not_available));
				event.setCancelled(true);
				return;
			    }
			} else {
			    eventPlayer.sendMessage(MessageManager.getMessage(error.arena_not_available));
			    event.setCancelled(true);
			    return;
			}
		    } catch (NumberFormatException e) {
			eventPlayer.sendMessage(MessageManager.getMessage(error.sign_layout) + " " + e.getMessage());
			event.setCancelled(true);
			return;
		    }
		} else {
		    eventPlayer.sendMessage(MessageManager.getMessage(error.sign_layout));
		    event.setCancelled(true);
		    return;
		}
	    } else {
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
		ZvPCommands.commandDenied(eventPlayer);
		return;
	    }
	}
    }
}
