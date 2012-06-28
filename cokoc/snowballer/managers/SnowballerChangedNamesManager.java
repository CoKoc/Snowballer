package cokoc.snowballer.managers;

import java.util.HashMap;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import cokoc.snowballer.Snowballer;

public class SnowballerChangedNamesManager {
	public static HashMap<String, String> changedNames = new HashMap<String, String>();

	public static void setPlayerDisplayName(Player player, String name) {
		player.setDisplayName(name + ChatColor.RESET);
		setPlayerListName(player);
		if(Snowballer.configsManager.changeNamePlates)
			setNamePlate(player, player.getDisplayName() + ChatColor.RESET);
	}

	private static void setNamePlate(Player player, String plateName) {
		Location loc;
		Location myLoc = player.getLocation();
		double myX = myLoc.getX();
		double myZ = myLoc.getZ();
		String myWorld = myLoc.getWorld().getName();
		double d0;
		double d1;

		StringBuilder stringBuilder = new StringBuilder(plateName);
		if(stringBuilder.length() > 16) {
			stringBuilder.delete(14, stringBuilder.length());
			stringBuilder.append("§f");
		}
		String newName = stringBuilder.toString();
		changedNames.put(player.getName(), newName);

		EntityHuman e = ((CraftPlayer) player).getHandle();
		Packet29DestroyEntity packet29 = new Packet29DestroyEntity(e.id);
		Packet20NamedEntitySpawn packet20 = new Packet20NamedEntitySpawn(e);
		packet20.b = newName;

		NetServerHandler ns;
		for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
			if(pl.getName().equals(player.getName()))
				continue;
			loc = pl.getLocation();
			if(!myWorld.equals(loc.getWorld().getName()))
				continue;
			d0 = loc.getX() - (double) (myX / 32);
			d1 = loc.getZ() - (double) (myZ / 32);
			if(d0 >= -512.0D && d0 <= 512.0D && d1 >= -512.0D && d1 <= 512.0D) {
				ns = ((CraftPlayer)pl).getHandle().netServerHandler;
				ns.sendPacket(packet29);
				ns.sendPacket(packet20);
			}
		}
	}

	private static void setPlayerListName(Player player) {
		String playerName = player.getDisplayName();
		if(playerName.length() > 16)
			playerName = playerName.substring(0, 15);
		player.setPlayerListName(playerName);
	}
}
