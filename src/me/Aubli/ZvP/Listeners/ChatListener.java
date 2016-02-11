package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.Arena.ArenaStatus;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageKeys.commands;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class ChatListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOW)
    public void onChatEvent(AsyncPlayerChatEvent event) {
	
	Player chatPlayer = event.getPlayer();
	
	if (GameManager.getManager().getPlayer(chatPlayer) != null && GameManager.getManager().isInGame(chatPlayer)) {
	    final ZvPPlayer player = GameManager.getManager().getPlayer(chatPlayer);
	    final Arena arena = player.getArena();
	    if (event.getMessage().equalsIgnoreCase("zvp vote")) {
		event.setCancelled(true);
		if (arena.getConfig().isVoteSystem()) {
		    if (arena.getStatus() == ArenaStatus.VOTING) {
			if (!player.hasVoted()) {
			    player.setVoted(true);
			    player.sendMessage(MessageManager.getMessage(game.voted_next_wave));
			    
			    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " voted in arena " + player.getArena().getID(), true);
			    
			    // Make sure that in asynchronous cases the non-thread
			    // safe methods are called synchronously
			    new BukkitRunnable() {
				
				@Override
				public void run() {
				    arena.updatePlayerBoards();
				    
				    if (arena.hasVoted()) {
					for (ZvPPlayer p : arena.getPlayers()) {
					    p.setVoted(false);
					}
					arena.reStart(0);
					arena.setStatus(ArenaStatus.RUNNING);
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 20L, 4 * 20L);
			    
			} else {
			    player.sendMessage(MessageManager.getMessage(game.already_voted));
			}
		    } else {
			player.sendMessage(MessageManager.getMessage(game.no_voting));
			
		    }
		} else {
		    player.sendMessage(MessageManager.getMessage(game.feature_disabled));
		}
	    } else {
		if (ZvPConfig.getModifyChat()) {
		    event.setCancelled(true);
		    String message = ZvP.getPrefix() + ChatColor.BLACK + "[" + ChatColor.GOLD + player.getName() + ChatColor.BLACK + "] " + ChatColor.RESET + event.getMessage();
		    player.getArena().sendMessage(message);
		    
		    if (player.getArena().hasPreLobby()) {
			player.getArena().getPreLobby().sendMessage(message);
		    }
		}
	    }
	    return;
	}
	
    }
    
    @EventHandler
    public void onCommandPreProcessing(PlayerCommandPreprocessEvent event) {
	
	Player eventPlayer = event.getPlayer();
	String command = event.getMessage().substring(1, event.getMessage().length()).toLowerCase();
	
	if (ZvPConfig.getModifyChat()) {
	    if (GameManager.getManager().isInGame(eventPlayer)) {
		if (!eventPlayer.hasPermission("zvp.command")) {
		    if (!command.startsWith("zvp")) {
			if (!ZvPConfig.getCommandWhitelist().contains(command)) {
			    event.setCancelled(true);
			    eventPlayer.sendMessage(MessageManager.getMessage(commands.no_commands_allowed));
			    return;
			} else {
			    ZvP.getPluginLogger().log(getClass(), Level.INFO, "Player " + eventPlayer.getName() + "(" + eventPlayer.getUniqueId().toString() + ") tried to execute " + event.getMessage() + "!", true, false);
			    return;
			}
		    }
		}
	    }
	}
    }
}
