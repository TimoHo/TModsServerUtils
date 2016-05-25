package me.tmods.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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
	
	public static String serializeItemStack(ItemStack stack){
	     try {
	         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	         BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	         dataOutput.writeObject(stack);
	         dataOutput.close();
	         return Base64Coder.encodeLines(outputStream.toByteArray());
	     }
	     catch (Exception e) {
	         throw new IllegalStateException("Unable to save item stack.", e);
	     }
	 }

	 public static ItemStack deserializeItemStack(String data){
	     try {
	         ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
	         BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
	         try {
	             return (ItemStack) dataInput.readObject();
	         } finally {
	             dataInput.close();
	         }
	     }
	     catch (Exception e) {
	         throw new IllegalStateException("Unable to decode class type.", e);
	     }
	 }
	 public static String serializeVector(Vector v) {
		 String s = v.getX() + "/:/" + v.getY() + "/:/" + v.getZ();
		 return s;
	 }
	 public static Vector deserializeVector(String s) {
		 Double x = Double.valueOf(s.split("/:/")[0]);
		 Double y = Double.valueOf(s.split("/:/")[1]);
		 Double z = Double.valueOf(s.split("/:/")[2]);
		 return new Vector(x,y,z);
	 }
	 public static String serializeInventory(Inventory inv) {
		 String s = "";
		 if (inv.getContents().length > 0) {
			 for (int i = 0;i<inv.getSize();i++) {
				 ItemStack is = inv.getItem(i);
				 String stack;
				 if (is == null) {
					 stack = "nullSlot";
				 } else {
					 stack = serializeItemStack(is) + "::" + i;
				 }
				 s = s + stack + "ispace";
			 }
		 }
		 return s;
	 }
	 public static Inventory deserializeInventory(String s) {
		 Inventory inv = Bukkit.createInventory(null, s.split("ispace").length);
		 for (int i = 0;i<s.split("ispace").length;i++) {
			 if (s.split("ispace")[i] == "nullSlot") {
				 inv.setItem(i, null);
			 } else {
				 inv.setItem(i, deserializeItemStack(s.split("ispace")[i]));
			 }
		 }
		 return inv;
	 }	 
}
