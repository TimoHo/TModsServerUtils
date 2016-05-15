package me.tmods.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.tmods.serverutils.Methods;

public class MailBox {
	public String puid;
	public List<PlayerMail> inbox;
	public MailBox(Player p) {
		this.puid = p.getUniqueId().toString();
		this.inbox = new ArrayList<PlayerMail>();
	}
	public MailBox(String uuid) {
		this.puid = uuid;
		this.inbox = new ArrayList<PlayerMail>();
	}
	public MailBox(String uuid,List<PlayerMail> inbox) {
		this.puid = uuid;
		this.inbox = inbox;
	}
	
	public int getID(PlayerMail pm) {
		if (inbox.size() > 0) {
			for (int i = 0;i<inbox.size();i++) {
				if (pm.equals(inbox.get(i))) {
					return (i + 1);
				}
			}
		}
		return -1;
	}
	
	public static MailBox getMailbox(Player p) {
		try {
			File ibox = new File("plugins/TModsServerUtils/mail",p.getUniqueId().toString() + ".plm");
			MailBox mailBox = MailBox.readFile(ibox);
			if (mailBox == null) {
				mailBox = new MailBox(p.getUniqueId().toString());
				mailBox.save(ibox);
			}
			return mailBox;
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	public static MailBox getMailbox(String uid) {
		try {
			File ibox = new File("plugins/TModsServerUtils/mail",uid + ".plm");
			MailBox mailBox = MailBox.readFile(ibox);
			if (mailBox == null) {
				mailBox = new MailBox(uid);
				mailBox.save(ibox);
			}
			return mailBox;
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	private static MailBox readFile(File f) {
		if (f.exists()) {
			YamlConfiguration mb = YamlConfiguration.loadConfiguration(f);
			if (mb.getKeys(false).size() > 0) {
				String oId = mb.getString("player");
				MailBox mailBox = new MailBox(oId);
				List<String> msgStr = mb.getStringList("inbox");
				List<PlayerMail> inbox = new ArrayList<PlayerMail>();
				if (msgStr.size() > 0) {
					for (String estr:msgStr) {
						PlayerMail pm = PlayerMail.valueOf(Serializer.decrypt(estr));
						pm.setMailBox(mailBox);
						inbox.add(pm);
					}
				}
				mailBox.setInbox(inbox);
				return mailBox;
			}
		}
		return null;
	}
	public void setInbox(List<PlayerMail> inbox) {
		this.inbox = inbox;
	}
	public void save(File f) throws IOException{
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		List<String> ibox = new ArrayList<String>();
		for (PlayerMail m:this.inbox) {
			ibox.add(Serializer.encrypt(m.toString()));
		}
		cfg.set("player", puid);
		cfg.set("inbox", ibox);
		cfg.save(f);
	}
	public void add(PlayerMail m) {
		this.inbox.add(m);
	}
	public void delete(PlayerMail m) {
		this.inbox.remove(m);
	}
	public void delete(Integer id) {
		this.inbox.remove(id);
	}
}
