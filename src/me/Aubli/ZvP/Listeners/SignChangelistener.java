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
import me.Aubli.ZvP.Translation.MessageManager;

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


public class SignChangelistener implements Listener {
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
	
	Player eventPlayer = event.getPlayer();
	
	if (event.getLine(0).replace(" ", "").equalsIgnoreCase("[zvp]")) {
	    if (eventPlayer.hasPermission("zvp.manage.sign")) {
		if (!event.getLine(1).isEmpty() && !event.getLine(2).isEmpty()) {
		    if (event.getLine(3).equalsIgnoreCase("interact") || event.getLine(3).equalsIgnoreCase("info") || event.getLine(3).equalsIgnoreCase("shop")) {
			try {
			    int arenaID = Integer.parseInt(event.getLine(1));
			    int lobbyID = Integer.parseInt(event.getLine(2));
			    
			    Arena a = GameManager.getManager().getArena(arenaID);
			    Lobby l = GameManager.getManager().getLobby(lobbyID);
			    
			    SignType type;
			    
			    if (event.getLine(3).equalsIgnoreCase("interact")) {
				type = SignType.INTERACT_SIGN;
			    } else if (event.getLine(3).equalsIgnoreCase("info")) {
				type = SignType.INFO_SIGN;
			    } else if (event.getLine(3).equalsIgnoreCase("shop")) {
				type = SignType.SHOP_SIGN;
			    } else {
				return;
			    }
			    
			    if (a != null) {
				if (l != null) {
				    ISign sign = SignManager.getManager().createSign(type, event.getBlock().getLocation().clone(), a, l, null);
				    
				    if (sign != null) {
					event.setLine(0, ZvP.getPrefix().trim());
					event.setLine(1, "Arena: " + arenaID);
					
					if (type == SignType.INFO_SIGN) {
					    event.setLine(2, ChatColor.AQUA + "" + a.getPlayers().length + ChatColor.RESET + " / " + ChatColor.DARK_RED + a.getMaxPlayers());
					    event.setLine(3, ChatColor.BLUE + "" + a.getRound() + ":" + a.getWave() + ChatColor.RESET + " / " + ChatColor.DARK_RED + a.getMaxRounds() + ":" + a.getMaxWaves());
					} else if (type == SignType.INTERACT_SIGN) {
					    event.setLine(2, ChatColor.YELLOW + "Waiting");
					    event.setLine(3, ChatColor.GREEN + "[JOIN]");
					} else if (type == SignType.SHOP_SIGN) {
					    
					    Inventory catSelect = Bukkit.createInventory(eventPlayer, ((int) Math.ceil((ItemCategory.values().length / 9.0))) * 9, MessageManager.getMessage("inventory:select_category") + " (" + SignManager.getManager().getSign(event.getBlock().getLocation()).getID() + ")");
					    
					    for (ItemCategory cat : ItemCategory.values()) {
						if (cat.getIcon() != null) {
						    ItemStack icon = cat.getIcon().clone();
						    ItemMeta meta = icon.getItemMeta();
						    meta.setDisplayName(cat.toString());
						    icon.setItemMeta(meta);
						    catSelect.addItem(icon);
						}
					    }
					    
					    eventPlayer.closeInventory();
					    eventPlayer.openInventory(catSelect);
					}
					eventPlayer.sendMessage(MessageManager.getMessage("manage:sign_saved"));
					return;
				    } else {
					eventPlayer.sendMessage(MessageManager.getMessage("error:sign_place"));
					return;
				    }
				} else {
				    eventPlayer.sendMessage(MessageManager.getMessage("error:lobby_not_available"));
				    event.setCancelled(true);
				    return;
				}
			    } else {
				eventPlayer.sendMessage(MessageManager.getMessage("error:arena_not_available"));
				event.setCancelled(true);
				return;
			    }
			} catch (NumberFormatException e) {
			    eventPlayer.sendMessage(MessageManager.getMessage("error:sign_layout") + " " + e.getMessage());
			    event.setCancelled(true);
			    return;
			}
		    } else {
			eventPlayer.sendMessage(MessageManager.getMessage("error:sign_layout"));
			event.setCancelled(true);
			return;
		    }
		} else {
		    eventPlayer.sendMessage(MessageManager.getMessage("error:sign_layout"));
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
