package me.Aubli.ZvP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.ArenaArea;
import me.Aubli.ZvP.Game.ArenaScore.ScoreType;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ISign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ZvPCommands implements CommandExecutor {
    
    /*
     * ZvP-Commands:
     *
     * - zvp
     * - zvp help
     * - zvp update
     * - zvp status
     * - zvp list
     * - zvp save
     * - zvp reload
     *
     * - zvp leave
     *
     * - zvp add Arena
     * - zvp add Lobby
     * - zvp remove Arena
     * - zvp remove Lobby
     *
     * - zvp stop [arena]
     * - zvp stop
     *
     *
     */
    
    private ArrayList<Location> positions = new ArrayList<Location>();
    private GameManager game = GameManager.getManager();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
	
	if (!(sender instanceof Player)) {
	    if (cmd.getName().equals("zvp")) {
		if (args.length == 1) {
		    if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			GameManager.getManager().reloadConfig();
			sender.sendMessage(MessageManager.getMessage("config:reloaded"));
			return true;
		    }
		    if (args[0].equalsIgnoreCase("stop-all") || args[0].equalsIgnoreCase("stop")) {
			GameManager.getManager().stopGames();
			sender.sendMessage(MessageManager.getMessage("arena:stop_all"));
			return true;
		    }
		}
		
		String pluginName = ZvP.getInstance().getDescription().getName();
		String pluginVersion = ZvP.getInstance().getDescription().getVersion();
		String prefix = ZvP.getPrefix();
		
		sender.sendMessage("|--------------- " + pluginName + " v" + pluginVersion + " ( " + prefix + ") ---------------|");
		sender.sendMessage("| /zvp reload");
		sender.sendMessage("| /zvp stop-all");
		return true;
	    }
	    sender.sendMessage(MessageManager.getMessage("commands:only_for_Players"));
	    return true;
	}
	
	Player playerSender = (Player) sender;
	
	if (cmd.getName().equalsIgnoreCase("zvptest") && playerSender.isOp() && ZvP.getPluginLogger().isDebugMode()) {
	    // || (playerSender.getUniqueId().toString().equalsIgnoreCase("2b572c53-1e26-4c09-89a3-aca82bf1d585") ||
	    // playerSender.getUniqueId().toString().equalsIgnoreCase("c9062ab8-8764-370e-b57f-8a96641dbb79")))) {
	    // Test command
	    
	    if (args.length == 1) {
		
		if (args[0].equalsIgnoreCase("pos")) {
		    this.positions.add(playerSender.getLocation().clone());
		    playerSender.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
		    System.out.println(this.positions);
		}
		if (args[0].equalsIgnoreCase("posf")) {
		    System.out.println(this.positions);
		    ArrayList<Location> spawn = new ArrayList<Location>();
		    spawn.add(playerSender.getLocation());
		    
		    try {
			ArenaArea aa = new ArenaArea(playerSender.getWorld(), GameManager.getManager().getArena(10), this.positions, spawn, new Random());
			// aa.paintBounds();
			
			for (int i = 0; i < 3000; i++) {
			    // aa.getNewRandomLocation(false).getBlock().setType(Material.SANDSTONE);
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    this.positions.clear();
		}
		
		if (args[0].equalsIgnoreCase("u")) {
		    SignManager.getManager().updateSigns();
		    GameManager.getManager().getPlayer(playerSender).getArena().updatePlayerBoards();
		    return true;
		}
		
		if (args[0].equalsIgnoreCase("kill")) {
		    for (Entity e : playerSender.getWorld().getEntities()) {
			if (e instanceof Zombie) {
			    if (GameManager.getManager().getPlayer(playerSender).getArena().containsLocation(e.getLocation())) {
				e.remove();
			    }
			}
		    }
		    return true;
		}
	    }
	    
	    if (args.length == 2) {
		if (args[0].equalsIgnoreCase("kit")) {
		    
		    String kit = args[1];
		    
		    if (KitManager.getManager().getKit(kit) != null) {
			
			IZvPKit zKit = KitManager.getManager().getKit(kit);
			
			for (ItemStack item : zKit.getContents()) {
			    playerSender.getInventory().addItem(item);
			}
		    }
		    return true;
		}
		
		if (args[0].equalsIgnoreCase("shop")) {
		    ItemCategory cat = ItemCategory.valueOf(args[1]);
		    
		    if (cat != null) {
			Inventory shopInv = Bukkit.createInventory(playerSender, ((int) Math.ceil(ShopManager.getManager().getItems(cat).size() / 9.0)) * 9, "Items: " + cat.toString());
			
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
			playerSender.closeInventory();
			playerSender.openInventory(shopInv);
		    }
		    return true;
		}
		
		if (args[0].equalsIgnoreCase("m")) {
		    if (!ZvPConfig.getEnableEcon() && !ZvPConfig.getIntegrateGame()) { // possibility of cheating money
			Double sum = Double.parseDouble(args[1]);
			this.game.getPlayer(playerSender).getArena().getScore().addScore(this.game.getPlayer(playerSender), sum, ScoreType.SHOP_SCORE);
			return true;
		    }
		}
		
		Arena a = this.game.getArena(Integer.parseInt(args[0]));
		int round = Integer.parseInt(args[1].split(":")[0]);
		int wave = Integer.parseInt(args[1].split(":")[1]);
		a.setRound(round);
		a.setWave(wave);
	    }
	    
	    if (args.length == 6) {
		if (args[0].equalsIgnoreCase("rt")) {
		    int id = Integer.parseInt(args[1]);
		    int r = Integer.parseInt(args[2]);
		    int w = Integer.parseInt(args[3]);
		    int p = Integer.parseInt(args[4]);
		    int d = Integer.parseInt(args[5]);
		    Arena a = this.game.getArena(id);
		    
		    for (int ir = 1; ir <= r; ir++) {
			for (int iw = 1; iw <= w; iw++) {
			    playerSender.sendMessage("D:" + d + " P:" + p + " R:" + ir + " W:" + iw + " @" + id + " --> " + a.getSpawningZombies(iw, ir, p, d));
			}
		    }
		    
		}
	    }
	}
	
	if (cmd.getName().equalsIgnoreCase("zvp")) {
	    
	    String arguments = "";
	    for (String arg : args) {
		arguments += " " + arg;
	    }
	    
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + playerSender.getName() + " attempts to execute Command: " + cmd.getName() + arguments, true);
	    
	    if (args.length == 0) {
		printCommands(playerSender, 1);
		return true;
	    }
	    
	    if (args.length == 1) {
		
		if (args[0].equalsIgnoreCase("help")) {
		    printCommands(playerSender, 1);
		    return true;
		}
		if (args[0].equalsIgnoreCase("update")) {
		    if (playerSender.hasPermission("zvp.update")) {
			if (ZvP.updateAvailable) {
			    ZvP.getInstance().updatePlugin();
			    playerSender.sendMessage(ZvP.getPrefix() + "The new version is now downloading!");
			    return true;
			} else {
			    playerSender.sendMessage(ZvP.getPrefix() + "No update available!");
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("status")) {
		    
		    if (playerSender.hasPermission("zvp.status")) {
			String pluginName = ZvP.getInstance().getDescription().getName();
			String pluginVersion = ZvP.getInstance().getDescription().getVersion();
			
			playerSender.sendMessage("\n\n");
			playerSender.sendMessage(ChatColor.GRAY + "|------------ " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Status" + ChatColor.GRAY + " ------------|");
			
			for (Arena a : this.game.getArenas()) {
			    
			    String status = a.isRunning() ? ChatColor.GRAY + "| " + ChatColor.RED + "A: " + ChatColor.BLUE + a.getID() + " - " + a.getStatus().toString() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Player: " + ChatColor.BLUE + a.getPlayers().length + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getMaxPlayers() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Money: " + ChatColor.BLUE + a.getScore().getScore(null) + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Zombies: " + ChatColor.BLUE + a.getLivingZombieAmount() + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getSpawningZombies() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Killed: " + ChatColor.BLUE + a.getKilledZombies() : ChatColor.GRAY + "| " + ChatColor.RED + "A: " + ChatColor.BLUE + a.getID() + " - " + a.getStatus().toString() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Player: " + ChatColor.BLUE + a.getPlayers().length + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getMaxPlayers();
			    playerSender.sendMessage(status);
			}
			
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("list")) {
		    
		    if (playerSender.hasPermission("zvp.status")) {
			list(playerSender, "arenas");
			list(playerSender, "lobbys");
			list(playerSender, "signs");
			list(playerSender, "kits");
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
		    if (playerSender.hasPermission("zvp.reload")) {
			GameManager.getManager().reloadConfig();
			playerSender.sendMessage(MessageManager.getMessage("config:reloaded"));
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		if (args[0].equalsIgnoreCase("leave")) {
		    if (playerSender.hasPermission("zvp.play") || this.game.isInGame(playerSender)) {
			ZvPPlayer p = this.game.getPlayer(playerSender);
			if (p != null) {
			    Arena a = p.getArena();
			    boolean success = this.game.removePlayer(p);
			    
			    if (success) {
				playerSender.sendMessage(MessageManager.getFormatedMessage("game:left", p.getArena().getID()));
				a.sendMessage(MessageManager.getFormatedMessage("game:player_left", playerSender.getName()));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage("game:player_not_found"));
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage("game:not_in_game"));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		if (args[0].equalsIgnoreCase("stop")) {
		    if (playerSender.hasPermission("zvp.stop.all")) {
			this.game.stopGames();
			playerSender.sendMessage(MessageManager.getMessage("arena:stop_all"));
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		printCommands(playerSender, parseInt(args[0]));
		return true;
	    }
	    
	    if (args.length == 2) {
		if (args[0].equalsIgnoreCase("help")) {
		    printCommands(playerSender, parseInt(args[1]));
		    return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
		    if (playerSender.hasPermission("zvp.status")) {
			list(playerSender, args[1]);
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		if (args[0].equalsIgnoreCase("addkit")) {
		    if (playerSender.hasPermission("zvp.manage.kit")) {
			if (KitManager.getManager().getKit(args[1]) == null) {
			    KitManager.getManager().openAddKitGUI(playerSender, args[1]);
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getFormatedMessage("error:kit_already_exists", args[1]));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		if (args[0].equalsIgnoreCase("removekit")) {
		    if (playerSender.hasPermission("zvp.manage.kit")) {
			if (KitManager.getManager().getKit(args[1]) != null) {
			    KitManager.getManager().removeKit(args[1]);
			    playerSender.sendMessage(MessageManager.getFormatedMessage("manage:kit_removed", args[1]));
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getFormatedMessage("error:kit_does_not_exists", args[1]));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		if (args[0].equalsIgnoreCase("add")) {
		    if (args[1].equalsIgnoreCase("arena")) {
			if (playerSender.hasPermission("zvp.manage.arena")) {
			    playerSender.sendMessage(MessageManager.getMessage("manage:tool"));
			    playerSender.getInventory().addItem(ZvP.getTool(ZvP.ADDARENA));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    if (args[1].equalsIgnoreCase("lobby")) {
			if (playerSender.hasPermission("zvp.manage.lobby")) {
			    GameManager.getManager().addLobby(playerSender.getLocation().clone());
			    playerSender.sendMessage(MessageManager.getMessage("manage:lobby_saved"));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    if (args[1].equalsIgnoreCase("position")) {
			if (playerSender.hasPermission("zvp.manage.arena")) {
			    playerSender.getInventory().addItem(ZvP.getTool(ZvP.ADDPOSITION));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    printCommands(playerSender, 2);
		    return true;
		}
		if (args[0].equalsIgnoreCase("stop")) {
		    if (playerSender.hasPermission("zvp.stop")) {
			Arena a = this.game.getArena(parseInt(args[1]));
			if (a != null) {
			    a.stop();
			    playerSender.sendMessage(MessageManager.getFormatedMessage("arena:stop", a.getID()));
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getMessage("error:arena_not_available"));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		printCommands(playerSender, 2);
		return true;
	    }
	    
	    if (args.length == 3) {
		if (args[0].equalsIgnoreCase("set")) {
		    if (playerSender.hasPermission("zvp.manage.arena")) {
			Arena arena = this.game.getArena(parseInt(args[1]));
			if (arena != null) {
			    if (args[2].equalsIgnoreCase("offline") || args[2].equalsIgnoreCase("off")) {
				arena.setStatus(ArenaStatus.STOPED);
				arena.save();
				playerSender.sendMessage(MessageManager.getFormatedMessage("manage:arena_status_changed", "Offline"));
				return true;
			    } else if (args[2].equalsIgnoreCase("online") || args[2].equalsIgnoreCase("on")) {
				if (!arena.isOnline()) {
				    arena.setStatus(ArenaStatus.STANDBY);
				    arena.save();
				    playerSender.sendMessage(MessageManager.getFormatedMessage("manage:arena_status_changed", "Online"));
				    return true;
				}
			    } else {
				printCommands(playerSender, 2);
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage("error:arena_not_available"));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("add")) {
		    if (playerSender.hasPermission("zvp.manage.arena")) {
			Arena arena = this.game.getArena(parseInt(args[1]));
			if (arena != null) {
			    if (args[2].equalsIgnoreCase("prelobby") || args[2].equalsIgnoreCase("lobby")) {
				boolean success = arena.addArenaLobby(playerSender.getLocation().clone());
				
				if (success) {
				    playerSender.sendMessage(MessageManager.getFormatedMessage("manage:position_saved", "PreLobby"));
				} else {
				    playerSender.sendMessage(MessageManager.getMessage("error:prelobby_add"));
				}
				return true;
			    } else {
				printCommands(playerSender, 2);
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage("error:arena_not_available"));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("remove")) {
		    if (args[1].equalsIgnoreCase("arena")) {
			if (playerSender.hasPermission("zvp.manage.arena")) {
			    boolean success = GameManager.getManager().removeArena(GameManager.getManager().getArena(parseInt(args[2])));
			    
			    if (success) {
				playerSender.sendMessage(MessageManager.getMessage("manage:arena_removed"));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage("error:arena_not_available"));
				return true;
			    }
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    if (args[1].equalsIgnoreCase("lobby")) {
			if (playerSender.hasPermission("zvp.manage.lobby")) {
			    boolean success = GameManager.getManager().removeLobby(GameManager.getManager().getLobby(parseInt(args[2])));
			    
			    if (success) {
				playerSender.sendMessage(MessageManager.getMessage("manage:lobby_removed"));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage("error:lobby_not_available"));
				return true;
			    }
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    printCommands(playerSender, 2);
		    return true;
		}
		printCommands(playerSender, 2);
		return true;
	    }
	    
	    printCommands(playerSender, 1);
	    return true;
	}
	return true;
    }
    
    private void list(Player player, String option) {
	
	String pluginName = ZvP.getInstance().getDescription().getName();
	String pluginVersion = ZvP.getInstance().getDescription().getVersion();
	
	if (option.equalsIgnoreCase("signs") || option.equalsIgnoreCase("sign")) {
	    if (SignManager.getManager().getSigns().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|------------ " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Signs" + ChatColor.GRAY + " -------------|");
		
		for (ISign sign : SignManager.getManager().getSigns()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Type: " + ChatColor.BLUE + sign.getType() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Arena: " + ChatColor.BLUE + sign.getArena().getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Lobby: " + ChatColor.BLUE + sign.getLobby().getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Loc: " + ChatColor.BLUE + sign.getLocation().getBlockX() + ChatColor.DARK_GREEN + " | " + ChatColor.BLUE + sign.getLocation().getBlockY() + ChatColor.DARK_GREEN + " | " + ChatColor.BLUE + sign.getLocation().getBlockZ());
		}
	    }
	} else if (option.equalsIgnoreCase("arenas") || option.equalsIgnoreCase("arena")) {
	    if (this.game.getArenas().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|----------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Arenas" + ChatColor.GRAY + " ------------|");
		
		for (Arena a : this.game.getArenas()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "ID: " + ChatColor.BLUE + a.getID() + " - " + a.getStatus().toString() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Max. Players: " + ChatColor.BLUE + a.getMaxPlayers() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "PreLobby: " + ChatColor.BLUE + a.hasPreLobby() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Mode: " + ChatColor.BLUE + a.getDifficulty().name() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "World: " + ChatColor.BLUE + a.getWorld().getName());
		}
	    }
	} else if (option.equalsIgnoreCase("lobbys") || option.equalsIgnoreCase("lobby")) {
	    if (this.game.getLobbys().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|----------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Lobbys" + ChatColor.GRAY + " ------------|");
		
		for (Lobby l : this.game.getLobbys()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Lobby: " + ChatColor.BLUE + l.getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "World: " + ChatColor.BLUE + l.getWorld().getName());
		}
	    }
	} else if (option.equalsIgnoreCase("kits") || option.equalsIgnoreCase("kit")) {
	    if (KitManager.getManager().getKits().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|------------ " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Kits" + ChatColor.GRAY + " -------------|");
		
		for (IZvPKit kit : KitManager.getManager().getKits()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Kit: " + ChatColor.BLUE + kit.getName() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Enabled: " + ChatColor.BLUE + kit.isEnabled());
		}
	    }
	} else {
	    player.sendMessage("\n\n");
	    player.sendMessage(ChatColor.GRAY + "|----------- " + ChatColor.YELLOW + "List command Syntax" + ChatColor.GRAY + " ------------|");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list signs");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list arenas");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list lobbys");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list kits");
	}
    }
    
    private void printCommands(Player player, int page) {
	
	if (player.hasPermission("zvp.help")) {
	    
	    if (page > 2 || page < 1) {
		printCommands(player, 1);
		return;
	    }
	    
	    String pluginName = ZvP.getInstance().getDescription().getName();
	    String pluginVersion = ZvP.getInstance().getDescription().getVersion();
	    
	    player.sendMessage("\n\n");
	    player.sendMessage(ChatColor.GRAY + "|---------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Help: Page (" + page + "/2)" + ChatColor.GRAY + " ----------|");
	    player.sendMessage(ChatColor.GRAY + "| Use /zvp help [n] to get page [n] of help.\n|");
	    
	    switch (page) {
		case 1:
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp help [page]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp reload");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp update");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp status");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp leave");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop [Arena-ID]");
		    break;
		
		case 2:
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp addkit [Name]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp removekit [Name]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp pos1");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp pos2");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add lobby");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add position");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add [Arena-ID] preLobby");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp set [Arena-ID] [online|offline]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove arena [Arena-ID]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove lobby [Lobby-ID]");
		    break;
		
		default:
		    break;
	    }
	    
	} else {
	    commandDenied(player);
	}
    }
    
    private int parseInt(String s) {
	try {
	    return Integer.parseInt(s);
	} catch (NumberFormatException e) {
	    return -1;
	}
    }
    
    public static void commandDenied(Player player) {
	player.sendMessage(MessageManager.getMessage("commands:missing_Permission"));
    }
}
