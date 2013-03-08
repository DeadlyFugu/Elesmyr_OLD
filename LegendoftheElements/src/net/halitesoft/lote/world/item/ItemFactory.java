package net.halitesoft.lote.world.item;

import com.esotericsoftware.minlog.Log;
import groovy.lang.GroovyObject;
import net.halitesoft.lote.ScriptRunner;
import net.halitesoft.lote.util.HashmapLoader;

import java.util.HashMap;
import java.util.Map.Entry;

public class ItemFactory {
private static HashMap<String, Item> items=null;

public static void init() {
	if (items!=null)
		return;
	items=new HashMap<String, Item>();
	HashMap<String, String> item_str=HashmapLoader.readHashmap("data/item_def");
	for (Entry<String, String> e : item_str.entrySet()) {
		items.put(e.getKey(), parseItem(e.getKey(), e.getValue()));
	}
}

private static Item parseItem(String name, String str) {
	String[] parts=str.split(",", 3);
	if (parts.length==3)
		try {
			Item i=(Item) Class.forName("net.halitesoft.lote.world.item."+parts[0]).newInstance();
			i.ctor(name, parts[1], parts[2]);
			return i;
		} catch (ClassNotFoundException e) {
			GroovyObject go=ScriptRunner.get(parts[0]);
			if (go!=null) {
				go.invokeMethod("ctor", new Object[]{name, parts[1], parts[2]});
				return (Item) go.invokeMethod("toItem", new Object[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	Log.warn("Invalid item entry "+name+": "+str);

	return new Item().ctor("Null", "null", "");
}

public static Item getItem(String str) {
	return items.get(str);
}
}
