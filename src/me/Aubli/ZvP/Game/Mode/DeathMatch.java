package me.Aubli.ZvP.Game.Mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Listeners.EntityListener;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class DeathMatch extends ZvPMode {

    private int seconds = 0;
    private int spawnGoal;
    private boolean firstSpawn;
    private boolean spawnZombies;

    public static ItemStack speedToolEnable;
    public static ItemStack speedToolDisable;
    public static ItemStack playerCompass;

    private Map<ZvPPlayer, List<ItemStack>> playerDrops = new HashMap<ZvPPlayer, List<ItemStack>>();

    protected DeathMatch(Arena arena) {
	super(arena, "DEATHMATCH");
    }

    @Override
    public ModeType getType() {
	return ModeType.DEATHMATCH;
    }

    @Override
    public ZvPPlayer[] getLivingPlayers() {

	List<ZvPPlayer> livingPlayers = new ArrayList<ZvPPlayer>();

	for (ZvPPlayer p : getArena().getPlayers()) {
	    if (p.getGameMode() == GameMode.SURVIVAL) {
		livingPlayers.add(p);
	    }
	}

	ZvPPlayer[] playerArray = new ZvPPlayer[livingPlayers.size()];
	for (ZvPPlayer p : livingPlayers) {
	    playerArray[livingPlayers.indexOf(p)] = p;
	}
	return playerArray;
    }

    @Override
    public ZvPMode reInitialize() {
	return new DeathMatch(getArena());
    }

    @Override
    public void onJoin(ZvPPlayer player, Arena arena) {
	if (arena.getPlayers().length > arena.getConfig().getMaxPlayers()) {
	    player.getPlayer().setGameMode(GameMode.SPECTATOR);
	    player.sendMessage(MessageManager.getMessage(game.spectator_mode));
	}
    }

    @Override
    public void onDeath(ZvPPlayer player, PlayerDeathEvent event) {
	super.onDeath(player, event);

	this.playerDrops.put(player, new ArrayList<ItemStack>(event.getDrops()));
	event.getDrops().clear();

	Entity entitiy = getArena().getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
	if (entitiy != null) {
	    getArena().getDifficultyTool().customizeEntity(entitiy);
	}

	ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM);
	playerSkull.setDurability((short) 3);
	SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
	meta.setOwner(player.getName());
	playerSkull.setItemMeta(meta);

	Zombie z = (Zombie) entitiy;
	z.setBaby(false);
	z.getEquipment().setHelmet(playerSkull);
	z.setMaxHealth(2 * 20);
	getArena().updatePlayerBoards();
    }

    @Override
    public void onRespawn(ZvPPlayer player, PlayerRespawnEvent event) {
	event.setRespawnLocation(getArena().getArea().getNewRandomLocation(true));
	player.getPlayer().setGameMode(GameMode.SPECTATOR);
	player.getPlayer().getInventory().clear();

	speedToolEnable = new ItemStack(Material.LEATHER_BOOTS);
	ItemMeta meta = speedToolEnable.getItemMeta();
	meta.setDisplayName(ChatColor.GREEN + "Enable Speed");
	meta.setLore(Arrays.asList(MessageManager.getMessage(game.speedTool_description)));
	meta.addItemFlags(ItemFlag.values());
	meta.addEnchant(Enchantment.DURABILITY, 1, true);
	speedToolEnable.setItemMeta(meta);

	speedToolDisable = speedToolEnable.clone();
	meta = speedToolDisable.getItemMeta();
	meta.setDisplayName(ChatColor.RED + "Disable Speed");
	speedToolDisable.setItemMeta(meta);

	playerCompass = new ItemStack(Material.COMPASS);
	meta = playerCompass.getItemMeta();
	meta.setDisplayName("Teleport Tool");
	meta.setLore(Arrays.asList(MessageManager.getMessage(game.teleportTool_description)));
	meta.addItemFlags(ItemFlag.values());
	meta.addEnchant(Enchantment.DURABILITY, 1, true);
	playerCompass.setItemMeta(meta);

	if (getLivingPlayers().length > 0) {

	    for (Zombie z : getArena().getLivingZombies()) {
		z.setTarget(getArena().getRandomPlayer().getPlayer());
	    }

	    player.getPlayer().getInventory().addItem(speedToolEnable, playerCompass);
	    player.sendMessage(MessageManager.getMessage(game.spectator_mode));
	}
    }

    @Override
    public void onZombieKill(ZvPPlayer attacker, Entity zombie, EntityDeathEvent event) {
	super.onZombieKill(attacker, zombie, event);

	Zombie z = (Zombie) zombie;
	if (z.getEquipment().getHelmet().getType() == Material.SKULL_ITEM) {
	    if (z.getEquipment().getHelmet().hasItemMeta() && z.getEquipment().getHelmet().getItemMeta() instanceof SkullMeta) {
		SkullMeta meta = (SkullMeta) z.getEquipment().getHelmet().getItemMeta();

		if (meta.hasOwner()) {
		    ZvPPlayer player = GameManager.getManager().getPlayer(meta.getOwner());

		    if (player != null) {
			if (this.playerDrops.containsKey(player)) {
			    event.getDrops().clear();
			    event.getDrops().addAll(this.playerDrops.get(player));
			    this.playerDrops.remove(player);
			}
		    }
		}
	    }
	}
    }

    @Override
    public boolean allowFullArena() {
	return true;
    }

    @Override
    public boolean allowPlayerInteraction(ZvPPlayer player) {
	return player.getGameMode() != GameMode.SPECTATOR;
    }

    @Override
    public void run() {

	if (ZvP.getPluginLogger().isDebugMode() && (ZvP.getPluginLogger().getLogLevel() <= 100)) {
	    getArena().sendMessage("A:" + getArena().getID() + " ;" + ChatColor.RED + getArena().getStatus().toString() + ChatColor.RESET + "; " + getArena().getCurrentRound() + ":" + getArena().getCurrentWave() + " Z:" + getArena().getLivingZombieAmount() + ":" + getArena().getSpawningZombies() + " FS:" + this.firstSpawn + " SZ:" + this.spawnZombies + " T:" + this.seconds);
	}

	if (this.seconds < this.startDelay) {	// Waiting for players
	    if (getArena().getCurrentRound() == 0 && getArena().getCurrentWave() == 0) {
		getArena().setStatus(ArenaStatus.WAITING);
	    }

	    if (!getArena().hasKit()) {
		this.seconds = 0;
		getArena().setPlayerLevel(this.startDelay);
		return;
	    }

	    getArena().setPlayerLevel(this.startDelay - this.seconds);

	    this.seconds++;
	    return;
	}

	if (this.seconds == this.startDelay) {  // set game settings
	    if (getArena().getCurrentRound() == 0 && getArena().getCurrentWave() == 0) {
		getArena().initArenaScore(false);
		getArena().setRound(1);
		getArena().setWave(1);
	    }
	    getArena().setPlayerLevel(0);
	    getArena().setPlayerBoards();
	    getArena().removePlayerBoards();
	    getArena().updatePlayerBoards();

	    for (ZvPPlayer player : getArena().getPlayers()) {
		player.setSpawnProtected(false);
	    }

	    getArena().setStatus(ArenaStatus.RUNNING);

	    this.firstSpawn = true;
	    this.spawnZombies = false;
	    this.seconds++;
	    return;
	}

	if (this.seconds > this.startDelay && getLivingPlayers().length > 0) { // actuall game start

	    if (this.firstSpawn) {
		final int nextZombies = getArena().getSpawningZombies();
		this.spawnGoal = nextZombies - (int) (nextZombies * spawnRate);
		getArena().spawnZombies(this.spawnGoal);
		this.firstSpawn = false;
		this.spawnZombies = true;
	    } else {
		// INFO: More zombies will spawn if player joins while firstSpawn or spawnZombies is true
		// could be fixed by setting a global int in firstspawn. Depends on players opinion
		final int nextZombies = getArena().getSpawningZombies();
		// System.out.println(this.spawnGoal + " < " + nextZombies + " && " + this.spawnZombies);
		if ((this.spawnGoal < nextZombies) && this.spawnZombies) {
		    double missing = nextZombies - this.spawnGoal;
		    ZvP.getPluginLogger().log(this.getClass(), Level.FINER, "Arena: " + getArena().getID() + " Missing: " + (int) missing, true);

		    if (missing >= ((int) (nextZombies * 0.17)) && ((int) (nextZombies * 0.10)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.10));;
		    } else if (missing >= ((int) (nextZombies * 0.12)) && ((int) (nextZombies * 0.06)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.06));
		    } else if (missing >= ((int) (nextZombies * 0.08)) && ((int) (nextZombies * 0.02)) > 0) {
			this.spawnGoal += getArena().spawnZombies((int) (nextZombies * 0.02));
		    } else if (missing > getArena().getConfig().getSpawnRate() && ((int) (getArena().getConfig().getSpawnRate() * 0.5)) > 0) {
			this.spawnGoal += getArena().spawnZombies(getArena().getConfig().getSpawnRate() / 2);
		    } else {
			this.spawnGoal += getArena().spawnZombies(1);
		    }
		} else {
		    this.spawnZombies = false;
		    this.firstSpawn = false;
		}
	    }

	    if (this.firstSpawn == false && this.spawnZombies == false) {

		if (getArena().getConfig().isAutoWaves()) {
		    if (getArena().hasNext() && EntityListener.hasInteractionTimeout()) {
			if (!getArena().getConfig().isVoteSystem()) {
			    if (getArena().getLivingZombieAmount() < (getArena().getSpawningZombies() * EntityListener.ZOMBIEINTERACTIONFACTOR)) {
				getArena().next();
				getArena().updatePlayerBoards();

				ZvP.getPluginLogger().log(getClass(), Level.INFO, "Arena " + getArena().getID() + " moved into the next wave cause of no zombie interaction!", true, true);

				getArena().reStart(getArena().getConfig().getBreakTime());
				this.cancel();
			    }
			}
		    }
		}

		if (getArena().getLivingZombieAmount() == 0) {

		    getArena().updatePlayerBoards();
		    boolean stop = getArena().next(); // Stop checks if the last round is over

		    if (!stop) {
			if (getArena().getConfig().isVoteSystem()) {
			    getArena().setStatus(ArenaStatus.VOTING);
			    this.taskID = new BukkitRunnable() {

				@Override
				public void run() {

				    if (getArena().getStatus() == ArenaStatus.VOTING) {
					getArena().sendMessage(MessageManager.getMessage(game.vote_request));
				    } else {
					this.cancel();
				    }
				}
			    }.runTaskTimer(ZvP.getInstance(), 10L, 17 * 20L).getTaskId();

			    this.cancel();
			} else {
			    getArena().setStatus(ArenaStatus.BREAKWAITING);
			    getArena().reStart(getArena().getConfig().getBreakTime());
			    this.cancel();
			}
		    } else { // End of Game
			fireFirework();

			double money = getArena().getScore().getScoreDiffSum();
			int deaths = 0;

			for (ZvPPlayer p : getArena().getPlayers()) {
			    deaths += p.getDeaths();
			}

			String[] donP = MessageManager.getMessage(game.won_messages).split(";");
			int index = this.rand.nextInt(donP.length);
			String endMessage = MessageManager.getFormatedMessage(game.won, getArena().getKilledZombies(), (getArena().getConfig().getMaxRounds() * getArena().getConfig().getMaxWaves()), deaths, Math.round(money), donP[index]);
			getArena().sendMessage(endMessage);
			this.cancel();
			return;
		    }
		}
	    }
	} else {
	    // Game Over
	    if (getLivingPlayers().length == 0) {
		getArena().clearArena();
		fireFirework();
		getArena().sendMessage(MessageManager.getFormatedMessage(game.lost, getArena().getKilledZombies(), (getArena().getCurrentRound() * getArena().getCurrentWave())));
		this.cancel();
		return;
	    }

	    this.cancel();
	    getArena().stop();
	    return;
	}

	getArena().getWorld().setTime(15000L);
	this.seconds++;
    }
}
