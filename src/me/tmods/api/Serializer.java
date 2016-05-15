package me.tmods.api;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Serializer {
	public static String serializeLocation(Location loc) {
		return (loc.getX() + "/:/" + loc.getY() + "/:/" + loc.getZ() + "/:/" + loc.getYaw() + "/:/" + loc.getPitch() + "/:/" + loc.getWorld().getName());
	}
	public static Location deserializeLocation(String s) {
		String[] data = s.split("/:/");
		Location loc = new Location(Bukkit.getWorld(data[5]),Double.valueOf(data[0]),Double.valueOf(data[1]),Double.valueOf(data[2]));
		loc.setYaw(Float.valueOf(data[3]));
		loc.setPitch(Float.valueOf(data[4]));
		return loc;
	}
	public static  String encrypt(String string) {
		byte[] bytes = string.getBytes();
		return Base64.getEncoder().encodeToString(bytes);
	}
	public static String decrypt(String string) {
		byte[] bytes = Base64.getDecoder().decode(string);
		try {
			return new String(bytes,"UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
