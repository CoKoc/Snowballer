package cokoc.snowballer.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SnowballerMessager {
	private static String header = ChatColor.GOLD + "[" + ChatColor.AQUA + "Snow" + ChatColor.DARK_AQUA + "baller" + ChatColor.GOLD + "] ";

	public static void sendMessage(CommandSender target, String message) {
		String messageBuffer = header + ChatColor.RESET + message;
		target.sendMessage(messageBuffer);
	}

	public static void  broadcast(String message) {
		String messageBuffer = header + ChatColor.RESET + message;
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; ++i) {
			if(players[i].isOp())
				players[i].sendMessage(messageBuffer);
			if(!players[i].hasPermission("snowballer.nomessage"))
				if(! players[i].isOp())
					players[i].sendMessage(messageBuffer);
		}
	}

	public static void announceToGame(SnowballerGame game, String announcement) {
		String message = header + ChatColor.RESET + announcement;
		ArrayList<Player> playersInGame = game.getPlayers();
		for(int i = 0; i < playersInGame.size(); ++i)
			playersInGame.get(i).sendMessage(message);
		ArrayList<Player> spectatorsInGame = game.getSpectators();
		for(int i = 0; i < spectatorsInGame.size(); ++i)
			spectatorsInGame.get(i).sendMessage(message);
	}

	public static String getColoredString(String colorName) {
		if(colorName.equalsIgnoreCase("blue"))
			return "§1blue§f";
		if(colorName.equalsIgnoreCase("red"))
			return "§4red§f";
		return "default";
	}

	public static ChatColor getStringColor(String colorName) {
		if(colorName.equalsIgnoreCase("blue"))
			return ChatColor.DARK_BLUE;
		if(colorName.equalsIgnoreCase("red"))
			return ChatColor.DARK_RED;
		return ChatColor.RESET;
	}

	public static String formatLocation(Location location) {
		return "§f(§a" + location.getBlockX() + "§f, §a" + location.getBlockY() + "§f, §a" + location.getBlockZ() + "§f)";
	}
}
