package me.Aubli.ZvP;

import java.util.HashMap;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Kits.IZvPKit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ZvPCommands implements CommandExecutor {
	
	/*
	 * ZvP-Commands:
	 * 
	 * - zvp
	 * - zvp help
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		
		if(!(sender instanceof Player)){
			if(cmd.getName().equals("zvp")) {
				if(args.length==1) {
					if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
						GameManager.getManager().loadConfig();						
						sender.sendMessage("reloaded");//TODO message
						return true;
					}
					if(args[0].equalsIgnoreCase("stop-all")) {
						GameManager.getManager().stopGames();
						//TODO Message
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
			sender.sendMessage("This command is only for Players!");
			return true;
		}
		
		Player playerSender = (Player) sender;
		GameManager game = GameManager.getManager();
		
		if(cmd.getName().equalsIgnoreCase("test")){
		
			if(args.length==1) {
				String kitName = args[0];
				
				ItemStack[] content = playerSender.getInventory().getContents();
				
				KitManager.getManager().addKit(kitName, new ItemStack(Material.ITEM_FRAME), content);
			}
			
			if(args.length==2) {
				
				if(args[0].equalsIgnoreCase("kit")) {
				
					String kit = args[1];
					
					if(KitManager.getManager().getKit(kit)!=null) {
						
						IZvPKit zKit = KitManager.getManager().getKit(kit);
						
						for(ItemStack item : zKit.getContents()) {
							playerSender.getInventory().addItem(item);
						}
					}
					return true;
				}
				
				if(args[0].equalsIgnoreCase("m")) {
					Double sum = Double.parseDouble(args[1]);
					game.getPlayer(playerSender).getArena().addBalance(sum);
					return true;
				}
				
				
				Arena a = game.getArena(Integer.parseInt(args[0]));
				int round = Integer.parseInt(args[1].split(":")[0]);
				int wave = Integer.parseInt(args[1].split(":")[1]);
				a.setRound(round);
				a.setWave(wave);				
			}
		}
		
		
		
		if(cmd.getName().equalsIgnoreCase("zvp")){
			
			if(args.length==0){
				printCommands(playerSender);
				return true;
			}
			
			if(args.length==1){
				
				if(args[0].equalsIgnoreCase("help")){
					printCommands(playerSender);
					return true;
				}				
				if(args[0].equalsIgnoreCase("status")){
					//TODO command
					return false;
				}	
				if(args[0].equalsIgnoreCase("list")){
					//TODO command
					return true;
				}	
				if(args[0].equalsIgnoreCase("save")){
					if(playerSender.hasPermission("zvp.save")){
						GameManager.getManager().saveConfig();						
						playerSender.sendMessage("saved");//TODO message
						return true;
					}else{
						commandDenied(playerSender);
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")){
					if(playerSender.hasPermission("zvp.reload")){
						GameManager.getManager().loadConfig();						
						playerSender.sendMessage("reloaded");//TODO message
						return true;
					}else{
						commandDenied(playerSender);
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("leave")){
					if(playerSender.hasPermission("zvp.play")){
						ZvPPlayer p = game.getPlayer(playerSender);
						if(p!=null){
							Arena a = p.getArena();
							boolean success = game.removePlayer(p);
							
							if(success){
								playerSender.sendMessage("You left Arena " + p.getArena().getID()); //TODO Message
								a.sendMessage(playerSender.getName() + " has left!");
								return true;
							}else{
								playerSender.sendMessage("You were not found in a game"); //TODO Message
								return true;
							}
						}else{
							playerSender.sendMessage("not in a game"); //TODO Message
							return true;
						}
					}else{
						commandDenied(playerSender);
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("stop")){
					if(playerSender.hasPermission("zvp.stop.all")){
						game.stopGames();
						//TODO Message
						return true;
					}else{
						commandDenied(playerSender);
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("pos2")) {
					if(playerSender.hasPermission("zvp.manage.arena")){
						
						positions.put(args[0].toLowerCase(), playerSender.getLocation());
						playerSender.sendMessage(args[0].toLowerCase() + " saved!");
						
						if(positions.containsKey("pos1") && positions.containsKey("pos2")) {
							game.addArena(positions.get("pos1"), positions.get("pos2"));
							playerSender.sendMessage("arena created!"); //TODO Message
							positions.clear();
							return true;
						}
						return true;
					}else {
						commandDenied(playerSender);
						return true;
					}
				}
				
				
				printCommands(playerSender);
				return true;
			}
			
			if(args.length==2){
				if(args[0].equalsIgnoreCase("addkit")) {
					if(playerSender.hasPermission("zvp.manage.kit")) {
						if(KitManager.getManager().getKit(args[1])==null) {
							KitManager.getManager().openAddKitGUI(playerSender, args[1]);
							return true;
						}else {
							//TODO message kit already exists
							return true;
						}
					}else {
						//TODO permission
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("add")){
					if(args[1].equalsIgnoreCase("arena")){
						if(playerSender.hasPermission("zvp.manage.arena")){
							//TODO Message
							playerSender.getInventory().addItem(ZvP.tool);
							return true;
						}else{
							commandDenied(playerSender);
							return true;
						}
					}
					
					if(args[1].equalsIgnoreCase("lobby")){
						if(playerSender.hasPermission("zvp.manage.lobby")){	
							GameManager.getManager().addLobby(playerSender.getLocation().clone());
							playerSender.sendMessage("Lobby created!"); //TODO Message		
							return true;
						}else{
							commandDenied(playerSender);
							return true;
						}
					}					

					printCommands(playerSender);
					return true;
				}
				if(args[0].equalsIgnoreCase("stop")){
					if(playerSender.hasPermission("zvp.stop")){						
						Arena a = game.getArena(Integer.parseInt(args[1]));
						if(a!=null){
							a.stop();
							//TODO Message
							return true;
						}else{
							//TODO message
							return true;
						}
					}else{
						commandDenied(playerSender);
						return true;
					}
				}

				printCommands(playerSender);
				return true;
			}
			
			if(args.length==3){
				if(args[0].equalsIgnoreCase("remove")){
					if(args[1].equalsIgnoreCase("arena")){
						if(playerSender.hasPermission("zvp.manage.arena")){
							GameManager.getManager().removeArena(GameManager.getManager().getArena(Integer.parseInt(args[2])));
							playerSender.sendMessage("Arena removed"); //TODO message
							return true;
						}else{
							commandDenied(playerSender);
							return true;
						}
					}
					
					if(args[1].equalsIgnoreCase("lobby")){
						if(playerSender.hasPermission("zvp.manage.lobby")){
							GameManager.getManager().removeLobby(GameManager.getManager().getLobby(Integer.parseInt(args[2])));
							playerSender.sendMessage("Lobby removed"); //TODO message
							return true;
						}else{
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
	
	
	private void printCommands(Player player){
		
		if(player.hasPermission("zvp.help")){			
			String pluginName = ZvP.getInstance().getDescription().getName();
			String pluginVersion = ZvP.getInstance().getDescription().getVersion();
			String prefix = ZvP.getPrefix();
			
			player.sendMessage("\n\n");
			player.sendMessage(ChatColor.GRAY + "|--------------- " + ChatColor.YELLOW + pluginName + " v" + pluginVersion + ChatColor.RESET + " ( " + prefix + ")" + ChatColor.GRAY + " ---------------|");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp help");			
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp status");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp list");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp save");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp reload");
			
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp leave");
			
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add arena");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp add lobby");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove arena [Arena-ID]");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp remove lobby [Lobby-ID]");
			
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop");
			player.sendMessage(ChatColor.GRAY + "| " + ChatColor.RED + "/zvp stop [Arena-ID]");
			
		}else{
			commandDenied(player);
		}
	}
	
	private void commandDenied(Player player) {
		player.sendMessage("Permission needed!"); //TODO Permission message
	}
}
