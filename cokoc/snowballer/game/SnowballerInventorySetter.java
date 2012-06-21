package cokoc.snowballer.game;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SnowballerInventorySetter {
	public static void setInventory(Player player, String color) {
		player.getInventory().clear();
		if(color.equalsIgnoreCase("default")) {
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
	}
}
