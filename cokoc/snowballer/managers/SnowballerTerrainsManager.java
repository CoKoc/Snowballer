package cokoc.snowballer.managers;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import cokoc.snowballer.game.SnowballerTerrain;
import cokoc.snowballer.utils.TinyLocation;

public class SnowballerTerrainsManager extends SnowballerPersistentManager {
	private ArrayList<SnowballerTerrain> terrains;
	private ArrayList<Boolean> occupied;
	private TinyLocation hubSpawn;
	
	public SnowballerTerrainsManager() {
		terrains = new ArrayList<SnowballerTerrain>();
		occupied = new ArrayList<Boolean>();
		hubSpawn = new TinyLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
	}
	
	public void addTerrain(SnowballerTerrain terrain) {
		terrains.add(terrain);
		occupied.add(false);
	}
	
	public void removeTerrain(String terrainName) {
		for(int i = 0; i < terrains.size(); ++i)
			if(terrains.get(i).getName().equalsIgnoreCase(terrainName))
				terrains.remove(terrains.get(i));
	}
	
	public boolean hasTerrain(String terrainName) {
		for(int i = 0; i < terrains.size(); ++i)
			if(terrains.get(i).getName().equalsIgnoreCase(terrainName))
				return true;
		return false;
	}
	
	public ArrayList<Boolean> getOccupacy() {
		return occupied;
	}
	
	public void setOccupied(SnowballerTerrain terrain, boolean isOccupied) {
		occupied.set(getTerrainId(terrain), isOccupied);
	}
	
	public boolean isOccupied(SnowballerTerrain terrain) {
		return occupied.get(terrains.indexOf(terrain));
	}
	
	public SnowballerTerrain getRandomVacantTerrain() {
		Random generator = new Random();
		while(true) {
			int terrainId = generator.nextInt(terrains.size());
			if(! occupied.get(terrainId)) {
				return terrains.get(terrainId);
			}
		}
	}
	
	public boolean hasViableTerrain() {
		if(! terrains.isEmpty()) {
			for(int i = 0; i < terrains.size(); ++i) {
				if(terrains.get(i).hasSpawns("blue"))
					if(terrains.get(i).hasSpawns("red"))
						return true;
			}
		} return false;
	}
	
	public SnowballerTerrain getTerrain(String name) {
		for(int i = 0; i < terrains.size(); ++i) {
			if(terrains.get(i).getName().equalsIgnoreCase(name))
				return terrains.get(i);
		} return new SnowballerTerrain("default");
	}
	
	public Location getHubSpawn() {
		if(hubSpawn != null)
			return hubSpawn.toLocation();
		return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	
	public void setHubSpawn(Location location) {
		this.hubSpawn = new TinyLocation(location);
	}
	
	public ArrayList<SnowballerTerrain> getTerrains() {
		return terrains;
	}
	
	public int getTerrainId(SnowballerTerrain terrain) {
		for(int i = 0; i < terrains.size(); ++i)
			if(terrains.get(i).getName().equalsIgnoreCase(terrain.getName()))
				return i;
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public void loadData() {
		try {
			terrains = (ArrayList<SnowballerTerrain>) load("plugins/Snowballer/terrains.bin");
			hubSpawn = (TinyLocation) load("plugins/Snowballer/hubSpawn.bin");
			for(int i = 0; i < terrains.size(); ++i)
				occupied.add(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			save(terrains, "plugins/Snowballer/terrains.bin");
			save(hubSpawn, "plugins/Snowballer/hubSpawn.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
