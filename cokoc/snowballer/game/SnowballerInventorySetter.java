package cokoc.snowballer.game;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SnowballerInventorySetter {
	public static void setInventory(Player player, String color) {
		player.getInventory().clear();
		if(color.equalsIgnoreCase("default")) {
			colorNamePlate(player, color);
			ItemStack headGear = new ItemStack(Material.AIR, 1);
			player.getInventory().setHelmet(headGear);
			return;
		}
		
		ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 16);
		for(int i = 0; i < 4; ++i)
			player.getInventory().addItem(snowballs);
		short colorId = 0;
		if(color.equalsIgnoreCase("blue"))
			colorId = (short) 11;
		if(color.equalsIgnoreCase("red"))
			colorId = (short) 14;
		ItemStack headGear = new ItemStack(Material.WOOL, 1, colorId);
		player.getInventory().setHelmet(headGear);
		colorNamePlate(player, color);
	}
	
	private static void colorNamePlate(Player p, String teamColor) {
		Location loc;
		Location myLoc = p.getLocation();
		double myX = myLoc.getX();
		double myZ = myLoc.getZ();
		String myWorld = myLoc.getWorld().getName();
		double d0;
		double d1;

		EntityHuman e = ((CraftPlayer)p).getHandle();

		Packet29DestroyEntity packet29 = new Packet29DestroyEntity(e.id);
		Packet20NamedEntitySpawn packet20 = new Packet20NamedEntitySpawn(e);
		
		char colorId = 0;
		if(teamColor.equalsIgnoreCase("blue"))
			colorId = '1';
		if(teamColor.equalsIgnoreCase("red"))
			colorId = '4';
		if(teamColor.equalsIgnoreCase("default")) 
			colorId = 'f';
		
		String name = p.getName();
		if(name.length() > 14)
			name = name.substring(0, 8);
		
		packet20.b = ChatColor.getByChar(colorId) + name;

		NetServerHandler ns;

		for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
			if(pl.getName().equals(p.getName()))
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
}
