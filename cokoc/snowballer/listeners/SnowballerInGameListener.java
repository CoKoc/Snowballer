package cokoc.snowballer.listeners;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerGame;
import cokoc.snowballer.game.SnowballerInventorySetter;
import cokoc.snowballer.game.SnowballerMessager;
import cokoc.snowballer.game.SnowballerVanisher;
import cokoc.snowballer.managers.SnowballerChangedNamesManager;
import cokoc.snowballer.managers.SnowballerGamesManager;
import cokoc.translate.PluginHook;
import cokoc.translate.Translate;

public class SnowballerInGameListener implements Listener {
	PluginHook t = Translate.getPluginHook(Snowballer.getInstance());
	@EventHandler(ignoreCancelled = true)
	public void onSnowballHit(EntityDamageByEntityEvent event) {
		if(! event.getCause().equals(DamageCause.PROJECTILE))
			return;

		if(! event.getDamager().getType().equals(EntityType.SNOWBALL))
			return;

		Snowball snowball = (Snowball) event.getDamager();
		if(! (snowball.getShooter() instanceof Player))
			return;

		if(! (event.getEntity() instanceof Player))
			return;

		Player targetPlayer = (Player) event.getEntity();
		Player shooterPlayer = (Player) snowball.getShooter();

		if(! Snowballer.gamesManager.isPlayerInGame(targetPlayer))
			return;
		if(! Snowballer.gamesManager.isPlayerInGame(shooterPlayer))
			return;

		SnowballerGamesManager gamesManager = Snowballer.gamesManager;

		if(! gamesManager.getGameByPlayer(targetPlayer).equals(gamesManager.getGameByPlayer(shooterPlayer)))
			return;

		SnowballerGame game = gamesManager.getGameByPlayer(targetPlayer);

		if(! Snowballer.configsManager.friendlyFire)
			if(game.getPlayerTeam(shooterPlayer).equals(game.getPlayerTeam(targetPlayer)))
				return;

		game.killPlayer(targetPlayer, shooterPlayer);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(Snowballer.gamesManager.isPlayerInGame(player))
				event.setDamage(0);
			else
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeathInGame(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if(Snowballer.gamesManager.isPlayerInGame(player))
			Snowballer.gamesManager.getGameByPlayer(player).killPlayer(player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ArrayList<SnowballerGame> games = Snowballer.gamesManager.getGamesPlayerIsWaitingIn(player);
		for(int i = 0; i < games.size(); ++i)
			Snowballer.gamesManager.playerQuitGameQueue(player, games.get(i));
		if(Snowballer.gamesManager.isPlayerInGame(player)) {
			SnowballerGame game = Snowballer.gamesManager.getGameByPlayer(player);
			game.removePlayer(player);
			game.checkWin();
		} if(Snowballer.gamesManager.isPlayerSpectator(player))
			Snowballer.gamesManager.getGameByPlayer(player).removeSpectator(player);
		SnowballerInventorySetter.setInventory(player, "default");
		SnowballerChangedNamesManager.setPlayerDisplayName(player, player.getName());
		SnowballerVanisher.playerSeeAllPlayers(player);
		player.teleport(Snowballer.terrainsManager.getHubSpawn());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
		if(Snowballer.configsManager.invinciblePlayers)
			if(event.getEntity() instanceof Player)
				event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropSnowball(PlayerDropItemEvent event) {
		if(Snowballer.gamesManager.isPlayerInGame(event.getPlayer())) {
			if(event.getItemDrop().getItemStack().getType().equals(Material.SNOW_BALL)) {
				SnowballerMessager.sendMessage(event.getPlayer(), t.s("CANT_DROP_SNOWBALLS"));
				event.setCancelled(true);
			}	
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerThrowWhileSpectating(ProjectileLaunchEvent event) {
		if(event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();
			if(Snowballer.gamesManager.isPlayerSpectator(player))
				event.setCancelled(true);
		}
	}
}
