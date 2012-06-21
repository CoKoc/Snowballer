package cokoc.snowballer.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.internal.Pair;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.snowballer.game.SnowballerTerrain;
import cokoc.snowballer.utils.TinyLocation;

public class AdminCommandExecutor implements CommandExecutor {
	private boolean hasAtLeastNArgs(String[] args, int minimumArgs) {
		if(args.length >= minimumArgs)
			return true;
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.isOp() || sender.hasPermission("snowballer.admin")) {
			if(command.getName().equalsIgnoreCase("snow")) {
				if(! hasAtLeastNArgs(args, 1)) {
					SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough asd arguments");
					return true;
				}

				String subCommand = args[0];
				if(subCommand.equalsIgnoreCase("createterrain") || subCommand.equalsIgnoreCase("ct")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, "§4ERROR: §fNot enough arguments");
						return true;
					}

					SnowballerTerrain terrain = new SnowballerTerrain(args[1]);
					Snowballer.terrainsManager.addTerrain(terrain);
					SnowballerMessager.sendMessage(sender, "You created a new terrain named " + ChatColor.GREEN + args[1]);
				} if(subCommand.equalsIgnoreCase("deleteterrain") || subCommand.equalsIgnoreCase("dt")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, "§4ERROR: §fNot enough arguments");
						return true;
					}
					String terrainName = args[1];
					if(Snowballer.terrainsManager.hasTerrain(terrainName)) {
						Snowballer.terrainsManager.removeTerrain(terrainName);
						SnowballerMessager.sendMessage(sender, "You deleted terrain " + ChatColor.GREEN + terrainName);
					} else {
						SnowballerMessager.sendMessage(sender, "§4ERROR: §fThis terrain doesn't exist!");
					}
				}if(subCommand.equalsIgnoreCase("createspawn") || subCommand.equalsIgnoreCase("cs")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to use this command.");
						return true;
					} if(! hasAtLeastNArgs(args, 3)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}

					SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
					if(terrain.getName().equalsIgnoreCase("default")) {
						SnowballerMessager.sendMessage(sender, "§4ERROR: §fTerrain §a" + args[1] + "§f doesn't exist!");
						return true;
					}
					String teamColor = args[2];
					Player senderPlayer = (Player) sender;
					terrain.addSpawn(teamColor, senderPlayer.getLocation());
					SnowballerMessager.sendMessage(sender, "You created a new spawnpoint for team " + SnowballerMessager.getColoredString(teamColor) + " at " 
							+ SnowballerMessager.formatLocation(senderPlayer.getLocation()) + " in map §a" + terrain.getName());
				} if(subCommand.equalsIgnoreCase("deletespawn") || subCommand.equalsIgnoreCase("ds")) {
					if(! hasAtLeastNArgs(args, 4)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
					SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
					if(terrain.getName().equalsIgnoreCase("default")) {
						SnowballerMessager.sendMessage(sender, "§4ERROR: §fTerrain §a" + args[1] + "§f doesn't exist!");
						return true;
					}
					String teamColor = args[2];
					int spawnId = Integer.parseInt(args[3]);
					terrain.removeSpawn(teamColor, spawnId);
					SnowballerMessager.sendMessage(sender, "You deleted spawn ID §a" + spawnId + "§f of team " + teamColor + " from terrain §a" + terrain.getName());
				} if(subCommand.equalsIgnoreCase("spawns")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
					if(args.length == 2) {
						SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
						if(terrain.getName().equalsIgnoreCase("default")) {
							SnowballerMessager.sendMessage(sender, "§4ERROR:§f That terrain doesn't exist");
							return true;
						}

						HashMap<String, ArrayList<TinyLocation>> spawns = terrain.getSpawns();

						if(spawns.size() == 0) {
							SnowballerMessager.sendMessage(sender, "There are no spawns on terrain §a" + terrain.getName());
							return true;
						} 

						SnowballerMessager.sendMessage(sender, "The spawns of terrain §a" + terrain.getName() + "§f are: ");
						Iterator<Entry<String, ArrayList<TinyLocation>>> it = spawns.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, ArrayList<TinyLocation>> pairs = (Map.Entry<String, ArrayList<TinyLocation>>) it.next();
							for(int i = 0; i < pairs.getValue().size(); ++i) {
								SnowballerMessager.sendMessage(sender, "Spawn ID §a" + i + "§f for team " 
										+ SnowballerMessager.getColoredString(pairs.getKey()) + " at " + SnowballerMessager.formatLocation(pairs.getValue().get(i).toLocation()));
							}
						}
					} if(args.length > 2) {
						String spawnsListString = new String();
						SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
						if(terrain.getName().equalsIgnoreCase("default")) {
							SnowballerMessager.sendMessage(sender, "§4ERROR:§f That terrain doesn't exist");
							return true;
						}
						ArrayList<TinyLocation> spawns = new ArrayList<TinyLocation>();
						spawns = terrain.getSpawns(args[2]);
						for(int i = 0; i < spawns.size(); ++i) {
							SnowballerMessager.sendMessage(sender, "Spawn ID §a" + i + "§f for team " 
									+ SnowballerMessager.getColoredString(args[2]) + " at " + SnowballerMessager.formatLocation(spawns.get(i).toLocation()));
						}
						SnowballerMessager.sendMessage(sender, spawnsListString);
					} 
				} if(subCommand.equalsIgnoreCase("terrains")) {
					ArrayList<SnowballerTerrain> terrains = Snowballer.terrainsManager.getTerrains();
					ArrayList<Boolean> occupacy = Snowballer.terrainsManager.getOccupacy();

					if(terrains.size() == 0) {
						SnowballerMessager.sendMessage(sender, "There are no registred terrains! Use /snow to administrate Snowballer");
						return true;
					}
					String terrainsListString = "The server's registred terrains are: ";
					for(int i = 0; i < terrains.size(); ++i) {
						terrainsListString = terrainsListString + "§a" + terrains.get(i).getName();
						if(occupacy.get(i))
							terrainsListString = terrainsListString + "§f(§aOCCUPIED§f)";
						if(i != terrains.size()-1)
							terrainsListString = terrainsListString + "§f, "; 
					}

					SnowballerMessager.sendMessage(sender, terrainsListString);
				} if(subCommand.equalsIgnoreCase("sethubspawn") || subCommand.equalsIgnoreCase("hub")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to use this command.");
						return true;
					}
					Player senderPlayer = (Player) sender;
					Location spawnLocation = senderPlayer.getLocation();
					Snowballer.terrainsManager.setHubSpawn(spawnLocation);
					senderPlayer.getWorld().setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
					SnowballerMessager.sendMessage(senderPlayer, "You've set the hub spawn location to " + SnowballerMessager.formatLocation(spawnLocation));
				} if(subCommand.equalsIgnoreCase("info")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
				} if(subCommand.equalsIgnoreCase("speedball")) {
					if(Snowballer.gamesManager.isSpeedball()) {
						Snowballer.gamesManager.stopSpeedball();
					} else {
						Snowballer.gamesManager.startSpeedball();
					}
				} if(subCommand.equalsIgnoreCase("tp")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to use this command.");
						return true;
					}
					Player senderPlayer = (Player) sender;
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
					String terrainName = args[1];
					if(terrainName.equalsIgnoreCase("hub")) {
						Location tpLocation = Snowballer.terrainsManager.getHubSpawn();
						senderPlayer.teleport(tpLocation);
						return true;
					}
					if(! hasAtLeastNArgs(args, 3)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
					String team = args[2];
					if(! Snowballer.terrainsManager.hasTerrain(terrainName)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "There is no terrain by that name!");
						return true;
					}
					SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(terrainName);
					if(! terrain.hasSpawns(team)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "There are no spawns for that team!");
						return true;
					}
					Location tpLocation = terrain.getRandomSpawnPoint(team);
					if(args.length == 4) {
						int spawnId = Integer.parseInt(args[3]);
						if(terrain.hasSpawn(team, spawnId))
							tpLocation = terrain.getSpawn(team, spawnId);
						else {
							SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "There is no spawn with that id!");
							return true;
						}
					}
					senderPlayer.teleport(tpLocation);
				} if(subCommand.equalsIgnoreCase("info")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
						return true;
					}
					String subsubCommand = args[1];

					if(subsubCommand.equalsIgnoreCase("pool")) {
						if(! Snowballer.gamesManager.isSpeedball()) {
							SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "There is no pool because speedball isn't enabled!");
							return true;
						}
						ArrayList<Pair<Player, String>> players = Snowballer.gamesManager.getGameByHost("Speedball").getWaitingPlayers();
						if(players.isEmpty()) {
							SnowballerMessager.sendMessage(sender, "There are no players in the pool.");
						} else {
							String poolString = "The players in the pool are: ";
							for(int i = 0; i < players.size(); ++i) {
								ChatColor teamColor = SnowballerMessager.getStringColor(players.get(i).second);
								poolString = poolString + teamColor + players.get(i).first.getName() + ChatColor.RESET;
								if(i < players.size()-1)
									poolString = poolString + ", ";
							}
							SnowballerMessager.sendMessage(sender, poolString);
						}
					}

					if(subsubCommand.equalsIgnoreCase("player")) {
						if(! hasAtLeastNArgs(args, 3)) {
							SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Not enough arguments");
							return true;
						}
						Player player = Bukkit.getServer().getPlayer(args[2]);
						if(player != null) {
							SnowballerGame game = Snowballer.gamesManager.getGameByPlayer(player);
							if(game != null) {
								int gameId = Snowballer.gamesManager.getGameId(game);
								if(gameId != -1) {
									if(Snowballer.gamesManager.isPlayerSpectator(player)) {
										SnowballerMessager.sendMessage(sender, SnowballerMessager.getStringColor(game.getPlayerTeam(player))
												+ player.getName() + "§f is spectating in game ID " + Snowballer.gamesManager.getGameId(game));
									} else {
										SnowballerMessager.sendMessage(sender, SnowballerMessager.getStringColor(game.getPlayerTeam(player))
												+ player.getName() + "§f is playing in game ID " + Snowballer.gamesManager.getGameId(game));
									}
								} else {
									if(Snowballer.gamesManager.getGameById(gameId).isPlayerAwaiting(player)) {
										ChatColor teamColor = SnowballerMessager.getStringColor(Snowballer.gamesManager.getGameById(gameId).getPlayerTeam(player));
										SnowballerMessager.sendMessage(sender, player.getName() + " is in player pool with team " 
												+ teamColor + Snowballer.gamesManager.getGameById(gameId).getPlayerTeam(player));
									} else {
										SnowballerMessager.sendMessage(sender, player.getName() + " isn't currently in a game.");
									}
								}
							} else {
								SnowballerMessager.sendMessage(sender, player.getName() + " isn't currently in a game.");
							}
						} else {
							SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "That player doesn't exist!");
						}
					} if(subsubCommand.equalsIgnoreCase("games")) {
						ArrayList<SnowballerGame> games = Snowballer.gamesManager.getGames();
						if(games.isEmpty()) {
							SnowballerMessager.sendMessage(sender, "There are no games runnning currently");
							return true;
						}
						SnowballerMessager.sendMessage(sender, "The currently running games are: ");
						for(int i = 0; i < games.size(); ++i) {
							String gameInfoString = "Game ID " + i + ": ";
							ArrayList<Player> players = games.get(i).getPlayers();
							for(int j = 0; j < players.size(); ++j) {
								SnowballerMessager.broadcast("name: " + players.get(j).getName());
								gameInfoString = gameInfoString + SnowballerMessager.getStringColor(games.get(i).getPlayerTeam(players.get(j)))
										+ players.get(j).getName();
								if(j != players.size()-1)
									gameInfoString = gameInfoString + ", ";
							}
							SnowballerMessager.sendMessage(sender, gameInfoString);
						}
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You don't have permissions to use this command.");
		}

		return true;
	}
}
