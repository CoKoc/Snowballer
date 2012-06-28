package cokoc.snowballer.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.utils.FileIO;

public class SnowballerShopsConfigManager {
	private HashMap<Integer, List<String>> ranksBenefits;
	private HashMap<Integer, Integer> prices;

	SnowballerShopsConfigManager() {
		ranksBenefits = new HashMap<Integer, List<String>>();
		prices = new HashMap<Integer, Integer>();
	}
	
	public int getRankPrice(int rank) {
		if(prices.containsKey(rank))
			return prices.get(rank);
		else
			return -1;
	}
	
	public List<String> getBenefits(int rank) {
		if(ranksBenefits.containsKey(rank))
			return ranksBenefits.get(rank);
		else
			return null;
	}
	
	public int getNumberOfRanks() {
		return ranksBenefits.size();
	}
	
	public void loadConfigs() {
		if(FileIO.checkFileCreate("/Snowballer/", "progression.yml")) {
			File file = new File("plugins/Snowballer/progression.yml");
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(file);
				Set<String> keys = config.getKeys(false);
				ranksBenefits  = new HashMap<Integer, List<String>>();
				Iterator<String> it = keys.iterator();
				while(it.hasNext()) {
					String currentKey = it.next();
					if(currentKey.contains("price"))
						continue;
					int rankId = Integer.parseInt(currentKey);
					ranksBenefits.put(rankId, config.getStringList(currentKey));
					prices.put(rankId, config.getInt("price " + currentKey));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			FileIO.copyFile("/Snowballer/", "progression.yml", Snowballer.getInstance().getResource("progression.yml"));
		}
	}
}
