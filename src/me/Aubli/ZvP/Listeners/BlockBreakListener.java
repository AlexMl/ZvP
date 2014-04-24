package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Sign.SignManager;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener{

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		Player eventPlayer = event.getPlayer();
		SignManager sm = SignManager.getManager();		
		
		if(eventPlayer.getItemInHand()!=null){
			if(eventPlayer.getItemInHand().equals(ZvP.tool)){
				if(eventPlayer.hasPermission("zvp.tool")){
					event.setCancelled(true);
					return;
				}else{
					//TODO permission message
					ZvP.getInstance().removeTool(eventPlayer);
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
}
