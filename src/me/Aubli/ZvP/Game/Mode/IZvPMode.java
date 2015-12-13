package me.Aubli.ZvP.Game.Mode;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.ZvPPlayer;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public interface IZvPMode {
    
    public String getName();
    
    public int getTaskID();
    
    /**
     * Start the game task
     */
    public void start(int startDelay);
    
    public void stop();
    
    /**
     * Called when {@link ZvPPlayer} joins {@link Arena}
     * 
     * @param player
     *        player who joined
     * @param arena
     *        arena which is joined
     */
    public void onJoin(ZvPPlayer player, Arena arena);
    
    /**
     * Called when {@link ZvPPlayer} leaves the game by command or disconnect
     * 
     * @param player
     *        player who left
     */
    public void onLeave(ZvPPlayer player);
    
    /**
     * Called when {@link ZvPPlayer} dies. //TODO deathcause enum?
     * 
     * @param player
     *        player who died
     */
    public void onDeath(ZvPPlayer player, PlayerDeathEvent event);
    
    /**
     * Calles when {@link ZvPPlayer} respawns.
     * 
     * @param player
     *        player who respawned
     */
    public void onRespawn(ZvPPlayer player, PlayerRespawnEvent event);
    
    /**
     * Called when a Zombie is killed by a player
     * 
     * @param attacker
     *        the {@link ZvPPlayer} who killed the zombie
     * @param zombie
     *        the zombie {@link Entity} who is killed
     */
    public void onZombieKill(ZvPPlayer attacker, Entity zombie, EntityDeathEvent event);
    
    /**
     * Called when a Player is killed by another player
     * 
     * @param attacker
     *        the {@link ZvPPlayer} who killed the player
     * @param victim
     *        the {@link ZvPPlayer} who is killed by the attacker
     */
    public void onPlayerKill(ZvPPlayer attacker, ZvPPlayer victim);
    
    // public void onZombieInteraction(ZvPPlayer player, Entity zombie);
    
    /**
     * Called when {@link ZvPPlayer} got damaged by an entity
     * 
     * @param player
     *        the player who is damaged
     * @param damager
     *        the entity who damaged the player
     */
    public void onPlayerDamage(ZvPPlayer player, Entity damager, EntityDamageByEntityEvent event);
    
    /**
     * Called when zombie got damaged by an entity
     * 
     * @param damager
     *        the entity causing the damage
     * @param victim
     *        the entity who is damaged
     */
    public void onZombieDamage(ZvPPlayer damager, Entity victim, EntityDamageByEntityEvent event);
    
}
