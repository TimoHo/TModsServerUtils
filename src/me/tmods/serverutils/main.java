package me.tmods.serverutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import me.tmods.stacktraces.StacktraceSender;

public class main extends JavaPlugin implements Listener{
	public boolean alert = true;
	public String home = "";
	public int clearLag = 0;
	public static File file = new File("plugins/TModsServerUtils", "data.yml");
	public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	public static File language = new File("plugins/TModsServerUtils","lang.yml");
	public static FileConfiguration lang = YamlConfiguration.loadConfiguration(language);
	public static StacktraceSender s;
	public void updateMultiversion() {
		InputStream newMv = null;
		if (getVersion().equalsIgnoreCase("v1_8_R3")) {
			newMv = getResource("mv18.jar");
		} else if (getVersion().equalsIgnoreCase("v1_9_R1")){
			newMv = getResource("mv19.jar");
		}
		if (new File("plugins","mv.jar").exists()) {
			new File("plugins","mv.jar").delete();
		}
		FileOutputStream mv = null;
		try {
			mv = new FileOutputStream("plugins/mv.jar");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (mv != null) {
			try {
				IOUtils.copy(newMv, mv);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (Bukkit.getPluginManager().getPlugin("TMods_Multiversion") != null) {
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("TMods_Multiversion"));
		} else {
			try {
				Bukkit.getPluginManager().loadPlugin(new File("plugins","mv.jar"));
			} catch (UnknownDependencyException e) {
				e.printStackTrace();
			} catch (InvalidPluginException e) {
				e.printStackTrace();
			} catch (InvalidDescriptionException e) {
				e.printStackTrace();
			}
		}
		Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("TMods_Multiversion"));
		Methods.print("If you see any errors, an update of the multiversion api could have caused it. please restart your server if you get any errors.", false, ChatColor.RED + "");
	}
	@Override
	public void onEnable() {
		s = new StacktraceSender(Bukkit.getVersion() + " Release: " + getVersion(),this.getDescription().getVersion(),this.getDescription().getName());
		try {
			File mv = new File("plugins","mv.jar");
			if (!mv.exists()) {
				updateMultiversion();
			} else {
				Plugin pl = Bukkit.getPluginManager().getPlugin("TMods_Multiversion");
				if (!pl.getDescription().getVersion().equalsIgnoreCase(this.getDescription().getVersion())) {
					updateMultiversion();
				}
			}
			InputStream source = getResource("lang.yml");
			Reader read = new InputStreamReader(source);
			FileConfiguration langcfg = YamlConfiguration.loadConfiguration(read);
			if (lang.getKeys(true) != langcfg.getKeys(true)) {
				try {
					langcfg.save(language);
				} catch (IOException e) {
					e.printStackTrace();
				}
				lang = YamlConfiguration.loadConfiguration(language);
			}
			try {
				read.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				source.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (cfg.getConfigurationSection("Shop") == null) {
				cfg.set("Shop.jsdkgldhfvbermnldsagvzjrabdhgvajvbh", "temporaryvalue");
				try {
					cfg.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				cfg.set("Shop.jsdkgldhfvbermnldsagvzjrabdhgvajvbh", null);
				try {
					cfg.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			getCommand("permission").setExecutor(new permission(this));
			
			getServer().getPluginManager().registerEvents(new permission(this), this);
			
			getConfig().options().copyDefaults(true);
			saveConfig();
			if (this.getClass().getName() != "me.tmods.serverutils.main") {
				Bukkit.getPluginManager().disablePlugin(this);
			}
			if (getConfig().get("alert") != null) {
				alert = getConfig().getBoolean("alert");
			} else {
				alert = false;
			}
			Bukkit.getPluginManager().registerEvents(this, this);
			Methods.print("|---------------------|",true,"" + ChatColor.AQUA);
			Methods.print("|                     |",true,"" + ChatColor.AQUA);
			Methods.print("| TMods Utils enabled |",true,"" + ChatColor.AQUA);
			Methods.print("|                     |",true,"" + ChatColor.AQUA);
			Methods.print("|---------------------|",true,"" + ChatColor.AQUA);
			if (alert) {
				Methods.print(Methods.getLang("smactive"),true,"" + ChatColor.DARK_RED);
			} else {
				Methods.print(Methods.getLang("sminactive"),true,"" + ChatColor.DARK_GREEN);
			}
			if (getVersion().equalsIgnoreCase("v1_9_R1")) {
				Methods.print("Detected version: " + getVersion() + " no multiversion support needed.", false, ChatColor.GREEN + "");
			} else if (getVersion().equalsIgnoreCase("v1_8_R3")) {
				Methods.print("Multiversion support enabled for " + getVersion() + ". there may be some things, that don't work correctly! If you want them to work, the please upgrade to v1_9_R1", false, ChatColor.YELLOW + "");
			} else {
				Methods.print("Your version is: " + getVersion() + ". This is not compatible with this plugin! please upgrade to v1_8_R3 or v1_9_R1", false, ChatColor.RED + "");
			}
			if (getConfig().getBoolean("clearLag")) {
				Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					@Override
					public void run() {
						if (clearLag >= 1) {
							for(World w:Bukkit.getWorlds()) {
								for(Entity e:w.getEntities()) {
									if (e instanceof Item) {
										e.remove();
									}
								}
							}
							if (getConfig().getBoolean("clearLagBroadcast")) {
								Bukkit.broadcastMessage("[TMods Server Utils] clearLag " + Methods.getLang("complete"));
							}
							clearLag = 0;
						} else {
							if (getConfig().getBoolean("clearLagBroadcast")) {
								Bukkit.broadcastMessage("[TMods Server Utils] clearLag in " + getConfig().getInt("clearCooldown") / 2  + " " + Methods.getLang("seconds"));
							}
							clearLag += 1;
						}
					}
				}, 100, 10 * getConfig().getInt("clearCooldown"));
			}
			if (cfg.getConfigurationSection("Recipes") != null) {
				if (cfg.getConfigurationSection("Recipes").getKeys(false).size() > 0) {
					for (String s:cfg.getConfigurationSection("Recipes").getKeys(false)) {
						Methods.loadRecipe("Recipes." + s);
					}
				}
			}
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(!event.getPlayer().hasPermission("ServerUtils.build")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractAtEntityEvent event) {
		if(!event.getPlayer().hasPermission("ServerUtils.build")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		try {
			if (event.getInventory().getTitle().contains("RC >> ")) {
				if (event.getInventory().getItem(16) == null) {
					event.getPlayer().sendMessage(Methods.getLang("rcoutputneeded"));
					return;
				}
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".output", event.getInventory().getItem(16));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.0", event.getInventory().getItem(0));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.1", event.getInventory().getItem(1));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.2", event.getInventory().getItem(2));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.3", event.getInventory().getItem(9));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.4", event.getInventory().getItem(10));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.5", event.getInventory().getItem(11));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.6", event.getInventory().getItem(18));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.7", event.getInventory().getItem(19));
				cfg.set("Recipes." + event.getInventory().getTitle().split(">> ")[1] + ".input.8", event.getInventory().getItem(20));
				try {
					cfg.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (event.getInventory().getItem(16) != null) {
					Methods.loadRecipe("Recipes." + event.getInventory().getTitle().split(">> ")[1]);
					event.getPlayer().sendMessage(Methods.getLang("rccreated"));
				}
			}
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			if (event.getInventory().getTitle().contains("SHOP>>") && cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot()) != null) {
				if (cfg.getInt(event.getWhoClicked().getUniqueId() + ".money") >= cfg.getInt("Shop." + event.getInventory().getTitle().split(">> ")[1] +  ".cost." + event.getRawSlot())) {
					ItemStack is = cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot());
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(cfg.getString("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".names." + event.getRawSlot()));
					is.setItemMeta(im);
					event.getWhoClicked().getInventory().addItem(is);
					event.getWhoClicked().sendMessage(Methods.getLang("yhp1") + " " + cfg.getInt("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".cost." + event.getRawSlot()) + "$ " + Methods.getLang("yhp2") + " " + cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot()).getAmount() + " " + cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot()).getType().toString() + " " + Methods.getLang("yhp3"));
					if (Bukkit.getPlayer(cfg.getString("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".owner")).isOnline()) {
						Bukkit.getPlayer(cfg.getString("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".owner")).sendMessage(Methods.getLang("yhr1") + " " + cfg.getInt("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".cost." + event.getRawSlot()) + "$ " + Methods.getLang("yhr2") + " " + cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot()).getAmount() + " " + cfg.getItemStack("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot()).getType().toString() + " " + Methods.getLang("yhr3"));
					}
					cfg.set(Bukkit.getPlayer(cfg.getString("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".owner")).getUniqueId() + ".money", Math.addExact(cfg.getInt(Bukkit.getPlayer(cfg.getString("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".owner")).getUniqueId() + ".money"), cfg.getInt("Shop." + event.getInventory().getTitle().split(">> ")[1] +  ".cost." + event.getRawSlot())));
					cfg.set(event.getWhoClicked().getUniqueId() + ".money", Math.subtractExact(cfg.getInt(event.getWhoClicked().getUniqueId() + ".money"), cfg.getInt("Shop." + event.getInventory().getTitle().split(">> ")[1] +  ".cost." + event.getRawSlot())));
					cfg.set("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory." + event.getRawSlot() , null);
					cfg.set("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".cost." + event.getRawSlot(), 999999999);
					if (cfg.getConfigurationSection("Shop." + event.getInventory().getTitle().split(">> ")[1] + ".inventory").getKeys(false).size() < 1) {
						cfg.set("Shop." + event.getInventory().getTitle().split(">> ")[1], null);
						Bukkit.broadcastMessage(Methods.getLang("tsc1") + " " + event.getInventory().getTitle().split(">> ")[1] + " " + Methods.getLang("tsc2"));
					}
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					event.setCancelled(true);
					Inventory inv = event.getInventory();
					inv.setItem(event.getRawSlot(), new ItemStack(Material.AIR,0));
					event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(inv);
				}
				event.setCancelled(true);
			}
			if (event.getInventory().getTitle().contains("RC >>")) {
				int[] deny = {3,12,21,5,14,23,6,15,24,7,25,8,17,26};
				for (Integer i:deny) {
					if (i == event.getRawSlot()) {
						event.setCancelled(true);
					}
				}
			}
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		if (alert) {
		event.setMaxPlayers(0);
		event.setMotd("§4§l§n" + Methods.getLang("smm"));
		}

	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (!event.getDamager().hasPermission("ServerUtils.build")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (cfg.getConfigurationSection("Bans.players") != null) {
				if (cfg.getConfigurationSection("Bans.players").getKeys(true).contains(event.getPlayer().getName()) || cfg.getConfigurationSection("Bans.players").getKeys(true).contains(event.getPlayer().getUniqueId() + "")) {
					event.getPlayer().kickPlayer("You've been SUBanned!");
				}
			}
			if (alert) {
				if(!event.getPlayer().hasPermission("ServerUtils.joinOnSecure")) {
					event.getPlayer().kickPlayer("§4§l§n" + Methods.getLang("smjoindeny"));
					Methods.print("Player tried to join without permission",true,"" + ChatColor.DARK_RED);
				}
			}
			Methods.print("Player " + event.getPlayer().getName() + " has joined with effective permissions:",true,"" + ChatColor.AQUA);
			if (event.getPlayer().isOp()) {
				Methods.print("OPERATOR",false,"" + ChatColor.DARK_RED);
				event.setJoinMessage(ChatColor.DARK_RED + Methods.getLang("opjoin1") + " " + event.getPlayer().getName() + " " + Methods.getLang("opjoin2"));
			} else {
				for(PermissionAttachmentInfo s:event.getPlayer().getEffectivePermissions()) {
					Methods.print("permission: " + s.getPermission(),false,"" + ChatColor.AQUA);
				}
			}
			if (getConfig().getBoolean("TeleportToSpawnOnJoin")) {
				Location loc;
				try {
					loc = new Location(Bukkit.getWorld(cfg.getString("SpawnLoc.world")),cfg.getDouble("SpawnLoc.x"),cfg.getDouble("SpawnLoc.y"),cfg.getDouble("SpawnLoc.z"));
				} catch(IllegalArgumentException e) {
					loc = new Location(event.getPlayer().getWorld(),0,0,0);
				}
				loc.setYaw((float) cfg.getDouble("SpawnLoc.yaw"));
				if (loc.getX() != 0 && loc.getY() != 0 && loc.getZ() != 0) {
					event.getPlayer().teleport(loc);
				}
			}
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
		try {
			if (cmd.getName().equalsIgnoreCase("spawn")) {
				if (sender.hasPermission("ServerUtils.warpSpawn")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						Location loc;
						try {
							loc = new Location(Bukkit.getWorld(cfg.getString("SpawnLoc.world")),cfg.getDouble("SpawnLoc.x"),cfg.getDouble("SpawnLoc.y"),cfg.getDouble("SpawnLoc.z"));
						} catch(IllegalArgumentException e) {
							loc = new Location(((Player) sender).getWorld(),0,0,0);
						}
						loc.setYaw((float) cfg.getDouble("SpawnLoc.yaw"));
						if (loc.getX() != 0 && loc.getY() != 0 && loc.getZ() != 0) {
							p.teleport(loc);
						} else {
							sender.sendMessage(Methods.getLang("nospawn"));
						}
					} else {
						sender.sendMessage("You're not a Player");
					}
				} else {
					sender.sendMessage(Methods.getLang("permdeny"));
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("setspawn")) {
				if (sender.hasPermission("ServerUtils.setSpawn")) {
					if (sender instanceof Player) {
						cfg.set("SpawnLoc.world", ((Player) sender).getWorld().getName());
						cfg.set("SpawnLoc.x", ((Player) sender).getLocation().getX());
						cfg.set("SpawnLoc.y", ((Player) sender).getLocation().getY());
						cfg.set("SpawnLoc.z", ((Player) sender).getLocation().getZ());
						cfg.set("SpawnLoc.yaw", ((Player) sender).getLocation().getYaw());
						try {
							cfg.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						sender.sendMessage("You're not a player!");
					}
				} else {
					sender.sendMessage(Methods.getLang("permdeny"));
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("suban")) {
				if (!sender.hasPermission("ServerUtils.suban")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
				if (args.length != 2) {
					return false;
				}
				if (args[0].equalsIgnoreCase("ban")) {
					Methods.banPlayer(Bukkit.getPlayer(args[1]));
					Bukkit.getPlayer(args[1]).kickPlayer("You've been SUBanned!");
					return true;
				}
				if (args[0].equalsIgnoreCase("unban")) {
					Methods.unbanPlayer(args[1]);
					return true;
				}
			}
			if (cmd.getName().equalsIgnoreCase("recipe")) {
				if (sender instanceof Player && args.length == 2 && args[0].equalsIgnoreCase("create") && sender.hasPermission("ServerUtils.recipe")) {
					Inventory inv = Bukkit.createInventory(Bukkit.getPlayer(sender.getName()), 27, "RC >> " + args[1]);
					ItemStack info1 = new ItemStack(Material.COOKIE);
					ItemMeta infometa1 = info1.getItemMeta();
					infometa1.setDisplayName("The first 3*3 = crafting space.");
					info1.setItemMeta(infometa1);
					inv.setItem(4, info1);
					ItemStack info2 = new ItemStack(Material.COOKIE);
					ItemMeta infometa2 = info2.getItemMeta();
					infometa2.setDisplayName("the dead bush space = output.");
					info2.setItemMeta(infometa2);
					inv.setItem(13, info2);
					ItemStack info3 = new ItemStack(Material.COOKIE);
					ItemMeta infometa3 = info3.getItemMeta();
					infometa3.setDisplayName("You can eat these cookies :)");
					info3.setItemMeta(infometa3);
					inv.setItem(22, info3);
					inv.setItem(6, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(7, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(8, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(15, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(17, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(24, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(25, new ItemStack(Material.DEAD_BUSH,1));
					inv.setItem(26, new ItemStack(Material.DEAD_BUSH,1));
					Bukkit.getPlayer(sender.getName()).openInventory(inv);
					return true;
				}
				if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("ServerUtils.recipe")) {
					cfg.set("Recipes." + args[1], null);
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sender.sendMessage("Recipe removed.");
					Bukkit.resetRecipes();
					if (cfg.getConfigurationSection("Recipes") != null) {
						if (cfg.getConfigurationSection("Recipes").getKeys(false).size() > 0) {
							for (String s:cfg.getConfigurationSection("Recipes").getKeys(false)) {
								Methods.loadRecipe("Recipes." + s);
							}
						}
					}
					return true;
				}
				if (args.length == 1 && args[0].equalsIgnoreCase("list") && sender.hasPermission("ServerUtils.recipe")) {
					sender.sendMessage("Recipes: ");
					if (cfg.getConfigurationSection("Recipes") != null) {
						if (cfg.getConfigurationSection("Recipes").getKeys(false).size() > 0) {
							for(String s:cfg.getConfigurationSection("Recipes").getKeys(false)) {
								sender.sendMessage(s);
							}
						}
					}
					sender.sendMessage("-----------");
					return true;
				}
				if (!sender.hasPermission("ServerUtils.recipe")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
			}
			if (cmd.getName().equalsIgnoreCase("shop")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("Shop:");
					sender.sendMessage(" ");
					if (cfg.getConfigurationSection("Shop").getKeys(false).size() > 0) {
						for(String s:cfg.getConfigurationSection("Shop").getKeys(false)) {
							sender.sendMessage(s);
						}
					}
					sender.sendMessage("------------------------");
				}
				if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
					if (!sender.hasPermission("ServerUtils.economy.manageShop")) {
						sender.sendMessage(Methods.getLang("permdeny"));
						return true;
					}
					if (cfg.getConfigurationSection("Shop").getKeys(false).contains(args[1])) {
						sender.sendMessage(Methods.getLang("saex"));
						return true;
					}
					cfg.set("Shop." + args[1] + ".owner", sender.getName());
					Block block = Bukkit.getPlayer(sender.getName()).getTargetBlock((Set<Material>)null, 200);
					if (block.getType().toString() != "CHEST") {
						sender.sendMessage(Methods.getLang("chestlook"));
						return true;
					}
					Chest chest = (Chest)((Block) block).getState();
					for (int i = 0; i < chest.getBlockInventory().getContents().length;i++) {
						if (chest.getBlockInventory().getContents()[i] != null) {
							cfg.set("Shop." + args[1] + ".names." + i, chest.getBlockInventory().getContents()[i].getItemMeta().getDisplayName());
						}
						cfg.set("Shop." + args[1] + ".inventory." + i, chest.getBlockInventory().getContents()[i]);
						cfg.set("Shop." + args[1] + ".cost." + i, 999999999);
					}
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					chest.getBlockInventory().clear();
					chest.getBlock().breakNaturally();
					sender.sendMessage(Methods.getLang("costtable1") + ": ");
					for (String s:cfg.getConfigurationSection("Shop." + args[1] + ".inventory").getKeys(false)) {
						if (cfg.getItemStack("Shop." + args[1] + ".inventory." + s) != null) {
							sender.sendMessage(Methods.getLang("costtable2") + " " + cfg.getItemStack("Shop." + args[1] + ".inventory." + s).getType().toString() + " * " + cfg.getItemStack("Shop." + args[1] + ".inventory." + s).getAmount() + " " + Methods.getLang("costtable3"));
							sender.sendMessage("Type /shop cost [shop] " + s + " [cost]");
						}
					}
					sender.sendMessage(" ");
					sender.sendMessage("end of cost table");
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Bukkit.broadcastMessage(Methods.getLang("newshop") + " " + args[1]);
					return true;
				}
				if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
					if (!sender.hasPermission("ServerUtils.economy.buyShop")) {
						sender.sendMessage(Methods.getLang("permdeny"));
						return true;
					}
					if (cfg.getConfigurationSection("Shop").getKeys(false).contains(args[1])) {
						Inventory shop = Bukkit.createInventory(Bukkit.getPlayer(sender.getName()), 27,"SHOP>> " + args[1]);
						String s = "";
						for (int i = 0; i < cfg.getConfigurationSection("Shop." + args[1] + ".inventory").getKeys(false).size(); i++) {
							s = (String) cfg.getConfigurationSection("Shop." + args[1] + ".inventory").getKeys(false).toArray()[i];
							ItemStack is = (cfg.getItemStack("Shop." + args[1] + ".inventory." + s));;
							ItemMeta im = is.getItemMeta();
							if (cfg.getString("Shop." + args[1] + ".names." + s) != null) {
								im.setDisplayName(cfg.getString("Shop." + args[1] + ".names." + s) + " Price: " + cfg.getInt("Shop." + args[1] + ".cost." + s));
							} else {
								im.setDisplayName("Price: " + cfg.getInt("Shop." + args[1] + ".cost." + s));
							}
							is.setItemMeta(im);
							shop.setItem(Integer.valueOf(s), is);
						}
						Bukkit.getPlayer(sender.getName()).openInventory(shop);
					} else {
						sender.sendMessage(Methods.getLang("shopnotfound"));
					}
					return true;
				}
				if (args.length == 4 && args[0].equalsIgnoreCase("cost")) {
					if (!sender.hasPermission("ServerUtils.economy.manageShop")) {
						sender.sendMessage(Methods.getLang("permdeny"));
						return true;
					}
					if (!sender.getName().equalsIgnoreCase(cfg.getString("Shop." + args[1] + ".owner"))) {
						sender.sendMessage(Methods.getLang("notyourshop"));
						sender.sendMessage("owner:" + cfg.getString("Shop." + args[1] + ".owner"));
						sender.sendMessage("niyou:" + sender.getName());
						return true;
					}
					if (args[2].equalsIgnoreCase("all")) {
						for (String s:cfg.getConfigurationSection("Shop." + args[1] + ".cost").getKeys(false)) {
							cfg.set("Shop." + args[1] + ".cost." + s, Integer.valueOf(args[3]));	
						}
						try {
							cfg.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Bukkit.broadcastMessage(Methods.getLang("pricechange1") + " all " + " " + Methods.getLang("pricechange2") + " " + args[3] + "$ " + Methods.getLang("pricechange3") + " " + args[1]);
						return true;
					}
					Bukkit.broadcastMessage(Methods.getLang("pricechange1") + " " + cfg.getItemStack("Shop." + args[1] + ".inventory." + args[2]).getType().toString() + " " + Methods.getLang("pricechange2") + " " + args[3] + "$ " + Methods.getLang("pricechange3") + " " + args[1]);
					cfg.set("Shop." + args[1] + ".cost." + args[2], Integer.valueOf(args[3]));
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("bal") && sender instanceof Player) {
				if (!sender.hasPermission("ServerUtils.economy.bal")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
				sender.sendMessage(Methods.getLang("bal") + ": " + cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".money"));
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("pay") && sender instanceof Player && args.length == 2) {
				if (!sender.hasPermission("ServerUtils.economy.pay")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					if (Double.valueOf(args[1]) > 0) {
						if (cfg.getDouble(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".money") >= Double.valueOf(args[1])) {
							cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".money",cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".money") - Integer.valueOf(args[1]));
							cfg.set(Bukkit.getPlayer(args[0]).getUniqueId() + ".money",cfg.getInt(Bukkit.getPlayer(args[0]).getUniqueId() + ".money") + Integer.valueOf(args[1]));
							try {
								cfg.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
							sender.sendMessage(args[1] + "$ " +  Methods.getLang("paid"));
							Bukkit.getPlayer(args[0]).sendMessage(args[1] + "$ " + Methods.getLang("gotpaid1") + " " + sender.getName() + " " + Methods.getLang("gotpaid2"));
						} else {
							sender.sendMessage(Methods.getLang("nemoney"));
						}
					} else {
						sender.sendMessage(Methods.getLang("cbpaid"));
					}
				} else {
					sender.sendMessage(Methods.getLang("notonline"));
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("setMoney") && args.length == 2) {
				if (!sender.hasPermission("ServerUtils.economy.set")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					cfg.set(Bukkit.getPlayer(args[0]).getUniqueId() + ".money", Integer.valueOf(args[1]));
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sender.sendMessage("Money of " + args[0] + " set to " + args[1] + "$.");
					return true;
				} else {
					sender.sendMessage(Methods.getLang("notonline"));
				}
			}
			if (cmd.getName().equalsIgnoreCase("inv") && sender.hasPermission("ServerUtils.inv") && args.length == 1 && Bukkit.getPlayer(sender.getName()) != null) {
				Bukkit.getPlayer(sender.getName()).openInventory(Bukkit.getPlayer(args[0]).getInventory());
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("enderinv") && sender.hasPermission("ServerUtils.enderinv") && args.length == 1 && Bukkit.getPlayer(sender.getName()) != null) {
				Bukkit.getPlayer(sender.getName()).openInventory(Bukkit.getPlayer(args[0]).getEnderChest());
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("trash") && sender instanceof Player) {
				if (!sender.hasPermission("ServerUtils.trash")) {
					sender.sendMessage(Methods.getLang("permdeny"));
					return true;
				}
				Inventory inv = Bukkit.createInventory(Bukkit.getPlayer(sender.getName()), 18,"Trash");
				Bukkit.getPlayer(sender.getName()).openInventory(inv);
				inv = null;
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("home") && sender.hasPermission("ServerUtils.home")  && sender instanceof Player) {
				if (args.length == 0) {
					home = "default";
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list")) {
						sender.sendMessage("------- " + Methods.getLang("homeof") + sender.getName() + " -------");
						if (cfg.getConfigurationSection(((Player) sender).getUniqueId() + ".home") != null) {
							if (cfg.getConfigurationSection(((Player) sender).getUniqueId() + ".home").getKeys(false).size() > 0) {
								for(String s:cfg.getConfigurationSection(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home").getKeys(false)) {
									sender.sendMessage(s);
								}
							}
						}
						sender.sendMessage("-------------------------------------------");
						return true;
					} else {
						if (sender.hasPermission("ServerUtils.multihome")) {
							home = args[0];
						} else {
							sender.sendMessage(Methods.getLang("mhdeny"));
							return true;
						}
					}
				}
				if (args.length == 2 && args[0].equalsIgnoreCase("delete")){
					cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + args[1], null);
					sender.sendMessage(Methods.getLang("delhome"));
					try {
						cfg.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
				if (args.length != 0 && args.length != 1) {
					return false;
				}
				int x = cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".x");
				int y = cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".y");
				int z = cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".z");
				int jaw = cfg.getInt(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".yaw");
				if (x != 0 && y != 0 && z != 0) {
					Bukkit.getPlayer(sender.getName()).teleport(new Location(Bukkit.getPlayer(sender.getName()).getWorld(),x,y,z,jaw,0));
				} else {
					sender.sendMessage(Methods.getLang("notfoundh"));
					return false;
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("sethome") && sender instanceof Player) {
				if (args.length == 0) {
					home = "default";
				}
				if (args.length == 1) {
					home = args[0];
				}
				if (args.length != 0 && args.length != 1) {
					return false;
				}
				int x = Bukkit.getPlayer(sender.getName()).getLocation().getBlockX();
				int y = Bukkit.getPlayer(sender.getName()).getLocation().getBlockY();
				int z = Bukkit.getPlayer(sender.getName()).getLocation().getBlockZ();
				float yaw = Bukkit.getPlayer(sender.getName()).getLocation().getYaw();
				cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".x", x);
				cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".y", y);
				cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".z", z);
				cfg.set(Bukkit.getPlayer(sender.getName()).getUniqueId() + ".home." + home + ".yaw", yaw);
				try {
					cfg.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender.sendMessage(Methods.getLang("sethome"));
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("e")){
				if (sender.hasPermission("ServerUtils.setAlert")) {
					if (alert) {
						alert = false;
						getConfig().set("alert",false);
						sender.sendMessage(ChatColor.GREEN + Methods.getLang("deacsave"));
						saveConfig();
					} else {
						alert = true;
						getConfig().set("alert",true);
						sender.sendMessage(ChatColor.DARK_RED + Methods.getLang("acsave"));
						saveConfig();
						Collection<? extends Player> online = Bukkit.getOnlinePlayers();
						for(Player p:online) {
							if (!p.hasPermission("ServerUtils.joinOnSecure")) {
								p.kickPlayer(Methods.getLang("savekick"));
							}
						}
					}
					return true;
				} else {
					sender.sendMessage(Methods.getLang("permdeny"));
				}
			}
			if (cmd.getName().equalsIgnoreCase("bp") && sender instanceof Player) {
				if (sender.hasPermission("ServerUtils.backpack")) {
					sender.getServer().getPlayer(sender.getName()).openInventory(sender.getServer().getPlayer(sender.getName()).getEnderChest());
				} else {
					sender.sendMessage(Methods.getLang("permdeny"));
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("getalert")) {
				if (alert) {
					sender.sendMessage(Methods.getLang("smactive"));
				} else {
					sender.sendMessage(Methods.getLang("sminactive"));
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
		return false;
	}	
	public static String getVersion() {
		try {
			final String svPackageName = Bukkit.getServer().getClass().getPackage().getName();
			return svPackageName.substring(svPackageName.lastIndexOf('.') + 1);
		} catch (Exception e) {
			s.log(e); Methods.print("This error was rent to the developer.",false,ChatColor.RED + "");
		}
		return null;
	}
}
