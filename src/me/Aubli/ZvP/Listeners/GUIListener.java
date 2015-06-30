package me.Aubli.ZvP.Listeners;

import java.text.DecimalFormat;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ScoreType;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.EconomyResponse;

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


public class GUIListener implements Listener {
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
	
	Player eventPlayer = (Player) event.getWhoClicked();
	
	// Super awesome Clickevent future
	// if(event.getRawSlot()==-999) {
	// eventPlayer.closeInventory();
	// }
	// Cool but very dangerous for importent user input
	
	// System.out.println(event.getRawSlot() + " " + event.getSlot() + "; " + event.getInventory().getSize());
	if (eventPlayer.hasPermission("zvp.play")) {
	    if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
		
		boolean onlyTopinventory = false;
		
		if (event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage(inventory.kit_select)) || event.getInventory().getTitle().contains(MessageManager.getMessage(inventory.select_category)) || event.getInventory().getTitle().contains("Items: ")) {
		    onlyTopinventory = true;
		}
		
		if (((event.getRawSlot() != event.getSlot()) || event.getSlot() >= event.getInventory().getSize()) && onlyTopinventory) {
		    eventPlayer.sendMessage(MessageManager.getMessage(error.wrong_inventory));
		    event.setCancelled(true);
		    return;
		}
		
		// System.out.println(event.getRawSlot() + " " + event.getSlot() + ": " + event.getCurrentItem().getItemMeta().getDisplayName());
		
		if (event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage(inventory.kit_select))) {
		    event.setCancelled(true);
		    eventPlayer.closeInventory();
		    
		    String kitName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
		    ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
		    IZvPKit kit = KitManager.getManager().getKit(kitName);
		    
		    if (kit != null && player != null) {
			
			if (ZvPConfig.getEnableEcon()) {
			    // if economy and sellKits is true withdraw some money
			    if (ZvPConfig.getIntegrateKits()) {
				if (ZvP.getEconProvider().has(eventPlayer, kit.getPrice())) {
				    EconomyResponse response = ZvP.getEconProvider().withdrawPlayer(eventPlayer, kit.getPrice());
				    if (response.transactionSuccess()) {
					player.sendMessage(MessageManager.getFormatedMessage(game.player_bought_kit, kitName, new DecimalFormat("#0.00").format(response.amount) + " " + ZvP.getEconProvider().currencyNamePlural(), new DecimalFormat("#0.00").format(response.balance) + " " + ZvP.getEconProvider().currencyNamePlural()));
					player.setKit(kit);
				    } else {
					player.setKit(kit);
					player.sendMessage(MessageManager.getMessage(error.transaction_failed));
					ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! " + response.errorMessage + " for Kit " + kit.getName(), false);
				    }
				} else {
				    player.sendMessage(MessageManager.getMessage(error.no_money));
				    return;
				}
			    } else {
				player.setKit(kit);
			    }
			} else {
			    player.setKit(kit);
			}
			
			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, player.getName() + " took the " + player.getKit().getName() + " Kit", true);
			return;
		    }
		}
		if (event.getInventory().getTitle().contains(MessageManager.getMessage(inventory.select_category))) {
		    event.setCancelled(true);
		    eventPlayer.closeInventory();
		    
		    int signID = Integer.parseInt(event.getInventory().getTitle().split("Category ")[1].replace("(", "").replace(")", ""));
		    ItemCategory cat = ItemCategory.getEnum(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
		    
		    if (cat != null && SignManager.getManager().getSign(signID) != null) {
			ShopSign sign = (ShopSign) SignManager.getManager().getSign(signID);
			
			Arena a = sign.getArena();
			Lobby l = sign.getLobby();
			Location lo = sign.getLocation();
			
			SignManager.getManager().removeSign(signID);
			SignManager.getManager().createSign(SignType.SHOP_SIGN, lo, a, l, cat);
		    }
		}
		if (event.getInventory().getTitle().contains("Items: ")) {
		    event.setCancelled(true);
		    ZvP.getPluginLogger().log(this.getClass(), Level.FINEST, "ShopClick: Slot: " + event.getSlot() + " RawSlot: " + event.getRawSlot() + " Result: " + event.getResult().toString(), true);
		    
		    if (event.getRawSlot() > event.getInventory().getSize()) {
			ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Player " + eventPlayer.getName() + " tryed to acces slot " + event.getRawSlot() + " (index:" + event.getInventory().getSize() + ")", true);
			return;
		    }
		    
		    ItemCategory cat = ItemCategory.getEnum(event.getInventory().getTitle().split("s: ")[1]);
		    ShopItem item = ShopManager.getManager().getItem(cat, event.getCurrentItem());
		    ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
		    
		    if (item != null && player != null) {
			if (GameManager.getManager().isInGame(player.getPlayer()) && !player.getArena().isWaiting()) {
			    
			    switch (event.getClick()) {
			    
				case LEFT: // Buy
				    if (player.getArena().getScore().getScore(player) >= item.getPrice()) {
					
					ItemStack boughtItem = new ItemStack(item.getItem().getType(), item.getItem().getAmount());
					boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
					boughtItem.setDurability(item.getItem().getDurability());
					
					player.getArena().getScore().subtractScore(player, item.getPrice(), ScoreType.SHOP_SCORE);
					player.getPlayer().getInventory().addItem(boughtItem);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_bought, player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getPrice()));
				    } else {
					player.sendMessage(MessageManager.getMessage(error.no_money));
				    }
				    break;
				
				case SHIFT_LEFT: // Buy all
				    if (player.getArena().getScore().getScore(player) >= item.getPrice()) {
					
					int amount = (int) (player.getArena().getScore().getScore(player) / item.getPrice()) < 64 ? (int) (player.getArena().getScore().getScore(player) / item.getPrice()) : 64;
					
					ItemStack boughtItem = new ItemStack(item.getItem().getType(), amount);
					boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
					boughtItem.setDurability(item.getItem().getDurability());
					
					player.getArena().getScore().subtractScore(player, item.getPrice() * amount, ScoreType.SHOP_SCORE);
					player.getPlayer().getInventory().addItem(boughtItem);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_bought_more, player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), Math.round(item.getPrice() * amount)));
				    } else {
					player.sendMessage(MessageManager.getMessage(error.no_money));
				    }
				    break;
				
				case RIGHT: // Sell
				    
				    ItemStack stack = new ItemStack(item.getItem().getType());
				    stack.setDurability(item.getItem().getDurability());
				    stack.addUnsafeEnchantments(item.getItem().getEnchantments());
				    
				    if (player.getPlayer().getInventory().containsAtLeast(stack, 1)) {
					player.getPlayer().getInventory().removeItem(stack);
					player.getArena().getScore().addScore(player, item.getPrice(), ScoreType.SHOP_SCORE);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_sold, player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getPrice()));
				    } else {
					player.sendMessage(MessageManager.getMessage(game.no_item_to_sell));
				    }
				    
				    break;
				
				case SHIFT_RIGHT: // sell all
				    
				    ItemStack stack1 = new ItemStack(item.getItem().getType());
				    stack1.setDurability(item.getItem().getDurability());
				    stack1.addUnsafeEnchantments(item.getItem().getEnchantments());
				    
				    int amount = 0;
				    
				    if (player.getPlayer().getInventory().containsAtLeast(stack1, 1)) {
					for (int i = 0; i < player.getPlayer().getInventory().getSize(); i++) {
					    ItemStack invItem = player.getPlayer().getInventory().getItem(i);
					    
					    if (invItem != null && invItem.getType() != Material.AIR) {
						if (invItem.getType() == stack1.getType() && invItem.getDurability() == stack1.getDurability() && invItem.getEnchantments().equals(stack1.getEnchantments())) {
						    amount += invItem.getAmount();
						    player.getPlayer().getInventory().clear(i);
						}
					    }
					}
					
					player.getArena().getScore().addScore(player, item.getPrice() * amount, ScoreType.SHOP_SCORE);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_sold_more, player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), Math.round(item.getPrice() * amount)));
				    } else {
					player.sendMessage(MessageManager.getMessage(game.no_item_to_sell));
				    }
				    
				    break;
				default:
				    break;
			    }
			}
		    }
		    event.getView().close();
		    return;
		}
	    }
	}
    }
    
    private ItemStack[] content = null;
    private String name;
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
	final Player eventPlayer = (Player) event.getPlayer();
	
	if (ChatColor.stripColor(event.getInventory().getTitle()).startsWith("ZvP-Kit: ") && event.getInventory().getSize() == 9) {
	    
	    Inventory eventInv = event.getInventory();
	    this.name = ChatColor.stripColor(eventInv.getTitle().split("ZvP-Kit: ")[1]);
	    this.content = eventInv.getContents();
	    
	    for (ItemStack item : this.content) {
		if (item != null && item.getType() != Material.AIR) {
		    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
			
			@Override
			public void run() {
			    KitManager.getManager().openAddKitIconGUI(eventPlayer);
			}
		    }, 1 * 10L);
		    break;
		}
	    }
	}
	
	if (event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage(inventory.kit_select))) {
	    final Player player = eventPlayer;
	    
	    Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		
		@Override
		public void run() {
		    if (player != null) {
			ZvPPlayer zvpp = GameManager.getManager().getPlayer(eventPlayer);
			
			if (zvpp != null) {
			    if (!zvpp.hasKit()) {
				zvpp.setCanceled(true);
				GameManager.getManager().removePlayer(zvpp);
				return;
			    }
			}
		    }
		}
	    }, 1L);
	}
	
	if (event.getInventory().getTitle().equals(MessageManager.getMessage(inventory.place_icon))) {
	    
	    for (ItemStack item : event.getInventory().getContents()) {
		if (item != null && item.getType() != Material.AIR) {
		    KitManager.getManager().addKit(this.name, item, this.content);
		    eventPlayer.sendMessage(MessageManager.getFormatedMessage(manage.kit_saved, this.name));
		    break;
		}
	    }
	    this.content = null;
	    this.name = null;
	    return;
	} else {
	    return;
	}
    }
}
