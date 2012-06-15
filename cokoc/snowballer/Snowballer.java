package cokoc.snowballer;

import org.bukkit.plugin.java.JavaPlugin;

import cokoc.snowballer.utils.FileIO;
import cokoc.snowballer.commands.AdminCommandExecutor;
import cokoc.snowballer.commands.SnowCommandExecutor;
import cokoc.snowballer.commands.TeamCommandExecutor;
import cokoc.snowballer.listeners.SnowballerPlayerListener;
import cokoc.snowballer.managers.SnowballerGamesManager;
import cokoc.snowballer.managers.SnowballerConfigsManager;
import cokoc.snowballer.managers.SnowballerKillVerbsManager;
import cokoc.snowballer.managers.SnowballerTerrainsManager;

public class Snowballer extends JavaPlugin {
	public static SnowballerGamesManager gamesManager;
	public static SnowballerTerrainsManager terrainsManager;
	public static SnowballerConfigsManager configsManager;
	public static Snowballer instance;

	public void onEnable() {
		instance = this;
		configsManager = new SnowballerConfigsManager(this);
		terrainsManager = new SnowballerTerrainsManager();
		gamesManager = new SnowballerGamesManager();

		configsManager.loadConfigs();
		if(FileIO.checkFileCreate("/Snowballer/", "terrains.bin")) {
			terrainsManager.loadData();
		} if(! FileIO.checkFile("/Snowballer/", "killverbs.txt")) {
			FileIO.copyFile("/Snowballer/", "killverbs.txt", this.getResource("killverbs.txt"));
		} SnowballerKillVerbsManager.loadMessages();

		getCommand("red").setExecutor(new TeamCommandExecutor());
		getCommand("blue").setExecutor(new TeamCommandExecutor());
		getCommand("random").setExecutor(new TeamCommandExecutor());
		getCommand("leave").setExecutor(new TeamCommandExecutor());
		getCommand("snow").setExecutor(new AdminCommandExecutor());
		getCommand("pool").setExecutor(new SnowCommandExecutor());
		getCommand("spectate").setExecutor(new SnowCommandExecutor());

		getServer().getPluginManager().registerEvents(new SnowballerPlayerListener(), this);

		if(Snowballer.configsManager.speedball)
			gamesManager.startSpeedball();
	}

	public void onDisable() {
		terrainsManager.saveData();
		gamesManager.stopAllGames();
	}

	public static Snowballer getInstance() {
		return instance;
	}
}
