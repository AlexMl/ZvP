package me.Aubli.ZvP.Game.Score;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageManager;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;


public class ArenaScore {

    public enum ScoreType {
	DEATH_SCORE,
	KILL_SCORE,
	SHOP_SCORE;
    }

    private Arena arena;

    private double score;

    private Map<ZvPPlayer, Double> playerScore;

    private static final ScoreBenefit NULL_BENEFIT = new ScoreBenefit("zvp.play", 1.0, 1.0);
    private ScoreBenefit[] benefits;

    private final boolean separated;
    private final boolean vaultEcon;

    public ArenaScore(Arena arena, boolean separated, boolean econSupport, boolean econGameIntegration) {
	this.arena = arena;
	this.vaultEcon = (econSupport && econGameIntegration);
	this.separated = useVaultEconomy() ? true : separated;

	initAccounts(ZvPConfig.getStartCapital(), isSeparated());
	initBenefits();

	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Finished init of " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + " for arena " + arena.getID(), true);
    }

    private void initAccounts(String startCapital, boolean separated) {
	if (separated) {
	    this.playerScore = new HashMap<ZvPPlayer, Double>();

	    for (ZvPPlayer player : getArena().getPlayers()) {
		initPlayer(player, getStartAmount(player));
	    }

	} else {
	    this.score = getStartAmount(null);
	}
    }

    private double getStartAmount(ZvPPlayer player) {
	/* startCapital = 0:
	 * - this.score = 0
	 * - playerScore = 0
	 * - econScore = 0
	 *
	 * startCapital != 0:
	 * - this.score = start
	 * - playerScore = start
	 * - econScore = start; account -= start;
	 *
	 * startCapital = all:
	 * - this.score = 0
	 * - this.playerScore = 0
	 * - this.econScore = account
	 */

	boolean all = false;
	double start = 0.0;

	if (ZvPConfig.getStartCapital().equalsIgnoreCase("all")) {
	    all = true;
	} else {
	    try {
		start = Double.parseDouble(ZvPConfig.getStartCapital());
	    } catch (NumberFormatException e) {
		start = 0.0;
	    }
	}

	if (useVaultEconomy()) {
	    if (all | !ZvP.getEconProvider().has(player.getPlayer(), start)) {
		return ZvP.getEconProvider().getBalance(player.getPlayer());
	    }
	}
	return start;
    }

    private void initPlayer(ZvPPlayer player, double startAmount) {
	if (useVaultEconomy()) {
	    EconomyResponse response = ZvP.getEconProvider().withdrawPlayer(player.getPlayer(), startAmount);
	    printResponse(response);
	}

	this.playerScore.put(player, startAmount);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "Finished init for " + player.getName() + "'s " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore") + " with startamount of " + startAmount), true, true);
    }

    private void initBenefits() {
	this.benefits = ZvPConfig.getBenefits();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Loaded " + this.benefits.length + " premium benefit(s) from config.", true);
    }

    public void reInitPlayer(ZvPPlayer player) {
	if (isSeparated()) {
	    initPlayer(player, getStartAmount(player));
	}
    }

    public double getScore(ZvPPlayer player) {
	if (!isSeparated()) {
	    return this.score;
	} else if (isSeparated() && player != null) {
	    return this.playerScore.get(player);
	} else {
	    ZvP.getPluginLogger().log(this.getClass(), Level.WARNING, "Error while returning score for Arena:" + this.arena.getID() + "; separated:" + isSeparated() + " player==null:" + (player == null), true, false);
	    return 0.0;
	}
    }

    public ScoreBenefit getBenefit(ZvPPlayer player, ScoreType scoreType) {
	if (scoreType == ScoreType.SHOP_SCORE) {
	    return NULL_BENEFIT;
	}

	for (ScoreBenefit b : this.benefits) {
	    if (b.applies(player)) {
		return b;
	    }
	}
	return NULL_BENEFIT;
    }

    public Arena getArena() {
	return this.arena;
    }

    public boolean isSeparated() {
	return this.separated;
    }

    public boolean useVaultEconomy() {
	return this.vaultEcon;
    }

    public void addScore(ZvPPlayer player, double origScore, ScoreType type) {
	ScoreBenefit benefit = getBenefit(player, type);
	double score = origScore * benefit.getPositivMultiplier();

	if (isSeparated()) {
	    this.playerScore.put(player, this.playerScore.get(player) + score);
	    player.updateScoreboard();
	} else {
	    this.score += score;
	    this.arena.updatePlayerBoards();
	}

	this.arena.getRecordManager().addMoney(player.getUuid(), score);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "A" + getArena().getID() + ": " + player.getName() + " ++ " + origScore + "x" + benefit.getPositivMultiplier() + "; " + score + " --> " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + "; Task:" + type, true);
    }

    public void subtractScore(ZvPPlayer player, double origScore, ScoreType type) {
	ScoreBenefit benefit = getBenefit(player, type);
	double score = origScore * benefit.getNegativMultiplier();

	if (isSeparated()) {
	    double prevScore = this.playerScore.get(player);
	    if (prevScore <= score && !ZvPConfig.getAllowDebts()) {
		this.playerScore.put(player, 0.0);
	    } else {
		this.playerScore.put(player, this.playerScore.get(player) - score);
	    }
	    player.updateScoreboard();
	} else {
	    if (score >= this.score && !ZvPConfig.getAllowDebts()) {
		this.score = 0;
	    } else {
		this.score -= score;
	    }
	    this.arena.updatePlayerBoards();
	}

	this.arena.getRecordManager().subtractMoney(player.getUuid(), score);
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "A" + getArena().getID() + ": " + player.getName() + " -- " + origScore + "x" + benefit.getNegativMultiplier() + "; " + score + " --> " + (useVaultEconomy() ? "EconAccount" : (isSeparated() ? "personalScore" : "sharedScore")) + "; Task:" + type, true);
    }

    public void syncScores() {
	for (ZvPPlayer player : getArena().getPlayers()) {
	    syncScore(player);
	}
    }

    public void syncScore(ZvPPlayer player) {
	if (!useVaultEconomy() || !this.playerScore.containsKey(player)) {
	    return;
	}

	Double playerScore = getScore(player);
	EconomyResponse response;
	if (playerScore < 0) {
	    response = ZvP.getEconProvider().withdrawPlayer(player.getPlayer(), Math.abs(playerScore));
	} else {
	    response = ZvP.getEconProvider().depositPlayer(player.getPlayer(), playerScore);
	}
	if (!response.transactionSuccess()) {
	    player.sendMessage(MessageManager.getMessage(error.transaction_failed));
	    ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Transaction failed for " + player.getName() + "! Acquired score was " + playerScore + response.errorMessage + "; Task: ScoreSync", false);
	}

	printResponse(response);
    }

    private void printResponse(EconomyResponse res) {
	ZvP.getPluginLogger().log(this.getClass(), Level.FINE, "EconomyResponse: " + res.type + (res.type == ResponseType.FAILURE ? ": " + res.errorMessage : "") + " Amount:" + res.amount + " ---> " + res.balance, true);
    }
}
