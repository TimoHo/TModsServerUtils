package me.tmods.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileSyncronizer {
	private static boolean localNewer(File f,MySQL s,String name) {
		long lastLocal = f.lastModified();
		ResultSet res = s.query("SELECT * FROM fileSync WHERE name = '" + name + "';");
		try {
			if (res.next()) {
				if (res.getLong("lastModified") < lastLocal) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	private static boolean remoteNewer(File f,MySQL s,String name) {
		long lastLocal = f.lastModified();
		ResultSet res = s.query("SELECT * FROM fileSync WHERE name = '" + name + "';");
		try {
			if (res.next()) {
				if (res.getLong("lastModified") > lastLocal) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	private static String get(File f,MySQL s,String name) {
		ResultSet res = s.query("SELECT * FROM fileSync WHERE name = '" + name + "';");
		try {
			if (res.next()) {
				try {
					return Serializer.decrypt(res.getString("file"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				return "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	private static void push(File f,MySQL s,boolean update,String name) {
		String file = "";
		try {
			file = Serializer.encrypt(IOUtils.toString(new FileInputStream(f)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(update) {
			s.execute("UPDATE fileSync SET file = '" + file + "' WHERE name = '" + name + "';");
			s.execute("UPDATE fileSync SET lastModified = '" + f.lastModified() + "' WHERE name = '" + name + "';");
		} else {
			s.execute("INSERT INTO fileSync (file,lastModified,name) Values('" + file + "','" + f.lastModified() + "','" + name +  "');");
		}
	}
	private static void pull(File f,MySQL s,String name) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String file = get(f,s,name);
		InputStream is = IOUtils.toInputStream(file);
		try {
			FileUtils.copyInputStreamToFile(is, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sync(File f,MySQL s,String name) {
		s.execute("CREATE TABLE IF NOT EXISTS fileSync"
				+ "("
				+ "file TEXT,"
				+ "lastModified TEXT,"
				+ "name varchar(255)"
				+ ");");
		boolean remoteExists = remoteExists(f, s,name);
		boolean localExists = f.exists();
		boolean localNewer = localNewer(f, s,name);
		boolean remoteNewer = remoteNewer(f, s,name);
		if (remoteExists) {
			if (localExists) {
				if (localNewer) {
					push(f, s, true,name);
				} else if (remoteNewer) {
					pull(f, s,name);
				}
			} else {
				pull(f, s,name);
			}
		} else {
			if (localExists) {
				push(f, s, false,name);
			}
		}
	}
	private static boolean remoteExists(File f, MySQL s,String name) {
		ResultSet res = s.query("SELECT * FROM fileSync WHERE name = '" + name + "';");
		try {
			if (res.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
