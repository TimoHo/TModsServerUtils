package me.tmods.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.tmods.serverutils.Methods;

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
	
    public static String serializeItemStack(ItemStack i) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(i.serialize());
            oos.flush();
            return DatatypeConverter.printBase64Binary(bos.toByteArray());
        }
        catch (Exception e) {
        	Methods.log(e);
        }
        return "";
    }
    @SuppressWarnings("unchecked")
	public static ItemStack deserializeItemStack(String s) {
        try {
            ItemStack istack;
        	ByteArrayInputStream bis = new ByteArrayInputStream(
            DatatypeConverter.parseBase64Binary(s));
            ObjectInputStream ois = new ObjectInputStream(bis);
            Map<String,Object> s1 = (Map<String, Object>) ois.readObject();
            if (s1.containsKey("meta")) {
                Map<String, Object> im = new HashMap<>((Map<String, Object>) s1.remove("meta"));
                im.put("==", "ItemMeta");
                ItemStack is = ItemStack.deserialize(s1);
                is.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(im));
                istack = is;
            }
            else {
                istack = ItemStack.deserialize(s1);
            }
            
            return istack;
        }
        catch (Exception e) {
            Methods.log(e);
        }
        return null;
    }
}
