package cokoc.snowballer.thirdpartycode;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.bukkit.entity.Player;

import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityEgg;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityEnderPearl;
import net.minecraft.server.EntityEnderSignal;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntitySmallFireball;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityThrownExpBottle;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.IAnimal;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet17EntityLocationAction;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet26AddExpOrb;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet5EntityEquipment;

public class SnowballerEntityEntry extends EntityTrackerEntry {
	private final Field f;

	public SnowballerEntityEntry(Entity entity, int i, int j, boolean flag) {
		super(entity, i, j, flag);
		Field f;
		try {
			f = EntityTrackerEntry.class.getDeclaredField("isMoving");
		} catch(Exception e) {
			f = null;
			e.printStackTrace();
		}
		this.f = f;
		this.f.setAccessible(true);
	}

	public void updatePlayer(EntityPlayer entityplayer) {
		if(entityplayer != this.tracker) {
			double d0 = entityplayer.locX - (double) (this.xLoc / 32);
			double d1 = entityplayer.locZ - (double) (this.zLoc / 32);

			if(d0 >= (double) (-this.b) && d0 <= (double) this.b && d1 >= (double) (-this.b) && d1 <= (double) this.b) {
				if(!this.trackedPlayers.contains(entityplayer)) {
					if (tracker instanceof EntityPlayer) {
						Player player = ((EntityPlayer) tracker).getBukkitEntity();
						if(!entityplayer.getBukkitEntity().canSee(player)) {
							return;
						}
					}
					
					entityplayer.netServerHandler.sendPacket(this.b());
					try {
						if(f.getBoolean(this))
							entityplayer.netServerHandler.sendPacket(new Packet28EntityVelocity(this.tracker.id, this.tracker.motX, this.tracker.motY, this.tracker.motZ));
					} catch(Exception e) {
						e.printStackTrace();
					}

					ItemStack[] aitemstack = this.tracker.getEquipment();

					if(aitemstack != null)
						for(int i = 0; i < aitemstack.length; ++i)
							entityplayer.netServerHandler.sendPacket(new Packet5EntityEquipment(this.tracker.id, i, aitemstack[i]));

					if(this.tracker instanceof EntityHuman) {
						EntityHuman entityhuman = (EntityHuman)this.tracker;

						if(entityhuman.isSleeping())
							entityplayer.netServerHandler.sendPacket(new Packet17EntityLocationAction(this.tracker, 0, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
						if(entityhuman.isSneaking())
							entityplayer.netServerHandler.sendPacket(new Packet17EntityLocationAction(this.tracker, 0, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
					}

					if(this.tracker instanceof EntityLiving) {
						EntityLiving entityliving = (EntityLiving) this.tracker;
						@SuppressWarnings("rawtypes")
						Iterator iterator = entityliving.getEffects().iterator();

						while(iterator.hasNext()) {
							MobEffect mobeffect = (MobEffect) iterator.next();

							entityplayer.netServerHandler.sendPacket(new Packet41MobEffect(this.tracker.id, mobeffect));
						}
					}
				}
			} else if(this.trackedPlayers.contains(entityplayer)) {
				this.trackedPlayers.remove(entityplayer);
				entityplayer.netServerHandler.sendPacket(new Packet29DestroyEntity(this.tracker.id));
			}
		}
	}

	private Packet b() {
		if(this.tracker.dead)
			System.out.println("Fetching addPacket for removed entity: " + this.tracker.getBukkitEntity().toString());

		if(this.tracker instanceof EntityItem) {
			EntityItem entityitem = (EntityItem) this.tracker;
			Packet21PickupSpawn packet21pickupspawn = new Packet21PickupSpawn(entityitem);

			entityitem.locX = (double) packet21pickupspawn.b / 32.0D;
			entityitem.locY = (double) packet21pickupspawn.c / 32.0D;
			entityitem.locZ = (double) packet21pickupspawn.d / 32.0D;
			return packet21pickupspawn;
		}
		
		if(this.tracker instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)this.tracker;
			Packet20NamedEntitySpawn packet20 = new Packet20NamedEntitySpawn(ep);
			return packet20;
		}
		
		if(this.tracker instanceof EntityPlayer)
			return new Packet20NamedEntitySpawn((EntityHuman) this.tracker);
		
		if(this.tracker instanceof EntityMinecart) {
			EntityMinecart entityminecart = (EntityMinecart) this.tracker;

			if(entityminecart.type == 0)
				return new Packet23VehicleSpawn(this.tracker, 10);

			if(entityminecart.type == 1)
				return new Packet23VehicleSpawn(this.tracker, 11);

			if(entityminecart.type == 2)
				return new Packet23VehicleSpawn(this.tracker, 12);
		}

		if(this.tracker instanceof EntityBoat)
			return new Packet23VehicleSpawn(this.tracker, 1);
		if(this.tracker instanceof IAnimal)
			return new Packet24MobSpawn((EntityLiving) this.tracker);
		if(this.tracker instanceof EntityEnderDragon)
			return new Packet24MobSpawn((EntityLiving) this.tracker);
		if(this.tracker instanceof EntityFishingHook)
			return new Packet23VehicleSpawn(this.tracker, 90);
		if(this.tracker instanceof EntityArrow) {
			Entity entity = ((EntityArrow) this.tracker).shooter;
			return new Packet23VehicleSpawn(this.tracker, 60, entity != null ? entity.id : this.tracker.id);
		}
		if(this.tracker instanceof EntitySnowball)
			return new Packet23VehicleSpawn(this.tracker, 61);
		if(this.tracker instanceof EntityPotion)
			return new Packet23VehicleSpawn(this.tracker, 73, ((EntityPotion) this.tracker).getPotionValue());
		if(this.tracker instanceof EntityThrownExpBottle)
			return new Packet23VehicleSpawn(this.tracker, 75);
		if (this.tracker instanceof EntityEnderPearl)
			return new Packet23VehicleSpawn(this.tracker, 65);
		if(this.tracker instanceof EntityEnderSignal)
			return new Packet23VehicleSpawn(this.tracker, 72);

		Packet23VehicleSpawn packet23vehiclespawn = null;

		if(this.tracker instanceof EntitySmallFireball) {
			EntitySmallFireball entitysmallfireball = (EntitySmallFireball)this.tracker;

			if(entitysmallfireball.shooter != null)
				packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 64, entitysmallfireball.shooter.id);
			else
				packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 64, 0);

			packet23vehiclespawn.e = (int)(entitysmallfireball.dirX * 8000.0D);
			packet23vehiclespawn.f = (int)(entitysmallfireball.dirY * 8000.0D);
			packet23vehiclespawn.g = (int)(entitysmallfireball.dirZ * 8000.0D);
		}
		
		else if(this.tracker instanceof EntityFireball) {
			EntityFireball entityfireball = (EntityFireball) this.tracker;

			if(entityfireball.shooter != null)
				packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 63, ((EntityFireball) this.tracker).shooter.id);
			else
				packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 63, 0);

			packet23vehiclespawn.e = (int)(entityfireball.dirX * 8000.0D);
			packet23vehiclespawn.f = (int)(entityfireball.dirY * 8000.0D);
			packet23vehiclespawn.g = (int)(entityfireball.dirZ * 8000.0D);
		}
		
		else if(this.tracker instanceof EntityEgg)
			packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 62);
		if(this.tracker instanceof EntityTNTPrimed)
			packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 50);
		if(this.tracker instanceof EntityEnderCrystal)
			packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 51);
		if(packet23vehiclespawn != null)
			return packet23vehiclespawn;
		if(this.tracker instanceof EntityFallingBlock) {
			EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.tracker;

			if(entityfallingblock.id == Block.SAND.id)
				return new Packet23VehicleSpawn(this.tracker, 70);
			if(entityfallingblock.id == Block.GRAVEL.id)
				return new Packet23VehicleSpawn(this.tracker, 71);
			if(entityfallingblock.id == Block.DRAGON_EGG.id)
				return new Packet23VehicleSpawn(this.tracker, 74);
		}

		if(this.tracker instanceof EntityPainting)
			return new Packet25EntityPainting((EntityPainting) this.tracker);
		if(this.tracker instanceof EntityExperienceOrb)
			return new Packet26AddExpOrb((EntityExperienceOrb) this.tracker);
		throw new IllegalArgumentException("Don\'t know how to add " + this.tracker.getClass() + "!");
	}
}
