package me.Aubli.ZvP.Game.Mode;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.ZvPPlayer;


public class DevMode extends ZvPMode {
    
    protected DevMode(Arena arena) {
	super(arena, "DEV");
    }
    
    @Override
    public ModeType getType() {
	return ModeType.DEV;
    }
    
    @Override
    public ZvPPlayer[] getLivingPlayers() {
	return getArena().getPlayers();
    }
    
    @Override
    public ZvPMode reInitialize() {
	return new DevMode(getArena());
    }
    
    @Override
    public boolean allowPlayerInteraction(ZvPPlayer player) {
	return true;
    }
    
    @Override
    public void run() {
	// TODO Auto-generated method stub
	
    }
    
}
