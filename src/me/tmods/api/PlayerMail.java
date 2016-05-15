package me.tmods.api;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import me.tmods.serverutils.Methods;

public class PlayerMail {
	private UUID senderUUID;
	private String senderName;
	private ItemStack content;
	private String text;
	private MailBox mb;
	public PlayerMail(Player sender,String text,MailBox mb) {
		this.senderName = sender.getName();
		this.senderUUID = sender.getUniqueId();
		this.text = text;
		this.content = null;
		this.mb = mb;
	}
	public PlayerMail(Player sender,String text,ItemStack content,MailBox mb) {
		this.senderName = sender.getName();
		this.senderUUID = sender.getUniqueId();
		this.text = text;
		this.content = content;
		this.mb = mb;
	}
	public PlayerMail(String senderName,UUID senderUUID,String text,ItemStack content,MailBox mb) {
		this.senderName = senderName;
		this.senderUUID = senderUUID;
		this.text = text;
		this.content = content;
		this.mb = mb;
	}
	
	public PlayerMail setSender(Player sender) {
		this.senderName = sender.getName();
		this.senderUUID = sender.getUniqueId();
		return this;
	}
	
	public PlayerMail setText(String text) {
		this.text = text;
		return this;
	}
	public PlayerMail setContent(ItemStack content) {
		this.content = content;
		return this;
	}
	
	public void open(Player p) {
		try {
			ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) is.getItemMeta();
			bm.setTitle("Message " + mb.getID(this));
			bm.setAuthor(this.senderName);
			bm.setPages(this.text);
			is.setItemMeta(bm);
			p.getInventory().addItem(is);
			p.getInventory().addItem(this.content);
			mb.delete(this);
			mb.save(new File("plugins/TModsServerUtils/mail",mb.puid + ".plm"));
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		String serializedMap = "";
		if (this.content == null) {
			serializedMap = "noContent";
		} else {
			Map<String,Object> map = this.content.serialize();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLEncoder xmlEncoder = new XMLEncoder(bos);
			xmlEncoder.writeObject(map);
			xmlEncoder.flush();
			serializedMap = bos.toString();
			xmlEncoder.close();
		}
		s = this.senderName + "-:-" + this.senderUUID.toString() + "-:-" + this.text + "-:-" + serializedMap;
		return s;
	}
	@SuppressWarnings("unchecked")
	public static PlayerMail valueOf(String s) {
		String[] data = s.split("-:-");
		ItemStack content = null;
		if (!data[3].equalsIgnoreCase("noContent")) {
			String serializedMap = data[3];
			XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(serializedMap.getBytes()));
			Map<String, Object> parsedMap = (Map<String, Object>) xmlDecoder.readObject();
			content = ItemStack.deserialize(parsedMap);
			xmlDecoder.close();
		}
		String senderName = data[0];
		UUID senderUUID = UUID.fromString(data[1]);
		String text = data[2];
		return new PlayerMail(senderName, senderUUID, text, content,null);
	}
	public OfflinePlayer getSender() {
		return Bukkit.getOfflinePlayer(senderUUID);
	}
	public void setMailBox(MailBox mb) {
		this.mb = mb;
	}
}
