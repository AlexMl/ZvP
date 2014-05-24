package me.Aubli.ZvP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("This command is only for Players!");
			return true;
		}
		
		Player playerSender = (Player) sender;
		GameManager game = GameManager.getManager();
		
		if(cmd.getName().equalsIgnoreCase("test")){
		
			Arena a = game.getArena(Integer.parseInt(args[0]));
			Bukkit.broadcastMessage(a.getLivingZombies() + " Zombies");
			a.clearArena();
			
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
					
					return false;
				}	
				if(args[0].equalsIgnoreCase("list")){
					
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
				if(args[0].equalsIgnoreCase("reload")){
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
							boolean success = game.removePlayer(p);
							
							if(success){
								playerSender.sendMessage("You left Arena " + p.getArena().getID()); //TODO Message
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
				
				printCommands(playerSender);
				return true;
			}
			
			if(args.length==2){
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
	
	/*@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		
		FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		if(sender instanceof Player){
		
		Player playerSender = (Player) sender;
		PlayerInventory inv = playerSender.getInventory();
		Player messagePlayer;
		
		String specNotSet = messageFileConfiguration.getString("config.error_messages.error_spectator_place_not_set");
		String[] specNotSetArray = specNotSet.split("<newLine>");		
		
		Location zombiePvpLoc = null;
		World zombieZvpLocWorld;
		double zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ;
		float zombieZvpLocYaw, zombieZvpLocPitch; 
		
		if(plugin.getConfig().getString("config.mem.zombieZvPlocation")!=null){	
			
			zombieZvpLocWorld = Bukkit.getServer().getWorld(plugin.getConfig().getString("config.mem.zombieZvPlocation.world"));
			zombieZvpLocX = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.x");
			zombieZvpLocY = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.y");
			zombieZvpLocZ = plugin.getConfig().getDouble("config.mem.zombieZvPlocation.z");
			zombieZvpLocPitch = (float) plugin.getConfig().getDouble("config.mem.zombieZvPlocation.pitch");
			zombieZvpLocYaw = (float) plugin.getConfig().getDouble("config.mem.zombieZvPlocation.yaw");
			
			zombiePvpLoc = new Location(zombieZvpLocWorld, zombieZvpLocX, zombieZvpLocY, zombieZvpLocZ);
			zombiePvpLoc.setYaw(zombieZvpLocYaw);
			zombiePvpLoc.setPitch(zombieZvpLocPitch);
		}
		
		ItemStack kartoffelStack, karottenStack, eisenbarrenStack, pfeil64Stack, pfeil32Stack,verrottetesFleisch64Stack;
		int kartoffelPreis, karottenPreis, eisenbarrenPreis, pfeil64Preis, pfeil32Preis, verrottetesFleisch64Preis;
		
		kartoffelPreis = plugin.getConfig().getInt("config.price.sell.potato");
		karottenPreis = plugin.getConfig().getInt("config.price.sell.carrot");
		eisenbarrenPreis = plugin.getConfig().getInt("config.price.sell.ironingot");
		verrottetesFleisch64Preis = plugin.getConfig().getInt("config.price.sell.rottenflesh64");
		
		pfeil32Preis = plugin.getConfig().getInt("config.price.sell.arrow32");
		pfeil64Preis = plugin.getConfig().getInt("config.price.sell.arrow64");		
		
		//zomdef Befehle
		if(cmd.getName().equalsIgnoreCase("zvp")){
			if(args.length==0){
				//playerSender.sendMessage(ChatColor.AQUA + "/zomdef status " + ChatColor.GRAY + ":" + ChatColor.GOLD + " Zeigt den Status an\n" + ChatColor.AQUA + "/zomdef setspawn " + ChatColor.GRAY + ":" + ChatColor.GOLD + " Setzt einen Spawn für ZvP(optional)!\n" + ChatColor.AQUA + "/zomdef preise kaufen " + ChatColor.GRAY + ":" + ChatColor.GOLD + " Zeigt die Preisliste\n" + ChatColor.AQUA + "/zomdef preise verkaufen " + ChatColor.GRAY + ":" + ChatColor.GOLD + " Zeigt die Verkaufspreise");
				return false;
			}
			
			if(args.length==1||args.length==2){
				//sprache setzen				
				if(args[0].equalsIgnoreCase("setlang")){
					if(playerSender.hasPermission("zvp.admin.set.language")){
						if(args.length==2){
							
							plugin.getConfig().set("config.misc.language", args[1].toString());
							plugin.saveConfig();
							plugin.getServer().reload();
							plugin.reloadConfig();
							playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.restart"));
							return true;
						}else{
							//nicht genügend Arguments
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_not_enoug_args"));
							return false;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
				
				//status
				if(args[0].equalsIgnoreCase("status")){
					if(playerSender.hasPermission("zvp.admin.status")){
						
						int zombies = 0;
						for(int i=0; i<playerSender.getWorld().getEntities().size();i++){
							if(playerSender.getWorld().getEntities().get(i).toString().equalsIgnoreCase("craftzombie")){
								zombies++;
							}
						}						
						playerSender.sendMessage("Kontostand : " + plugin.Konto);
						playerSender.sendMessage("Übrige Zombies : " + (plugin.Runde*plugin.Welle*30 - plugin.gesammtKill) + " , " + zombies);
						
						return true;
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
								
				//Add StarterKit
				if(args[0].equalsIgnoreCase("addkit")){
					if(playerSender.hasPermission("zvp.addkit")){
						if(args.length==2){
							if(args[1].getBytes().length<=20){								
								plugin.addKit(playerSender,args[1]);							
								return true;
							}else{
								//zu viele Buchstaben								
								playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_to_long_name"));
								return true;
							}
						}else if(args.length >= 3){
							//zu viele argumente
							playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_to_many_args"));
							return false;
						}else if(args.length <= 1){
							//zu wenig argumente
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_not_enoug_args"));
							return false;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
				
				//Spectator spawn setzen
				if(args[0].equalsIgnoreCase("setspecspawn")){
					if(playerSender.hasPermission("zvp.admin.set.spectatorspawn")){
						if(args.length==1){
							
							plugin.getConfig().set("config.mem.spectatorZvPLocation.world", playerSender.getWorld().getName());
							plugin.getConfig().set("config.mem.spectatorZvPLocation.x", playerSender.getLocation().getX());
							plugin.getConfig().set("config.mem.spectatorZvPLocation.y", playerSender.getLocation().getY());
							plugin.getConfig().set("config.mem.spectatorZvPLocation.z", playerSender.getLocation().getZ());
							plugin.getConfig().set("config.mem.spectatorZvPLocation.yaw", playerSender.getLocation().getYaw());
							plugin.getConfig().set("config.mem.spectatorZvPLocation.pitch", playerSender.getLocation().getPitch());
							plugin.saveConfig();
							
							//Spawn wurde gesetzt
							playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.spawn_set_message"));
							return true;
						}else{
							//zu viele argumente
							playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_to_many_args"));
							return false;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
				
				//Spawn setzen
				if(args[0].equalsIgnoreCase("setspawn")){
					if(playerSender.hasPermission("zvp.admin.set.spawn")){
						if(args.length==1){
							
							plugin.getConfig().set("config.mem.zombieZvPlocation.world", playerSender.getWorld().getName());
							plugin.getConfig().set("config.mem.zombieZvPlocation.x", playerSender.getLocation().getX());
							plugin.getConfig().set("config.mem.zombieZvPlocation.y", playerSender.getLocation().getY());
							plugin.getConfig().set("config.mem.zombieZvPlocation.z", playerSender.getLocation().getZ());
							plugin.getConfig().set("config.mem.zombieZvPlocation.yaw", playerSender.getLocation().getYaw());
							plugin.getConfig().set("config.mem.zombieZvPlocation.pitch", playerSender.getLocation().getPitch());
							plugin.saveConfig();
							
							//Spawn wurde gesetzt
							playerSender.sendMessage(ChatColor.GREEN + messageFileConfiguration.getString("config.messages.spawn_set_message"));
							return true;
						}else{
							//zu viele argumente
							playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_to_many_args"));
							return true;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("skip")){
					
					plugin.gesammtKill = plugin.zombieCount;
					return true;
				}
				return false;
			}
			return false;
		}			
		
			//Starten
			if(cmd.getName().equalsIgnoreCase("zvpstart")){
				if(plugin.start==false){
				if(playerSender.hasPermission("zvp.start")){
					if(args.length==1){
						if(args[0].matches("\\d*")){
							
							int rundenZahl = Integer.parseInt(args[0]);
							
							if(rundenZahl>0){
								if(plugin.changeToSpectator==true){
									if(plugin.getConfig().get("config.mem.spectatorZvPLocation")!=null){
										plugin.zomStart(playerSender,rundenZahl);	
										return true;
									}else{
										playerSender.sendMessage(ChatColor.RED + specNotSetArray[0] + "\n" + specNotSetArray[1]);
										return true;
									}
								}else{
									plugin.zomStart(playerSender,rundenZahl);	
									return true;
								}
							}else{
								//Rundenzahl muss > 0
								playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_number_less_than_1"));
								return true;
							}
						}else{
							// Du musst eine zahl eingeben
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_number"));
							return true;
						}
					}else{
						//Rundenzahl angeben
						playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_rounds_entered"));
						return true;
					}
				}else{
					// permission denieded
					playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
					return true;
				}	
				}else{
					//Es existiert bereits ein spiel
					playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_event_already_started"));
					return true;
				}
			}
			
			
			//stopen
			if(cmd.getName().equalsIgnoreCase("zvpstop")){
				if(playerSender.hasPermission("zvp.stop")){
					if(plugin.start==true){
						plugin.zomStop(playerSender);
						return true;
					}else{
						//Erst ein Event starten
						playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_event_started"));
						return true;
					}					
				}else{
					// permission denieded
					playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
					return true;
				}
			}
			
			//Verkaufen			
			if(plugin.start==true){
				if(cmd.getName().equalsIgnoreCase("zvpsell")){
					if(playerSender.hasPermission("zvp.sell")){
						if(plugin.playerVote.contains(playerSender)){
							if(plugin.welle==false){
					
							int Umsatz;
							int slot;
							slot = inv.getHeldItemSlot();
							ItemStack handStack = inv.getItem(slot);
						
							kartoffelStack = new ItemStack(Material.POTATO_ITEM);
							karottenStack = new ItemStack(Material.CARROT_ITEM);
							eisenbarrenStack = new ItemStack(Material.IRON_INGOT);
							verrottetesFleisch64Stack = new ItemStack(Material.ROTTEN_FLESH, 64);
							pfeil32Stack = new ItemStack(Material.ARROW, 32);
							pfeil64Stack = new ItemStack(Material.ARROW, 64);
						
							if(handStack!=null){					
								if(handStack.isSimilar(kartoffelStack)){							
									Umsatz = ((handStack.getAmount())*kartoffelPreis);
									plugin.Konto += Umsatz;
									//Kontostand ausgeben
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", playerSender);
									inv.removeItem(handStack);
								}
							
								if(handStack.isSimilar(karottenStack)){							
									Umsatz = ((handStack.getAmount())*karottenPreis);
									plugin.Konto += Umsatz;
									//Kontostand ausgeben
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", playerSender);
									inv.removeItem(handStack);
								}
							
								if(handStack.isSimilar(eisenbarrenStack)){							
									Umsatz = ((handStack.getAmount())*eisenbarrenPreis);
									plugin.Konto += Umsatz;
									//Kontostand ausgeben
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", playerSender);
									inv.removeItem(handStack);
								}
								
								if(handStack.isSimilar(pfeil64Stack)){
									if(handStack.getAmount() == 64){
										Umsatz = pfeil64Preis;
										plugin.Konto += Umsatz;
										//Kontostand ausgeben
										for(int y=0; y<plugin.playerVote.size();y++){
											messagePlayer = plugin.playerVote.get(y);
											messagePlayer.sendMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$");
										}
										inv.removeItem(handStack);
									}
								}
							
								if(handStack.isSimilar(pfeil32Stack)){
									if(handStack.getAmount() == 32){
										Umsatz = pfeil32Preis;
										plugin.Konto += Umsatz;
										//Kontostand ausgeben
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", playerSender);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$");
										inv.removeItem(handStack);
									}
								}
								
								if(handStack.isSimilar(verrottetesFleisch64Stack)){
									if(handStack.getAmount() == 64){
										Umsatz = verrottetesFleisch64Preis;
										plugin.Konto += Umsatz;
										//Kontostand ausgeben
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", playerSender);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message") + " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$");
										inv.removeItem(handStack);
									}
								}	
							}
							return true;
						}else{
							// wärend der welle handeln
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_trading_time_over"));
							return true;
						}
						}else{
							//nicht eingeloggt
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_not_loged_in"));
							return true;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}
				
				if(cmd.getName().equalsIgnoreCase("zvpleave")){
					if(playerSender.hasPermission("zvp.join")){
						if(plugin.playerVote.contains(playerSender)){
							plugin.leave(playerSender);
							return true;
						}else{
							playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_not_loged_in"));
							return true;
						}
					}else{
						// permission denieded
						playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return true;
					}
				}				
				
				//join
				if(cmd.getName().equalsIgnoreCase("zvpjoin")){
					if(plugin.voteZeit == true){					
						if(playerSender.hasPermission("zvp.join")){
							plugin.zomjoin(playerSender);
							return true;
						}else{
							// permission denieded
							playerSender.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
							return true;
						}
					}else{
						//Anmeldezeit vorbei
						playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_login_time_over"));
						return true;
					}
				}
			}else{
				//Kein spiel aktiv
				playerSender.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_event_started"));
				return true;
			}			
		return true;
	}else{
		// Du musst ein Spieler sein
		log.info(messageFileConfiguration.getString("config.error_messages.error_console_command"));
		return true;
	}	
	}*/
}
