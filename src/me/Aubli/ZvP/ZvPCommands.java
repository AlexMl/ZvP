package me.Aubli.ZvP;

import java.util.HashMap;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KCustomKit;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Sign.ISign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;


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
    
    private HashMap<String, Location> positions = new HashMap<String, Location>();
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
		    if (args[0].equalsIgnoreCase("stop-all")) {
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
		
		if (args[0].equalsIgnoreCase("m")) {
		    Double sum = Double.parseDouble(args[1]);
		    this.game.getPlayer(playerSender).getArena().getScore().addScore(this.game.getPlayer(playerSender), sum);
		    return true;
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
			    playerSender.sendMessage("D:" + d + " P:" + p + " RW:" + iw + ":" + ir + " ->" + a.getSpawningZombies(iw, ir, p, d));
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
	    
	    ZvP.getPluginLogger().log(Level.INFO, "Player " + playerSender.getName() + " attempts to execute Command: " + cmd.getName() + arguments, true);
	    
	    if (args.length == 0) {
		printCommands(playerSender);
		return true;
	    }
	    
	    if (args.length == 1) {
		
		if (args[0].equalsIgnoreCase("help")) {
		    printCommands(playerSender);
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
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "A: " + ChatColor.BLUE + a.getID() + " - " + a.getStatus().toString() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Player: " + ChatColor.BLUE + a.getPlayers().length + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getMaxPlayers() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Money: " + ChatColor.BLUE + a.getScore().getScore(null) + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Zombies: " + ChatColor.BLUE + a.getLivingZombies() + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getRound() * a.getWave() * a.getSpawnRate() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Killed: " + ChatColor.BLUE + a.getKilledZombies());
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
		
		if (args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("pos2")) {
		    if (playerSender.hasPermission("zvp.manage.arena")) {
			
			this.positions.put(args[0].toLowerCase(), playerSender.getLocation());
			playerSender.sendMessage(MessageManager.getFormatedMessage("manage:position_saved", args[0].toLowerCase()));
			
			if (this.positions.containsKey("pos1") && this.positions.containsKey("pos2")) {
			    this.game.addArena(this.positions.get("pos1"), this.positions.get("pos2"));
			    playerSender.sendMessage(MessageManager.getMessage("manage:arena_saved"));
			    this.positions.clear();
			    return true;
			}
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		
		printCommands(playerSender);
		return true;
	    }
	    
	    if (args.length == 2) {
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
		    
		    printCommands(playerSender);
		    return true;
		}
		if (args[0].equalsIgnoreCase("stop")) {
		    if (playerSender.hasPermission("zvp.stop")) {
			Arena a = this.game.getArena(Integer.parseInt(args[1]));
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
		
		printCommands(playerSender);
		return true;
	    }
	    
	    if (args.length == 3) {
		if (args[0].equalsIgnoreCase("remove")) {
		    if (args[1].equalsIgnoreCase("arena")) {
			if (playerSender.hasPermission("zvp.manage.arena")) {
			    GameManager.getManager().removeArena(GameManager.getManager().getArena(Integer.parseInt(args[2])));
			    playerSender.sendMessage(MessageManager.getMessage("manage:arena_removed"));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    if (args[1].equalsIgnoreCase("lobby")) {
			if (playerSender.hasPermission("zvp.manage.lobby")) {
			    GameManager.getManager().removeLobby(GameManager.getManager().getLobby(Integer.parseInt(args[2])));
			    playerSender.sendMessage(MessageManager.getMessage("manage:lobby_removed"));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }
		    
		    printCommands(playerSender);
		    return true;
		}
		printCommands(playerSender);
		return true;
	    }
	    
	    printCommands(playerSender);
	    return true;
	}
	return true;
    }
    
    private void list(Player player, String option) {
	
	String pluginName = ZvP.getInstance().getDescription().getName();
	String pluginVersion = ZvP.getInstance().getDescription().getVersion();
	
	if (option.equalsIgnoreCase("signs")) {
	    if (SignManager.getManager().getSigns().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|------------ " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Signs" + ChatColor.GRAY + " -------------|");
		
		for (ISign sign : SignManager.getManager().getSigns()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Type: " + ChatColor.BLUE + sign.getType() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Arena: " + ChatColor.BLUE + sign.getArena().getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Lobby: " + ChatColor.BLUE + sign.getLobby().getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Loc: " + ChatColor.BLUE + sign.getLocation().getBlockX() + ChatColor.DARK_GREEN + " | " + ChatColor.BLUE + sign.getLocation().getBlockY() + ChatColor.DARK_GREEN + " | " + ChatColor.BLUE + sign.getLocation().getBlockZ());
		}
	    }
	} else if (option.equalsIgnoreCase("arenas")) {
	    if (this.game.getArenas().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|----------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Arenas" + ChatColor.GRAY + " ------------|");
		
		for (Arena a : this.game.getArenas()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Arena: " + ChatColor.BLUE + a.getID() + " - " + a.getStatus().toString() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Player: " + ChatColor.BLUE + a.getPlayers().length + ChatColor.DARK_GREEN + "/" + ChatColor.BLUE + a.getMaxPlayers() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "   World: " + ChatColor.BLUE + a.getWorld().getName());
		}
	    }
	} else if (option.equalsIgnoreCase("lobbys")) {
	    if (this.game.getLobbys().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|----------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Lobbys" + ChatColor.GRAY + " ------------|");
		
		for (Lobby l : this.game.getLobbys()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Lobby: " + ChatColor.BLUE + l.getID() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "World: " + ChatColor.BLUE + l.getWorld().getName());
		    
		}
	    }
	} else if (option.equalsIgnoreCase("kits")) {
	    if (KitManager.getManager().getKits().length > 0) {
		
		player.sendMessage("\n\n");
		player.sendMessage(ChatColor.GRAY + "|------------ " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Kits" + ChatColor.GRAY + " -------------|");
		
		for (IZvPKit kit : KitManager.getManager().getKits()) {
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Kit: " + ChatColor.BLUE + kit.getName() + ChatColor.DARK_GREEN + ", " + ChatColor.RED + "Custom: " + ChatColor.BLUE + (kit instanceof KCustomKit));
		    
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
    
    private void printCommands(Player player) {
	
	if (player.hasPermission("zvp.help")) {
	    String pluginName = ZvP.getInstance().getDescription().getName();
	    String pluginVersion = ZvP.getInstance().getDescription().getVersion();
	    
	    player.sendMessage("\n\n");
	    player.sendMessage(ChatColor.GRAY + "|------------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " Help" + ChatColor.GRAY + " -------------|");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp help");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp update");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp status");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp save");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp reload");
	    
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp leave");
	    
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp addkit [Name]");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp removekit [Name]");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add lobby");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add position");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp pos1");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp pos2");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove arena [Arena-ID]");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove lobby [Lobby-ID]");
	    
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop");
	    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop [Arena-ID]");
	    
	} else {
	    commandDenied(player);
	}
    }
    
    public static void commandDenied(Player player) {
	player.sendMessage(MessageManager.getMessage("commands:missing_Permission"));
    }
}
