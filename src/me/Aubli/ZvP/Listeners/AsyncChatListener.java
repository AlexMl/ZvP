package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;

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
			
			player.getArena().sendMessage(ChatColor.GOLD + "[" + player.getName() + "] " + ChatColor.RESET + event.getMessage());
			return;
		}
		
	}
	
}
