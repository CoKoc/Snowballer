package cokoc.snowballer.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.translate.PluginHook;
import cokoc.translate.Translate;

public class TeamCommandExecutor implements CommandExecutor {
	PluginHook t = Translate.getPluginHook(Snowballer.getInstance());
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(Snowballer.gamesManager.isSpeedball()) {
				SnowballerGame speedballGame = Snowballer.gamesManager.getGameByHost("Speedball");
				if(command.getName().equalsIgnoreCase("blue")) {
					Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "blue");
					SnowballerMessager.sendMessage(player, t.s("JOINED_THE_SPEEDBALL") + SnowballerMessager.getColoredString("blue") + t.s("TEAM"));
				} if(command.getName().equalsIgnoreCase("red")) {
					Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "red");
					SnowballerMessager.sendMessage(player, t.s("JOINED_THE_SPEEDBALL") + SnowballerMessager.getColoredString("red") + t.s("TEAM"));
				} if(command.getName().equalsIgnoreCase("random")) {
					Random generator = new Random();
					boolean rand = generator.nextBoolean();
					if(rand) {
						Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "blue");
						SnowballerMessager.sendMessage(player, t.s("JOINED_THE_SPEEDBALL") + SnowballerMessager.getColoredString("blue") + t.s("TEAM"));
					} else {
						Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "red");
						SnowballerMessager.sendMessage(player, t.s("JOINED_THE_SPEEDBALL") + SnowballerMessager.getColoredString("red") + t.s("TEAM"));
					}
				} if(command.getName().equalsIgnoreCase("leave")) {
					ArrayList<SnowballerGame> games = Snowballer.gamesManager.getGamesPlayerIsWaitingIn(player);
					for(int i = 0; i < games.size(); ++i)
						Snowballer.gamesManager.playerQuitGameQueue(player, games.get(i));
					SnowballerMessager.sendMessage(player, t.s("LEFT_PLAYER_POOL"));
				}
			} else {
				sender.sendMessage(t.s("ERROR") + t.s("NO_SPEEDBALL"));
			}
		} else {
			sender.sendMessage(t.s("NEED_BE_PLAYER"));
		}

		return true;
	}
}
