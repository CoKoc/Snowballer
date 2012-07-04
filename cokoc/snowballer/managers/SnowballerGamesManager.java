package cokoc.snowballer.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerTerrain;
import cokoc.snowballer.game.SnowballerVanisher;
import cokoc.translate.PluginHook;
import cokoc.translate.Translate;

public class SnowballerGamesManager {
	PluginHook t = Translate.getPluginHook(Snowballer.getInstance());
	public ArrayList<SnowballerGame> games;
	private boolean speedballRunning;

	public SnowballerGamesManager() {
		speedballRunning = false;
		games = new ArrayList<SnowballerGame>();
	}

	public void issueGame(final SnowballerGame game) {
		games.add(game);
		if(game.getHost().equalsIgnoreCase("Speedball")) {
			if(speedballRunning) {
				Snowballer.terrainsManager.setOccupied(getGameByHost("Speedball").getTerrain(), false);
				SnowballerTerrain newTerrain = Snowballer.terrainsManager.getRandomVacantTerrain();
				getGameByHost("Speedball").setTerrain(newTerrain);
				Snowballer.terrainsManager.setOccupied(newTerrain, true);
				long delay = Snowballer.configsManager.speedballDelay;
				SnowballerMessager.broadcast(t.s("SPEEDBALL") + delay + t.s("SECONDS_UNTIL") + game.getTerrain().getName() + "§f!");
				SnowballerMessager.broadcast(t.s("SPEEDBALL") + t.s("TYPE_RED_OR_BLUE"));
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Snowballer.getInstance(), new Runnable() {
					public void run() {
						if(speedballRunning) {
							if(getGameByHost("Speedball").getWaitingPlayers().size() <= 1) {
								SnowballerMessager.broadcast(t.s("SPEEDBALL") + t.s("NOT_ENOUGH_PLAYERS"));
								stopGame(getGameByHost("Speedball"));
							} else {
								if(getGameByHost("Speedball").getWaitingTeams().size() > 1) {
									SnowballerMessager.broadcast(t.s("SPEEDBALL") + t.s("GAME_HAS_STARTED"));
									getGameByHost("Speedball").setup();
								} else {
									SnowballerMessager.broadcast(t.s("SPEEDBALL") + t.s("ALL_SAME_TEAM"));
									stopGame(getGameByHost("Speedball"));
								}
							}
						} else
							forceStopGame(getGameByHost("Speedball"));
					}
				}, (20L * delay));
			} else
				forceStopGame(getGameByHost("Speedball"));
		}
	}

	public void stopGame(SnowballerGame game) {
		game.end();
		if(games.contains(game))
			games.remove(game);
	}

	public void forceStopGame(SnowballerGame game) {
		game.forceEnd();
		if(games.contains(game))
			games.remove(game);
	}

	public void stopAllGames() {
		for(int i = 0; i < games.size(); ++i) {
			games.get(i).forceEnd();
		}
	}

	public int getGameId(SnowballerGame game) {
		if(games.contains(game)) {
			for(int i = 0; i < games.size(); ++i)
				if(games.get(i).equals(game))
					return i;
		} return -1;
	}

	public void playerJoinGameQueue(Player player, SnowballerGame game, String team) {
		playerStopSpectate(player);
		if(! isPlayerInGame(player))
			SnowballerChangedNamesManager.setPlayerDisplayName(player, SnowballerMessager.getStringColor(team) + player.getName() + ChatColor.RESET);
		game.addPlayerToQueue(player, team);
	}

	public void playerQuitGameQueue(Player player, SnowballerGame game) {
		if(game.isPlayerAwaiting(player)) {
			game.removePlayerFromQueue(player);
			if(! isPlayerInGame(player))
				SnowballerChangedNamesManager.setPlayerDisplayName(player, player.getName());
		}
	}

	public void playerSpectateGame(Player player, SnowballerGame game) {
		playerStopSpectate(player);
		SnowballerMessager.announceToGame(game, player.getName() + t.s("JOINED_AS_SPECTATOR"));
		game.addSpectator(player);
		SnowballerVanisher.vanishToPlayersInGame(player, game);
		Location tpLocation = player.getLocation();
		if(game.getTerrain().hasSpawns("spectator")) {
			tpLocation = game.getTerrain().getRandomSpawnPoint("spectator");
		} else {
			SnowballerMessager.sendMessage(player, t.s("NO_REGISTERED_SPECTATOR_SPAWN"));
			tpLocation = game.getTerrain().getRandomSpawnPoint("red");
		}
		player.teleport(tpLocation);
		player.setGameMode(GameMode.CREATIVE);
		game.addSpectator(player);
	}

	public void playerStopSpectate(Player player) {
		if(isPlayerSpectator(player)) {
			player.teleport(Snowballer.terrainsManager.getHubSpawn());
			player.setGameMode(GameMode.SURVIVAL);
			SnowballerGame currentGame = getGameByPlayer(player);
			if(currentGame != null)
				currentGame.removeSpectator(player);
		}
	}

	public boolean isPlayerInGame(Player player) {
		for(int i = 0; i < games.size(); ++i) {
			if(games.get(i).isPlayerInGame(player))
				return true;
		} return false;
	}

	public boolean isPlayerSpectator(Player player) {
		for(int i = 0; i < games.size(); ++i) {
			if(games.get(i).isPlayerSpectator(player))
				return true;
		} return false;
	}

	public boolean isPlayerAwaiting(Player player) {
		for(int i = 0; i < games.size(); ++i) {
			if(games.get(i).isPlayerAwaiting(player))
				return true;
		} return false;
	}

	public String getPlayerTeamInGame(Player player) {
		return getGameByPlayer(player).getPlayerTeam(player);
	}

	public SnowballerGame getGameByPlayer(Player player) {
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).isPlayerSpectator(player))
				return games.get(i);
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).isPlayerInGame(player))
				return games.get(i);
		return null;
	}

	public ArrayList<SnowballerGame> getGamesPlayerIsWaitingIn(Player player) {
		ArrayList<SnowballerGame> gamesPlayerIsWaiting = new ArrayList<SnowballerGame>();
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).isPlayerAwaiting(player))
				gamesPlayerIsWaiting.add(games.get(i));
		return gamesPlayerIsWaiting;
	}

	public ArrayList<SnowballerGame> getGames() {
		return games;
	}

	public SnowballerGame getGameByHost(String hostName) {
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).getHost().equalsIgnoreCase(hostName))
				return games.get(i);
		return null;
	}

	public SnowballerGame getGameById(int gameId) {
		return games.get(gameId);
	}

	public void startSpeedball() {
		if(Snowballer.terrainsManager.hasViableTerrain()) {
			speedballRunning = true;
			SnowballerMessager.broadcast(t.s("SPEEDBALL") +  t.s("SPEEDBALL_ENABLED"));
			SnowballerTerrain terrain = Snowballer.terrainsManager.getRandomVacantTerrain();
			SnowballerGame speedballGame = new SnowballerGame("Speedball", terrain, "speedball");
			issueGame(speedballGame);
		} else {
			SnowballerMessager.broadcast(t.s("ERROR") + t.s("NO_VIABLE_TERRAINS"));
		}
	}

	public void stopSpeedball() {
		if(speedballRunning) {
			speedballRunning = false;
			SnowballerMessager.broadcast(t.s("SPEEDBALL") + t.s("SPEEDBALL_DISABLED"));
			if(getGameByHost("Snowballer") != null)
				getGameByHost("Snowballer").forceEnd();
		}
	}

	public boolean isSpeedball() {
		return speedballRunning;
	}
}
