package cokoc.snowballer.managers;

import java.util.ArrayList;
import java.util.UUID;

import cokoc.snowballer.game.SnowballerShop;

public class SnowballerShopsManager extends SnowballerPersistentManager {
	public ArrayList<SnowballerShop> shops = new ArrayList<SnowballerShop>();
	public SnowballerShopsConfigManager shopsConfig = new SnowballerShopsConfigManager();
			
	public void addShop(SnowballerShop shop) {
		shops.add(shop);
	}
	
	public void removeShop(SnowballerShop shop) {
		shops.remove(shop);
	}
	
	public void removeShop(UUID entityUID) {
		removeShop(getShopById(entityUID));
	}
	
	public void removeShop(int relativeId) {
		if(relativeId < shops.size())
			shops.remove(relativeId);
	}
	
	public boolean isEntityShop(UUID entityUID) {
		for(int i = 0; i < shops.size(); ++i)
			if(shops.get(i).isShop(entityUID))
				return true;
		return false;
	}
	
	public SnowballerShop getShopById(UUID entityUID) {
		for(int i = 0; i < shops.size(); ++i) {
			if(shops.get(i).isShop(entityUID))
				return shops.get(i);
		} return null;
	}
	
	@SuppressWarnings("unchecked")
	public void loadData() {
		shopsConfig.loadConfigs();
		try {
			shops = (ArrayList<SnowballerShop>) load("plugins/Snowballer/shops.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			save(shops, "plugins/Snowballer/shops.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
