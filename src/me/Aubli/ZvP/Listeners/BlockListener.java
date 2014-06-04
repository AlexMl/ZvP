package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Sign.SignManager;

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
		
		if(eventPlayer.getItemInHand()!=null){
			if(eventPlayer.getItemInHand().equals(ZvP.tool)){
				if(eventPlayer.hasPermission("zvp.tool")){
					event.setCancelled(true);
					return;
				}else{
					//TODO permission message
					ZvP.getInstance().removeTool(eventPlayer);
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if(event.getBlock().getState() instanceof Sign){
			Location signLoc = event.getBlock().getLocation().clone();
			if(sm.isZVPSign(signLoc)){
				if(eventPlayer.hasPermission("zvp.manage.sign")){					
					boolean success = sm.removeSign(sm.getType(signLoc), signLoc);					
					if(success){
						//TODO Message
						eventPlayer.sendMessage("successfully removed");
						return;
					}else{
						//TODO Message
						eventPlayer.sendMessage("error");
						return;
					}					
				}else{
					//TODO Permission Message
					event.setCancelled(true);
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
