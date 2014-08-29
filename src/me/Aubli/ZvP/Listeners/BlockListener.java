package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPCommands;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener{
	
	private Player eventPlayer;
	private GameManager game = GameManager.getManager();
	private SignManager sm = SignManager.getManager();

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		eventPlayer = event.getPlayer();
		
		if(game.isInGame(eventPlayer)){
			event.setCancelled(true);
			return;
		}
		
		if(eventPlayer.getItemInHand()!=null){
			if(eventPlayer.getItemInHand().isSimilar(ZvP.tool)){
				if(eventPlayer.hasPermission("zvp.manage.arena")){
					event.setCancelled(true);
					return;
				}else{
					ZvP.getInstance().removeTool(eventPlayer);
					event.setCancelled(true);
					ZvPCommands.commandDenied(eventPlayer);
					return;
				}
			}
		}
		
		if(event.getBlock().getState() instanceof Sign){
			Location signLoc = event.getBlock().getLocation().clone();
			if(sm.isZVPSign(signLoc)){
				if(eventPlayer.hasPermission("zvp.manage.sign")){					
					boolean success = sm.removeSign(signLoc);					
					if(success){
						eventPlayer.sendMessage(MessageManager.getMessage("manage:sign_removed"));
						return;
					}else{
						eventPlayer.sendMessage(MessageManager.getMessage("error:sign_remove"));
						return;
					}					
				}else{
					event.setCancelled(true);
					ZvPCommands.commandDenied(eventPlayer);
					return;
				}
			}
		}	
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		eventPlayer = event.getPlayer();		
		
		if(game.isInGame(eventPlayer)){
			event.setCancelled(true);
			return;
		}
	}
}
