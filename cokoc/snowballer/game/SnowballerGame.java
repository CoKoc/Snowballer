package cokoc.snowballer.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

import com.google.gson.internal.Pair;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.managers.AwaitingPlayersManager;
import cokoc.snowballer.managers.SnowballerKillVerbsManager;


public class SnowballerGame {
	protected HashMap<String, String> players;
	protected AwaitingPlayersManager awaitingPlayers;
	protected ArrayList<String> spectators;
	protected SnowballerTerrain terrain;
	private boolean running;
	private String type;
	private String host;

	public SnowballerGame(String host, SnowballerTerrain terrain, String type) {
		this.players = new HashMap<String, String>();
		this.spectators = new ArrayList<String>();
		this.awaitingPlayers = new AwaitingPlayersManager();
		this.terrain = terrain;
		this.type = type;
		this.host = host;
		running = false;
	}

	public void addPlayerToQueue(Player player, String teamColor) {
		awaitingPlayers.addPlayer(player, teamColor);
	}

	public void removePlayerFromQueue(Player player) {
		awaitingPlayers.removePlayer(player);
	}

	public void addSpectator(Player player) {
		spectators.add(player.getName());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getName());
	}

	public boolean isPlayerSpectator(Player player) {
		if(spectators.contains(player.getName()))
			return true;
		return false;
	}

	public void removePlayer(Player player) {
		SnowballerInventorySetter.setInventory(player, "default");
		player.teleport(Snowballer.terrainsManager.getHubSpawn());
		players.remove(player.getName());
	}

	public void setup() {
		ArrayList<Pair<Player, String>> gamePlayers = getWaitingPlayers();
		running = true;
		for(int i = 0; i < gamePlayers.size(); ++i) {
			Player player = gamePlayers.get(i).first;
			String playerTeamColor = gamePlayers.get(i).second;
			player.teleport(terrain.getRandomSpawnPoint(playerTeamColor));
			SnowballerInventorySetter.setInventory(player, playerTeamColor);
			awaitingPlayers.removePlayer(player);
			player.setDisplayName(SnowballerMessager.getStringColor(playerTeamColor) + player.getName());
			player.setPlayerListName(player.getDisplayName());
			players.put(gamePlayers.get(i).first.getName(), gamePlayers.get(i).second);
		} if(! type.equalsIgnoreCase("speedball"))
			SnowballerMessager.announceToGame(this, "Game started!");
	}

	public SnowballerTerrain getTerrain() {
		return terrain;
	}

	public void setTerrain(SnowballerTerrain terrain) {
		this.terrain = terrain;
	}

	public void end() {
		running = false;
		Snowballer.terrainsManager.setOccupied(terrain, false);
		ArrayList<Player> players = getPlayers();
		for(int i = 0; i < spectators.size(); ++i)
			Snowballer.gamesManager.playerStopSpectate(Bukkit.getServer().getPlayer(spectators.get(i)));
		for(int i = 0; i < players.size(); ++i) {
			SnowballerInventorySetter.setInventory(players.get(i), "default");
			players.get(i).teleport(Snowballer.terrainsManager.getHubSpawn());
			Snowballer.gamesManager.playerQuitGameQueue(players.get(i), this);
			if(type.equalsIgnoreCase("speedball"))
				addPlayerToQueue(players.get(i), getPlayerTeam(players.get(i)));
			removePlayer(players.get(i));
		} if(type.equalsIgnoreCase("speedball"))
			Snowballer.gamesManager.issueGame(this);
	}

	public void forceEnd() {
		running = false;
		Snowballer.terrainsManager.setOccupied(terrain, false);
		ArrayList<Player> players = getPlayers();
		for(int i = 0; i < players.size(); ++i) {
			SnowballerInventorySetter.setInventory(players.get(i), "default");
			players.get(i).teleport(Snowballer.terrainsManager.getHubSpawn());
			Snowballer.gamesManager.playerQuitGameQueue(players.get(i), this);
			if(type.equalsIgnoreCase("speedball"))
				addPlayerToQueue(players.get(i), getPlayerTeam(players.get(i)));
			removePlayer(players.get(i));
		}
	}

	private boolean checkWin() {
		if(getTeams().size() <= 1)
			return true;
		return false;
	}

	public void killPlayer(Player target, Player killer) {
		if(running) {
			ChatColor fraggedTeamColor = SnowballerMessager.getStringColor(getPlayerTeam(target));
			ChatColor fraggerTeamColor = SnowballerMessager.getStringColor(getPlayerTeam(killer));
			String playerFragAnnouncement = fraggerTeamColor + killer.getDisplayName() + ChatColor.RESET + " ";
			if(target.isSneaking())
				playerFragAnnouncement = playerFragAnnouncement + SnowballerKillVerbsManager.getRandomKillVerb() + " " + fraggedTeamColor + target.getDisplayName() + ChatColor.RESET + " while he was hiding!";
			else if(target.isSprinting())
				playerFragAnnouncement = playerFragAnnouncement +SnowballerKillVerbsManager.getRandomKillVerb() + " " + fraggedTeamColor + target.getDisplayName() + ChatColor.RESET + " while he was making a run!";
			else
				playerFragAnnouncement = playerFragAnnouncement + SnowballerKillVerbsManager.getRandomKillVerb() + " " + fraggedTeamColor + target.getDisplayName() + ChatColor.RESET + "!";
			killer.getWorld().playEffect(killer.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 0, 2);
			if(type.equalsIgnoreCase("speedball"))
				SnowballerMessager.broadcast("[�dSpeed�bball�f] " + playerFragAnnouncement);
			else
				SnowballerMessager.announceToGame(this, playerFragAnnouncement);
			target.teleport(Snowballer.terrainsManager.getHubSpawn());
			SnowballerInventorySetter.setInventory(target, "default");
			Snowballer.gamesManager.playerQuitGameQueue(target, this);
			if(type.equalsIgnoreCase("speedball"))
				addPlayerToQueue(target, getPlayerTeam(target));
			SnowballerVanisher.playerSeePlayers(target, getSpectators());
			target.setDisplayName(target.getName());
			target.setPlayerListName(target.getDisplayName());
			removePlayer(target);
			if(checkWin()) {
				String teamWinAnnouncement = SnowballerMessager.getColoredString(getTeams().get(0)) + " team won the game.";
				SnowballerMessager.announceToGame(this, teamWinAnnouncement);
				Snowballer.gamesManager.stopGame(this);
			}
		}
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> playersBuffer = new ArrayList<Player>();
		Iterator<Entry<String, String>> it = players.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			String playerName = pairs.getKey();
			if(Bukkit.getServer().getPlayer(playerName) != null)
				playersBuffer.add(Bukkit.getServer().getPlayer(playerName));
		} return playersBuffer;
	}

	public ArrayList<Pair<Player, String>> getWaitingPlayers() {
		return awaitingPlayers.getWaitingPlayers();
	}

	public ArrayList<Player> getSpectators() {
		ArrayList<Player> playersBuffer = new ArrayList<Player>();
		for(int i = 0; i < spectators.size(); ++i)
			if(Bukkit.getServer().getPlayer(spectators.get(i)) != null)
				playersBuffer.add(Bukkit.getServer().getPlayer(spectators.get(i)));
		return playersBuffer;
	}

	public ArrayList<String> getTeams() {
		ArrayList<String> teamsBuffer = new ArrayList<String>();
		Iterator<Entry<String, String>> it = players.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			String playerName = pairs.getKey();
			if(Bukkit.getServer().getPlayer(playerName) != null) {
				Player player = Bukkit.getServer().getPlayer(playerName);
				if(! teamsBuffer.contains(getPlayerTeam(player)))
					teamsBuffer.add(getPlayerTeam(player));
			}
		} return teamsBuffer;
	}

	public ArrayList<String> getWaitingTeams() {
		return awaitingPlayers.getWaitingTeams();
	}

	public boolean isPlayerInGame(Player player) {
		if(players.containsKey(player.getName()))
			return true;
		return false;
	}

	public boolean isPlayerAwaiting(Player player) {
		if(awaitingPlayers.isAwaiting(player))
			return true;
		return false;
	}

	public String getPlayerTeam(Player player) {
		if(players.containsKey(player.getName()))
			return players.get(player.getName());
		return "default";
	}
	
	public String getHost() {
		return host;
	}
	
	public String getType() {
		return type;
	}
}