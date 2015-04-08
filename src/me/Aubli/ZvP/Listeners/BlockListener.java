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


public class BlockListener implements Listener {
    
    private Player eventPlayer;
    private GameManager game = GameManager.getManager();
    private SignManager sm = SignManager.getManager();
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
	
	this.eventPlayer = event.getPlayer();
	
	if (this.game.isInGame(this.eventPlayer)) {
	    event.setCancelled(true);
	    return;
	}
	
	if (this.eventPlayer.getItemInHand() != null) {
	    if (this.eventPlayer.getItemInHand().isSimilar(ZvP.getTool(ZvP.ADDARENA)) || this.eventPlayer.getItemInHand().isSimilar(ZvP.getTool(ZvP.ADDPOSITION))) {
		if (this.eventPlayer.hasPermission("zvp.manage.arena")) {
		    event.setCancelled(true);
		    return;
		} else {
		    ZvP.removeTool(this.eventPlayer);
		    event.setCancelled(true);
		    ZvPCommands.commandDenied(this.eventPlayer);
		    return;
		}
	    }
	}
	
	if (event.getBlock().getState() instanceof Sign) {
	    Location signLoc = event.getBlock().getLocation().clone();
	    if (this.sm.isZVPSign(signLoc)) {
		if (this.eventPlayer.hasPermission("zvp.manage.sign")) {
		    boolean success = this.sm.removeSign(signLoc);
		    if (success) {
			this.eventPlayer.sendMessage(MessageManager.getMessage("manage:sign_removed"));
			return;
		    } else {
			this.eventPlayer.sendMessage(MessageManager.getMessage("error:sign_remove"));
			return;
		    }
		} else {
		    event.setCancelled(true);
		    ZvPCommands.commandDenied(this.eventPlayer);
		    return;
		}
	    }
	}
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
	this.eventPlayer = event.getPlayer();
	
	if (this.game.isInGame(this.eventPlayer)) {
	    event.setCancelled(true);
	    return;
	}
    }
}
