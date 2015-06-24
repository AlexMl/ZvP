package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Game.GameRunnable;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
		if (arena.useVoteSystem()) {
		    if (arena.getStatus() == ArenaStatus.VOTING) {
			if (!player.hasVoted()) {
			    player.setVoted(true);
			    player.sendMessage(MessageManager.getMessage("game:voted_next_round"));
			    
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
					arena.setTaskID(new GameRunnable(arena, 0).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
					arena.setStatus(ArenaStatus.RUNNING);
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 20L, 4 * 20L);
			    
			} else {
			    player.sendMessage(MessageManager.getMessage("game:already_voted"));
			}
		    } else {
			player.sendMessage(MessageManager.getMessage("game:no_voting"));
			
		    }
		} else {
		    player.sendMessage(MessageManager.getMessage("game:voting_disabled"));
		}
	    } else {
		if (ZvPConfig.getModifyChat()) {
		    event.setCancelled(true);
		    player.getArena().sendMessage(ZvP.getPrefix() + ChatColor.BLACK + "[" + ChatColor.GOLD + player.getName() + ChatColor.BLACK + "] " + ChatColor.RESET + event.getMessage());
		}
	    }
	    return;
	}
	
    }
    
}
