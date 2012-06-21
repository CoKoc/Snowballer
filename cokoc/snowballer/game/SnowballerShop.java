package cokoc.snowballer.game;

import java.io.Serializable;

import org.bukkit.block.Block;

import cokoc.snowballer.utils.TinyLocation;

public class SnowballerShop implements Serializable {
	private static final long serialVersionUID = 8336408868316842012L;
	
	public int entityUID;
	public TinyLocation blockLocation;
	public String type;
	
	public SnowballerShop(int entityUID) {
		this.entityUID = entityUID;
		this.type = "entity";
	}
	
	public SnowballerShop(Block block) {
		this.blockLocation = new TinyLocation(block.getLocation());
		this.type = "block";
	}
	
	
}
