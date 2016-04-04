package me.tmods.serverutils;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class permission implements CommandExecutor, Listener{

	
	private main plugin;

	public permission(main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		PermissionAttachment attachment = e.getPlayer().addAttachment(plugin);
		for(String per : main.cfg.getStringList(e.getPlayer().getUniqueId() + ".Permissions.List")){
			attachment.setPermission(per, true);
		}
	}
	
	public void onEnable(){
		for(Player p : Bukkit.getOnlinePlayers()){
			PermissionAttachment attachment = p.addAttachment(plugin);
			for(String per : main.cfg.getStringList(p.getUniqueId() + ".Permissions.List")){
				attachment.setPermission(per, true);
			}
		}
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
		if (!(args.length > 0)) {
			return false;
		}
		if (args[0].equalsIgnoreCase("get") && args.length == 2) {
			if(Bukkit.getOfflinePlayer(args[1]).isOnline() && p.hasPermission("ServerUtils.Permissions.get")){
				Player target = Bukkit.getPlayer(args[1]);
				for(PermissionAttachmentInfo s:target.getEffectivePermissions()) {
					p.sendMessage("Permission: " + s.getPermission());
				}
				return true;
			}else{
				p.sendMessage(Methods.getLang("permerr"));
			}
		}
		if(args.length == 3 && p.hasPermission("ServerUtils.Permissions.manage")) {
			
			if (args[0].equalsIgnoreCase("add")) {
				if(Bukkit.getOfflinePlayer(args[1]).isOnline()){
					Player target = Bukkit.getPlayer(args[1]);
					
					main.cfg.set(target.getUniqueId() + ".Name", target.getName());
					List<String> Perm = main.cfg.getStringList(target.getUniqueId() + ".Permissions.List");
					Perm.add(args[2]);
					main.cfg.set(target.getUniqueId() + ".Permissions.List", Perm);
					try {
						main.cfg.save(main.file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					PermissionAttachment attachment = target.addAttachment(plugin);
					attachment.setPermission(args[2], true);
					p.sendMessage(Methods.getLang("padd1") + " " + args[2] + " " + Methods.getLang("padd2") + " " + args[1] + " " + Methods.getLang("padd3"));
					target.sendMessage(Methods.getLang("pgot1") + " " + args[2] + " " + Methods.getLang("pgot2"));

				}else{
					p.sendMessage(Methods.getLang("notonline"));
				}
			}
			if (args[0].equalsIgnoreCase("remove")) {
				if(Bukkit.getOfflinePlayer(args[1]).isOnline()){
					Player target = Bukkit.getPlayer(args[1]);
					
					main.cfg.set(target.getUniqueId() + ".Name", target.getName());
					List<String> Perm = main.cfg.getStringList(target.getUniqueId() + ".Permissions.List");
					if (Perm.contains(args[2])) {
						Perm.remove(args[2]);
						PermissionAttachment attachment = target.addAttachment(plugin);
						attachment.setPermission(args[2], false);
						p.sendMessage(Methods.getLang("padd1") + " " + args[2] + Methods.getLang("pdel2") + " " + args[1] + " " +  Methods.getLang("pdel3"));
						target.sendMessage(Methods.getLang("prem1") + " " + args[2] + " " + Methods.getLang("prem2"));
					} else {
						p.sendMessage(Methods.getLang("pnotgot"));
					}

					main.cfg.set(target.getUniqueId() + ".Permissions.List", Perm);
					try {
						main.cfg.save(main.file);
					} catch (IOException e) {
						e.printStackTrace();
					}


				}else{
					p.sendMessage(Methods.getLang("notonline"));
				}
			}
			
		} else {
			return false;
		}
		
		return true;
	}
	
	
}
