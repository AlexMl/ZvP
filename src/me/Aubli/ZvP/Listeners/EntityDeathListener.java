package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;

public class EntityDeathListener implements Listener{

	public EntityDeathListener(ZvP plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		
		if(plugin.start==true){		
			Entity eventEntity = event.getEntity();
			Player killer;
			int kills;
			
			if(eventEntity instanceof Zombie){
				if(((Zombie) eventEntity).getKiller() instanceof Player){
				
					killer = ((Zombie) eventEntity).getKiller();
					if(plugin.playerVote.contains(killer)){
						plugin.Konto = plugin.Konto + plugin.zombieCash;
						plugin.gesammtKill++;
											
						if((plugin.Runde*plugin.Welle*30 - plugin.gesammtKill)>0){
							//Nur noch
//							plugin.sendMessageJoinedPlayers(ChatColor.GREEN + zombiesLeftArray[0] + ChatColor.DARK_PURPLE + (plugin.Runde*plugin.Welle*30 - plugin.gesammtKill) + ChatColor.GREEN + zombiesLeftArray[1], killer);
						}
					
						if(plugin.kills.containsKey(killer)){
							kills = Integer.parseInt(plugin.kills.get(killer));
							kills++;
							plugin.kills.put(killer, (Integer.toString(kills)));
							//so viele zombies gekillt
//							killer.sendMessage(ChatColor.GOLD + zombiesKillesArray[0] + kills + zombiesKillesArray[1]);
						}else{
							kills = 1;
							plugin.kills.put(killer, (Integer.toString(kills)));
							//erster Zombie
//							killer.sendMessage(ChatColor.GOLD + messageFileConfiguration.getString("config.messages.zombie_killed_first_message"));
						}
						
						//Scoreboard neu schreiben
						plugin.board.clearSlot(DisplaySlot.SIDEBAR);
						
						plugin.Obj.setDisplaySlot(DisplaySlot.SIDEBAR);
						plugin.Obj.setDisplayName(ChatColor.RED + "Kills");
						plugin.Obj.getScore(((Zombie) eventEntity).getKiller()).setScore(kills);
						
						if(plugin.playerVote.size()==1){
							((Zombie) eventEntity).getKiller().setScoreboard(plugin.board);
						}else{
							for(int i=0;i<plugin.playerVote.size();i++){
								plugin.playerVote.get(i).setScoreboard(plugin.board);
							}
						}
						
					}
				}
			}
		}
	}
	private ZvP plugin;
}
