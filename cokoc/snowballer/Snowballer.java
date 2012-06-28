package cokoc.snowballer;

import net.minecraft.server.EntityTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import cokoc.snowballer.thirdpartycode.SnowballerEntityTracker;
import cokoc.snowballer.utils.FileIO;
import cokoc.snowballer.commands.AdminCommandExecutor;
import cokoc.snowballer.commands.SnowCommandExecutor;
import cokoc.snowballer.commands.TeamCommandExecutor;
import cokoc.snowballer.listeners.SnowballerInGameListener;
import cokoc.snowballer.listeners.SnowballerShopPlayerListener;
import cokoc.snowballer.managers.SnowballerGamesManager;
import cokoc.snowballer.managers.SnowballerConfigsManager;
import cokoc.snowballer.managers.SnowballerKillVerbsManager;
import cokoc.snowballer.managers.SnowballerPointsManager;
import cokoc.snowballer.managers.SnowballerRanksManager;
import cokoc.snowballer.managers.SnowballerShopsManager;
import cokoc.snowballer.managers.SnowballerTerrainsManager;

public class Snowballer extends JavaPlugin {
	public static SnowballerGamesManager gamesManager;
	public static SnowballerTerrainsManager terrainsManager;
	public static SnowballerConfigsManager configsManager;
	public static SnowballerPointsManager pointsManager;
	public static SnowballerRanksManager ranksManager;
	public static SnowballerShopsManager shopsManager;
	public static Snowballer instance;

	public void onEnable() {
		instance = this;
		configsManager = new SnowballerConfigsManager(this);
		terrainsManager = new SnowballerTerrainsManager();
		gamesManager = new SnowballerGamesManager();
		pointsManager = new SnowballerPointsManager();
		ranksManager = new SnowballerRanksManager();
		shopsManager = new SnowballerShopsManager();

		configsManager.loadConfigs();
		if(FileIO.checkFileCreate("/Snowballer/", "terrains.bin")) {
			terrainsManager.loadData();
		} if(FileIO.checkFileCreate("/Snowballer/", "points.bin")) {
			pointsManager.loadData();
		} if(FileIO.checkFileCreate("/Snowballer/", "ranks.bin")) {
			ranksManager.loadData();
		} if(FileIO.checkFileCreate("/Snowballer/", "shops.bin")) {
			shopsManager.loadData();
		}
		
		if(! FileIO.checkFile("/Snowballer/", "killverbs.txt")) {
			FileIO.copyFile("/Snowballer/", "killverbs.txt", this.getResource("killverbs.txt"));
		} SnowballerKillVerbsManager.loadMessages();

		MinecraftServer minecraftServer = ((CraftServer) getServer()).getServer();
		WorldServer worldServer;
		for(World world: getServer().getWorlds()) {
			worldServer = ((CraftWorld) world).getHandle();
			worldServer.tracker = new SnowballerEntityTracker(minecraftServer, worldServer);
			for(Object object: worldServer.entityList)
				worldServer.tracker.track((net.minecraft.server.Entity) object);
		}

		getCommand("red").setExecutor(new TeamCommandExecutor());
		getCommand("blue").setExecutor(new TeamCommandExecutor());
		getCommand("random").setExecutor(new TeamCommandExecutor());
		getCommand("leave").setExecutor(new TeamCommandExecutor());
		getCommand("snow").setExecutor(new AdminCommandExecutor());
		getCommand("pool").setExecutor(new SnowCommandExecutor());
		getCommand("spectate").setExecutor(new SnowCommandExecutor());
		getCommand("points").setExecutor(new SnowCommandExecutor());
		getCommand("rank").setExecutor(new SnowCommandExecutor());

		getServer().getPluginManager().registerEvents(new SnowballerInGameListener(), this);
		getServer().getPluginManager().registerEvents(new SnowballerShopPlayerListener(), this);

		if(Snowballer.configsManager.speedball)
			gamesManager.startSpeedball();
	}

	public void onDisable() {
		MinecraftServer minecraftServer = ((CraftServer) getServer()).getServer();
		WorldServer worldServer;
		for(World world: getServer().getWorlds()) {
			worldServer = ((CraftWorld) world).getHandle();
			worldServer.tracker = new EntityTracker(minecraftServer, worldServer);
			for(Object object: worldServer.entityList)
				worldServer.tracker.track((net.minecraft.server.Entity) object);
		}

		terrainsManager.saveData();
		pointsManager.saveData();
		ranksManager.saveData();
		shopsManager.saveData();
		gamesManager.stopAllGames();
	}

	public static Snowballer getInstance() {
		return instance;
	}
}
