package me.Aubli.ZvP.Listeners;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ScoreType;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Game.Mode.DeathMatch;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Sign.StatisticSign;
import me.Aubli.ZvP.Statistic.DataRecordType;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.util.Potion.PotionLayer;


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

		// Select kit on game start
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
				if (ZvP.getEconProvider() != null) {
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
			} else {
			    player.setKit(kit);
			}

			ZvP.getPluginLogger().log(this.getClass(), Level.INFO, player.getName() + " took the " + player.getKit().getName() + " Kit", true);
			return;
		    }
		}

		// Select category for shop sign
		if (event.getInventory().getTitle().contains(MessageManager.getMessage(inventory.select_category))) {
		    event.setCancelled(true);
		    eventPlayer.closeInventory();

		    int signID = Integer.parseInt(event.getInventory().getTitle().split(MessageManager.getMessage(inventory.select_category) + " ")[1].replace("(", "").replace(")", ""));
		    ItemCategory cat = ItemCategory.getEnum(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

		    if (cat != null && SignManager.getManager().getSign(signID) != null) {
			ShopSign sign = (ShopSign) SignManager.getManager().getSign(signID);

			Arena a = sign.getArena();
			Lobby l = sign.getLobby();
			Location lo = sign.getLocation();

			SignManager.getManager().removeSign(signID);
			SignManager.getManager().createSign(SignType.SHOP_SIGN, lo, a, l, cat);
			SignManager.getManager().updateSigns(SignType.SHOP_SIGN);
		    }
		    return;
		}

		// Select recordType for stats sign
		if (event.getInventory().getTitle().contains(MessageManager.getMessage(inventory.select_recordType))) {
		    event.setCancelled(true);
		    eventPlayer.closeInventory();

		    int signID = Integer.parseInt(event.getInventory().getTitle().split(MessageManager.getMessage(inventory.select_recordType) + " ")[1].replace("(", "").replace(")", ""));
		    DataRecordType recordType = DataRecordType.fromIcon(event.getCurrentItem());

		    if (recordType != null && SignManager.getManager().getSign(signID) != null) {
			StatisticSign sign = (StatisticSign) SignManager.getManager().getSign(signID);

			Arena a = sign.getArena();
			Lobby l = sign.getLobby();
			Location lo = sign.getLocation();

			SignManager.getManager().removeSign(signID);
			SignManager.getManager().createSign(SignType.STATISTIC_SIGN, lo, a, l, recordType);
			SignManager.getManager().updateSigns(SignType.STATISTIC_SIGN);
		    }
		    return;
		}

		if (event.getInventory().getType() == InventoryType.CRAFTING) {
		    if (GameManager.getManager().isInGame(eventPlayer)) {
			if (ZvP.equalsItemStack(event.getCurrentItem(), DeathMatch.playerCompass)) {
			    event.setCancelled(true);
			    eventPlayer.closeInventory();

			    Arena arena = GameManager.getManager().getPlayer(eventPlayer).getArena();

			    List<ZvPPlayer> playerList = new ArrayList<ZvPPlayer>();
			    for (ZvPPlayer player : arena.getPlayers()) {
				if (player.getGameMode() == GameMode.SURVIVAL) {
				    playerList.add(player);
				}
			    }

			    Inventory playerInv = Bukkit.createInventory(eventPlayer, (int) Math.ceil((playerList.size() / 9.0)) * 9, MessageManager.getMessage(inventory.living_players));
			    for (ZvPPlayer player : playerList) {
				ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM);
				playerSkull.setDurability((short) 3);
				SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
				meta.setDisplayName(player.getName());
				meta.setLore(Arrays.asList(MessageManager.getFormatedMessage(game.teleport_to, player.getName())));
				meta.setOwner(player.getName());
				playerSkull.setItemMeta(meta);
				playerInv.addItem(playerSkull);
			    }

			    eventPlayer.openInventory(playerInv);
			    return;
			} else if (ZvP.equalsItemStack(event.getCurrentItem(), DeathMatch.speedToolEnable)) {
			    event.setCancelled(true);
			    eventPlayer.closeInventory();
			    eventPlayer.setFlySpeed(0.5F);
			    eventPlayer.getInventory().clear(event.getSlot());
			    eventPlayer.getInventory().addItem(DeathMatch.speedToolDisable);
			    eventPlayer.sendMessage(MessageManager.getMessage(game.speedTool_enabled));
			    return;
			} else if (ZvP.equalsItemStack(event.getCurrentItem(), DeathMatch.speedToolDisable)) {
			    event.setCancelled(true);
			    eventPlayer.closeInventory();
			    eventPlayer.setFlySpeed(0.2F);
			    eventPlayer.getInventory().clear(event.getSlot());
			    eventPlayer.getInventory().addItem(DeathMatch.speedToolEnable);
			    eventPlayer.sendMessage(MessageManager.getMessage(game.speedTool_disabled));
			}
		    }
		}

		if (event.getInventory().getTitle().equals(MessageManager.getMessage(inventory.living_players))) {
		    event.setCancelled(true);
		    eventPlayer.closeInventory();

		    if (GameManager.getManager().isInGame(eventPlayer)) {
			if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
			    SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
			    ZvPPlayer player = GameManager.getManager().getPlayer(meta.getDisplayName());
			    eventPlayer.teleport(player.getLocation());
			    return;
			}
		    }
		}

		// Item sell/buy
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
				    if (player.getArena().getScore().getScore(player) >= item.getBuyPrice()) {

					ItemStack boughtItem = new ItemStack(item.getItem().getType(), item.getItem().getAmount());
					boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
					boughtItem.setDurability(item.getItem().getDurability());

					if (item.isPotion()) {
					    try {
						boughtItem = PotionLayer.fromItemStack(item.getItem()).toItemStack(boughtItem.getAmount());
					    } catch (Exception e) {
						player.sendMessage(MessageManager.getMessage(error.transaction_failed));
						ZvP.getPluginLogger().log(GUIListener.class, Level.SEVERE, "Can't process potion: " + item.getItem().toString() + " " + e.getMessage() + "; Aborting transaction!", true, false, e);
						return;
					    }
					}

					player.getArena().getScore().subtractScore(player, item.getBuyPrice(), ScoreType.SHOP_SCORE);
					player.getPlayer().getInventory().addItem(boughtItem);
					if (boughtItem.getAmount() == 1) {
					    player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_bought, player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getBuyPrice()));
					} else {
					    player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_bought_more, player.getName(), boughtItem.getAmount(), item.getType().toString().toLowerCase().replace("_", " "), item.getBuyPrice()));
					}
				    } else {
					player.sendMessage(MessageManager.getMessage(error.no_money));
				    }
				    break;

				case SHIFT_LEFT: // Buy all
				    if (player.getArena().getScore().getScore(player) >= item.getBuyPrice()) {

					int amount = (int) (player.getArena().getScore().getScore(player) / item.getBuyPrice()) < 64 ? (int) (player.getArena().getScore().getScore(player) / item.getBuyPrice()) : 64;

					ItemStack boughtItem = new ItemStack(item.getItem().getType(), amount);
					boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
					boughtItem.setDurability(item.getItem().getDurability());

					if (item.isPotion()) {
					    try {
						boughtItem = PotionLayer.fromItemStack(item.getItem()).toItemStack(boughtItem.getAmount());
					    } catch (Exception e) {
						player.sendMessage(MessageManager.getMessage(error.transaction_failed));
						ZvP.getPluginLogger().log(GUIListener.class, Level.SEVERE, "Can't process potion: " + item.getItem().toString() + " " + e.getMessage() + "; Aborting transaction!", true, false, e);
						return;
					    }
					}

					player.getArena().getScore().subtractScore(player, item.getBuyPrice() * amount, ScoreType.SHOP_SCORE);
					player.getPlayer().getInventory().addItem(boughtItem);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_bought_more, player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), new DecimalFormat("#0.00").format(item.getBuyPrice() * amount)));
				    } else {
					player.sendMessage(MessageManager.getMessage(error.no_money));
				    }
				    break;

				case RIGHT: // Sell

				    ItemStack stack = new ItemStack(item.getItem().getType());
				    stack.setDurability(item.getItem().getDurability());
				    stack.addUnsafeEnchantments(item.getItem().getEnchantments());

				    if (item.isPotion()) {
					try {
					    stack = PotionLayer.fromItemStack(item.getItem()).toItemStack(1);
					} catch (Exception e) {
					    player.sendMessage(MessageManager.getMessage(error.transaction_failed));
					    ZvP.getPluginLogger().log(GUIListener.class, Level.SEVERE, "Can't process potion: " + item.getItem().toString() + " " + e.getMessage() + "; Aborting transaction!", true, false, e);
					    return;
					}
				    }

				    if (player.getPlayer().getInventory().containsAtLeast(stack, 1)) {
					player.getPlayer().getInventory().removeItem(stack);
					player.getArena().getScore().addScore(player, item.getSellPrice(), ScoreType.SHOP_SCORE);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_sold, player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getSellPrice()));
				    } else {
					player.sendMessage(MessageManager.getMessage(game.no_item_to_sell));
				    }

				    break;

				case SHIFT_RIGHT: // sell all

				    ItemStack stack1 = new ItemStack(item.getItem().getType());
				    stack1.setDurability(item.getItem().getDurability());
				    stack1.addUnsafeEnchantments(item.getItem().getEnchantments());

				    if (item.isPotion()) {
					try {
					    stack1 = PotionLayer.fromItemStack(item.getItem()).toItemStack(1);
					} catch (Exception e) {
					    player.sendMessage(MessageManager.getMessage(error.transaction_failed));
					    ZvP.getPluginLogger().log(GUIListener.class, Level.SEVERE, "Can't process potion: " + item.getItem().toString() + " " + e.getMessage() + "; Aborting transaction!", true, false, e);
					    return;
					}
				    }

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

					player.getArena().getScore().addScore(player, item.getSellPrice() * amount, ScoreType.SHOP_SCORE);
					player.getArena().sendMessage(MessageManager.getFormatedMessage(game.player_sold_more, player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), new DecimalFormat("#0.00").format(item.getSellPrice() * amount)));
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
