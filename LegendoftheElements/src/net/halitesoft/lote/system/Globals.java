package net.halitesoft.lote.system;

import net.halitesoft.lote.util.HashmapLoader;

import java.util.HashMap;
import java.util.Map.Entry;

public class Globals {
	private static HashMap<String,String> globals;
	
	public static String get(String key, String def) {
		String ret = globals.get(key);
		if (ret==null) {
			globals.put(key, def);
			return def;
		}
		return ret;
	}
	
	public static boolean get(String key, boolean def) {
		Boolean ret = null;
		try {
			ret = Boolean.parseBoolean(globals.get(key));
		} catch (Exception e) {}

		if (!globals.containsKey(key) || ret==null) {
			globals.put(key, ""+def);
			return def;
		}
		
		
		return ret;
	}

	public static void set(String key, String value) {
		globals.put(key, value);
	}

	public static void setMap(HashMap<String, String> hashMap) {
		globals=hashMap;
	}

	public static boolean containsKey(String string) {
		return globals.containsKey(string);
	}

	public static void save() {
		HashmapLoader.writeHashmap("conf", globals);
	}
	
	public static String getString() {
		String ret = "";
		for (Entry<String,String> e : globals.entrySet())
			ret=ret+"\n"+e.getKey()+" = "+e.getValue();
		return ret.substring(1);
	}

public static String retrieve(String key) {
	return globals.get(key);
}
}
