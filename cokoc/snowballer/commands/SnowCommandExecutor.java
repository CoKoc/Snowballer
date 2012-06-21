package cokoc.snowballer.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerMessager;

import com.google.gson.internal.Pair;

public class SnowCommandExecutor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("pool")) {
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
		} if(command.getName().equalsIgnoreCase("spectate")) {
			if(! (sender instanceof Player)) {
				SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to run this command.");
				return true;
			} Player senderPlayer = (Player) sender;
			if(Snowballer.gamesManager.isPlayerInGame(senderPlayer)) {
				SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Can't spectate when in game.");
				return true;
			}
			if(args.length == 1) {
				String gameHost = args[0];
				if(Snowballer.gamesManager.getGameByHost(gameHost) == null) {
					SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "There is no game with that host!");
					return true;
				} else {
					SnowballerGame game = Snowballer.gamesManager.getGameByHost(gameHost);
					Snowballer.gamesManager.playerSpectateGame(senderPlayer, game);
					Player host = Bukkit.getServer().getPlayer(gameHost);
					SnowballerMessager.sendMessage(senderPlayer, "You have joined " + host.getDisplayName() + "§f's game as spectator.");
				} return true;
			} else {
				if(Snowballer.gamesManager.isPlayerSpectator(senderPlayer)) {
					Snowballer.gamesManager.playerStopSpectate(senderPlayer);
					SnowballerMessager.sendMessage(sender, "You are no longer spectating any game.");
					return true;
				} if(Snowballer.gamesManager.isSpeedball()) {
					SnowballerGame speedballGame = Snowballer.gamesManager.getGameByHost("Speedball");
					if(speedballGame != null) {
						if(speedballGame.isRunning()) {
							Snowballer.gamesManager.playerSpectateGame(senderPlayer, speedballGame);
							SnowballerMessager.sendMessage(senderPlayer, "You have joined the §dspeed§bball§f game as spectator.");
						} else {
							SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "The speedball game isn't going on right now, try again later.");
							return true;
						}
					} else {
						SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "The speedball game isn't going on right now, try again later.");
						return true;
					}
				} else {
					SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Speedball isn't running, can't spectate it!");
					return true;
				}
			}
			return true;
		} if(command.getName().equalsIgnoreCase("points")) {
			if(! (sender instanceof Player)) {
				SnowballerMessager.sendMessage(sender, ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to run this command.");
				return true;
			} Player senderPlayer = (Player) sender;
			String playerPointsString = "You currently have §a";
			int currentPoints = Snowballer.pointsManager.getPlayerPoints(senderPlayer.getName());
			if(currentPoints == 0)
				playerPointsString = playerPointsString + currentPoints + "§f point. Play a game to win points!";
			else
				playerPointsString = playerPointsString + currentPoints + "§f points.";
			SnowballerMessager.sendMessage(senderPlayer, playerPointsString);
		}

		return true;
	}
}