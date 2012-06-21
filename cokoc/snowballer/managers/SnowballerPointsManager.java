package cokoc.snowballer.managers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class SnowballerPointsManager {
	public HashMap<String, Integer> playerPoints = new HashMap<String, Integer>();
	
	public int getPlayerPoints(String playerName) {
		return playerPoints.get(playerName);
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

	protected void save(Object obj,String path) throws Exception
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	protected Object load(String path) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
}
