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
import cokoc.translate.PluginHook;
import cokoc.translate.Translate;

import com.google.gson.internal.Pair;

public class SnowCommandExecutor implements CommandExecutor {
	PluginHook t = Translate.getPluginHook(Snowballer.getInstance());
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("pool")) {
			if(! Snowballer.gamesManager.isSpeedball()) {
				SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("SPEEDBALL_NOT_ENABLED"));
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
			if(! Snowballer.configsManager.enableSpectating) {
				SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPECTATING"));
				return true;
			} if(! (sender instanceof Player)) {
				SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
				return true;
			} Player senderPlayer = (Player) sender;
			if(Snowballer.gamesManager.isPlayerInGame(senderPlayer)) {
				SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("CANT_SPECTATE_IN_GAME"));
				return true;
			}
			if(args.length == 1) {
				String gameHost = args[0];
				if(Snowballer.gamesManager.getGameByHost(gameHost) == null) {
					SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_GAME_WITH_HOST"));
					return true;
				} else {
					SnowballerGame game = Snowballer.gamesManager.getGameByHost(gameHost);
					Snowballer.gamesManager.playerSpectateGame(senderPlayer, game);
					Player host = Bukkit.getServer().getPlayer(gameHost);
					SnowballerMessager.sendMessage(senderPlayer, t.s("YOU_HAVE_JOINED") + host.getDisplayName() + t.s("GAME_AS_SPECTATOR"));
				} return true;
			} else {
				if(Snowballer.gamesManager.isPlayerSpectator(senderPlayer)) {
					Snowballer.gamesManager.playerStopSpectate(senderPlayer);
					SnowballerMessager.sendMessage(sender, t.s("NO_LONGER_SPECTATING"));
					return true;
				} if(Snowballer.gamesManager.isSpeedball()) {
					SnowballerGame speedballGame = Snowballer.gamesManager.getGameByHost("Speedball");
					if(speedballGame != null) {
						if(speedballGame.isRunning()) {
							Snowballer.gamesManager.playerSpectateGame(senderPlayer, speedballGame);
							SnowballerMessager.sendMessage(senderPlayer, t.s("JOINED_SPEEDBALL"));
						} else {
							SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPEEDBALL"));
							return true;
						}
					} else {
						SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPEEDBALL"));
						return true;
					}
				} else {
					SnowballerMessager.sendMessage(sender, t.s("ERROR") + t.s("NO_SPEEDBALL_SPECTATE"));
					return true;
				}
			}
			return true;
		} if(command.getName().equalsIgnoreCase("points")) {
			if(! (sender instanceof Player)) {
				SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
				return true;
			} Player senderPlayer = (Player) sender;
			String playerPointsString = t.s("CURRENTLY_HAVE");
			int currentPoints = Snowballer.pointsManager.getPlayerPoints(senderPlayer.getName());
			if(currentPoints == 0)
				playerPointsString = playerPointsString + currentPoints + t.s("POINTS_TIP");
			else
				playerPointsString = playerPointsString + currentPoints + t.s("POINTS");
			SnowballerMessager.sendMessage(senderPlayer, playerPointsString);
		} if(command.getName().equalsIgnoreCase("rank")) {
			if(! (sender instanceof Player)) {
				SnowballerMessager.sendMessage(sender, t.s("NEED_BE_PLAYER"));
				return true;
			} Player senderPlayer = (Player) sender;
			SnowballerMessager.sendMessage(sender, t.s("AT_RANK") + Snowballer.ranksManager.getPlayerRank(senderPlayer) + t.s("PERIOD"));
		}

		return true;
	}
}