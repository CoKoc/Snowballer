package cokoc.snowballer.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerMessager;

public class TeamCommandExecutor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(Snowballer.gamesManager.isSpeedball()) {
				SnowballerGame speedballGame = Snowballer.gamesManager.getGameByHost("Speedball");
				if(command.getName().equalsIgnoreCase("blue")) {
					Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "blue");
					SnowballerMessager.sendMessage(player, "You joined the §dspeed§bball§f's " + SnowballerMessager.getColoredString("blue") + " team.");
				} if(command.getName().equalsIgnoreCase("red")) {
					Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "red");
					SnowballerMessager.sendMessage(player, "You joined the §dspeed§bball§f's " + SnowballerMessager.getColoredString("red") + " team.");
				} if(command.getName().equalsIgnoreCase("random")) {
					Random generator = new Random();
					boolean rand = generator.nextBoolean();
					if(rand) {
						Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "blue");
						SnowballerMessager.sendMessage(player, "You joined the §dspeed§bball§f's " + SnowballerMessager.getColoredString("blue") + " team.");
					} else {
						Snowballer.gamesManager.playerJoinGameQueue(player, speedballGame, "red");
						SnowballerMessager.sendMessage(player, "You joined the §dspeed§bball§f's " + SnowballerMessager.getColoredString("red") + " team.");
					}
				} if(command.getName().equalsIgnoreCase("leave")) {
					ArrayList<SnowballerGame> games = Snowballer.gamesManager.getGamesPlayerIsWaitingIn(player);
					for(int i = 0; i < games.size(); ++i)
						Snowballer.gamesManager.playerQuitGameQueue(player, games.get(i));
					SnowballerMessager.sendMessage(player, "You left the player pool");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "Speedball isn't active.");
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "ERROR: " + ChatColor.RESET + "You need to be a player to use this command.");
		}

		return true;
	}
}
