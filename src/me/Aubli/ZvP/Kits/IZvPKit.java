package me.Aubli.ZvP.Kits;

import org.bukkit.inventory.ItemStack;


public interface IZvPKit {
    
    void delete();
    
    public boolean isEnabled();
    
    public String getName();
    
    public ItemStack getIcon();
    
    public ItemStack[] getContents();
    
}
