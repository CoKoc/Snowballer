package cokoc.snowballer.utils;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class TinyLocation implements Serializable {
	private static final long serialVersionUID = 1293841631691062060L;
	private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
 
    public TinyLocation(World world, Double x, Double y, Double z) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0;
        this.yaw = 0;
    }
    
    public TinyLocation(World world, int x, int y, int z) {
		this.world = world.getName();
		this.x = (double) x;
		this.y = (double) y;
		this.z = (double) z;
		this.pitch = 0;
		this.yaw = 0;
	}
 
    public TinyLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int)x;
        hash = 23 * hash + (int)y;
        hash = 23 * hash + (int)z;
        hash = 23 * hash + (int)yaw;
        hash = 23 * hash + (int)pitch;
        return hash;
    }
 
    public Location toLocation() {
        Location l = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        return l;
    }
}
