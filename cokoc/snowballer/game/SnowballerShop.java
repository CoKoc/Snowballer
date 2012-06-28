package cokoc.snowballer.game;

import java.io.Serializable;
import java.util.UUID;

public class SnowballerShop implements Serializable {
	private static final long serialVersionUID = 8336408868316842012L;
	public UUID entityUID;
	
	public SnowballerShop(UUID entityUID) {
		this.entityUID = entityUID;
	}
	
	public boolean isShop(UUID entityUID) {
		if(this.entityUID.equals(entityUID))
			return true;
		return false;
	}
}
