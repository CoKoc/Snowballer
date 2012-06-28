package cokoc.snowballer.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Targetter {
	public static Entity getEntityTarget(Player player) {
		List<Entity> nearbyEntities = player.getNearbyEntities(10, 7, 10);
		ArrayList<LivingEntity> livingEntities = new ArrayList<LivingEntity>();

		for (Entity e : nearbyEntities) {
			if (e instanceof LivingEntity) {
				livingEntities.add((LivingEntity) e);
			}
		}

		BlockIterator bItr = new BlockIterator(player, 10);
		Block currentBlock;
		Location currentBlockLocation;
		int bx, by, bz;
		double x, y, z;
		
		while (bItr.hasNext()) {
			currentBlock = bItr.next();
			bx = currentBlock.getX();
			by = currentBlock.getY();
			bz = currentBlock.getZ();
			for (LivingEntity currentEntity : livingEntities) {
				currentBlockLocation = currentEntity.getLocation();
				x = currentBlockLocation.getX();
				y = currentBlockLocation.getY();
				z = currentBlockLocation.getZ();
				if ((bx-.75 <= x && x <= bx+1.75) && (bz-.75 <= z && z <= bz+1.75) && (by-1 <= y && y <= by+2.5))
					return currentEntity;
			}
		} return null;
	}
}
