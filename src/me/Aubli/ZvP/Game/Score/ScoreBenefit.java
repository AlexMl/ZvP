package me.Aubli.ZvP.Game.Score;

import me.Aubli.ZvP.Game.ZvPPlayer;


public class ScoreBenefit {

    private String permission;

    private double multiplierPositiv;
    private double multiplierNegativ;

    public ScoreBenefit(String permissionNode, double multiplierPositiv, double multiplierNegativ) {
	this.permission = permissionNode;
	this.multiplierPositiv = multiplierPositiv;
	this.multiplierNegativ = multiplierNegativ;
    }

    public String getPermission() {
	return this.permission;
    }

    public double getPositivMultiplier() {
	return this.multiplierPositiv;
    }

    public double getNegativMultiplier() {
	return this.multiplierNegativ;
    }

    public boolean applies(ZvPPlayer player) {
	return player.getPlayer().hasPermission(getPermission());
    }

}
