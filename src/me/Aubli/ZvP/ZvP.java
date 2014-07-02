package me.Aubli.ZvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Listeners.BlockListener;
import me.Aubli.ZvP.Listeners.DeathListener;
import me.Aubli.ZvP.Listeners.GUIListener;
import me.Aubli.ZvP.Listeners.PlayerInteractListener;
import me.Aubli.ZvP.Listeners.PlayerQuitListener;
import me.Aubli.ZvP.Listeners.PlayerRespawnListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.util.Metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ZvP extends JavaPlugin{

	public static final Logger log = Bukkit.getLogger();
	private static ZvP instance;
		
	public static ItemStack tool;
	
	private static String pluginPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_RED + "Z" + ChatColor.DARK_GRAY + "v" + ChatColor.DARK_RED + "P" + ChatColor.DARK_GREEN + "]"  + ChatColor.RESET + " ";
	
	private static Locale locale;
	
	private static int maxPlayers;
	private static int DEFAULT_ROUNDS;
	private static int DEFAULT_WAVES;
	private static int START_DELAY;
	private static int TIME_BETWEEN_WAVES;
	private static int ZOMBIE_SPAWN_RATE;
	private static double SAVE_RADIUS;	
	
	
	private boolean useMetrics = false;

	@Override	
	public void onDisable() {
		
		for(Player p: Bukkit.getOnlinePlayers()){
			removeTool(p);
		}		
		GameManager.getManager().shutdown();
		
		log.info("[ZombieVsPlayer] Plugin is disabled!");
	}
	
	@Override
	public void onEnable() {
		
		initialize();		
		
		log.info("[ZombieVsPlayer] Plugin is enabled!");
	}
	
	
	private void initialize(){
		instance = this;
		loadConfig();		
		setTool();
		
		new MessageManager(locale);
		new GameManager();
		new KitManager();
		new SignManager();	
		
		registerListeners();
		getCommand("zvp").setExecutor(new ZvPCommands());
		getCommand("test").setExecutor(new ZvPCommands());
		
		if(useMetrics==true){
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();			   
			} catch (IOException e) {
			   log.info("[ZombieVsPlayer] Can't start Metrics! Skip!");
			}
		}
	}	
	
	private void registerListeners(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new BlockListener(), this);	
		pm.registerEvents(new DeathListener(), this);
		pm.registerEvents(new PlayerInteractListener(), this);
		pm.registerEvents(new PlayerQuitListener(), this);
		pm.registerEvents(new PlayerRespawnListener(), this);
		pm.registerEvents(new SignChangelistener(), this);		
		pm.registerEvents(new GUIListener(), this);
		
	}
	
	private void setTool(){
		tool = new ItemStack(Material.STICK);

		List<String> lore = new ArrayList<String>();
		
		ItemMeta toolMeta = tool.getItemMeta();
		toolMeta.setDisplayName(pluginPrefix + ChatColor.BOLD + "Tool");
		toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
		lore.add("Use this tool to add an Arena!");
		toolMeta.setLore(lore);
		
		tool.setItemMeta(toolMeta);
	}
	
	public boolean removeTool(Player player){		
		if(player.getInventory().contains(tool)){
			player.getInventory().removeItem(tool);
			return true;
		}
		return false;		
	}
	
	
	public static ZvP getInstance(){
		return instance;
	}
	
	public static String getPrefix(){
		return pluginPrefix;
	}
	
	public static int getMaxPlayers(){
		return maxPlayers;
	}
	
	public static int getDefaultRounds(){
		return DEFAULT_ROUNDS;
	}
	
	public static int getDefaultWaves(){
		return DEFAULT_WAVES;
	}
	
	public static int getStartDelay(){
		return START_DELAY;
	}
	
	public static int getSpawnRate() {
		return ZOMBIE_SPAWN_RATE;
	}
	
 	public static int getSaveTime() {
 		return TIME_BETWEEN_WAVES;
 	}
 	
 	public static double getDefaultDistance() {		
		return SAVE_RADIUS;
	}
	
 	//INTRESTING
	/* 
	 * 
	 * team.setAllowFriendlyFire(false);
	 * team.setCanSeeFriendlyInvisibles(true);
	 * team.setPrefix(ChatColor.DARK_RED + "");	
	 * 
	 * 
	 * playerSender.removePotionEffect(PotionEffectType.HEAL);
	 * playerSender.removePotionEffect(PotionEffectType.SPEED);
	 * playerSender.removePotionEffect(PotionEffectType.REGENERATION);
	 * playerSender.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
	 * 
	 * playerSender.setHealth(20);
	 * playerSender.setFoodLevel(20);
	 * 
	 * board.clearSlot(DisplaySlot.SIDEBAR);
	 * if(board.getTeam("zvpteam").hasPlayer(playerSender)){
	 * 		board.getTeam("zvpteam").removePlayer(playerSender);
	 * }
	 */
	
	private void loadConfig(){
		
		this.getConfig().options().header("\n" +
				"This is the main config file for PlayerVsZombies.\n" +
				"For more items write a comemnt or a ticket on the bukkit-dev website:\n" +
				"http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
		
		this.getConfig().addDefault("config.enableMetrics", true);
		this.getConfig().addDefault("config.maximal_Players", 20);
		
		this.getConfig().addDefault("config.rounds", 3);
		this.getConfig().addDefault("config.waves", 5);
		
		this.getConfig().addDefault("config.joinTime", 15);
		this.getConfig().addDefault("config.saveTime", 30);
		
		this.getConfig().addDefault("config.spawnRate", 30);
		this.getConfig().addDefault("config.saveRadius", 3.0);
		
		this.getConfig().addDefault("config.Locale", "en");
		
		this.getConfig().addDefault("config.misc.ZombieCash", 0.2);
		this.getConfig().addDefault("config.misc.PlayerCash", 2.5);
		this.getConfig().addDefault("config.misc.language", "en");
		this.getConfig().addDefault("config.misc.portOnJoinGame", true);
		this.getConfig().addDefault("config.misc.storeInventory", true);
		this.getConfig().addDefault("config.misc.storeEXPLevel", true);
		this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
		
		
		this.getConfig().addDefault("config.starterkit.enable", true);
		this.getConfig().addDefault("config.starterkit.whichkit", "standardKit");
		this.getConfig().addDefault("config.starterkit.standardKit", "1xSTONE_SWORD, 2xGOLDEN_APPLE, 1xLEATHER_HELMET, 1xLEATHER_LEGGINGS, 1xLEATHER_BOOTS");
		this.getConfig().addDefault("config.starterkit.armorKit", "1xWOOD_SWORD, 1xIRON_CHESTPLATE, 1xIRON_LEGGINGS, 1xIRON_BOOTS, 2xGOLDEN_APPLE");
		this.getConfig().addDefault("config.starterkit.bowKit", "1xBOW, 64xARROW, 64xARROW, 1xLEATHER_HELMET, 1xLEATHER_CHESTPLATE, 1xLEATHER_LEGGINGS, 1xLEATHER_BOOTS, 2xGOLDEN_APPLE");		
		
		this.getConfig().addDefault("config.price.sell.potato", 8);
		this.getConfig().addDefault("config.price.sell.carrot", 7);
		this.getConfig().addDefault("config.price.sell.ironingot", 4);
		this.getConfig().addDefault("config.price.sell.arrow64", 5); 
		this.getConfig().addDefault("config.price.sell.arrow32", 2);
		this.getConfig().addDefault("config.price.sell.rottenflesh64", 1);
		
		this.getConfig().addDefault("config.price.buy.ironSword", 13);
		this.getConfig().addDefault("config.price.buy.stoneSword", 8);
		this.getConfig().addDefault("config.price.buy.woodenSword", 5);
		this.getConfig().addDefault("config.price.buy.stoneAxe", 7); 
		this.getConfig().addDefault("config.price.buy.bow", 12);
		this.getConfig().addDefault("config.price.buy.arrow64", 10);
		this.getConfig().addDefault("config.price.buy.arrow32", 5);
		
		this.getConfig().addDefault("config.price.buy.brewingStand", 20);
		
		this.getConfig().addDefault("config.price.buy.potionStrenght", 15);
		this.getConfig().addDefault("config.price.buy.potionRegeneration", 15);
		this.getConfig().addDefault("config.price.buy.potionHealing", 15);
		this.getConfig().addDefault("config.price.buy.potionSpeed", 15);
		
		this.getConfig().addDefault("config.price.buy.leatherHelmet", 4);
		this.getConfig().addDefault("config.price.buy.leatherChestplate", 8);
		this.getConfig().addDefault("config.price.buy.leatherLeggings", 7);
		this.getConfig().addDefault("config.price.buy.leatherBoots", 4);
		this.getConfig().addDefault("config.price.buy.ironHelmet", 6);
		this.getConfig().addDefault("config.price.buy.ironChestplate", 11);
		this.getConfig().addDefault("config.price.buy.ironLeggings", 9);
		this.getConfig().addDefault("config.price.buy.ironBoots", 6);
		
		maxPlayers = getConfig().getInt("config.maximal_Players");
		useMetrics = getConfig().getBoolean("config.enableMetrics");
		
		DEFAULT_ROUNDS = getConfig().getInt("config.rounds");
		DEFAULT_WAVES = getConfig().getInt("config.waves");
		
		START_DELAY = getConfig().getInt("config.joinTime");
		TIME_BETWEEN_WAVES = getConfig().getInt("config.saveTime");
		
		ZOMBIE_SPAWN_RATE = getConfig().getInt("config.spawnRate");		
		
		SAVE_RADIUS = getConfig().getDouble("config.saveRadius");
		
		locale = new Locale(getConfig().getString("config.Locale"));
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
		
}