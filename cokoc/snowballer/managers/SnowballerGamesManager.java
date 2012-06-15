package cokoc.snowballer.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerTerrain;
import cokoc.snowballer.game.SnowballerVanisher;

public class SnowballerGamesManager {
	public ArrayList<SnowballerGame> games;
	private boolean speedballRunning = false;

	public SnowballerGamesManager() {
		games = new ArrayList<SnowballerGame>();
	}

	public void issueGame(final SnowballerGame game) {
		games.add(game);
		if(game.getHost().equalsIgnoreCase("Speedball")) {
			if(speedballRunning) {
				SnowballerMessager.broadcast("[§dSpeed§bball§f] Game is starting in 10 seconds!");
				SnowballerMessager.broadcast("[§dSpeed§bball§f] Type /red or /blue to choose a game.");
				Snowballer.terrainsManager.setOccupied(getGameByHost("Speedball").getTerrain(), false);
				SnowballerTerrain newTerrain = Snowballer.terrainsManager.getRandomVacantTerrain();
				getGameByHost("Speedball").setTerrain(newTerrain);
				Snowballer.terrainsManager.setOccupied(newTerrain, true);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Snowballer.getInstance(), new Runnable() {
					public void run() {
						if(speedballRunning) {
							if(getGameByHost("Speedball").getWaitingPlayers().size() <= 1) {
								SnowballerMessager.broadcast("[§dSpeed§bball§f] §4Can't start game! There are not enough players!");
								stopGame(getGameByHost("Speedball"));
							} else {
								if(getGameByHost("Speedball").getWaitingTeams().size() > 1) {
									SnowballerMessager.broadcast("[§dSpeed§bball§f] Game has started!");
									getGameByHost("Speedball").setup();
								} else {
									SnowballerMessager.broadcast("[§dSpeed§bball§f] §4Can't start game! All players are in the same team!");
									stopGame(getGameByHost("Speedball"));
								}
							}
						} else
							forceStopGame(getGameByHost("Speedball"));
					}
				}, 200L);
			} else
				forceStopGame(getGameByHost("Speedball"));
		}
	}

	public void stopGame(SnowballerGame game) {
		if(games.contains(game))
			games.remove(game);
		game.end();
	}
	
	public void forceStopGame(SnowballerGame game) {
		if(games.contains(game))
			games.remove(game);
		game.forceEnd();
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
		game.addPlayerToQueue(player, team);
		if(game.getType().equalsIgnoreCase("speedball")) {
			player.setDisplayName(SnowballerMessager.getStringColor(team) + player.getName());
			player.setPlayerListName(player.getDisplayName());
		}
	}
	
	public void playerQuitGameQueue(Player player, SnowballerGame game) {
		if(game.isPlayerAwaiting(player))
			game.removePlayerFromQueue(player);
		player.setDisplayName(player.getName());
		player.setPlayerListName(player.getDisplayName());
	}
	
	public void playerSpectateGame(Player player, SnowballerGame game) {
		playerStopSpectate(player);
		game.addSpectator(player);
		SnowballerVanisher.vanishToPlayersInGame(player, game);
		Location tpLocation = player.getLocation();
		if(! game.getTerrain().hasSpawns("spectator")) {
			tpLocation = game.getTerrain().getRandomSpawnPoint("spectator");
		} else {
			SnowballerMessager.sendMessage(player, "There are no registred spawns for spectators, teleporting to team spawns.");
			tpLocation = game.getTerrain().getRandomSpawnPoint("red");
		}
		player.teleport(tpLocation);
		player.setGameMode(GameMode.CREATIVE);
		game.addSpectator(player);
	}
	
	public void playerStopSpectate(Player player) {
		if(isPlayerSpectator(player)) {
			SnowballerVanisher.appearToPlayersInGame(player, getGameByPlayer(player));
			player.teleport(Snowballer.terrainsManager.getHubSpawn());
			player.setGameMode(GameMode.SURVIVAL);
			getGameByPlayer(player).removeSpectator(player);
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

	public String getPlayerTeamInGame(Player player) {
		return getGameByPlayer(player).getPlayerTeam(player);
	}

	public SnowballerGame getGameByPlayer(Player player) {
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).isPlayerInGame(player))
				return games.get(i);
		for(int i = 0; i < games.size(); ++i)
			if(games.get(i).isPlayerSpectator(player))
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
			SnowballerMessager.broadcast("[§dSpeed§bball§f] Speedball enabled! :)");
			SnowballerTerrain terrain = Snowballer.terrainsManager.getRandomVacantTerrain();
			SnowballerGame speedballGame = new SnowballerGame("Speedball", terrain, "speedball");
			issueGame(speedballGame);
		} else {
			SnowballerMessager.broadcast("§4ERROR:§f Can't start speedball! There are no viable terrains.");
		}
	}

	public void stopSpeedball() {
		if(speedballRunning) {
			speedballRunning = false;
			SnowballerMessager.broadcast("[§dSpeed§bball§f] Speedball disabled. :(");
			if(getGameByHost("Snowballer") != null)
				getGameByHost("Snowballer").forceEnd();
		}
	}

	public boolean isSpeedball() {
		return speedballRunning;
	}
}
