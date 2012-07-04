package cokoc.snowballer.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.gson.internal.Pair;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.snowballer.game.SnowballerShop;
import cokoc.snowballer.game.SnowballerTerrain;
import cokoc.snowballer.utils.Targetter;
import cokoc.snowballer.utils.TinyLocation;
import cokoc.translate.PluginHook;
import cokoc.translate.Translate;

public class AdminCommandExecutor implements CommandExecutor {
	PluginHook t = Translate.getPluginHook(Snowballer.getInstance());

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
					SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
					return true;
				}

				String subCommand = args[0];
				if(subCommand.equalsIgnoreCase("createterrain") || subCommand.equalsIgnoreCase("ct")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}

					SnowballerTerrain terrain = new SnowballerTerrain(args[1]);
					Snowballer.terrainsManager.addTerrain(terrain);
					SnowballerMessager.sendMessage(sender, t.s("CREATED_NEW_TERRAIN") + ChatColor.GREEN + args[1]);
				} if(subCommand.equalsIgnoreCase("deleteterrain") || subCommand.equalsIgnoreCase("dt")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}
					String terrainName = args[1];
					if(Snowballer.terrainsManager.hasTerrain(terrainName)) {
						Snowballer.terrainsManager.removeTerrain(terrainName);
						SnowballerMessager.sendMessage(sender, t.s("DELETED_TERRAIN") + ChatColor.GREEN + terrainName);
					} else {
						SnowballerMessager.sendMessage(sender, t.s("TERRAIN_DOESNT_EXIT"));
					}
				}if(subCommand.equalsIgnoreCase("createspawn") || subCommand.equalsIgnoreCase("cs")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
						return true;
					} if(! hasAtLeastNArgs(args, 3)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}

					SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
					if(terrain.getName().equalsIgnoreCase("default")) {
						SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("TERRAIN_ID") + args[1] + "§f" + t.s("DOESNT_EXIST"));
						return true;
					}
					String teamColor = args[2];
					Player senderPlayer = (Player) sender;
					terrain.addSpawn(teamColor, senderPlayer.getLocation());
					SnowballerMessager.sendMessage(sender, t.s("CREATED_SPAWNPOINT_FOR_TEAM") + SnowballerMessager.getColoredString(teamColor) + t.s("AT") 
							+ SnowballerMessager.formatLocation(senderPlayer.getLocation()) + t.s("IN_MAP") + terrain.getName());
				} if(subCommand.equalsIgnoreCase("deletespawn") || subCommand.equalsIgnoreCase("ds")) {
					if(! hasAtLeastNArgs(args, 4)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					} SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
					if(terrain.getName().equalsIgnoreCase("default")) {
						SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("TERRAIN_ID") + args[1] + "§f" + t.s("DOESNT_EXIST"));
						return true;
					}
					String teamColor = args[2];
					int spawnId = Integer.parseInt(args[3]);
					terrain.removeSpawn(teamColor, spawnId);
					SnowballerMessager.sendMessage(sender, t.s("DELETED_SPAWN_ID") + spawnId + "§f" + t.s("OF_TEAM") + teamColor + t.s("FROM_TERRAIN") + terrain.getName());
				} if(subCommand.equalsIgnoreCase("spawns")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}
					if(args.length == 2) {
						SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
						if(terrain.getName().equalsIgnoreCase("default")) {
							SnowballerMessager.sendMessage(sender, t.s("TERRAIN_DOESNT_EXIST"));
							return true;
						}

						HashMap<String, ArrayList<TinyLocation>> spawns = terrain.getSpawns();

						if(spawns.size() == 0) {
							SnowballerMessager.sendMessage(sender, t.s("NO_SPAWNS_FOR_TERRAIN") + terrain.getName());
							return true;
						} 

						SnowballerMessager.sendMessage(sender, t.s("SPAWNS_OF_TERRAIN") + terrain.getName() + t.s("ARE"));
						Iterator<Entry<String, ArrayList<TinyLocation>>> it = spawns.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, ArrayList<TinyLocation>> pairs = (Map.Entry<String, ArrayList<TinyLocation>>) it.next();
							for(int i = 0; i < pairs.getValue().size(); ++i) {
								SnowballerMessager.sendMessage(sender, t.s("SPAWN_ID") + i + t.s("FOR_TEAM") 
										+ SnowballerMessager.getColoredString(pairs.getKey()) + t.s("AT") + SnowballerMessager.formatLocation(pairs.getValue().get(i).toLocation()));
							}
						}
					} if(args.length > 2) {
						String spawnsListString = new String();
						SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(args[1]);
						if(terrain.getName().equalsIgnoreCase("default")) {
							SnowballerMessager.sendMessage(sender, t.s("TERRAIN_DOESNT_EXIST"));
							return true;
						}
						ArrayList<TinyLocation> spawns = new ArrayList<TinyLocation>();
						spawns = terrain.getSpawns(args[2]);
						for(int i = 0; i < spawns.size(); ++i) {
							SnowballerMessager.sendMessage(sender, t.s("SPAWN_ID") + i + t.s("FOR_TEAM")
									+ SnowballerMessager.getColoredString(args[2]) + " at " + SnowballerMessager.formatLocation(spawns.get(i).toLocation()));
						}
						SnowballerMessager.sendMessage(sender, spawnsListString);
					} 
				} if(subCommand.equalsIgnoreCase("terrains")) {
					ArrayList<SnowballerTerrain> terrains = Snowballer.terrainsManager.getTerrains();
					ArrayList<Boolean> occupacy = Snowballer.terrainsManager.getOccupacy();

					if(terrains.size() == 0) {
						SnowballerMessager.sendMessage(sender, t.s("NO_REGISTERED_TERRAIN"));
						return true;
					}
					String terrainsListString = t.s("REGISTERED_TERRAINS_ARE");
					for(int i = 0; i < terrains.size(); ++i) {
						terrainsListString = terrainsListString + "§a" + terrains.get(i).getName();
						if(occupacy.get(i))
							terrainsListString = terrainsListString + t.s("OCCUPIED");
						if(i != terrains.size()-1)
							terrainsListString = terrainsListString + t.s("COMMA"); 
					}

					SnowballerMessager.sendMessage(sender, terrainsListString);
				} if(subCommand.equalsIgnoreCase("sethubspawn") || subCommand.equalsIgnoreCase("hub")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
						return true;
					} Player senderPlayer = (Player) sender;
					Location spawnLocation = senderPlayer.getLocation();
					Snowballer.terrainsManager.setHubSpawn(spawnLocation);
					SnowballerMessager.sendMessage(senderPlayer, t.s("SET_HUB_SPAWN") + SnowballerMessager.formatLocation(spawnLocation));
				} if(subCommand.equalsIgnoreCase("info")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
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
						SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
						return true;
					} Player senderPlayer = (Player) sender;
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}
					String terrainName = args[1];
					if(terrainName.equalsIgnoreCase("hub")) {
						Location tpLocation = Snowballer.terrainsManager.getHubSpawn();
						senderPlayer.teleport(tpLocation);
						return true;
					}
					if(! hasAtLeastNArgs(args, 3)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					}
					String team = args[2];
					if(! Snowballer.terrainsManager.hasTerrain(terrainName)) {
						SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_TERRAIN_WITH_NAME"));
						return true;
					}
					SnowballerTerrain terrain = Snowballer.terrainsManager.getTerrain(terrainName);
					if(! terrain.hasSpawns(team)) {
						SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPAWNS_FOR_TEAM"));
						return true;
					}
					Location tpLocation = terrain.getRandomSpawnPoint(team);
					if(args.length == 4) {
						int spawnId = Integer.parseInt(args[3]);
						if(terrain.hasSpawn(team, spawnId))
							tpLocation = terrain.getSpawn(team, spawnId);
						else {
							SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPAWN_WITH_ID"));
							return true;
						}
					}
					senderPlayer.teleport(tpLocation);
				} if(subCommand.equalsIgnoreCase("info")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					} String subsubCommand = args[1];
					if(subsubCommand.equalsIgnoreCase("pool")) {
						if(! Snowballer.gamesManager.isSpeedball()) {
							SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("SPEEDBALL_NOT_ENABLED"));
							return true;
						}
						ArrayList<Pair<Player, String>> players = Snowballer.gamesManager.getGameByHost("Speedball").getWaitingPlayers();
						if(players.isEmpty()) {
							SnowballerMessager.sendMessage(sender, t.s("NO_PLAYERS_IN_POOL"));
						} else {
							String poolString = t.s("PLAYERS_IN_POOL_ARE");
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
							SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
							return true;
						} Player player = Bukkit.getServer().getPlayer(args[2]);
						if(player != null) {
							SnowballerGame game = Snowballer.gamesManager.getGameByPlayer(player);
							if(game != null) {
								int gameId = Snowballer.gamesManager.getGameId(game);
								if(gameId != -1) {
									if(Snowballer.gamesManager.isPlayerSpectator(player)) {
										SnowballerMessager.sendMessage(sender, SnowballerMessager.getStringColor(game.getPlayerTeam(player))
												+ player.getName() + t.s("IS_SPECTATING_IN_GAME_ID") + Snowballer.gamesManager.getGameId(game));
									} else {
										SnowballerMessager.sendMessage(sender, SnowballerMessager.getStringColor(game.getPlayerTeam(player))
												+ player.getName() + t.s("IS_PLAYING_IN_GAME_ID") + Snowballer.gamesManager.getGameId(game));
									}
								} else {
									if(Snowballer.gamesManager.getGameById(gameId).isPlayerAwaiting(player)) {
										ChatColor teamColor = SnowballerMessager.getStringColor(Snowballer.gamesManager.getGameById(gameId).getPlayerTeam(player));
										SnowballerMessager.sendMessage(sender, player.getName() + t.s("IS_IN_POOL_WITH") 
												+ teamColor + Snowballer.gamesManager.getGameById(gameId).getPlayerTeam(player));
									} else {
										SnowballerMessager.sendMessage(sender, player.getName() + t.s("ISNT_IN_GAME"));
									}
								}
							} else {
								SnowballerMessager.sendMessage(sender, player.getName() + t.s("ISNT_IN_GAME"));
							}
							int playerPoints = Snowballer.pointsManager.getPlayerPoints(player.getName());
							int playerRank = Snowballer.ranksManager.getPlayerRank(player);
							SnowballerMessager.sendMessage(sender, player.getName() + t.s("HAS") + playerPoints + t.s("POINTS"));
							SnowballerMessager.sendMessage(sender, player.getName() + t.s("IS_AT_RANK") + playerRank + t.s("PERIOD"));
						} else {
							SnowballerMessager.sendMessage(sender, t.s("PLAYER_DOESNT_EXIST"));
						}
					} if(subsubCommand.equalsIgnoreCase("games")) {
						ArrayList<SnowballerGame> games = Snowballer.gamesManager.getGames();
						if(games.isEmpty()) {
							SnowballerMessager.sendMessage(sender, t.s("NO_CURRENT_GAME"));
							return true;
						}
						SnowballerMessager.sendMessage(sender, t.s("CURRENT_RUNNING_GAMES_ARE"));
						for(int i = 0; i < games.size(); ++i) {
							String gameInfoString = t.s("GAME_ID") + i + ": ";
							ArrayList<Player> players = games.get(i).getPlayers();
							for(int j = 0; j < players.size(); ++j) {
								SnowballerMessager.broadcast(t.s("NAME_") + players.get(j).getName());
								gameInfoString = gameInfoString + SnowballerMessager.getStringColor(games.get(i).getPlayerTeam(players.get(j)))
										+ players.get(j).getName();
								if(j != players.size()-1)
									gameInfoString = gameInfoString + ", ";
							} SnowballerMessager.sendMessage(sender, gameInfoString);
						}
					}
				} if(subCommand.equalsIgnoreCase("shop")) {
					if(! (sender instanceof Player)) {
						SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
						return true;
					} Player senderPlayer = (Player) sender;
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					} String subsubCommand = args[1];
					if(subsubCommand.equalsIgnoreCase("create")) {
						Entity entity = Targetter.getEntityTarget(senderPlayer);
						if(entity == null) {
							SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("DIDNT_TARGET_ENTITY"));
							return true;
						} UUID entityId = entity.getUniqueId();
						if(! Snowballer.shopsManager.isEntityShop(entityId)) {
							SnowballerShop shop = new SnowballerShop(entityId);
							Snowballer.shopsManager.addShop(shop);
							SnowballerMessager.sendMessage(sender, t.s("CREATED_SHOP_AT") + SnowballerMessager.formatLocation(entity.getLocation()) + " §f!");
						} else
							SnowballerMessager.sendMessage(sender, t.s("ENTITY_ALREADY_SHOP"));
					} if(subsubCommand.equalsIgnoreCase("delete")) {
						if(hasAtLeastNArgs(args, 3)) {
							int entityId = Integer.parseInt(args[2]);
							if(entityId < Snowballer.shopsManager.shops.size()) {
								Snowballer.shopsManager.removeShop(entityId);
							} else
								SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SHOP_WITH_ID"));
						} else {
							if(Targetter.getEntityTarget(senderPlayer) != null) {
								UUID entityId = Targetter.getEntityTarget(senderPlayer).getUniqueId();
								if(Snowballer.shopsManager.isEntityShop(entityId)) {
									Snowballer.shopsManager.removeShop(entityId);
									SnowballerMessager.sendMessage(sender, t.s("DELETED_SHOP") + entityId + "§f !");
								} else {
									SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + t.s("NOT_A_SHOP"));
								}
							} else
								SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						}
					} if(subsubCommand.equalsIgnoreCase("list")) {
						ArrayList<SnowballerShop> shops = Snowballer.shopsManager.shops;
						if(shops.size() == 0) {
							SnowballerMessager.sendMessage(senderPlayer, t.s("NO_REGISTERED_SHOP"));
							return true;
						} SnowballerMessager.sendMessage(senderPlayer, t.s("CURRENT_SHOPS_ARE"));
						for(int i = 0; i < shops.size(); ++i) {
							int relativeId = i;
							List<Entity> entities = senderPlayer.getWorld().getEntities();
							Location entityLocation = senderPlayer.getLocation();
							for(int j = 0; j < entities.size(); ++j)
								if(entities.get(j).getUniqueId().equals(shops.get(i).entityUID))
									entityLocation = entities.get(j).getLocation();
							SnowballerMessager.sendMessage(senderPlayer, t.s("SHOP_ID") + relativeId + t.s("AT") + SnowballerMessager.formatLocation(entityLocation));
						}
					}
				} if(subCommand.equalsIgnoreCase("set")) {
					if(! hasAtLeastNArgs(args, 2)) {
						SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
						return true;
					} String subsubCommand = args[1];
					if(subsubCommand.equalsIgnoreCase("points")) {
						if(! hasAtLeastNArgs(args, 4)) {
							SnowballerMessager.sendMessage(sender, t.s("TOO_FEW_ARGUMENTS"));
							return true;
						} String playerName = args[2];
						int numberOfPoints = Integer.parseInt(args[3]);
						Snowballer.pointsManager.playerPoints.put(playerName, numberOfPoints);
						SnowballerMessager.sendMessage(sender, t.s("YOU_SET") + playerName + t.s("POINTS_TO") + numberOfPoints);
					} if(subsubCommand.equalsIgnoreCase("rank")) {
						if(! hasAtLeastNArgs(args, 4)) {
							SnowballerMessager.sendMessage(sender, t.s("YOO_FEW_ARGUMENTS"));
							return true;
						} String playerName = args[2];
						int rank = Integer.parseInt(args[3]);
						Snowballer.ranksManager.setRank(playerName, rank);
						SnowballerMessager.sendMessage(sender, t.s("YOU_SET") + playerName + t.s("RANK_TO") + rank);
					}
				}
			}
		} else {
			sender.sendMessage(t.s("NOT_ENOUGH_PERMISSIONS"));
		}

		return true;
	}
}
