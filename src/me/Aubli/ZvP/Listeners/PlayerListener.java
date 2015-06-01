package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class PlayerListener implements Listener {
    
    private GameManager game = GameManager.getManager();
    private Player eventPlayer;
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	this.eventPlayer = event.getPlayer();
	
	if (this.eventPlayer.hasPermission("zvp.update") && ZvP.updateAvailable) {
	    this.eventPlayer.sendMessage(ZvP.getPrefix() + ZvP.newVersion + " was released. Type " + ChatColor.GOLD + "'/zvp update'" + ChatColor.RESET + " to update automatically.");
	    return;
	}
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	this.eventPlayer = event.getPlayer();
	
	if (GameManager.getManager().isInGame(this.eventPlayer)) {
	    GameManager.getManager().removePlayer(GameManager.getManager().getPlayer(this.eventPlayer));
	    return;
	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	this.eventPlayer = event.getEntity();
	
	if (this.game.isInGame(this.eventPlayer)) {
	    ZvPPlayer player = this.game.getPlayer(this.eventPlayer);
	    
	    if (player.getArena().keepExp()) {
		player.getXPManager().setExp(0);
	    }
	    
	    player.die();
	    
	    event.setDeathMessage("");
	    player.getArena().sendMessage(MessageManager.getFormatedMessage("game:player_died", player.getName()));
	    return;
	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    // INFO: Highest Priority, otherwise overridden by essentials
    public void onPlayerRespawn(PlayerRespawnEvent event) {
	
	this.eventPlayer = event.getPlayer();
	
	if (this.game.isInGame(this.eventPlayer)) {
	    final ZvPPlayer player = this.game.getPlayer(this.eventPlayer);
	    
	    event.setRespawnLocation(player.getArena().getArea().getNewRandomLocation(true));
	    
	    if (player.getArena().getSpawnProtection()) {
		player.setSpawnProtected(true);
		player.sendMessage(MessageManager.getFormatedMessage("game:spawn_protection_enabled", player.getArena().getArenaProtectionDuration()));
		
		Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
		    
		    @Override
		    public void run() {
			player.setSpawnProtected(false);
			player.sendMessage(MessageManager.getMessage("game:spawn_protection_over"));
		    }
		}, player.getArena().getArenaProtectionDuration() * 20L);
	    }
	    return;
	}
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	this.eventPlayer = event.getPlayer();
	
	if (this.game.getPlayer(this.eventPlayer) != null) {
	    ZvPPlayer zPlayer = this.game.getPlayer(this.eventPlayer);
	    if (zPlayer.getArena().hasPreLobby()) {
		if (zPlayer.getArena().isWaiting()) {
		    
		    if (event.getTo().distanceSquared(event.getFrom()) > 0.0) {
			this.eventPlayer.teleport(event.getFrom(), TeleportCause.PLUGIN);
			return;
		    }
		}
	    }
	}
	
    }
}
