package cokoc.snowballer.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.internal.Pair;

public class AwaitingPlayersManager {
	public HashMap<String, String> players;

	public AwaitingPlayersManager() {
		players = new HashMap<String, String>();
	}

	public void addPlayer(Player player, String teamColor) {
		players.put(player.getName(), teamColor);
	}

	public void removePlayer(Player player) {
		players.remove(player.getName());
	}

	public ArrayList<Pair<Player, String>> getWaitingPlayers() {
		ArrayList<Pair<Player, String>> awaitingPlayers = new ArrayList<Pair<Player, String>>();
		Iterator<Entry<String, String>> it = players.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			if(Bukkit.getServer().getPlayer(pairs.getKey()) != null) {
				Player player = Bukkit.getServer().getPlayer(pairs.getKey());
				awaitingPlayers.add(new Pair<Player, String>(player, pairs.getValue()));
			}
		} return awaitingPlayers;
	}
	
	public ArrayList<String> getWaitingTeams() {
		ArrayList<String> awaitingTeams = new ArrayList<String>();
		Iterator<Entry<String, String>> it = players.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			if(Bukkit.getServer().getPlayer(pairs.getKey()) != null) {
				String teamName = pairs.getValue();
				if(! awaitingTeams.contains(teamName))
					awaitingTeams.add(teamName);
			}
		} return awaitingTeams;
	}
	
	public String getPlayerTeam(Player player) {
		if(players.containsKey(player.getName()))
			return players.get(player.getName());
		return "default";
	}
	
	public boolean isAwaiting(Player player) {
		if(players.containsKey(player.getName()))
			return true;
		return false;
	}
}
