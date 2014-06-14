package me.Aubli.ZvP.Sign;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;

public class SignManager {
	
	public enum SignType{
		INFO_SIGN,
		INTERACT_SIGN,
		SHOP_SIGN,
		;
	}	
	
	private static SignManager instance;
	
	private File interact;
	private File info;
	private File shop;
	
	private ArrayList<ISign> signs;

	public SignManager(){
		instance = this;
		
		signs = new ArrayList<ISign>();
		
		interact = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs/Interact");
		info = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs/Info");		
		shop = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs/Shop");
		reloadConfig();
	}		
	
	public void reloadConfig(){
		if(!interact.exists() || !info.exists() || !shop.exists()){
			info.mkdirs();
			interact.mkdirs();
			shop.mkdirs();
		}
		loadSigns();
	}
	
	private void loadSigns(){		
		
		signs = new ArrayList<ISign>();
		
		for(File f : info.listFiles()){
			InfoSign sign = new InfoSign(f);
			if(sign.getWorld()!=null){				
				signs.add(sign);
			}
		}		
		
		for(File f : interact.listFiles()){
			InteractSign sign = new InteractSign(f);
			if(sign.getWorld()!=null){			
				signs.add(sign);
			}
		}	
		
		for(File f : shop.listFiles()){
			ShopSign sign = new ShopSign(f);
			if(sign.getWorld()!=null){			
				signs.add(sign);
			}
		}
	}
		
	public static SignManager getManager(){
		return instance;
	}	
	
	public SignType getType(Location signLoc){
		
		for(ISign s : signs) {
			if(s.getLocation().equals(signLoc)) {
				return s.getType();
			}
		}
		return null;
	}	
	
	public ISign getSign(int ID) {
		for(ISign s : signs) {
			if(s.getID()==ID) {
				return s;
			}
		}
		return null;
	}
	
	public ISign getSign(Location signLoc) {
		for(ISign s : signs) {
			if(s.getLocation().equals(signLoc)) {
				return s;
			}
		}
		return null;
	}	
	
	public boolean isZVPSign(Location loc){
		if(getSign(loc)!=null){
			return true;
		}
		return false;
	}
	
	public boolean createSign(SignType type, Location signLoc, Arena arena, Lobby lobby){		
		if(signLoc.getBlock().getState() instanceof Sign){
			if(type==SignType.INFO_SIGN){
				try{
					String path = info.getPath();
					ISign s = new InfoSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);					
					signs.add(s);	
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
			if(type==SignType.INTERACT_SIGN){
				try{
					String path = interact.getPath();
					ISign s = new InteractSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
					signs.add(s);	
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
			if(type==SignType.SHOP_SIGN){
				try{
					String path = shop.getPath();
					ISign s = new ShopSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
					signs.add(s);	
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	public boolean removeSign(Location signLoc){		
		if(getSign(signLoc)!=null) {
			getSign(signLoc).delete();
			signs.remove(getSign(signLoc));
			return true;
		}		
		return false;
	}
	
	public boolean removeSign(int signID){	
		if(getSign(signID)!=null) {
			getSign(signID).delete();
			signs.remove(getSign(signID));
			return true;
		}				
		return false;
	}

	
	public void updateSigns(){		
		for(ISign s : signs) {
			s.update();
		}
	}
	
	public void updateSigns(Lobby lobby){		
		for(ISign s : signs) {
			if(s.getLobby().equals(lobby)) {
				s.update();
			}
		}
	}
	
	public void updateSigns(Arena arena){		
		for(ISign s : signs) {
			if(s.getArena().equals(arena)) {
				s.update();
			}
		}
	}

}
