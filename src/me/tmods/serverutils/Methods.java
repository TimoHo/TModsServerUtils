package me.tmods.serverutils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.tmods.serverutils.multiversion.v18r3;
import me.tmods.serverutils.multiversion.v19r1;

public class Methods {
	public static String getVersionFromURL(URL url) {
		try {
			ZipInputStream zin = new ZipInputStream(url.openStream());
			for (ZipEntry e; (e = zin.getNextEntry()) != null;) {
				if (e.getName().equalsIgnoreCase("plugin.yml")) {
					InputStreamReader r = new InputStreamReader(zin);
					FileConfiguration config = YamlConfiguration.loadConfiguration(r);
					String version =  config.getString("version");
					r.close();
					return version;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void log(Exception e) {
		if (main.getVersion().equalsIgnoreCase("v1_9_R1") || main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			main.s.log(e);
			e.printStackTrace();
			print("This error was sent to the developer with following information: ",false,ChatColor.RED + "");
			print("ServerVersion: " + Bukkit.getVersion(),false,ChatColor.RED + "");
			print("ServerUtilsVersion " + main.s.get().getCodeVersion(),false,ChatColor.RED + "");
		} else {
			e.printStackTrace();
			print("Your server's version is outdated! please use v1_9_R1 or v1_8_R3!",false,ChatColor.RED + "");
		}
	}
	public static String getLang(String key) {
		return main.lang.getString(YamlConfiguration.loadConfiguration(new File("plugins/TModsServerUtils","config.yml")).getString("language") + "." + key);
	}
	public static List<EntityType> getAnimals() {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			return v18r3.getAnimals();
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			return v19r1.getAnimals();
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
		return null;
	}
	public static List<EntityType> getMobs() {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			return v18r3.getMobs();
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			return v19r1.getMobs();
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
		return null;
	}
	public static void changeChestState(Location loc,boolean open) {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			v18r3.changeChestState(loc, open);
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			v19r1.changeChestState(loc, open);
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
	}
	public static void setItemInHand(Player p,ItemStack item) {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			v18r3.setItemInHand(p,item);
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			v19r1.setItemInHand(p,item);
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
	}
	public static ItemStack getItemInHand(Player p) {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			return v18r3.getItemInHand(p);
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			return v19r1.getItemInHand(p);
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
		return null;
	}
	public static void playSound(String type,Location loc,Player p) {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			v18r3.playSound(type,loc,p);
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			v19r1.playSound(type,loc,p);
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
	}
	public static void playEffect(Location loc,String p,float f,int amount,boolean showDistance) {
		if (main.getVersion().equalsIgnoreCase("v1_8_R3")) {
			v18r3.playEffect(loc, p, f, amount, showDistance);
		} else if (main.getVersion().equalsIgnoreCase("v1_9_R1")) {
			v19r1.playEffect(loc, p, f, amount, showDistance);
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You have not installed one of the compatible versions for TModsServerUtils!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Pleas use v1_8_R3 or v1_9_R1");
		}
	}
	
	public static void loadRecipe(String path) {
		ShapedRecipe recipe = new ShapedRecipe(main.cfg.getItemStack(path + ".output"));
		recipe.shape("abc","def","ghi");
		if (main.cfg.getItemStack(path + ".input.0") != null) {
			recipe.setIngredient('a', main.cfg.getItemStack(path + ".input.0").getType());
		}
		if (main.cfg.getItemStack(path + ".input.1") != null) {
			recipe.setIngredient('b', main.cfg.getItemStack(path + ".input.1").getType());
		}
		if (main.cfg.getItemStack(path + ".input.2") != null) {
			recipe.setIngredient('c', main.cfg.getItemStack(path + ".input.2").getType());
		}
		if (main.cfg.getItemStack(path + ".input.3") != null) {
			recipe.setIngredient('d', main.cfg.getItemStack(path + ".input.3").getType());
		}
		if (main.cfg.getItemStack(path + ".input.4") != null) {
			recipe.setIngredient('e', main.cfg.getItemStack(path + ".input.4").getType());
		}
		if (main.cfg.getItemStack(path + ".input.5") != null) {
			recipe.setIngredient('f', main.cfg.getItemStack(path + ".input.5").getType());
		}
		if (main.cfg.getItemStack(path + ".input.6") != null) {
			recipe.setIngredient('g', main.cfg.getItemStack(path + ".input.6").getType());
		}
		if (main.cfg.getItemStack(path + ".input.7") != null) {
			recipe.setIngredient('h', main.cfg.getItemStack(path + ".input.7").getType());
		}
		if (main.cfg.getItemStack(path + ".input.8") != null) {
			recipe.setIngredient('i', main.cfg.getItemStack(path + ".input.8").getType());
		}
		Bukkit.addRecipe(recipe);
	}
	  
	public static void print(String s,boolean highlight,String color) {
		if (highlight) {
			if (color != null) {
				Bukkit.getConsoleSender().sendMessage(color + "§l" + s);
			} else {
				Bukkit.getConsoleSender().sendMessage("$l" + s);
			}
		} else {
			if (color != null) {
				Bukkit.getConsoleSender().sendMessage(color  + s);
			} else {
				Bukkit.getConsoleSender().sendMessage(s);
			}
		}
	}
	
	public static void regenTerrain(Location loc) {
		loc.getWorld().regenerateChunk(loc.getChunk().getX(), loc.getChunk().getZ());
	}
	
	public static void banPlayer(Player p) {
		List<String> players = new ArrayList<String>();
		players.addAll(main.cfg.getConfigurationSection("Bans.players").getKeys(false));
		if (!players.contains(p.getName())) {
			main.cfg.set("Bans.players." + p.getName(), p.getUniqueId() + "");
		}
		try {
			main.cfg.save(main.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void unbanPlayer(String name) {
		List<String> players = new ArrayList<String>();
		players.addAll(main.cfg.getConfigurationSection("Bans.players").getKeys(false));
		if (players.contains(name)) {
			main.cfg.set("Bans.players." + name, null);
		}
		try {
			main.cfg.save(main.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
