package me.Aubli.ZvP.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPCommands;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.InteractSign;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PlayerInteractListener implements Listener {
    
    private HashMap<Action, Location> clickLoc = new HashMap<Action, Location>();
    
    private SignManager sm = SignManager.getManager();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
	
	Player eventPlayer = event.getPlayer();
	
	if (event.getItem() != null) {
	    if (event.getItem().isSimilar(ZvP.getTool(ZvP.ADDARENA))) {
		if (eventPlayer.hasPermission("zvp.manage.arena")) {
		    event.setCancelled(true);
		    
		    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			this.clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
			eventPlayer.sendMessage(MessageManager.getMessage("manage:right_saved"));
		    }
		    
		    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			this.clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
			eventPlayer.sendMessage(MessageManager.getMessage("manage:left_saved"));
		    }
		    
		    if (this.clickLoc.containsKey(Action.RIGHT_CLICK_BLOCK) && this.clickLoc.containsKey(Action.LEFT_CLICK_BLOCK)) {
			GameManager.getManager().addArena(this.clickLoc.get(Action.LEFT_CLICK_BLOCK), this.clickLoc.get(Action.RIGHT_CLICK_BLOCK));
			eventPlayer.sendMessage(MessageManager.getMessage("manage:arena_saved"));
			this.clickLoc.clear();
			return;
		    }
		} else {
		    ZvP.removeTool(eventPlayer);
		    ZvPCommands.commandDenied(eventPlayer);
		    return;
		}
	    } else if (event.getItem().isSimilar(ZvP.getTool(ZvP.ADDPOSITION))) {
		if (eventPlayer.hasPermission("zvp.manage.arena")) {
		    event.setCancelled(true);
		    
		    for (Arena arena : GameManager.getManager().getArenas()) {
			if (arena.containsLocation(eventPlayer.getLocation())) {
			    boolean success = arena.addSpawnLocation(event.getClickedBlock().getLocation().clone().add(0, 1, 0));
			    if (success) {
				eventPlayer.sendMessage(MessageManager.getFormatedMessage("manage:position_saved", "Position in arena " + arena.getID()));
			    } else {
				eventPlayer.sendMessage(MessageManager.getMessage("manage:position_not_saved"));
			    }
			    return;
			}
		    }
		    eventPlayer.sendMessage(MessageManager.getMessage("manage:position_not_in_arena"));
		    return;
		} else {
		    ZvP.removeTool(eventPlayer);
		    ZvPCommands.commandDenied(eventPlayer);
		    return;
		}
	    }
	}
	
	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {	// Player join per Sign
	    if (this.sm.isZVPSign(event.getClickedBlock().getLocation())) {
		if (this.sm.getType(event.getClickedBlock().getLocation()) == SignType.INTERACT_SIGN) {
		    if (!GameManager.getManager().isInGame(eventPlayer)) {
			if (eventPlayer.hasPermission("zvp.play")) {
			    InteractSign sign = (InteractSign) this.sm.getSign(event.getClickedBlock().getLocation());
			    if (sign.getArena() != null) {
				if (sign.getArena().isOnline()) {
				    boolean success = GameManager.getManager().createPlayer(eventPlayer, sign.getArena(), sign.getLobby());
				    
				    if (!success) {
					event.setCancelled(true);
					eventPlayer.sendMessage(MessageManager.getMessage("arena:not_ready"));
					return;
				    }
				} else {
				    event.setCancelled(true);
				    eventPlayer.sendMessage(MessageManager.getMessage("arena:offline"));
				    return;
				}
			    } else {
				event.setCancelled(true);
				eventPlayer.sendMessage(MessageManager.getMessage("error:arena_not_available"));
				return;
			    }
			} else {
			    event.setCancelled(true);
			    ZvPCommands.commandDenied(eventPlayer);
			    return;
			}
		    } else {
			event.setCancelled(true);
			eventPlayer.sendMessage(MessageManager.getMessage("game:already_in_game"));
			return;
		    }
		} else if (this.sm.getType(event.getClickedBlock().getLocation()) == SignType.SHOP_SIGN) {
		    if (GameManager.getManager().isInGame(eventPlayer)) {
			if (eventPlayer.hasPermission("zvp.play")) {
			    event.setCancelled(true);
			    
			    ShopSign shopSign = (ShopSign) this.sm.getSign(event.getClickedBlock().getLocation());
			    
			    ItemCategory cat = shopSign.getCategory();
			    Inventory shopInv = Bukkit.createInventory(eventPlayer, ((int) Math.ceil(ShopManager.getManager().getItems(cat).size() / 9.0)) * 9, "Items: " + cat.toString());
			    
			    for (ShopItem shopItem : ShopManager.getManager().getItems(cat)) {
				
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
			} else {
			    event.setCancelled(true);
			    ZvPCommands.commandDenied(eventPlayer);
			    return;
			}
		    } else {
			event.setCancelled(true);
			eventPlayer.sendMessage(MessageManager.getMessage("game:not_in_game"));
			return;
		    }
		}
	    }
	} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    if (GameManager.getManager().isInGame(eventPlayer)) {
		event.setCancelled(true);
		return;
	    }
	}
    }
}
