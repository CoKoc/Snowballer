package cokoc.snowballer.thirdpartycode;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;

/* This code is originating from the Internet. Thanks to the original author
 * and anyone who may have edited it! 
 */
public class SnowballerEntityTracker extends EntityTracker {
	private final Field worldField;
	private final Field dField;
	private final Field aField;

	public SnowballerEntityTracker(MinecraftServer minecraftserver, World world) {
		super(minecraftserver, world);
		Field worldField;
		Field dField;
		Field aField;
		try {
			worldField = EntityTracker.class.getDeclaredField("world");
			dField = EntityTracker.class.getDeclaredField("d");
			aField = EntityTracker.class.getDeclaredField("a");
			worldField.setAccessible(true);
			dField.setAccessible(true);
			aField.setAccessible(true);
		} catch(Exception e) {
			worldField = null;
			dField = null;
			aField = null;
			e.printStackTrace();
		}
		this.worldField = worldField;
		this.dField = dField;
		this.aField = aField;
	}

	@SuppressWarnings("unchecked")
	public synchronized void addEntity(Entity entity, int i, int j, boolean flag) {
		int d = 512;
		try {
			d = dField.getInt(this);
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(i > d)
			i = d;

		if(!this.trackedEntities.b(entity.id)) {
			SnowballerEntityEntry entityTrackerEntry = new SnowballerEntityEntry(entity, i, j, flag);

			Set<SnowballerEntityEntry> a = null;
			World world = null;
			try {
				a = (Set<SnowballerEntityEntry>) aField.get(this);
				world = (World) worldField.get(this);
			} catch(Exception e) {
				e.printStackTrace();
			}
			a.add(entityTrackerEntry);
			this.trackedEntities.a(entity.id, entityTrackerEntry);
			entityTrackerEntry.scanPlayers(world.players);
		}
	}
}
