package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.GameRunnable;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncChatListener implements Listener {

	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent event) {
		
		Player chatPlayer = event.getPlayer();
		
		if(GameManager.getManager().getPlayer(chatPlayer)!=null && GameManager.getManager().isInGame(chatPlayer)) {
			event.setCancelled(true);
			ZvPPlayer player = GameManager.getManager().getPlayer(chatPlayer);
			
			if(event.getMessage().equalsIgnoreCase("zvp vote")) {
				if(player.getArena().getStatus()==ArenaStatus.VOTING) {
					if(!player.hasVoted()) {
						player.setVoted(true);
						player.sendMessage(MessageManager.getMessage("game:voted_next_round"));
						
						Arena a = player.getArena();
						a.updatePlayerBoards();
						if(a.hasVoted()) {
							for(ZvPPlayer p : a.getPlayers()) {
								p.setVoted(false);
							}					
							a.setTaskID(new GameRunnable(a, ZvP.getStartDelay(), ZvP.getSpawnRate()).runTaskTimer(ZvP.getInstance(), 0L, 20L).getTaskId());
							a.setStatus(ArenaStatus.RUNNING);
						}
					}else {
						player.sendMessage(MessageManager.getMessage("game:already_voted"));
					}
				}else {
					player.sendMessage(MessageManager.getMessage("game:no_voting"));
				}
			}else {
				player.getArena().sendMessage(ChatColor.BLACK + "[" + ChatColor.GOLD + player.getName() + ChatColor.BLACK + "] " + ChatColor.RESET + event.getMessage());
			}
			return;
		}
		
	}
	
}
