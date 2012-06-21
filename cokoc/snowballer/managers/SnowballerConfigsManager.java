package cokoc.snowballer.managers;

import org.bukkit.configuration.file.FileConfiguration;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.utils.FileIO;

public class SnowballerConfigsManager {
	private Snowballer plugin;
	public boolean friendlyFire = false;
	public boolean speedball = true;
	public boolean changeNamePlates = false;
	public long speedballDelay = 10;
	
	public SnowballerConfigsManager(Snowballer plugin) {
		this.plugin = plugin;
	}
	
	public void loadConfigs() {
		if(!FileIO.checkFile("/Snowballer/", "config.yml"))
			plugin.saveDefaultConfig();
		else
			plugin.getConfig().options().copyDefaults(true);
		
		FileConfiguration configs = plugin.getConfig();
		friendlyFire = configs.getBoolean("friendly fire");
		speedball = configs.getBoolean("speedball");
		speedballDelay = configs.getLong("speedball delay");
		changeNamePlates = configs.getBoolean("change name plate");
	}
}
