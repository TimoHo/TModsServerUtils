package me.tmods.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class MySQL {
	private String host;
	private String port;
	private String user;
	private String pass;
	private String datb;
	private Connection con;
	public MySQL(String host,String port,String datb,String user,String pass) {
		this.host = host;
		this.port = port;
		this.datb = datb;
		this.user = user;
		this.pass = pass;
		try {
			con = open();
		} catch (ClassNotFoundException | SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Connecting to SQL server failed!",e);
		}
	}
	private Connection open() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + datb, user, pass);
	}
	public void execute(String query) {
		Statement stat = null;
		try {
			stat = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (stat == null) {
			return;
		}
		try {
			stat.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ResultSet query(String query) {
		if (con == null) {
			return null;
		}
		Statement stat = null;
		try {
			stat = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (stat != null) {
			try {
				return stat.executeQuery(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public void close() {
		if (this.con != null) {
			try {
				this.con.close();
			} catch (SQLException e) {}
			this.con = null;
		}
	}
	public boolean isConnected() {
		if (this.con == null) {
			return false;
		}
		try {
			if (this.con.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
