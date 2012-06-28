package cokoc.snowballer.game;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.managers.SnowballerShopsConfigManager;

public class SnowballerInventorySetter {
	private static int getNumberOfSnowballsByRank(int rank) {
		SnowballerShopsConfigManager shopsConfig = Snowballer.shopsManager.shopsConfig;
		for(int i = rank; i >= 0; --i) {
			List<String> benefits = shopsConfig.getBenefits(i);
			if(benefits != null) {
				for(int j = 0; j < benefits.size(); ++j)
					if(benefits.get(j).contains("snowballs")) {
						String numberOfSnowballsString = benefits.get(j).split(" ")[1];
						if(numberOfSnowballsString == null)
							return -1;
						int numberOfSnowballs = Integer.parseInt(numberOfSnowballsString);
						return numberOfSnowballs;
					}
			} else
				return -1;
		} return 16;
	}

	public static void setInventory(Player player, String color) {
		player.getInventory().clear();
		SnowballerShopsConfigManager shopsConfig = Snowballer.shopsManager.shopsConfig;
		int playerRank = Snowballer.ranksManager.getPlayerRank(player);
		for(int i = 0; i <= playerRank; ++i) {
			List<String> benefits = shopsConfig.getBenefits(i);
			if(benefits != null) {
				for(int j = 0; j < benefits.size(); ++j) {
					Material mat = Material.getMaterial(benefits.get(j));
					if(mat != null) {
						ItemStack item = new ItemStack(mat, 1);
						if(benefits.get(j).contains("HELMET"))
							player.getInventory().setHelmet(item);
						if(benefits.get(j).contains("CHESTPLATE"))
							player.getInventory().setChestplate(item);
						if(benefits.get(j).contains("LEGGINGS"))
							player.getInventory().setLeggings(item);
						if(benefits.get(j).contains("BOOTS"))
							player.getInventory().setBoots(item);
					}
				}
			}
		}

		if(color.equalsIgnoreCase("default")) {
			ItemStack headGear = new ItemStack(Material.AIR, 1);
			player.getInventory().setHelmet(headGear);
			return;
		}

		int numberOfSnowballs = getNumberOfSnowballsByRank(playerRank);
		if(numberOfSnowballs == -1) {
			SnowballerMessager.sendMessage(player, "There has been a problem with your snowballs delivery! D:");
			numberOfSnowballs = 16;
		}
		int numberOfStacks = numberOfSnowballs / 16;
		int remainders = numberOfSnowballs % 16;
		ItemStack remindingSnowballs = new ItemStack(Material.SNOW_BALL, remainders);
		player.getInventory().addItem(remindingSnowballs);
		ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 16);
		for(int i = 0; i < numberOfStacks; ++i)
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
