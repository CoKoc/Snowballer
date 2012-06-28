package cokoc.snowballer.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import cokoc.snowballer.Snowballer;
import cokoc.snowballer.game.SnowballerInventorySetter;

public class SnowballerShopPlayerListener implements Listener {
	// Hardcoded.
	public String getBenefitText(String benefitKey) {
		if(benefitKey.contains("snowballs")) {
			String numberOfSnowballsString = benefitKey.split(" ")[1];
			if(numberOfSnowballsString == null)
				return "(unimplemented key)";
			int numberOfSnowballs = Integer.parseInt(numberOfSnowballsString);
			return (numberOfSnowballs + " snowballs");
		}

		if(benefitKey.equalsIgnoreCase("DIAMOND_HELMET"))
			return "a diamond helmet";
		if(benefitKey.equalsIgnoreCase("DIAMOND_CHESTPLATE"))
			return "a diamond chestplate";
		if(benefitKey.equalsIgnoreCase("DIAMOND_LEGGINGS"))
			return "a pair of diamond leggings";
		if(benefitKey.equalsIgnoreCase("DIAMOND_BOOTS"))
			return "a pair of diamond boots";
		if(benefitKey.equalsIgnoreCase("GOLD_HELMET"))
			return "a golden helmet";
		if(benefitKey.equalsIgnoreCase("GOLD_CHESTPLATE"))
			return "a golden chestplate";
		if(benefitKey.equalsIgnoreCase("GOLD_LEGGINGS"))
			return "a pair of golden leggings";
		if(benefitKey.equalsIgnoreCase("GOLD_BOOTS"))
			return "a pair of golden boots";
		if(benefitKey.equalsIgnoreCase("CHAINMAIL_HELMET"))
			return "a chainmail helmet";
		if(benefitKey.equalsIgnoreCase("CHAINMAIL_CHESTPLATE"))
			return "a chainmail chestplate";
		if(benefitKey.equalsIgnoreCase("CHAINMAIL_LEGGINGS"))
			return "a pair of chainmail leggings";
		if(benefitKey.equalsIgnoreCase("CHAINMAIL_BOOTS"))
			return "a pair of chainmail boots";
		if(benefitKey.equalsIgnoreCase("IRON_HELMET"))
			return "an iron helmet";
		if(benefitKey.equalsIgnoreCase("IRON_CHESTPLATE"))
			return "an iron chestplate";
		if(benefitKey.equalsIgnoreCase("IRON_LEGGINGS"))
			return "a pair of iron leggings";
		if(benefitKey.equalsIgnoreCase("IRON_BOOTS"))
			return "a pair of iron boots";
		if(benefitKey.equalsIgnoreCase("LEATHER_HELMET"))
			return "a leather helmet";
		if(benefitKey.equalsIgnoreCase("LEATHER_CHESTPLATE"))
			return "a leather hat";
		if(benefitKey.equalsIgnoreCase("LEATHER_LEGGINGS"))
			return "a pair of leather leggings";
		if(benefitKey.equalsIgnoreCase("LEATHER_BOOTS"))
			return "a pair of leather boots";
		return "(unimplemented key)";
	}

	@EventHandler
	public void onPlayerClickShop(PlayerInteractEntityEvent event) {
		UUID entityId = event.getRightClicked().getUniqueId();
		if(Snowballer.shopsManager.isEntityShop(entityId)) {
			Player player = event.getPlayer();
			int currentPlayerPoints = Snowballer.pointsManager.getPlayerPoints(player.getName());
			int currentPlayerRank = Snowballer.ranksManager.getPlayerRank(player);
			int currentPointObjective = Snowballer.shopsManager.shopsConfig.getRankPrice(currentPlayerRank+1);
			if(currentPlayerRank < Snowballer.shopsManager.shopsConfig.getNumberOfRanks()) {
				if(currentPlayerPoints >= currentPointObjective) {
					Snowballer.pointsManager.addPointsToPlayer(player.getName(), -1 * currentPointObjective);
					Snowballer.ranksManager.upgradePlayer(player);
					SnowballerInventorySetter.setInventory(player, "default");
					String playerBenefitsMessage = "Congrats! You now have ";
					List<String> benefits = Snowballer.shopsManager.shopsConfig.getBenefits(currentPlayerRank+1);
					if(benefits == null)
						return;
					if(benefits.size() == 0)
						player.sendMessage("Sorry, you don't get anything from ranking up.");
					for(int i = 0; i < benefits.size(); ++i) {
						playerBenefitsMessage = playerBenefitsMessage + "§a" + getBenefitText(benefits.get(i)) + "§f";
						if(i < benefits.size()-2)
							playerBenefitsMessage = playerBenefitsMessage + ", ";
						if(i == benefits.size()-2)
							playerBenefitsMessage = playerBenefitsMessage + " and ";
						if(i == benefits.size()-1)
							playerBenefitsMessage = playerBenefitsMessage + "!";
					} player.sendMessage(playerBenefitsMessage);
				} else {
					player.sendMessage("Sadly, you don't have enough points for an upgrade.");
					player.sendMessage("You currently have §a" + currentPlayerPoints + "§f points, but you need §c" + currentPointObjective + "§f.");
					String playerBenefitsMessage = "Next rank, you'll have access to ";
					List<String> benefits = Snowballer.shopsManager.shopsConfig.getBenefits(currentPlayerRank+1);
					if(benefits.size() == 0)
						player.sendMessage("Sorry, you don't get anything from ranking up.");
					for(int i = 0; i < benefits.size(); ++i) {
						playerBenefitsMessage = playerBenefitsMessage + "§a" + getBenefitText(benefits.get(i)) + "§f";
						if(i < benefits.size()-2)
							playerBenefitsMessage = playerBenefitsMessage + ", ";
						if(i == benefits.size()-2)
							playerBenefitsMessage = playerBenefitsMessage + " and ";
					} player.sendMessage(playerBenefitsMessage);
				}
			} else {
				player.sendMessage("You're at maximum rank. Congrats! <3");
			}
		}
	}
}
