package cokoc.snowballer.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SnowballerRanksManager extends SnowballerPersistentManager {
	private HashMap<String, Integer> ranks = new HashMap<String, Integer>();
	
	public int getPlayerRank(Player player) {
		if(ranks.containsKey(player.getName()))
			return ranks.get(player.getName());
		else {
			ranks.put(player.getName(), 0);
			return 0;
		}
	}
	
	public void upgradePlayer(Player player) {
		if(ranks.containsKey(player.getName())) {
			int currentRank = ranks.get(player.getName());
			ranks.put(player.getName(), (currentRank + 1));
		} else {
			ranks.put(player.getName(), 1);
		}
	}
	
	public void setRank(String playerName, int rank) {
		ranks.put(playerName, rank);
	}
	
	@SuppressWarnings("unchecked")
	public void loadData() {
		try {
			ranks = (HashMap<String, Integer>) load("plugins/Snowballer/ranks.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			save(ranks, "plugins/Snowballer/ranks.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
