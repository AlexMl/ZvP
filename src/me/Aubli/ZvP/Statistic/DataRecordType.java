package me.Aubli.ZvP.Statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.Aubli.ZvP.Translation.MessageKeys.dataType;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.util.SortMap.SortMap;


public enum DataRecordType {
    
    NULL("NULL", new ItemStack(Material.ITEM_FRAME)),
    KILLS(MessageManager.getMessage(dataType.kills), new ItemStack(Material.IRON_SWORD)),
    KILLRECORD(MessageManager.getMessage(dataType.kill_record), new ItemStack(Material.DIAMOND_SWORD)),
    DEATHS(MessageManager.getMessage(dataType.deaths), new ItemStack(Material.SKULL_ITEM)),
    LEFTMONEY(MessageManager.getMessage(dataType.left_money), new ItemStack(Material.GOLD_INGOT)), ;
    
    private String displayName;
    private ItemStack icon;
    
    private DataRecordType(String name, ItemStack icon) {
	this.displayName = name;
	this.icon = icon;
    }
    
    public String getDisplayName() {
	return this.displayName;
    }
    
    public ItemStack getIcon() {
	return this.icon;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> Map<UUID, T> getValueMap() {
	DataRecord[] records = DatabaseManager.getManager().getDataRecords();
	Map<UUID, T> recordMap = new HashMap<UUID, T>();
	
	for (DataRecord record : records) {
	    recordMap.put(record.getPlayerUUID(), (T) record.getValue(this));
	}
	
	recordMap = SortMap.sortByValue(recordMap);
	
	// System.out.println(type);
	// System.out.println(recordMap);
	return recordMap;
    }
    
    public static DataRecordType fromIcon(ItemStack icon) {
	for (DataRecordType type : DataRecordType.values()) {
	    if (type.getIcon().getType() == icon.getType()) {
		return type;
	    }
	}
	return null;
    }
}
