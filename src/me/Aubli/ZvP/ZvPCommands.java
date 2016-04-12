package me.Aubli.ZvP;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.GameEnums.ScoreType;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Game.ArenaParts.ArenaArea;
import me.Aubli.ZvP.Kits.IZvPKit;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Listeners.InteractListener;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ISign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Statistic.DataRecord;
import me.Aubli.ZvP.Statistic.DatabaseManager;
import me.Aubli.ZvP.Translation.MessageKeys;
import me.Aubli.ZvP.Translation.MessageKeys.arena;
import me.Aubli.ZvP.Translation.MessageKeys.commands;
import me.Aubli.ZvP.Translation.MessageKeys.config;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.util.Potion.PotionLayer;
import org.util.Potion.PotionLayer.PotionType;
import org.util.TabText.TabText;


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

    private GameManager game = GameManager.getManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {

	if (!(sender instanceof Player)) {
	    if (cmd.getName().equals("zvp")) {
		if (args.length == 1) {
		    if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			ZvP.getInstance().reloadPlugin();
			sender.sendMessage(MessageManager.getMessage(config.reloaded));
			return true;
		    }
		    if (args[0].equalsIgnoreCase("stop-all") || args[0].equalsIgnoreCase("stop")) {
			GameManager.getManager().stopGames();
			sender.sendMessage(MessageManager.getMessage(arena.stop_all));
			return true;
		    }
		}

		sender.sendMessage(getChatHeader("Console Commands"));
		sender.sendMessage("| /zvp reload");
		sender.sendMessage("| /zvp stop-all");
		return true;
	    }
	    return true;
	}

	Player playerSender = (Player) sender;

	if (cmd.getName().equalsIgnoreCase("zvptest") && ZvP.getPluginLogger().isDebugMode()) {
	    // Test command

	    if (args.length == 1) {

		if (args[0].startsWith("pos")) {
		    int arenaID = Integer.parseInt(args[0].substring(3, args[0].length()));

		    ArenaArea aa = GameManager.getManager().getArena(arenaID).getArea();
		    Location spawn;
		    for (int i = 0; i < 30000; i++) {
			try {
			    spawn = aa.getNewSaveLocation();
			    if (spawn.getBlock().isEmpty()) { // Dont want to harm the arena hu?
				spawn.getBlock().setType(Material.SANDSTONE);
			    }
			} catch (StackOverflowError e) {
			    break;
			}
		    }
		    return true;
		}

		if (args[0].equalsIgnoreCase("u")) {
		    SignManager.getManager().updateSigns();
		    GameManager.getManager().getPlayer(playerSender).getArena().updatePlayerBoards();
		    return true;
		}

		if (args[0].equalsIgnoreCase("tpr")) {
		    ZvPPlayer p = this.game.getPlayer(playerSender);
		    playerSender.teleport(p.getArena().getArea().getNewRandomLocation(true), TeleportCause.PLUGIN);
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

		if (args[0].equalsIgnoreCase("uloc")) {
		    double md = Double.parseDouble(args[1]);

		    ZvPPlayer player = this.game.getPlayer(playerSender);

		    long start = System.currentTimeMillis();
		    int i = 0;
		    for (i = 0; i < 10000; i++) {
			try {
			    player.getArena().getArea().getNewUnsaveLocation(md).clone().add(0, 10, 0).getBlock().setType(Material.WOOL);
			} catch (StackOverflowError e) {
			    e.printStackTrace();
			}
		    }

		    long ende = System.currentTimeMillis();
		    System.out.println("Für " + i + " durchläufe " + ((ende - start) / 1000.0) + " sekunden gebraucht!");
		    return true;
		}

		if (args[0].equalsIgnoreCase("spawn")) {
		    int amount = Integer.parseInt(args[1]);

		    ZvPPlayer player = this.game.getPlayer(playerSender);
		    player.getArena().spawnZombies(amount);
		    return true;
		}

		if (args[0].equalsIgnoreCase("potion")) {
		    PotionType type = PotionType.valueOf(args[1]);
		    if (type != null) {
			try {
			    // [type: STRENGTH, level: 1, isSplash: true, isExtended: true]
			    PotionLayer l = PotionLayer.createPotion(PotionType.STRENGTH);
			    l.setStrong(false);
			    l.setSplash(true);
			    l.setHasExtendedDuration(true);

			    playerSender.getInventory().addItem(PotionLayer.createPotion(type).toItemStack(1), l.toItemStack(1));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		    return true;
		}

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
			    lore.add(ChatColor.RED + "Price: " + shopItem.getBuyPrice());

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
		    if (!ZvPConfig.getEnableEcon() || (!ZvPConfig.getEnableEcon() && !ZvPConfig.getIntegrateGame())) { // possibility of cheating money
			Double sum = Double.parseDouble(args[1]);
			this.game.getPlayer(playerSender).getArena().getScore().addScore(this.game.getPlayer(playerSender), sum, ScoreType.SHOP_SCORE);
			return true;
		    }
		    return true;
		}

		if (args[0].equalsIgnoreCase("insert")) {
		    int max = Integer.parseInt(args[1]);

		    long start = System.currentTimeMillis();
		    Random rand = new Random();

		    for (int l = 0; l < 2; l++) {

			DataRecord[] ra = new DataRecord[max];
			for (int i = 0; i < max; i++) {
			    ra[i] = new DataRecord(UUID.randomUUID(), rand.nextInt(50), rand.nextInt(50), rand.nextInt(50), rand.nextDouble() * 100.0);
			}
			DatabaseManager.getManager().handleRecord(ra);

		    }
		    long end = System.currentTimeMillis();

		    // double diff1 = (middle - start) / 1000.0;
		    // double diff2 = (end - middle) / 1000.0;
		    double diff3 = (end - start) / 1000.0;

		    System.out.println("Für " + max + ":");
		    // System.out.println("Randomize: " + diff1);
		    // System.out.println("Insert: " + diff2);
		    System.out.println("Together: " + diff3);
		    return true;
		}

		Arena a = this.game.getArena(Integer.parseInt(args[0]));
		int round = Integer.parseInt(args[1].split(":")[0]);
		int wave = Integer.parseInt(args[1].split(":")[1]);
		a.setRound(round);
		a.setWave(wave);
	    }

	    if (args.length == 4) {
		if (args[0].equalsIgnoreCase("insert")) {
		    int kills = Integer.parseInt(args[1]);
		    int deaths = Integer.parseInt(args[2]);
		    double money = Double.parseDouble(args[3]);

		    DatabaseManager.getManager().handleRecord(new DataRecord(playerSender.getUniqueId(), kills, kills, deaths, money));
		    return true;
		}
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
			    int sz = a.getSpawningZombies(iw, ir, p, d);
			    playerSender.sendMessage("D:" + d + " P:" + p + " R:" + ir + " W:" + iw + " @" + id + " --> " + sz + " --> IT: " + EntityListener.getArenaInteractionTime(sz));
			}
		    }
		    return true;
		}
	    }
	}

	if (cmd.getName().equalsIgnoreCase("zvp")) {

	    String arguments = "";
	    for (String arg : args) {
		arguments += " " + arg;
	    }

	    ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Player " + playerSender.getName() + " attempts to execute Command: " + cmd.getName() + arguments, true);

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
			playerSender.sendMessage(getChatHeader("Status"));
			playerSender.sendMessage(ChatColor.GRAY + " Use '" + ChatColor.RED + "/zvp status [A ID]" + ChatColor.GRAY + "' for more info");

			String tableString = "A ID`Status`P/MaxP`Score`Living`Spawning`Killed\n";

			for (Arena a : this.game.getArenas()) {
			    if (a.isRunning()) {
				tableString += ChatColor.BLUE + "" + a.getID() + "`" + a.getStatus().toString() + "`" + a.getPlayers().length + ChatColor.RED + " / " + ChatColor.BLUE + a.getConfig().getMaxPlayers() + "`" + (a.getScore().isSeparated() ? "separated" : "shared") + "`" + a.getLivingZombieAmount() + "`" + a.getSpawningZombies() + "`" + a.getKilledZombies() + "\n";
			    } else {
				tableString += ChatColor.BLUE + "" + a.getID() + "`" + a.getStatus().toString() + "`" + "0" + ChatColor.RED + " / " + ChatColor.BLUE + a.getConfig().getMaxPlayers() + "`-`-`-`-\n";
			    }
			}

			TabText text = new TabText(tableString);
			text.setTabs(5, 15, 23, 33, 39, 47);
			playerSender.sendMessage("\n" + text.getPage(0, false));
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("list")) {

		    if (playerSender.hasPermission("zvp.status")) {
			list(playerSender, "");
			return true;
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
		    if (playerSender.hasPermission("zvp.reload")) {
			ZvP.getInstance().reloadPlugin();
			playerSender.sendMessage(MessageManager.getMessage(config.reloaded));
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
				playerSender.sendMessage(MessageManager.getFormatedMessage(MessageKeys.game.left, p.getArena().getID()));
				a.sendMessage(MessageManager.getFormatedMessage(MessageKeys.game.player_left, playerSender.getName()));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage(MessageKeys.game.player_not_found));
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(MessageKeys.game.not_in_game));
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
			playerSender.sendMessage(MessageManager.getMessage(arena.stop_all));
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

		if (args[0].equalsIgnoreCase("status")) {
		    if (playerSender.hasPermission("zvp.status")) {
			Arena arena = GameManager.getManager().getArena(parseInt(args[1]));

			if (arena != null) {
			    playerSender.sendMessage(getChatHeader("Arena " + arena.getID()));
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "ID: " + ChatColor.BLUE + arena.getID());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "World: " + ChatColor.BLUE + arena.getWorld().getName());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Prelobby: " + ChatColor.BLUE + arena.hasPreLobby());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Status: " + ChatColor.BLUE + arena.getStatus().name());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Waves: " + ChatColor.BLUE + arena.getCurrentWave() + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getConfig().getMaxWaves());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Rounds: " + ChatColor.BLUE + arena.getCurrentRound() + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getConfig().getMaxRounds());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Players(current/min/max): " + ChatColor.BLUE + arena.getPlayers().length + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getConfig().getMinPlayers() + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getConfig().getMaxPlayers());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Difficulty: " + ChatColor.BLUE + arena.getDifficulty().name());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Mode: " + ChatColor.BLUE + arena.getArenaMode().getName());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Zombies(current/spawning/dead): " + ChatColor.BLUE + arena.getLivingZombieAmount() + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getSpawningZombies() + ChatColor.GRAY + "/" + ChatColor.BLUE + arena.getKilledZombies());
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Interaction Timeout: " + ChatColor.BLUE + EntityListener.getArenaInteractionTime(arena.getSpawningZombies()) + " s");
			    playerSender.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "Seperated Scores: " + ChatColor.BLUE + ((ZvPConfig.getEnableEcon() && ZvPConfig.getIntegrateGame()) ? true : arena.getConfig().isSeparatedScores()));
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}

		if (args[0].equalsIgnoreCase("join")) {
		    if (playerSender.hasPermission("zvp.play")) {
			if (!GameManager.getManager().isInGame(playerSender)) {
			    Arena arena = GameManager.getManager().getArena(parseInt(args[1]));

			    if (arena != null) {
				boolean success = GameManager.getManager().createPlayer(playerSender, arena, SignManager.getManager().getSigns(arena)[0].getLobby());

				if (!success) {
				    playerSender.sendMessage(MessageManager.getMessage(MessageKeys.arena.not_ready));
				}
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(MessageKeys.game.already_in_game));
			    return true;
			}
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
			    playerSender.sendMessage(MessageManager.getFormatedMessage(error.kit_already_exists, args[1]));
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
			    playerSender.sendMessage(MessageManager.getFormatedMessage(manage.kit_removed, args[1]));
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getFormatedMessage(error.kit_not_exist, args[1]));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}

		if (args[0].equalsIgnoreCase("record")) {
		    if (playerSender.hasPermission("zvp.manage")) {
			if (ZvPConfig.getEnabledStatistics()) {
			    try {
				long duration = (long) (Double.parseDouble(args[1]) * 3600 * 1000);
				if (duration > 0) {
				    boolean success = DatabaseManager.getManager().startTimedStatistics(duration);
				    if (success) {
					playerSender.sendMessage(MessageManager.getFormatedMessage(manage.record_start, (duration / 3600000)));
				    } else {
					playerSender.sendMessage(MessageManager.getMessage(manage.record_already_running));
				    }
				} else {
				    playerSender.sendMessage(MessageManager.getMessage(error.negative_duration));
				}
			    } catch (SQLException e) {
				playerSender.sendMessage(MessageManager.getMessage(error.record_start_error));
				ZvP.getPluginLogger().log(getClass(), Level.SEVERE, playerSender.getName() + " tried starting a record but an error interupted: ", true, false, e);
			    }
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(MessageKeys.game.feature_disabled));
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
			    playerSender.sendMessage(MessageManager.getMessage(manage.tool));
			    playerSender.getInventory().addItem(ZvP.getTool(ZvP.ADDARENA_SINGLE));
			    return true;
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }

		    if (args[1].equalsIgnoreCase("lobby")) {
			if (playerSender.hasPermission("zvp.manage.lobby")) {
			    GameManager.getManager().addLobby(playerSender.getLocation().clone());
			    playerSender.sendMessage(MessageManager.getMessage(manage.lobby_saved));
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
			    playerSender.sendMessage(MessageManager.getFormatedMessage(arena.stop, a.getID()));
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
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
				arena.getConfig().saveConfig();
				playerSender.sendMessage(MessageManager.getFormatedMessage(manage.arena_status_changed, "Offline"));
				return true;
			    } else if (args[2].equalsIgnoreCase("online") || args[2].equalsIgnoreCase("on")) {
				if (!arena.isOnline()) {
				    arena.setStatus(ArenaStatus.STANDBY);
				    arena.getConfig().saveConfig();
				    playerSender.sendMessage(MessageManager.getFormatedMessage(manage.arena_status_changed, "Online"));
				    return true;
				}
			    } else {
				printCommands(playerSender, 2);
				return true;
			    }
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
			    return true;
			}
		    } else {
			commandDenied(playerSender);
			return true;
		    }
		}
		if (args[0].equalsIgnoreCase("add")) {
		    if (playerSender.hasPermission("zvp.manage.arena")) {
			if (args[1].equalsIgnoreCase("arena")) {
			    if (args[2].equalsIgnoreCase("polygon")) {
				playerSender.sendMessage(MessageManager.getMessage(manage.tool));
				playerSender.getInventory().addItem(ZvP.getTool(ZvP.ADDARENA_POLYGON));
				return true;
			    }
			    if (args[2].equalsIgnoreCase("clear")) {
				InteractListener.clearPositionList();
				playerSender.sendMessage(MessageManager.getMessage(manage.position_cleared));
				return true;
			    }
			    if (args[2].equalsIgnoreCase("finish")) {
				Arena arena = InteractListener.createArenaFromList();
				InteractListener.clearPositionList();

				if (arena != null) {
				    playerSender.sendMessage(MessageManager.getFormatedMessage(manage.arena_saved, arena.getID()));
				} else {
				    playerSender.sendMessage(MessageManager.getMessage(error.arena_place));
				}
				return true;
			    }
			    printCommands(playerSender, 2);
			    return true;
			}

			Arena arena = this.game.getArena(parseInt(args[1]));
			if (arena != null) {
			    if (args[2].equalsIgnoreCase("prelobby") || args[2].equalsIgnoreCase("lobby")) {
				boolean success = arena.addArenaLobby(playerSender.getLocation().clone());

				if (success) {
				    playerSender.sendMessage(MessageManager.getMessage(manage.lobby_saved));
				} else {
				    playerSender.sendMessage(MessageManager.getMessage(error.prelobby_add));
				}
				return true;
			    }
			    if (args[2].equalsIgnoreCase("prelobbyposition") || args[2].equalsIgnoreCase("lobbyposition")) {
				if (arena.hasPreLobby()) {
				    arena.getPreLobby().addSpawnLocation(playerSender.getLocation());
				    playerSender.sendMessage(MessageManager.getFormatedMessage(manage.position_saved, "Position in PreLobby"));
				    return true;
				} else {
				    playerSender.sendMessage(MessageManager.getMessage(error.no_prelobby));
				    return true;
				}
			    }

			    printCommands(playerSender, 2);
			    return true;
			} else {
			    playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
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
				playerSender.sendMessage(MessageManager.getMessage(manage.arena_removed));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
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
				playerSender.sendMessage(MessageManager.getMessage(manage.lobby_removed));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage(error.lobby_not_available));
				return true;
			    }
			} else {
			    commandDenied(playerSender);
			    return true;
			}
		    }

		    if (args[1].equalsIgnoreCase("prelobby")) {
			if (playerSender.hasPermission("zvp.manage.arena")) {
			    Arena arena = GameManager.getManager().getArena(parseInt(args[2]));

			    if (arena != null) {
				arena.deleteArenaLobby();
				playerSender.sendMessage(MessageManager.getMessage(manage.lobby_removed));
				return true;
			    } else {
				playerSender.sendMessage(MessageManager.getMessage(error.arena_not_available));
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

	if (option.equalsIgnoreCase("signs") || option.equalsIgnoreCase("sign")) {
	    if (SignManager.getManager().getSigns().length > 0) {

		player.sendMessage(getChatHeader("Signs"));
		String tableString = "Arena`Lobby`Type`ID`X`Y`Z`World\n";

		for (ISign sign : SignManager.getManager().getSigns()) {
		    tableString += ChatColor.BLUE + "" + sign.getArena().getID() + "`" + sign.getLobby().getID() + "`" + sign.getType() + "`" + sign.getID() + "`" + +sign.getLocation().getBlockX() + "`" + sign.getLocation().getBlockY() + "`" + sign.getLocation().getBlockZ() + "`" + sign.getWorld().getName() + "\n";
		}
		TabText text = new TabText(tableString);
		text.setTabs(6, 12, 26, 29, 35, 38, 43);
		player.sendMessage("\n" + text.getPage(0, false));
	    }
	} else if (option.equalsIgnoreCase("arenas") || option.equalsIgnoreCase("arena")) {
	    if (this.game.getArenas().length > 0) {

		player.sendMessage(getChatHeader("Arenas"));
		String tableString = "ID`Status`Min / Max`PreLobby`Mode`World\n";

		for (Arena a : this.game.getArenas()) {
		    tableString += ChatColor.BLUE + "" + a.getID() + "`" + a.getStatus().toString() + "`" + a.getConfig().getMinPlayers() + ChatColor.RED + " / " + ChatColor.BLUE + a.getConfig().getMaxPlayers() + "`" + a.hasPreLobby() + "`" + a.getDifficulty().name() + "`" + a.getWorld().getName() + "\n";
		}
		TabText text = new TabText(tableString);
		text.setTabs(3, 12, 22, 31, 40);
		player.sendMessage("\n" + text.getPage(0, false));
	    }
	} else if (option.equalsIgnoreCase("lobbys") || option.equalsIgnoreCase("lobby")) {
	    if (this.game.getLobbys().length > 0) {

		player.sendMessage(getChatHeader("Lobbies"));
		String tableString = "ID`World\n";

		for (Lobby l : this.game.getLobbys()) {
		    tableString += ChatColor.BLUE + "" + l.getID() + "`" + l.getWorld().getName() + "\n";
		}
		TabText text = new TabText(tableString);
		text.setTabs(10);
		player.sendMessage("\n" + text.getPage(0, false));
	    }
	} else if (option.equalsIgnoreCase("kits") || option.equalsIgnoreCase("kit")) {
	    if (KitManager.getManager().getKits().length > 0) {

		player.sendMessage(getChatHeader("Kits"));
		String tableString = "Name`Price`Enabled`Permission\n";

		for (IZvPKit kit : KitManager.getManager().getKits()) {
		    tableString += ChatColor.BLUE + "" + kit.getName() + "`" + kit.getPrice() + "`" + kit.isEnabled() + "`" + kit.getPermissionNode() + "\n";
		}
		TabText text = new TabText(tableString);
		text.setTabs(15, 22, 33);
		player.sendMessage("\n" + text.getPage(0, false));
	    }
	} else {
	    player.sendMessage(getChatHeader("Command Syntax"));
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

	    player.sendMessage(getChatHeader("Help: Page (" + page + "/2)"));
	    player.sendMessage(ChatColor.GRAY + "| Use /zvp help [n] to get page [n] of help.\n|");

	    switch (page) {
		case 1:
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp help [page]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp reload");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp update");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list [arena|lobby|sign|kit]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp status");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp leave");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp join [Arena-ID]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop [Arena-ID]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp addkit [Name]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp removekit [Name]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp record [duration (in hours)]");
		    break;

		case 2:
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add lobby");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena polygon");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena clear");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena finish");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add position");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add [Arena-ID] preLobby");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add [Arena-ID] preLobbyPosition");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp set [Arena-ID] [online|offline]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove arena [Arena-ID]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove lobby [Lobby-ID]");
		    player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove preLobby [Arena-ID]");
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

    private String getChatHeader(String headerType) {

	String pluginName = ZvP.getInstance().getDescription().getName();
	String pluginVersion = ZvP.getInstance().getDescription().getVersion();
	String content = " " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + " " + headerType + " ";

	int contentLength = ChatColor.stripColor(content).length();
	int dashCount = (int) Math.ceil((53 - contentLength - 2) / 2.0);// INFO: Magic numbers

	StringBuilder builder = new StringBuilder();
	builder.append("\n\n");
	builder.append(ChatColor.GRAY + "|");

	for (int i = 0; i < dashCount; i++) {
	    builder.append('-');
	}
	builder.append(content);
	builder.append(ChatColor.GRAY);
	for (int i = 0; i < dashCount; i++) {
	    builder.append('-');
	}

	builder.append('|');
	return builder.toString();
    }

    public static void commandDenied(Player player) {
	player.sendMessage(MessageManager.getMessage(commands.missing_permission));
    }
}
