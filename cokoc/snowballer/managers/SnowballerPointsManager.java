package cokoc.snowballer.managers;

import java.util.HashMap;

public class SnowballerPointsManager extends SnowballerPersistentManager {
	public HashMap<String, Integer> playerPoints = new HashMap<String, Integer>();
	
	public int getPlayerPoints(String playerName) {
		if(playerPoints.containsKey(playerName))
			return playerPoints.get(playerName);
		else {
			playerPoints.put(playerName, 0);
			return 0;
		}
	}
	
	public void addPointsToPlayer(String playerName, int points) {
		if(playerPoints.containsKey(playerName)) {
			int oldPoints = getPlayerPoints(playerName);
			playerPoints.put(playerName, points + oldPoints);
		} else 
			playerPoints.put(playerName, points);
	}
	
	@SuppressWarnings("unchecked")
	public void loadData() {
		try {
			playerPoints = (HashMap<String, Integer>) load("plugins/Snowballer/points.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			save(playerPoints, "plugins/Snowballer/points.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
