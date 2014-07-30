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
import me.Aubli.ZvP.Listeners.EntityDamageListener;
import me.Aubli.ZvP.Listeners.GUIListener;
import me.Aubli.ZvP.Listeners.PlayerInteractListener;
import me.Aubli.ZvP.Listeners.PlayerQuitListener;
import me.Aubli.ZvP.Listeners.PlayerRespawnListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Shop.ShopManager;
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
	
	private Locale locale;
	
	private static int maxPlayers;
	private static int DEFAULT_ROUNDS;
	private static int DEFAULT_WAVES;
	private static int START_DELAY;
	private static int TIME_BETWEEN_WAVES;
	private static int ZOMBIE_SPAWN_RATE;
	private static double SAVE_RADIUS;	
	private static double ZOMBIE_FUND;
	private static double DEATH_FEE;
	
	private boolean useMetrics = false;

	@Override	
	public void onDisable() {
		
		for(Player p : Bukkit.getOnlinePlayers()){
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
		new ShopManager();
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
		pm.registerEvents(new EntityDamageListener(), this);
		
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
 	
 	public static double getZombieFund() {
 		return ZOMBIE_FUND;
 	}
 	
	public static double getDeathFee() {
 		return DEATH_FEE;
 	}
 	
	
 	//INTRESTING
	/* 
	 * 
	 * team.setAllowFriendlyFire(false);
	 * team.setCanSeeFriendlyInvisibles(true);
	 * team.setPrefix(ChatColor.DARK_RED + "");	
	 * 
	 * board.clearSlot(DisplaySlot.SIDEBAR);
	 * if(board.getTeam("zvpteam").hasPlayer(playerSender)){
	 * 		board.getTeam("zvpteam").removePlayer(playerSender);
	 * }
	 */
	
	private void loadConfig(){
		
		this.getConfig().options().header("\n" +
				"This is the main config file for PlayerVsZombies.\n" +
				"For more items write a coment or a ticket on the bukkit-dev website:\n" +
				"http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
		
		this.getConfig().addDefault("plugin.enableMetrics", true);
		this.getConfig().addDefault("plugin.Locale", "en");
		
		useMetrics = getConfig().getBoolean("plugin.enableMetrics");
		locale = new Locale(getConfig().getString("plugin.Locale"));
		
		
		this.getConfig().addDefault("game.maximal_Players", 25);		
		this.getConfig().addDefault("game.default_rounds", 3);
		this.getConfig().addDefault("game.default_waves", 5);
		
		maxPlayers = getConfig().getInt("game.maximal_Players");
		DEFAULT_ROUNDS = getConfig().getInt("game.default_rounds");
		DEFAULT_WAVES = getConfig().getInt("game.default_waves");
		
		
		this.getConfig().addDefault("times.joinTime", 15);
		this.getConfig().addDefault("times.saveTime", 30);
		
		START_DELAY = getConfig().getInt("times.joinTime");
		TIME_BETWEEN_WAVES = getConfig().getInt("times.saveTime");
		
		
		this.getConfig().addDefault("zombies.spawnRate", 30);
		this.getConfig().addDefault("zombies.saveRadius", 3.0);	
		
		ZOMBIE_SPAWN_RATE = getConfig().getInt("zombies.spawnRate");		
		SAVE_RADIUS = getConfig().getDouble("zombies.saveRadius");
		
		
		this.getConfig().addDefault("money.ZombieFund", 0.37);
		this.getConfig().addDefault("money.DeathFee", 3);	
		
		ZOMBIE_FUND = getConfig().getDouble("money.ZombieFund");
		DEATH_FEE = getConfig().getDouble("money.DeathFee");
		

	//	this.getConfig().addDefault("config.misc.portOnJoinGame", true);
	//	this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
		
}