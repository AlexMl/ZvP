package me.Aubli.ZvP.Listeners;

import java.util.HashMap;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Sign.InteractSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener{
	
	private HashMap<Action, Location> clickLoc = new HashMap<Action, Location>();
	
	private SignManager sm = SignManager.getManager();
		
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		Player eventPlayer = (Player) event.getPlayer();
		
		if(event.getItem()!=null){
			if(event.getItem().equals(ZvP.tool)){
				if(eventPlayer.hasPermission("zvp.tool") && eventPlayer.hasPermission("zvp.manage.arena")){
					event.setCancelled(true);
					
					if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
						clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
						eventPlayer.sendMessage("Right click saved!"); //TODO Message
					}
					
					if(event.getAction() == Action.LEFT_CLICK_BLOCK){
						clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
						eventPlayer.sendMessage("Left click saved!"); //TODO Message						
					}
					
					if(clickLoc.containsKey(Action.RIGHT_CLICK_BLOCK) && clickLoc.containsKey(Action.LEFT_CLICK_BLOCK)){
						GameManager.getManager().addArena(clickLoc.get(Action.LEFT_CLICK_BLOCK), clickLoc.get(Action.RIGHT_CLICK_BLOCK));
						eventPlayer.sendMessage("arena created!"); //TODO Message
						clickLoc.clear();
						return;
					}
				}else{
					//TODO Permissions
					ZvP.getInstance().removeTool(eventPlayer);
					return;
				}
			}
		}
		
		
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(sm.isZVPSign(event.getClickedBlock().getLocation())){
				if(sm.getType(event.getClickedBlock().getLocation())==SignType.INTERACT_SIGN){
					if(!GameManager.getManager().isInGame(eventPlayer)){
						if(eventPlayer.hasPermission("zvp.play")){
							
							InteractSign sign = sm.getInteractSign(event.getClickedBlock().getLocation());
							if(sign.getArena().isOnline()){
								boolean success = GameManager.getManager().createPlayer(eventPlayer, sign.getArena(), sign.getLobby());
								
								if(success){
									eventPlayer.sendMessage("You joined Arena " + sign.getArena().getID());
									return;
								}else{
									event.setCancelled(true);
									eventPlayer.sendMessage("arena full or running"); //TODO message
									return;
								}
							}else{
								event.setCancelled(true);
								eventPlayer.sendMessage("arena offline"); //TODO message
								return;
							}
						}else{
							event.setCancelled(true);
							eventPlayer.sendMessage("No permissions"); //TODO Permission Message
							return;						
						}
					}else{
						event.setCancelled(true);
						eventPlayer.sendMessage("already in game"); //TODO message
						return;
					}
				}
			}		
		}
		
		/*
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
		
			FileConfiguration messageFileConfiguration = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
			
			PlayerInventory inv = eventPlayer.getInventory();
			
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
			
			eisenSchwertPreis = plugin.getConfig().getInt("config.price.buy.ironSword");//i:sword
			steinSchwertPreis = plugin.getConfig().getInt("config.price.buy.stoneSword");//s:sword
			holzSchwertPreis = plugin.getConfig().getInt("config.price.buy.woodenSword");//w:sword
			steinAxtPreis = plugin.getConfig().getInt("config.price.buy.stoneAxe");//s:axe
			bogenPreis = plugin.getConfig().getInt("config.price.buy.bow");//bow
			pfeilPreis64 = plugin.getConfig().getInt("config.price.buy.arrow64");//arrow64
			pfeilPreis32 = plugin.getConfig().getInt("config.price.buy.arrow32");//arrow32
		
			braustandPreis = plugin.getConfig().getInt("config.price.buy.brewingStand");//brewingstand
		
			potionRegenPreis = plugin.getConfig().getInt("config.price.buy.potionRegeneration");//p:regen;
			potionStrenghtPreis = plugin.getConfig().getInt("config.price.buy.potionStrenght");//p:strenght;
			potionHealPreis = plugin.getConfig().getInt("config.price.buy.potionHealing");//p:heal;
			potionSpeedPreis = plugin.getConfig().getInt("config.price.buy.potionSpeed");//p:speed
			
			lederHelmPreis = plugin.getConfig().getInt("config.price.buy.leatherHelmet");//l:helmet
			lederBrustpanzerPreis = plugin.getConfig().getInt("config.price.buy.leatherChestplate");//l:chestplate
			lederBeinschutzPreis = plugin.getConfig().getInt("config.price.buy.leatherLeggings");//l:leggings
			lederStiefelPreis = plugin.getConfig().getInt("config.price.buy.leatherBoots");//l:boots
		
			eisenHelmPreis = plugin.getConfig().getInt("config.price.buy.ironHelmet");//i:helmet
			eisenBrustpanzerPreis = plugin.getConfig().getInt("config.price.buy.ironChestplate");//i:chestplate
			eisenBeinschutzPreis = plugin.getConfig().getInt("config.price.buy.ironLeggings");//i:leggings
			eisenStiefelPreis = plugin.getConfig().getInt("config.price.buy.ironBoots");//i:boots

			BlockState blockState = event.getClickedBlock().getState();
			
			if(blockState instanceof Sign){
				Sign sign = (Sign)blockState;
			
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.GOLD + "[" + ChatColor.AQUA + "Z" + ChatColor.DARK_RED + "v" + ChatColor.AQUA + "P" + ChatColor.GOLD + "]")){
					sign.setLine(0, "[ZvP]");
				}
				if(sign.getLine(0).equalsIgnoreCase("[ZvP]")){
					sign.setLine(0, ChatColor.GOLD + "[" + ChatColor.AQUA + "Z" + ChatColor.DARK_RED + "v" + ChatColor.AQUA + "P" + ChatColor.GOLD + "]");
					sign.update();
					
					//start per sign
					if(sign.getLine(1).equalsIgnoreCase("ZvP start")){
						if(plugin.start==false){
						if(eventPlayer.hasPermission("zomdef.start")){
						if(sign.getLine(3).equalsIgnoreCase("")==false){
							if(sign.getLine(3).matches("\\d*")){
								
								int rundenZahl = Integer.parseInt(sign.getLine(3));
								
								if(rundenZahl>0){
									if(plugin.changeToSpectator==true){
										if(plugin.getConfig().get("config.mem.spectatorZvPLocation")!=null){
											sign.setLine(2, "Rounds:");
											sign.update();
											plugin.zomStart(eventPlayer,rundenZahl);	
											plugin.zomjoin(eventPlayer);	
											return;
										}else{
											eventPlayer.sendMessage(ChatColor.RED + specNotSetArray[0] + "\n" + specNotSetArray[1]);											
										}
									}else{
										sign.setLine(2, "Rounds:");
										sign.update();
								
										plugin.zomStart(eventPlayer, Integer.parseInt(sign.getLine(3)));
										plugin.zomjoin(eventPlayer);
										return;
									}
								}else{
									//message: invalid sign
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_invalid_sign"));
									return;
									}
								
							return;
							}else{
								//message: invalid sign
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_invalid_sign"));								
								return;
							}
						}else{
							//message: invalid sign
							eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_invalid_sign"));
							return;
						}
					}else{
						// permission denieded
						eventPlayer.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
						return;
					}
						}else{
						//Es existiert bereits ein spiel
						eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_event_already_started"));
						return;
					}
				}
					//Join per sign
					if(sign.getLine(2).equalsIgnoreCase("zvp join")){
						if(plugin.start==true){
							if(plugin.voteZeit == true){			
									if(eventPlayer.hasPermission("zomdef.join")){
										plugin.zomjoin(eventPlayer);
										return;
									}else{
										// permission denieded
										eventPlayer.sendMessage(ChatColor.DARK_RED + messageFileConfiguration.getString("config.error_messages.error_missing_permissions"));
										return;
									}
								
							}else{
								//Anmeldezeit vorbei
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_login_time_over"));
								return;
							}
						}else{
							//Kein spiel aktiv
							eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_event_started"));
							return;
						}
					}
					
					if(plugin.start ==true){
					if(plugin.playerVote.contains(eventPlayer)){
						
						if(sign.getLine(3).equalsIgnoreCase("i:sword")){//Eisenschwert									
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironSword")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<eisenSchwertPreis){
										//zu wenig geld
										eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.IRON_SWORD));
										plugin.Konto -= eisenSchwertPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("s:sword")){//Steinschwert
										
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.stoneSword")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{										
									if(plugin.Konto<steinSchwertPreis){
										//zu wenig geld
										eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.STONE_SWORD));
										plugin.Konto -= steinSchwertPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("w:sword")){//Holzschwert
											
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.woodenSword")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{									
									if(plugin.Konto<holzSchwertPreis){
										//zu wenig geld
										eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.WOOD_SWORD));
										plugin.Konto -= holzSchwertPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("s:axe")){//Steinaxt	
											
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.stoneAxe")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<steinAxtPreis){
										//zu wenig geld
										eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.STONE_AXE));
										plugin.Konto -= steinAxtPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("bow")){//Bogen	
											
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.bow")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<bogenPreis){
										//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.BOW));
										plugin.Konto -= bogenPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("arrow64")){//64 Pfeile
											
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.arrow64")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<pfeilPreis64){
										//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.ARROW, 64));
										plugin.Konto -= pfeilPreis64;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("arrow32")){//32 Pfeile
											
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.arrow32")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<pfeilPreis32){
										//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.ARROW, 32));
										plugin.Konto -= pfeilPreis32;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("brewingstand")){//Braustand
												
								int slot = 0;
								slot = inv.firstEmpty();
								
								sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.brewingStand")) + ChatColor.DARK_GREEN + "$");
								sign.update();
								
								if(slot<0){
									//Kein platz im inventar
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
									event.setCancelled(true);
								}else{							
									if(plugin.Konto<braustandPreis){
										//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
										event.setCancelled(true);
									}else{							
										inv.setItem(slot,new ItemStack(Material.BREWING_STAND_ITEM));
										plugin.Konto -= braustandPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}
								}							
						}
						
						if(sign.getLine(3).equalsIgnoreCase("p:speed")){//Potion geschwindigkeit
							
							int slot = 0;
							slot = inv.firstEmpty();
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionSpeed")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
							if(slot<0){
								//Kein platz im inventar
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
								event.setCancelled(true);
							}else{							
								if(plugin.Konto<potionSpeedPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{					
									ItemStack potion = new ItemStack(Material.POTION);
									potion.setDurability((short) 2);//geschwindigkeit
									inv.setItem(slot, potion);
									plugin.Konto -= potionSpeedPreis;
									//kontostand
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
									//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
								}
							}							
						}
						if(sign.getLine(3).equalsIgnoreCase("p:strenght")){//Potion stärke
							
							int slot = 0;
							slot = inv.firstEmpty();
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionStrenght")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
							if(slot<0){
								//Kein platz im inventar
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
								event.setCancelled(true);
							}else{							
								if(plugin.Konto<potionStrenghtPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{					
									ItemStack potion = new ItemStack(Material.POTION);
									potion.setDurability((short) 9);//stärke
									inv.setItem(slot, potion);
									plugin.Konto -= potionStrenghtPreis;
									//kontostand
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
								}
							}							
						}
						if(sign.getLine(3).equalsIgnoreCase("p:heal")){//Potion gesundheit
							
							int slot = 0;
							slot = inv.firstEmpty();
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionHealing")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
							if(slot<0){
								//Kein platz im inventar
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
								event.setCancelled(true);
							}else{							
								if(plugin.Konto<potionHealPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{					
									ItemStack potion = new ItemStack(Material.POTION);
									potion.setDurability((short) 37);//gesundheit
									inv.setItem(slot, potion);
									plugin.Konto -= potionHealPreis;
									//kontostand
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
								}
							}							
						}
						if(sign.getLine(3).equalsIgnoreCase("p:regen")){//Potion regen
							
							int slot = 0;
							slot = inv.firstEmpty();
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.potionRegeneration")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
							if(slot<0){
								//Kein platz im inventar
								eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
								event.setCancelled(true);
							}else{							
								if(plugin.Konto<potionRegenPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{					
									ItemStack potion = new ItemStack(Material.POTION);
									potion.setDurability((short) 1);//regen
									inv.setItem(slot, potion);
									plugin.Konto -= potionRegenPreis;
									//kontostand
									plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
								}
							}							
						}
						
						
						if(sign.getLine(3).equalsIgnoreCase("l:helmet")){//Leder Helm
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherHelmet")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<lederHelmPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getHelmet()==null){
										inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
										plugin.Konto -= lederHelmPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.LEATHER_HELMET));
											plugin.Konto -= lederHelmPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
											//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
										}									
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("l:chestplate")){//Leder Brustpanzer
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherChestplate")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<lederBrustpanzerPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getChestplate()==null){
										inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
										plugin.Konto -= lederBrustpanzerPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.LEATHER_CHESTPLATE));
											plugin.Konto -= lederBrustpanzerPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
											//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
										}									
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("l:leggings")){//Leder Beinschutz
								
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherLeggings")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<lederBeinschutzPreis){
									//zu wenig geld
										eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getLeggings()==null){
										inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
										plugin.Konto -= lederBeinschutzPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.LEATHER_LEGGINGS));
											plugin.Konto -= lederBeinschutzPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
											//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
										}									
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("l:boots")){//Leder Stiefel
								
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.leatherBoots")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<lederStiefelPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getBoots()==null){
										inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
										plugin.Konto -= lederStiefelPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.LEATHER_BOOTS));
											plugin.Konto -= lederStiefelPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
											//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
										}									
									}
								}
							
						}
						
						//EisenRüstung
						if(sign.getLine(3).equalsIgnoreCase("i:helmet")){//Eisen Helm
							
							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironHelmet")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<eisenHelmPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getHelmet()==null){
										inv.setHelmet(new ItemStack(Material.IRON_HELMET));
										plugin.Konto -= eisenHelmPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.IRON_HELMET));
											plugin.Konto -= eisenHelmPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
											//plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " +(int) plugin.Konto);
										}									
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("i:chestplate")){//Eisen Brustpanzer

							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironChestplate")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<eisenBrustpanzerPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getChestplate()==null){
										inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
										plugin.Konto -= eisenBrustpanzerPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.IRON_CHESTPLATE));
											plugin.Konto -= eisenBrustpanzerPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										}									
									}
								}							
						}
						if(sign.getLine(3).equalsIgnoreCase("i:leggings")){//Eisen Beinschutz

							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironLeggings")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
									if(plugin.Konto<eisenBeinschutzPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getLeggings()==null){
										inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
										plugin.Konto -= eisenBeinschutzPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.IRON_LEGGINGS));
											plugin.Konto -= eisenBeinschutzPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										}									
									}
								}
							
						}
						if(sign.getLine(3).equalsIgnoreCase("i:boots")){//Eisen Stiefel

							sign.setLine(2, "Price: " + ChatColor.DARK_PURPLE + Integer.toString(plugin.getConfig().getInt("config.price.buy.ironBoots")) + ChatColor.DARK_GREEN + "$");
							sign.update();
							
								if(plugin.Konto<eisenStiefelPreis){
									//zu wenig geld
									eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_empty_bank_account"));
									event.setCancelled(true);
								}else{	
									if(inv.getBoots()==null){
										inv.setBoots(new ItemStack(Material.IRON_BOOTS));
										plugin.Konto -= eisenStiefelPreis;
										//kontostand
										plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
									
									}else{
										int slot = 0;
										slot = inv.firstEmpty();
										
										if(slot<0){
											//Kein platz im inventar
											eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_full_inventory"));
											event.setCancelled(true);
										}else{
											inv.setItem(slot,new ItemStack(Material.IRON_BOOTS));
											plugin.Konto -= eisenStiefelPreis;
											//kontostand
											plugin.sendMessageJoinedPlayers(ChatColor.DARK_GREEN + messageFileConfiguration.getString("config.messages.bank_balance_message")+ " " + ChatColor.DARK_PURPLE + (int) plugin.Konto + ChatColor.DARK_GREEN + "$", eventPlayer);
										}									
									}
								}
						}
						eventPlayer.updateInventory();
					}else{
						//nicht im Spiel
						eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_not_loged_in"));	
					}
				}else{
					//kein Event gestartet
					eventPlayer.sendMessage(ChatColor.RED + messageFileConfiguration.getString("config.error_messages.error_no_event_started"));
				}
			}			
			}	
		}*/
	}		
}