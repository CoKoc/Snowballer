package cokoc.snowballer.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;

import cokoc.snowballer.utils.TinyLocation;


public class SnowballerTerrain implements Serializable {
	private static final long serialVersionUID = -6649307512371349177L;

	private HashMap<String, ArrayList<TinyLocation>> spawns;
	String name;

	public SnowballerTerrain(String name) {
		spawns = new HashMap<String, ArrayList<TinyLocation>>();
		this.name = name;
	}

	public void addSpawn(String teamColor, Location location) {
		if(spawns.containsKey(teamColor))
			spawns.get(teamColor).add(new TinyLocation(location));
		else {
			ArrayList<TinyLocation> spawnsBuffer = new ArrayList<TinyLocation>();
			spawnsBuffer.add(new TinyLocation(location));
			spawns.put(teamColor, spawnsBuffer);
		}
	}

	public void removeSpawn(String teamColor, int index)  {
		if(spawns.containsKey(teamColor))
			if(index < spawns.get(teamColor).size())
				spawns.get(teamColor).remove(index);
	}

	public Location getRandomSpawnPoint(String teamColor) {
		Random generator = new Random();
		int randomIndex = generator.nextInt(spawns.get(teamColor).size());
		return spawns.get(teamColor).get(randomIndex).toLocation();
	}

	public Location getSpawn(String teamColor, int id) {
		if(hasSpawn(teamColor, id))
			return spawns.get(teamColor).get(id).toLocation();
		else
			return null;
	}

	public boolean hasSpawns() {
		if(spawns.isEmpty())
			return false;
		return true;
	}

	public boolean hasSpawns(String teamColor) {
		if(spawns.containsKey(teamColor))
			return true;
		return false;
	}

	public boolean hasSpawn(String teamColor, int id) {
		if(hasSpawns(teamColor))
			if(id < spawns.get(teamColor).size())
				return true;
		return false;
	}

	public HashMap<String, ArrayList<TinyLocation>> getSpawns() {
		return spawns;
	}

	public ArrayList<TinyLocation> getSpawns(String teamColor) {
		if(spawns.containsKey(teamColor))
			return spawns.get(teamColor);
		else
			return new ArrayList<TinyLocation>();
	}

	public String getName() {
		return name;
	}
}
