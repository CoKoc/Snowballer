package cokoc.snowballer.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class SnowballerVanisher {
	public static void vanishToPlayersInGame(Player player, SnowballerGame game) {
		ArrayList<Player> players = game.getPlayers();
		for(int i = 0; i < players.size(); ++i) {
			players.get(i).hidePlayer(player);
		}
	}
	
	public static void appearToPlayersInGame(Player player, SnowballerGame game) {
		ArrayList<Player> players = game.getPlayers();
		for(int i = 0; i < players.size(); ++i) {
			players.get(i).showPlayer(player);
		}
	}
	
	public static void playerSeePlayers(Player player, ArrayList<Player> players) {
		for(int i = 0; i < players.size(); ++i) {
			player.showPlayer(players.get(i));
		}
	}
}
